# xlSQL 变更日志

## [4.0] - 2025-11-23

### Added
- 新增完整的单元测试套件，覆盖核心功能
  - 为 `xlDriver`、`xlInstance` 等核心类添加单元测试
  - 将原有的 `main` 方法测试转换为 JUnit 5 单元测试
  - 新增 `TestXlsql`、`TestExcelJDBC`、`ComprehensiveExcelTest` 等测试类
- 新增环境变量配置支持
  - 支持通过 `XLSQL_CONFIG_PATH` 环境变量指定配置文件路径
  - 支持通过 `XLSQL_LOG_PATH` 环境变量指定日志文件路径
  - 支持通过系统属性 `xlsql.config.path` 和 `xlsql.log.path` 配置路径
- 新增密码存储安全警告
  - 在 `setPassword()` 方法中添加安全警告，提醒用户不要明文存储密码
- 新增 JavaDoc 文档
  - 为所有公共类和方法补充完整的 JavaDoc 文档
  - 移除所有 `TODO: javadoc` 标记

### Changed
- 统一依赖版本管理
  - 将 HSQLDB 从 2.7.2 降级到 2.5.2，确保与 Java 8 完全兼容
  - 统一 README 和 pom.xml 中的版本信息
- 改进代码规范
  - 提取魔法数字和字符串为常量
  - 统一异常处理模式
  - 改进命名规范和代码风格
- 改进配置管理
  - 支持多级配置路径查找（系统属性 > 环境变量 > 默认路径）
  - 改进配置文件的加载和验证逻辑
- 优化资源管理
  - 修复所有资源泄漏问题（BufferedReader、Workbook、连接等）
  - 使用 try-with-resources 确保资源正确关闭
  - 改进 Workbook 生命周期管理

### Fixed
- 修复 HSQLDB 版本兼容性问题
  - 解决 Java 8 环境下 `UnsupportedClassVersionError` 问题
- 修复 URL 构建错误
  - 修复 `TestXlsql` 中 URL 格式错误（多余的冒号）
- 修复资源管理问题
  - 修复 `xlSheet` 中 Workbook 资源管理问题
  - 修复所有文件流、连接等资源未关闭问题
- 修复过时 API 使用
  - 替换所有 `newInstance()` 为 `getDeclaredConstructor().newInstance()`
- 修复异常处理
  - 改进异常处理逻辑和错误信息
  - 统一异常处理模式
- 修复代码质量问题
  - 移除重复的类定义
  - 清理未使用的代码和注释

### Security
- 添加密码存储安全警告
  - 在配置文件中存储密码时显示警告信息
  - 建议使用环境变量或凭证管理系统

### Testing
- 完善单元测试
  - 所有测试类使用 JUnit 5
  - 测试覆盖率达到核心功能
  - 所有测试通过验证

### Documentation
- 更新版本信息
  - README.md 和 README-zh.md 更新为 4.0 版本
  - pom.xml 版本更新为 4.0
- 补充 JavaDoc
  - 为所有公共 API 添加完整的 JavaDoc
  - 改进代码可读性和可维护性

