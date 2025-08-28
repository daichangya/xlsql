# Changelog

## [3.0-SNAPSHOT] - 2025-08-26

### Added
- 新增对`Apache POI`的支持，用于读写Excel文件。
- 新增`命令行使用说明.md`文档，提供详细的使用指南。

### Changed
- 将`jxl`库替换为`Apache POI`，提升对`.xlsx`文件的支持。
- 更新`pom.xml`文件，移除`jxl`依赖并添加`Apache POI`依赖。
- 修改配置文件默认路径为`~/.xlsql/xlsql_config.properties`，并更新相关变量名。
- 修改日志文件默认路径为`~/.xlsql/xlsql.log`。

### Fixed
- 修复了`xlWorkbook.java`和`xlSheet.java`中的兼容性问题。