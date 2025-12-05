# XLSQL 变更日志

## [3.0.0] - 2025-08-28

### Added
- 新增对Excel 365最新格式的全面支持
- 新增流式处理API，支持大数据量Excel文件处理
- 新增`ExcelUtils`工具类，提供常用Excel操作封装
- 新增连接池管理功能，提升高并发场景性能
- 新增`ModernExcelReader`实现，基于Java 8 Stream API
- 新增`ConnectionPoolManager`连接池管理组件

### Changed
- 全面升级至Java 11基础环境
- 重构核心引擎，采用模块化架构设计
- 优化SQL解析器，支持更多标准SQL语法
- 改进异常处理机制，提供更详细的错误信息
- 更新文档系统，添加现代化API文档
- 性能优化，查询速度提升约40%

### Deprecated
- 标记`jxlReader`为@Deprecated，将在4.0版本移除
- 弃用旧版配置文件格式，推荐使用YAML配置

### Fixed
- 修复大数据量导出时的内存泄漏问题
- 修复日期类型转换的时区处理错误
- 修复并发环境下连接泄露的问题
- 修复特殊字符在SQL查询中的转义问题