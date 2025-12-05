# XLSQL 变更日志

## [5.1.1] - 2025-12-05

### 重大更新

#### Native SQL 引擎正式发布
这是 XLSQL 5.1.1 的核心特性，提供了一个完全自研的 SQL 执行引擎，不依赖任何外部数据库（HSQLDB/H2/MySQL）。

**主要优势：**
- **零外部依赖**：不依赖 HSQLDB、H2 或 MySQL，减少系统资源占用
- **按需加载**：只加载查询涉及的表数据，节省内存
- **完全控制**：可以针对 Excel 场景进行专门优化
- **DBeaver 兼容**：完全支持 DBeaver 等数据库工具的标准 JDBC 模式

### Added

#### Native 引擎核心功能
- **Native SQL 引擎实现**
  - 完整的 SQL 解析和执行引擎
  - 支持 SELECT、FROM、WHERE、JOIN、GROUP BY、HAVING、ORDER BY、LIMIT
  - 支持聚合函数（COUNT、SUM、AVG、MAX、MIN）
  - 支持多表 JOIN（INNER、LEFT、RIGHT、FULL OUTER）
  - 支持表达式计算和条件过滤

- **JSQLParser 集成**
  - 使用 JSQLParser 4.9 进行 SQL 解析
  - 支持 MySQL 兼容语法
  - 使用反射机制兼容不同版本的 JSQLParser（解决 DBeaver 版本冲突）

- **DatabaseMetaData 完整实现**
  - 实现了所有必需的 DatabaseMetaData 方法
  - 支持表、列、索引等元数据查询
  - 完全兼容 JDBC 标准接口

- **Statement 增强**
  - 支持 `execute()` + `getResultSet()` 模式（DBeaver 标准模式）
  - 自动管理 ResultSet 生命周期
  - 支持多次查询的结果集切换

#### 日志系统
- **统一日志框架**
  - 基于 `java.util.logging` 的统一日志系统
  - 支持文件输出（默认：`~/.xlsql/xlsql.log`）
  - 支持控制台输出（DBeaver 等工具可捕获）
  - 可配置日志级别（INFO、WARNING、ERROR 等）
  - 自动过滤敏感信息（密码等）

- **日志分类**
  - CONNECTION：连接相关日志
  - SQL：SQL 执行日志（包含性能指标）
  - SQL_PARSE：SQL 解析日志
  - DEBUG：调试信息
  - ERROR：错误信息

#### 兼容性改进
- **JSQLParser 版本兼容**
  - 使用反射机制动态调用 JSQLParser API
  - 兼容 DBeaver 内置的旧版本 JSQLParser
  - 解决 `NoSuchMethodError` 和 `IncompatibleClassChangeError`

- **Log4j 兼容性**
  - 添加 `log4j-api` 依赖（POI 需要）
  - 不包含 `log4j-core`，避免日志系统冲突
  - 解决 `ClassNotFoundException` 问题

#### 测试覆盖
- **单元测试完善**
  - 新增 `xlNativeStatementTest` 测试套件
  - 测试 `execute()` + `getResultSet()` 模式
  - 测试 ResultSet 生命周期管理
  - 测试多次查询的结果集切换
  - 所有测试用例通过验证

### Changed

- **依赖更新**
  - JSQLParser 升级到 4.9 版本
  - 添加 `log4j-api` 2.18.0（compile 范围）
  - 保持其他依赖版本稳定

- **代码质量改进**
  - 改进资源管理（ResultSet 自动关闭）
  - 改进异常处理和错误信息
  - 统一代码风格和命名规范

### Fixed

- **DBeaver 兼容性问题**
  - 修复 `getResultSet()` 返回 null 导致查询不到数据的问题
  - 修复 `execute()` 方法未保存 ResultSet 的问题
  - 修复 Statement 关闭时 ResultSet 未关闭的资源泄漏

- **JSQLParser 版本冲突**
  - 修复 `NoSuchMethodError: getSelectBody()` 错误
  - 修复 `IncompatibleClassChangeError: SelectItem` 错误
  - 使用反射机制实现版本兼容

- **日志系统问题**
  - 修复日志文件创建失败的问题
  - 修复日志级别配置不生效的问题
  - 修复 DBeaver 中日志不输出的问题

### Security

- **敏感信息保护**
  - 日志系统自动过滤 JDBC URL 中的密码
  - 日志系统自动过滤 Properties 中的密码字段

### Performance

- **内存优化**
  - 改进 ResultSet 资源管理，避免内存泄漏
  - 优化表数据加载策略

### Documentation

- **文档更新**
  - 更新 README.md 和 README-zh.md
  - 更新 INSTALLATION_GUIDE.md
  - 新增 RELEASE_NOTES_5.1.0.md
  - 新增 RELEASE_GUIDE.md（Maven Central 发布指南）

### Migration Guide

从 4.0 版本升级到 5.1.1：

1. **依赖更新**
   ```xml
   <dependency>
       <groupId>io.github.daichangya</groupId>
       <artifactId>xlsql</artifactId>
       <version>5.1.1</version>
   </dependency>
   ```

2. **Native 引擎使用**
   - Native 引擎是默认引擎，无需额外配置
   - JDBC URL 格式不变：`jdbc:xlsql:excel:/path/to/excel/files`

3. **日志配置（可选）**
   - 可以通过系统属性或环境变量配置日志级别和文件路径
   - 默认日志文件：`~/.xlsql/xlsql.log`

4. **DBeaver 使用**
   - 现在完全支持 DBeaver 的标准 JDBC 模式
   - 无需特殊配置即可正常使用

### Known Issues

- Native 引擎当前为只读模式，不支持 UPDATE/INSERT/DELETE
- 大文件可能占用较多内存（所有数据加载到内存）
- 复杂子查询和 UNION 操作尚未完全支持

### Future Plans

- 支持 UPDATE/INSERT/DELETE 操作
- 添加数据缓存机制
- 支持流式处理大文件
- 添加索引支持以加速查询
- 支持更多 SQL 特性（子查询、UNION 等）

