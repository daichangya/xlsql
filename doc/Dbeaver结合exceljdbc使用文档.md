# DBeaver 与 Excel JDBC 驱动使用说明文档

## 1. 概述

本文档详细介绍了如何在 DBeaver 中配置和使用 Excel JDBC 驱动来连接和操作 Excel 文件。Excel JDBC 驱动允许用户像操作数据库一样查询和修改 Excel 文件中的数据。

## 2. 准备工作

### 2.1 系统要求
- Java 8 或更高版本
- DBeaver 21.0 或更高版本
- Excel JDBC 驱动 JAR 文件

### 2.2 获取 Excel JDBC 驱动
获取驱动文件：
```
https://central.sonatype.com/repository/maven-snapshots/com/jsdiff/xlsql/2.0-SNAPSHOT/xlsql-2.0-20250827.014816-1.jar 
```


## 3. 在 DBeaver 中配置 Excel JDBC 驱动

### 3.1 打开驱动管理器
1. 启动 DBeaver
2. 点击菜单栏 **Database** → **Driver Manager**

### 3.2 创建新驱动
1. 点击 **New** 按钮创建新驱动
2. 在 **Settings** 标签页中填写以下信息：
    - **Driver Name**: Excel JDBC Driver
    - **Class Name**: `com.jsdiff.excel.jdbc.xlDriver`
    - **URL Template**: `jdbc:jsdiff:excel:{path}`
    - **Port**: (留空)
   
![dbeaver1](dbeaver1.png)

### 3.3 添加驱动文件
1. 切换到 **Libraries** 标签页
2. 点击 **Add File** 按钮
3. 选择你的 Excel JDBC 驱动 JAR 文件
    - 路径示例：`/path/to/xlsql-2.0-20250827.014816-1.jar `
4. 点击 **OK** 保存驱动配置

## 4. 创建数据库连接

### 4.1 新建连接
1. 点击 **Database** → **New Database Connection**
2. 在连接类型列表中选择 **Generic** → **Generic JDBC**
3. 点击 **Next**

### 4.2 配置连接参数
1. **Driver**: 选择之前创建的 "Excel JDBC Driver"
2. **JDBC URL**: 输入 Excel 文件路径
   ```
   jdbc:jsdiff:excel:/path
   ```

   示例：
   ```
   jdbc:jsdiff:excel:/Users/username/Documents
   ```


### 4.3 测试连接
1. 点击 **Test Connection** 按钮
2. 如果配置正确，会显示 "Connected" 消息
3. 点击 **Finish** 完成连接创建

## 5. 使用 Excel JDBC 驱动

### 5.1 浏览数据结构
连接成功后，你可以在 DBeaver 的数据库导航器中看到：
- Excel 文件作为数据库显示
- 每个工作表作为数据表显示
- 表的列对应 Excel 中的第一行标题

### 5.2 执行 SQL 查询
在 SQL 编辑器中可以执行标准 SQL 查询：（特别注意标名称 and 字段名称都要加双引号）

```sql
-- 查询所有数据
SELECT * FROM "test.Sheet1";

-- 条件查询
SELECT * FROM "test.Sheet1" WHERE "column1" = 'value';

-- 聚合查询
SELECT COUNT(*) FROM "test.Sheet1";

-- 排序查询
SELECT * FROM "test.Sheet1" ORDER BY "column1";
```

![dbeaver2](dbeaver2.png)

## 6. Excel 文件要求

### 6.1 文件格式
- 支持 `.xls` 格式（Excel 97-2003）

### 6.2 工作表结构
1. 第一行为列标题
2. 标题应使用有效的 SQL 标识符
3. 避免使用特殊字符和空格
4. 每列应保持数据类型一致

### 6.3 示例 Excel 结构
```
| Name    | Age | City      |
|---------|-----|-----------|
| John    | 25  | New York  |
| Jane    | 30  | Los Angeles |
```


## 7. 常见问题和解决方案

### 7.1 连接失败
**问题**: `Cannot invoke "String.length()" because "<parameter1>" is null`
**解决方案**:
- 检查 JDBC URL 中的文件路径是否正确
- 确保 Excel 文件存在且可访问

### 7.2 驱动未找到
**问题**: `Driver class not found`
**解决方案**:
- 确认驱动 JAR 文件已正确添加到驱动配置中
- 检查驱动类名是否正确：`com.jsdiff.excel.jdbc.xlDriver`

### 7.3 权限问题
**问题**: `Permission denied` 访问 Excel 文件
**解决方案**:
- 检查文件权限
- 确保 DBeaver 进程有读写文件的权限

### 7.4 中文字符乱码
**解决方案**:
- 确保 Excel 文件使用 UTF-8 编码
- 在连接参数中指定字符集

## 8. 高级配置

### 8.1 连接属性
可以在连接配置中设置以下属性：
- `charset`: 指定字符集编码
- `readonly`: 设置只读模式

### 8.2 性能优化
- 对于大型 Excel 文件，建议使用过滤条件减少数据加载
- 避免在复杂公式的工作表上执行查询

## 9. 限制和注意事项

### 9.1 功能限制
1. 不支持复杂的数据类型（如图片、图表等）
2. 不支持 Excel 公式计算
3. 对大型文件的性能可能较差
4. 并发访问支持有限

### 9.2 数据类型映射
| Excel 类型 | SQL 类型 |
|------------|----------|
| 文本       | VARCHAR  |
| 数字       | NUMERIC  |
| 日期       | DATE     |
| 布尔值     | BOOLEAN  |

### 9.3 最佳实践
1. 定期备份重要的 Excel 文件
2. 在执行写操作前确认文件未被其他程序占用
3. 避免在生产环境中直接修改原始数据文件
4. 使用副本文件进行测试操作

## 10. 故障排除

### 10.1 日志查看
1. 在 DBeaver 中打开 **Window** → **Show View** → **Error Log**
2. 查看详细错误信息

### 10.2 启用调试模式
在启动 DBeaver 时添加调试参数：
```bash
dbeaver -vmargs -Dorg.jkiss.dbeaver.debug=true
```


### 10.3 联系支持
如果遇到无法解决的问题，请提供：
- 完整的错误日志
- 使用的 Excel 文件示例
- DBeaver 和驱动版本信息

## 11. 版本兼容性

| DBeaver 版本 | Excel JDBC 驱动版本 | 兼容性 |
|--------------|---------------------|--------|
| 21.x         | 2.0-SNAPSHOT        | ✓      |
| 22.x         | 2.0-SNAPSHOT        | ✓      |

## 12. 更新日志

### 版本 2.0-SNAPSHOT
- 初始版本
- 支持基本的 CRUD 操作
- 支持 .xls .xlsx 格式文件
- 与 DBeaver 集成

---

**注意**: 本文档基于 Excel JDBC 驱动版本 2.0-SNAPSHOT 编写，具体功能可能因版本更新而有所变化。建议在使用前确认当前版本的功能特性。