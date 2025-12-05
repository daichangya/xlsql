# XLSQL 5.1.1 发布说明

## 发布日期
2025-12-05

## 概述

XLSQL 5.1.1 是一个重要的里程碑版本，正式发布了 **Native SQL 引擎**，这是一个完全自研的 SQL 执行引擎，不依赖任何外部数据库。此版本还修复了多个兼容性问题，特别是对 DBeaver 等数据库工具的完整支持。

## 主要特性

### 🚀 Native SQL 引擎

XLSQL 5.1.1 的核心特性是 Native SQL 引擎，它提供了：

- **零外部依赖**：不再需要 HSQLDB、H2 或 MySQL，减少系统资源占用
- **按需加载**：只加载查询涉及的表数据，节省内存
- **完全控制**：可以针对 Excel 场景进行专门优化
- **完整 SQL 支持**：支持 SELECT、JOIN、聚合函数、分组、排序等

### 🔧 DBeaver 完整支持

修复了 DBeaver 使用时的多个问题：

- ✅ 修复查询不到数据的问题（`getResultSet()` 返回 null）
- ✅ 支持标准的 `execute()` + `getResultSet()` 模式
- ✅ 自动管理 ResultSet 生命周期
- ✅ 解决 JSQLParser 版本冲突问题

### 📝 统一日志系统

新增了基于 `java.util.logging` 的统一日志系统：

- 支持文件输出（默认：`~/.xlsql/xlsql.log`）
- 支持控制台输出（DBeaver 等工具可捕获）
- 可配置日志级别
- 自动过滤敏感信息

### 🛡️ 兼容性改进

- 使用反射机制兼容不同版本的 JSQLParser
- 解决 Log4j 依赖冲突问题
- 改进资源管理和异常处理

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>io.github.daichangya</groupId>
    <artifactId>xlsql</artifactId>
    <version>5.1.1</version>
</dependency>
```

### 基本使用

```java
// 注册驱动
Class.forName("jdbc.io.github.daichangya.xlsql.xlDriver");

// 创建连接
String url = "jdbc:xlsql:excel:/path/to/excel/files";
Connection conn = DriverManager.getConnection(url);

// 执行查询
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM workbook_sheet LIMIT 10");

// 处理结果
while (rs.next()) {
    System.out.println(rs.getString(1));
}

// 关闭资源
rs.close();
stmt.close();
conn.close();
```

## 变更详情

### 新增功能

1. **Native SQL 引擎**
   - 完整的 SQL 解析和执行
   - 支持 SELECT、JOIN、聚合、分组、排序等
   - 支持多表 JOIN（INNER、LEFT、RIGHT、FULL OUTER）

2. **DatabaseMetaData 完整实现**
   - 所有必需的元数据方法已实现
   - 支持表、列、索引等元数据查询

3. **统一日志系统**
   - 文件和控制台输出
   - 可配置日志级别和文件路径
   - 自动过滤敏感信息

4. **Statement 增强**
   - 支持 `execute()` + `getResultSet()` 模式
   - 自动管理 ResultSet 生命周期

### 修复问题

1. **DBeaver 兼容性**
   - 修复查询不到数据的问题
   - 修复 ResultSet 资源泄漏

2. **JSQLParser 版本冲突**
   - 使用反射机制实现版本兼容
   - 解决 `NoSuchMethodError` 和 `IncompatibleClassChangeError`

3. **日志系统**
   - 修复日志文件创建失败
   - 修复日志级别配置不生效
   - 修复 DBeaver 中日志不输出

### 改进

1. **代码质量**
   - 改进资源管理
   - 改进异常处理
   - 统一代码风格

2. **测试覆盖**
   - 新增完整的单元测试套件
   - 所有测试用例通过验证

## 系统要求

- **Java**: JDK 8 或更高版本
- **Maven**: 3.6.0 或更高版本（用于构建）
- **操作系统**: Windows、Linux、macOS

## 依赖项

XLSQL 5.1.1 的主要依赖：

- Apache POI 5.2.3（Excel 文件读写）
- JSQLParser 4.9（SQL 解析）
- log4j-api 2.18.0（POI 需要）
- JUnit 5.9.3（测试，仅测试范围）

## 升级指南

从 4.0 版本升级：

1. 更新 Maven 依赖版本为 `5.1.1`
2. Native 引擎是默认引擎，无需额外配置
3. JDBC URL 格式不变
4. 可选：配置日志系统（系统属性或环境变量）

## 已知问题

- Native 引擎当前为只读模式，不支持 UPDATE/INSERT/DELETE
- 大文件可能占用较多内存
- 复杂子查询和 UNION 操作尚未完全支持

## 未来计划

- 支持 UPDATE/INSERT/DELETE 操作
- 添加数据缓存机制
- 支持流式处理大文件
- 添加索引支持
- 支持更多 SQL 特性

## 贡献

感谢所有为 XLSQL 做出贡献的开发者！

## 许可证

XLSQL 使用 GNU General Public License (GPL) v2 许可证。

## 链接

- **GitHub**: https://github.com/daichangya/xlsql
- **文档**: 查看 `doc/` 目录下的文档
- **问题反馈**: https://github.com/daichangya/xlsql/issues

## 联系方式

- **作者**: daichangya
- **邮箱**: daichangya@163.com
- **网站**: http://xlsql.jsdiff.com

