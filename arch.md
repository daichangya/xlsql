# ExcelJDBC 架构文档

## 1. 概述
ExcelJDBC 是一个基于 Java 的 JDBC 驱动，用于通过 SQL 查询和操作 Excel 文件。它支持将 Excel 文件作为数据库表进行查询和操作，提供了丰富的 JDBC 接口实现。

## 2. 核心模块

### 2.1 数据库连接模块 (`xlInstance`)
- **功能**: 管理数据库连接配置，包括日志、数据库路径、引擎类型等。
- **关键类**: `xlInstance`
  - 负责加载和保存配置文件 (`xlsql_config.xml`)。
  - 提供连接数据库的方法 (`connect`)。
  - 支持多种数据库引擎（如 HSQLDB、MySQL）。

### 2.2 JDBC 驱动模块 (`xlDriver`)
- **功能**: 实现 JDBC 驱动接口，注册驱动并创建数据库连接。
- **关键类**: `xlDriver`
  - 提供标准的 JDBC 驱动接口实现。
  - 支持通过 URL 和配置连接到 Excel 数据库。

### 2.3 元数据模块 (`xlDatabaseMetaData`)
- **功能**: 提供数据库元数据信息，如数据库版本、表结构、列信息等。
- **关键类**: `xlDatabaseMetaData`
  - 实现 `DatabaseMetaData` 接口，支持查询数据库和表的元数据。
  - 提供兼容性支持，适配不同数据库引擎的元数据查询。

### 2.4 Excel 文件操作模块 (`xlWorkbook`)
- **功能**: 管理 Excel 文件（工作簿）的读取和写入操作。
- **关键类**: `xlWorkbook`
  - 封装 Excel 工作簿的读写逻辑。
  - 支持创建工作簿、读取工作表、保存修改等操作。
  - 依赖 `jxl` 库处理 Excel 文件。

## 3. 数据流
1. **连接阶段**: 用户通过 `xlDriver` 创建连接，`xlInstance` 加载配置并初始化引擎。
2. **查询阶段**: 用户通过 JDBC 接口提交 SQL 查询，驱动将查询转换为对 Excel 文件的操作。
3. **结果返回**: 驱动将 Excel 数据封装为 JDBC 结果集 (`ResultSet`) 返回给用户。

## 4. 依赖项
- **jxl**: 用于读写 Excel 文件。
- **HSQLDB/MySQL**: 可选数据库引擎支持。

## 5. 扩展性
- 支持自定义数据库引擎（通过 `xlDatabaseFactory` 扩展）。
- 支持自定义 SQL 解析器（通过 `ASqlParser` 扩展）。

## 6. 限制
- 仅支持 `.xls` 格式的 Excel 文件。
- 部分 JDBC 高级功能（如事务）可能受限。