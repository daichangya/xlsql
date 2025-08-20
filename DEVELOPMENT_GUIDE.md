# ExcelJDBC 开发指南

## 1. 开发环境配置
### 基础要求
- JDK 8+
- Maven 3.6+
- Git

### 初始化步骤
```bash
git clone <项目仓库地址>
cd exceljdbc
mvn clean install
```

## 2. 项目结构说明
```
├── src/
│   ├── main/java/            # 核心代码
│   │   ├── com/jsdiff/excel/
│   │   │   ├── database/     # 数据库连接模块
│   │   │   ├── jdbc/         # JDBC驱动实现
│   │   │   ├── ui/           # 命令行界面
│   │   │   └── util/         # 工具类
│   ├── resources/            # 配置文件
├── test/                     # 单元测试
├── pom.xml                   # 项目依赖
```

## 3. 核心模块开发规范
### 数据库连接模块 (`xlInstance`)
- **职责**：管理数据库配置和连接
- **规范**：
  - 配置文件路径：`resources/xlsql_config.xml`
  - 必须实现 `ConfigurationListener` 接口

### JDBC驱动模块 (`xlDriver`)
- **职责**：实现 `java.sql.Driver` 接口
- **规范**：
  - 驱动类名：`com.jsdiff.excel.jdbc.xlDriver`
  - URL格式：`jdbc:xl:file:/path/to/excel.xls`

## 4. 测试指南
### 单元测试
- 使用 JUnit 5
- 测试目录：`src/test/java`

### 集成测试
```bash
mvn verify  # 运行所有测试
```

## 5. 贡献流程
1. Fork 项目仓库
2. 创建特性分支 (`git checkout -b feature/xxx`)
3. 提交代码 (`git commit -am 'Add some feature'`)
4. 推送分支 (`git push origin feature/xxx`)
5. 创建 Pull Request

## 6. 代码风格
- 遵循 Google Java Style Guide
- 使用 Checkstyle 校验（配置见 `checkstyle.xml`）

## 7. 问题排查
- 查看日志文件：`logs/xlsql.log`
- 启用调试模式：在配置文件中设置 `debug=true`
