# Native 引擎架构文档

## 概述

Native 引擎是 XLSQL 项目中的自研 SQL 执行引擎，不依赖任何外部数据库（HSQLDB/H2），直接基于 Excel 数据执行 SQL 查询。本文档详细说明 Native 引擎如何读取 Excel 文件并进行查询。

## 目录

1. [架构设计](#架构设计)
2. [Excel 文件读取机制](#excel-文件读取机制)
3. [查询执行流程](#查询执行流程)
4. [数据流图](#数据流图)
5. [核心组件说明](#核心组件说明)
6. [性能特点](#性能特点)

---

## 架构设计

### 整体架构

```
用户SQL查询
    ↓
xlConnectionNative (连接层)
    ↓
NativeSqlEngine (引擎层)
    ↓
xlNativeSelect (查询执行器)
    ↓
NativeSqlParser → PlainSelectAdapter (SQL解析)
    ↓
loadTableData() (按需加载Excel数据)
    ↓
执行查询操作 (JOIN/WHERE/GROUP BY等)
    ↓
构建ResultSet
    ↓
返回查询结果
```

### 核心组件

1. **xlConnectionNative** - Native 引擎的连接实现
2. **NativeSqlEngine** - SQL 执行引擎接口实现
3. **xlNativeSelect** - 查询执行器
4. **MySQLSqlParser** - 基于 JSqlParser 的 SQL 解析器（支持MySQL语法，但引擎本身不依赖MySQL数据库）
5. **PlainSelectAdapter** - PlainSelect 到 QueryPlan 的适配器
6. **TableInfo** - 表数据的内存表示
7. **ADatabase/xlDatabase** - Excel 数据存储抽象

---

## Excel 文件读取机制

### 数据读取层次

```
xlDatabase (数据库层)
  └── xlWorkbook (工作簿层，对应Excel文件)
      └── xlSheet (工作表层，对应Excel中的Sheet)
          └── Apache POI (底层读取库)
              └── Excel文件 (.xls / .xlsx)
```

### 初始化阶段

**1. 连接创建时 (`xlConnectionNative` 构造函数)**

```java
// 从URL提取目录路径
String dir = URL.substring(URL_PFX_XLS.length());

// 创建数据库对象，扫描Excel文件
datastore = xlDatabaseFactory.create(new File(dir), "xls");

// 创建自研SQL引擎
nativeEngine = new NativeSqlEngine();
nativeEngine.initialize(datastore);
```

**2. Excel 文件扫描**

- 使用 `ModernExcelReader` 扫描指定目录下的所有 Excel 文件
- 支持 `.xls` (Excel 97-2003) 和 `.xlsx` (Excel 2007+)
- 读取每个 Excel 文件的工作表结构（元数据）
- **不预加载数据**，只扫描文件结构

**3. 工作表验证**

- 检查工作表是否可以作为 SQL 表使用
- 第一行必须是列名（标题行）
- 后续行为数据行

### 按需数据加载

**数据加载时机：**
- 仅在执行查询时加载相关表的数据
- 根据查询计划中的表信息（workbook.sheet）按需读取

**数据加载方法：**

```java
// 在 xlNativeSelect.loadTableData() 中
String[] columnNames = datastore.getColumnNames(workbook, sheet);
String[] columnTypes = datastore.getColumnTypes(workbook, sheet);
String[][] values = datastore.getValues(workbook, sheet);
int rowCount = datastore.getRows(workbook, sheet);

// 加载到 TableInfo 对象
table.loadData(columnNames, columnTypes, values, rowCount);
```

**底层读取流程：**

1. `AReader.getColumnNames()` → `xlSheet.getColumnNames()`
   - 使用 Apache POI 打开 Excel 文件
   - 读取第一行作为列名

2. `AReader.getColumnTypes()` → `xlSheet.getColumnTypes()`
   - 根据单元格数据类型推断 SQL 类型
   - 支持：VARCHAR、INTEGER、DOUBLE、DATE、TIMESTAMP 等

3. `AReader.getValues()` → `xlSheet.getValues()`
   - 读取所有数据单元格
   - 转换为字符串数组：`String[][]`（列优先：`data[列索引][行索引]`）

4. `AReader.getRows()` → `xlSheet.getRows()`
   - 获取数据行数（不包括标题行）

### Apache POI 使用

**文件格式支持：**
- `.xls` (Excel 97-2003)：使用 `HSSFWorkbook`
- `.xlsx` (Excel 2007+)：使用 `XSSFWorkbook`

**读取示例：**

```java
Workbook wb = null;
try {
    if (FileType.XLSX.equals(getFileType())) {
        wb = new XSSFWorkbook(getWorkbookFile());
    } else if (FileType.XLS.equals(getFileType())) {
        wb = new HSSFWorkbook(new FileInputStream(getWorkbookFile()));
    }
    
    Sheet sheet = wb.getSheet(sheetName);
    // 读取数据...
} finally {
    IOUtils.closeQuietly(wb); // 确保资源释放
}
```

---

## 查询执行流程

### 完整执行流程

```
1. SQL解析
   ↓
2. 加载表数据（按需）
   ↓
3. 执行FROM和JOIN
   ↓
4. 应用WHERE条件
   ↓
5. 执行聚合和分组
   ↓
6. 构建结果集
   ↓
7. 返回ResultSet
```

### 详细步骤说明

#### 步骤1：SQL解析

```java
// 使用JSqlParser解析SQL
NativeSqlParser parser = new NativeSqlParser();
PlainSelect plainSelect = parser.parse(sql);

// 转换为QueryPlan（适配器模式）
PlainSelectAdapter adapter = new PlainSelectAdapter();
QueryPlan plan = adapter.toQueryPlan(plainSelect);
```

**支持的功能：**
- SELECT 子句（列选择、聚合函数）
- FROM 子句（单表、多表）
- JOIN（INNER、LEFT、RIGHT、FULL OUTER）
- WHERE 条件（所有表达式类型）
- GROUP BY、HAVING
- ORDER BY
- LIMIT/OFFSET

#### 步骤2：加载表数据

```java
private List<TableInfo> loadTables(QueryPlan plan) throws SQLException {
    List<TableInfo> tables = new ArrayList<>();
    
    // 加载主表
    TableInfo mainTable = plan.getMainTable();
    loadTableData(mainTable);
    tables.add(mainTable);
    
    // 加载JOIN表
    for (JoinInfo join : plan.getJoins()) {
        TableInfo joinTable = join.getTable();
        loadTableData(joinTable);
        tables.add(joinTable);
    }
    
    return tables;
}
```

**数据加载到内存：**
- 数据存储在 `TableInfo` 对象中
- 格式：`String[][] data`（列优先）
- 包含列名、列类型、行数等元数据

#### 步骤3：执行FROM和JOIN

```java
// 将主表数据转换为行列表
List<String[]> rows = convertTableToRows(mainTable);

// 执行JOIN操作
JoinExecutor joinExecutor = new JoinExecutor();
for (JoinInfo join : plan.getJoins()) {
    rows = joinExecutor.execute(rows, rightTable, join, columnIndexMap);
}
```

**JOIN算法：**
- 嵌套循环连接（Nested Loop Join）
- 哈希连接（Hash Join）
- 混合策略（根据数据大小选择）

#### 步骤4：应用WHERE条件

```java
if (plan.getWhereClause() != null) {
    ConditionEvaluator evaluator = new ConditionEvaluator();
    rows = applyWhereCondition(rows, plan, tables);
}
```

**条件评估：**
- 使用 `ExpressionEvaluator` 评估 SQL 表达式
- 支持列引用、字面量、算术运算、比较运算、逻辑运算等
- 逐行过滤，只保留满足条件的行

#### 步骤5：执行聚合和分组

```java
if (plan.hasAggregation() || plan.hasGroupBy()) {
    AggregationExecutor aggExecutor = new AggregationExecutor();
    rows = aggExecutor.execute(rows, plan, columnIndexMap);
}
```

**支持的聚合函数：**
- COUNT、SUM、AVG、MAX、MIN
- 支持 DISTINCT
- 支持 GROUP BY 分组

#### 步骤6：构建结果集

```java
ResultSetBuilder builder = new ResultSetBuilder();
return builder.build(rows, plan, columnIndexMap, tables);
```

**结果集构建：**
- 选择指定的列（SELECT 子句）
- 应用排序（ORDER BY）
- 应用限制（LIMIT/OFFSET）
- 封装为 `xlNativeResultSet`

---

## 数据流图

### 完整数据流

```
┌─────────────────────────────────────────────────────────────┐
│                    用户SQL查询                               │
│          SELECT * FROM workbook_sheet                        │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────┐
│              MySQLSqlParser (JSqlParser)                    │
│              解析SQL → PlainSelect AST                       │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────┐
│              PlainSelectAdapter                             │
│          PlainSelect → QueryPlan 转换                        │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────┐
│              xlNativeSelect.loadTables()                     │
│              根据QueryPlan加载表数据                         │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────┐
│              ADatabase.getValues()                           │
│          workbook, sheet → Excel数据                         │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────┐
│              xlSheet (Apache POI)                            │
│              读取Excel文件 → String[][]                      │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────┐
│              TableInfo.loadData()                            │
│              数据加载到内存                                  │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────┐
│              执行查询操作                                    │
│  JOIN → WHERE → GROUP BY → ORDER BY → LIMIT                 │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────┐
│              ResultSetBuilder                                │
│              构建 xlNativeResultSet                          │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────┐
│              返回查询结果                                    │
│              ResultSet                                       │
└─────────────────────────────────────────────────────────────┘
```

### 内存数据结构

```
TableInfo
├── workbook: String          (工作簿名)
├── sheet: String             (工作表名)
├── alias: String             (表别名)
├── columnNames: String[]     (列名数组)
├── columnTypes: String[]     (列类型数组)
├── data: String[][]          (数据矩阵，列优先)
│   └── data[列索引][行索引]
├── rowCount: int             (行数)
└── columnIndexMap: Map       (列名→索引映射)
```

---

## 核心组件说明

### 1. xlConnectionNative

**职责：**
- 管理 Native 引擎连接
- 初始化 Excel 数据库对象
- 创建 NativeSqlEngine 实例

**关键方法：**
- `xlConnectionNative(String url)` - 构造函数，初始化引擎
- `createStatement()` - 创建 Statement
- `prepareStatement(String sql)` - 创建 PreparedStatement

### 2. NativeSqlEngine

**职责：**
- SQL 执行引擎接口实现
- 管理查询执行器

**关键方法：**
- `initialize(ADatabase datastore)` - 初始化引擎
- `executeQuery(String sql)` - 执行查询
- `shutdown()` - 关闭引擎

### 3. xlNativeSelect

**职责：**
- 执行 SQL 查询
- 协调各个执行器
- 管理数据加载

**关键方法：**
- `executeQuery(String sql)` - 执行完整查询流程
- `loadTableData(TableInfo table)` - 加载表数据
- `executeFromAndJoins()` - 执行 JOIN
- `applyWhereCondition()` - 应用 WHERE 条件

### 4. MySQLSqlParser

**职责：**
- 使用 JSqlParser 解析 SQL
- 支持 MySQL 语法

**关键方法：**
- `parse(String sql)` - 解析 SQL，返回 PlainSelect

### 5. PlainSelectAdapter

**职责：**
- 将 JSqlParser 的 PlainSelect 转换为 QueryPlan
- 适配器模式，保持向后兼容

**关键方法：**
- `toQueryPlan(PlainSelect plainSelect)` - 转换查询计划

### 6. TableInfo

**职责：**
- 存储表数据的内存表示
- 提供数据访问接口

**关键方法：**
- `loadData()` - 加载数据
- `getRow(int rowIndex)` - 获取指定行
- `getColumnIndex(String columnName)` - 获取列索引

### 7. ADatabase / xlDatabase

**职责：**
- Excel 数据存储抽象
- 提供数据读取接口

**关键方法：**
- `getColumnNames(workbook, sheet)` - 获取列名
- `getColumnTypes(workbook, sheet)` - 获取列类型
- `getValues(workbook, sheet)` - 获取数据值
- `getRows(workbook, sheet)` - 获取行数

---

## 性能特点

### 优势

1. **按需加载**
   - 只加载查询涉及的表数据
   - 不预加载所有数据，节省内存

2. **零外部依赖**
   - 不依赖 HSQLDB/H2/MySQL
   - 减少系统资源占用

3. **直接操作**
   - 直接基于 Excel 数据执行查询
   - 无需数据导入导出

4. **完全控制**
   - 可以针对 Excel 场景优化查询
   - 支持自定义查询策略

### 限制

1. **内存限制**
   - 所有数据加载到内存
   - 大文件可能占用大量内存

2. **只读模式**
   - 当前版本不支持 UPDATE/INSERT/DELETE
   - 只支持 SELECT 查询

3. **性能考虑**
   - 每次查询都需要读取 Excel 文件
   - 没有缓存机制（可扩展）

### 优化建议

1. **缓存机制**
   - 可以添加表数据缓存
   - 减少重复读取 Excel 文件

2. **流式处理**
   - 对于大文件，可以考虑流式读取
   - 避免一次性加载所有数据

3. **索引支持**
   - 可以添加内存索引
   - 加速 WHERE 和 JOIN 操作

---

## 示例

### 简单查询

```sql
SELECT * FROM test1_Sheet1 LIMIT 10
```

**执行流程：**
1. 解析 SQL，识别表 `test1_Sheet1`（下划线格式）
2. 加载 `test1.xls` 文件的 `Sheet1` 工作表数据
3. 应用 LIMIT 10，只返回前10行
4. 返回结果集

### JOIN 查询

```sql
SELECT t1.id, t1.name, t2.value 
FROM workbook1_sheet1 t1
INNER JOIN workbook2_sheet2 t2 
ON t1.id = t2.foreign_id
WHERE t1.status = 'active'
```

**执行流程：**
1. 解析 SQL，识别两个表和 JOIN 条件
2. 加载两个表的数据到内存
3. 执行 INNER JOIN（嵌套循环或哈希连接）
4. 应用 WHERE 条件过滤
5. 选择指定列
6. 返回结果集

### 聚合查询

```sql
SELECT category, COUNT(*), SUM(amount), AVG(price)
FROM sales_data
GROUP BY category
HAVING COUNT(*) > 10
ORDER BY SUM(amount) DESC
```

**执行流程：**
1. 解析 SQL，识别聚合函数和分组
2. 加载表数据
3. 按 category 分组
4. 计算 COUNT、SUM、AVG
5. 应用 HAVING 条件
6. 按 SUM(amount) 降序排序
7. 返回结果集

---

## 总结

Native 引擎通过以下方式实现 Excel 数据的 SQL 查询：

1. **按需加载**：查询时才读取相关 Excel 文件
2. **内存处理**：数据加载到内存后执行 SQL 操作
3. **零依赖**：不依赖外部数据库引擎
4. **完整支持**：支持 JOIN、聚合、排序等复杂查询

这种设计使得 Native 引擎既灵活又高效，特别适合 Excel 数据的查询场景。

---

**文档版本：** 1.0  
**最后更新：** 2025-11-25  
**作者：** XLSQL 开发团队

