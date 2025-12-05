# 测试数据准备指南

## 概述

xlSQL的集成测试需要真实的Excel文件作为测试数据。本文档说明如何准备这些测试数据文件。

## 快速开始

### 自动生成测试数据（推荐）

项目提供了`TestDataFileGenerator`工具类，可以自动生成所有必需的测试数据文件。

#### 方法1：使用Java命令

```bash
# 1. 编译测试代码
mvn test-compile

# 2. 生成测试数据文件
java -cp "target/test-classes:target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
     com.jsdiff.xlsql.util.TestDataFileGenerator
```

#### 方法2：使用Maven执行

```bash
mvn test-compile exec:java \
    -Dexec.mainClass="com.jsdiff.xlsql.util.TestDataFileGenerator" \
    -Dexec.classpathScope=test
```

#### 方法3：在IDE中运行

直接运行`TestDataFileGenerator`类的`main`方法。

### 验证文件生成

生成成功后，`database`目录下应该有以下文件：

```bash
ls -lh database/test*.xls
```

应该看到：
- `database/test1.xls` (约100KB，1000行数据)
- `database/test2.xls` (约50KB，500行数据)
- `database/test3.xls` (约20KB，200行数据)

## 测试数据文件说明

**重要**：所有测试数据文件都位于项目根目录下的`database`目录中。

### test1.xls

**位置**：`database/test1.xls`

**用途**：主测试数据文件，用于基础查询、WHERE条件、ORDER BY、聚合查询等测试。

**结构**：
- **工作表名称**：Sheet1
- **列定义**：
  - `a` (VARCHAR): 字符串列，值如value1, value2, value3等（循环使用value1-value5）
  - `b` (VARCHAR): 字符串列，值如data1, data2, data3等
  - `id` (INTEGER): 整数列，值1-1000
  - `age` (INTEGER): 整数列，值25-35（循环）
  - `salary` (DOUBLE): 数值列，值5000.0-7000.0（循环）
- **数据行数**：1000行（不含表头）

**示例数据**：
```
a       | b       | id | age | salary
--------|---------|----|----|--------
value1  | data1   | 1  | 25 | 5000.0
value2  | data2   | 2  | 30 | 6000.0
value3  | data3   | 3  | 35 | 7000.0
...
```

**在SQL中的使用**：
```sql
SELECT * FROM test1_Sheet1 LIMIT 5;
SELECT * FROM test1_Sheet1 WHERE a = 'value1';
SELECT COUNT(*) FROM test1_Sheet1;
SELECT a, COUNT(*) FROM test1_Sheet1 GROUP BY a;
```

### test2.xls

**位置**：`database/test2.xls`

**用途**：JOIN测试数据文件，用于测试表连接功能。

**结构**：
- **工作表名称**：Sheet1
- **列定义**：
  - `id` (INTEGER): 整数列，值1-500（与test1.xls的id部分匹配）
  - `b` (VARCHAR): 字符串列，值如join_data1, join_data2等
  - `name` (VARCHAR): 字符串列，值如Department1, Department2等（循环使用5个部门）
- **数据行数**：500行（不含表头）

**示例数据**：
```
id | b          | name
---|------------|------------
1  | join_data1 | Department1
2  | join_data2 | Department2
3  | join_data3 | Department3
...
```

**在SQL中的使用**：
```sql
SELECT t1.a, t2.b 
FROM test1_Sheet1 t1 
INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id;
```

### test3.xls

**位置**：`database/test3.xls`

**用途**：多表JOIN测试数据文件，用于测试三表及以上的连接。

**结构**：
- **工作表名称**：Sheet1
- **列定义**：
  - `id` (INTEGER): 整数列，值1-200
  - `c` (VARCHAR): 字符串列，值如extra_data1, extra_data2等
  - `description` (VARCHAR): 字符串列，值如Description1, Description2等
- **数据行数**：200行（不含表头）

**在SQL中的使用**：
```sql
SELECT t1.a, t2.b, t3.c 
FROM test1_Sheet1 t1 
INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id
INNER JOIN test3_Sheet1 t3 ON t2.id = t3.id;
```

## 表名格式说明

xlSQL使用下划线分隔的表名格式：`workbook_sheet`

例如：
- `test1_Sheet1` 表示 `test1.xls` 文件的 `Sheet1` 工作表
- `test2_Sheet1` 表示 `test2.xls` 文件的 `Sheet1` 工作表

**注意**：表名不区分大小写，但建议使用与工作表名称完全匹配的大小写。

## 清理测试数据

测试完成后，可以清理生成的测试数据文件：

```java
import com.jsdiff.xlsql.util.TestDataFileGenerator;

// 清理测试数据文件（从database目录）
TestDataFileGenerator.cleanupTestFiles(System.getProperty("user.dir"));
```

或者手动删除：

```bash
rm database/test1.xls database/test2.xls database/test3.xls
```

## 在CI/CD中使用

### GitHub Actions示例

```yaml
- name: Generate test data
  run: |
    mvn test-compile exec:java \
      -Dexec.mainClass="com.jsdiff.xlsql.util.TestDataFileGenerator" \
      -Dexec.classpathScope=test

- name: Run tests
  run: mvn test
```

### 在测试基类中自动生成

可以在测试基类中添加`@BeforeAll`方法：

```java
@BeforeAll
public static void setUpTestData() throws IOException {
    String baseDir = System.getProperty("user.dir");
    // 检查文件是否已存在，避免重复生成
    File testFile = new File(baseDir, "test1.xls");
    if (!testFile.exists()) {
        TestDataFileGenerator.generateAllTestFiles(baseDir);
    }
}
```

## 故障排除

### 问题1：文件生成失败

**错误信息**：`java.io.FileNotFoundException` 或权限错误

**解决方案**：
- 确保项目根目录有写入权限
- 检查是否有其他进程正在使用这些文件
- 确保Maven依赖已正确下载（Apache POI）

### 问题2：测试找不到表

**错误信息**：`Table not found: test1_Sheet1` 或 `Table not found: test1_Sheet1`

**可能原因**：
1. 测试数据文件不存在（应该在`database`目录下）
2. 表名格式不正确（应该使用下划线：`test1_Sheet1`）
3. 工作表名称不匹配（检查Excel文件中的实际工作表名称）
4. 连接URL指向错误的目录

**解决方案**：
1. 确认测试数据文件已生成：`ls database/test*.xls`
2. 检查表名格式是否正确
3. 使用Excel打开文件，确认工作表名称
4. 确认连接URL指向`database`目录

### 问题3：数据不匹配

**错误信息**：断言失败，数据不符合预期

**解决方案**：
- 检查测试代码中的预期值是否与生成的数据匹配
- 重新生成测试数据文件
- 查看`TestDataFileGenerator`中的数据定义

## 自定义测试数据

如果需要自定义测试数据，可以修改`TestDataFileGenerator`类中的方法：

1. 修改`generateTest1Xls()`方法以更改test1.xls的数据
2. 修改`generateTest2Xls()`方法以更改test2.xls的数据
3. 修改`generateTest3Xls()`方法以更改test3.xls的数据

或者创建新的生成方法来生成自定义测试文件。

## 相关文档

- [测试实现总结](TEST_IMPLEMENTATION_SUMMARY.md)
- [测试评估报告](TEST_EVALUATION_REPORT.md)

---

**最后更新**: 2025-12-04  
**作者**: daichangya

