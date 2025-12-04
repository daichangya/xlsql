# xlSQL
## 微信公众号

扫码关注微信公众号，Java码界探秘。
![Java码界探秘](http://images.jsdiff.com/qrcode_for_gh_1e2587cc42b1_258_1587996055777.jpg)

[https://blog.jsdiff.com/](https://blog.jsdiff.com/)

## 项目概述
xlSQL 是一个基于 Java 的 JDBC 驱动，允许用户通过 SQL 查询和操作 Excel 文件。它将 Excel 文件视为数据库表，支持标准的 JDBC 接口，方便开发者集成到现有项目中。

## 功能特性
- **支持 SQL 查询**：通过 JDBC 接口执行 SQL 查询，操作 Excel 数据。
- **多引擎支持**：支持 HSQLDB、H2 和 Native 作为底层数据库引擎。
- **Excel 文件读写**：支持 `.xls` `.xlsx`  格式的 Excel 文件读写操作。
- **元数据查询**：提供数据库和表的元数据信息（如表结构、列信息等）。

## 系统要求

- **Java**: JDK 8 或更高版本
- **Maven**: 3.6.0 或更高版本
- **操作系统**: Windows、Linux、macOS

## 快速开始

### 1. 依赖配置

在 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>com.jsdiff</groupId>
    <artifactId>xlsql</artifactId>
    <version>5.0-SNAPSHOT</version>
</dependency>
```

### 2. 从源码构建

#### 前置条件

确保已安装 Java 8+ 和 Maven：

```bash
java -version  # 应显示 Java 8 或更高版本
mvn -version   # 应显示 Maven 3.6.0 或更高版本
```

#### 构建步骤

**1. 克隆仓库**
```bash
git clone https://github.com/daichangya/xlsql.git
cd xlsql
```

**2. 清理项目**
```bash
mvn clean
```

**3. 编译项目**
```bash
mvn compile
```

**4. 运行测试**
```bash
mvn test
```

**5. 打包项目**
```bash
mvn package
```

打包后会生成：
- `target/xlsql-5.0-SNAPSHOT.jar` - 标准 JAR 文件
- `target/xlsql-5.0-SNAPSHOT-shaded.jar` - 包含所有依赖的 Fat JAR

**6. 安装到本地 Maven 仓库**
```bash
mvn install
```

这将把项目安装到本地 Maven 仓库（`~/.m2/repository/com/jsdiff/xlsql/5.0-SNAPSHOT/`），供其他项目使用。

**7. 跳过测试进行构建**
```bash
mvn package -DskipTests
```

**8. 生成源码 JAR**
```bash
mvn source:jar
```

**9. 生成 Javadoc JAR**
```bash
mvn javadoc:jar
```

### 3. 使用构建的 JAR

#### 方式一：使用标准 JAR（需要单独提供依赖）

在 `pom.xml` 中添加依赖（如上所示）

#### 方式二：直接使用 Shaded JAR（Fat JAR）

1. 将 `xlsql-5.0-SNAPSHOT-shaded.jar` 复制到项目
2. 手动添加到 classpath
3. 或安装到本地仓库：
```bash
mvn install:install-file \
  -Dfile=target/xlsql-5.0-SNAPSHOT-shaded.jar \
  -DgroupId=com.jsdiff \
  -DartifactId=xlsql \
  -Dversion=5.0-SNAPSHOT \
  -Dpackaging=jar
```

### 4. 连接数据库

```java
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) throws Exception {
        // 注册驱动（通常不需要显式注册）
        Class.forName("com.jsdiff.xlsql.jdbc.xlDriver");

        // 创建连接
        String url = "jdbc:xlsql:excel:/path/to/excel/files";
        Connection conn = DriverManager.getConnection(url);
        System.out.println("连接成功！");
    }
}
```

### 5. 执行查询

```java
import java.sql.ResultSet;
import java.sql.Statement;

// 创建 Statement
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM test2.Sheet1 LIMIT 10");

// 遍历结果集
while (rs.next()) {
    System.out.println(rs.getString(1));
}

// 关闭资源
rs.close();
stmt.close();
conn.close();
```

## 开发指南

### 项目结构

```
xlsql/
├── src/
│   ├── main/java/          # 主要源代码
│   └── test/java/           # 测试代码
├── doc/                     # 文档
├── pom.xml                  # Maven 配置
└── README.md                # 说明文档
```

### 运行测试

运行所有测试：
```bash
mvn test
```

运行特定测试类：
```bash
mvn test -Dtest=TestXlsql
```

### IDE 设置

#### IntelliJ IDEA
1. File → Open → 选择 `pom.xml`
2. Maven 会自动导入依赖
3. 配置 JDK: File → Project Structure → Project SDK (Java 8+)

#### Eclipse
1. File → Import → Maven → Existing Maven Projects
2. 选择项目目录
3. 配置 JDK: Project → Properties → Java Build Path

#### VS Code
1. 安装 Java Extension Pack
2. 打开项目文件夹
3. VS Code 会自动检测 Maven 项目

## 配置

### 环境变量

- `XLSQL_CONFIG_PATH`: 配置文件路径
- `XLSQL_LOG_PATH`: 日志文件路径

### 系统属性

- `xlsql.config.path`: 配置文件路径
- `xlsql.log.path`: 日志文件路径

### 配置文件

默认位置：`~/.xlsql/xlsql_config.properties`

示例配置：
```properties
# 数据库引擎 (hsqldb, h2 或 native)
engine=hsqldb

# HSQLDB 配置
hsqldb.url=jdbc:hsqldb:mem:xlsql
hsqldb.user=sa
hsqldb.password=

# H2 配置（如果使用 H2 引擎）
h2.url=jdbc:h2:mem:xlsql
h2.user=sa
h2.password=
```

## 常见问题

### 1. ClassNotFoundException
**问题**: 找不到 xlDriver 类
**解决**: 确保 JAR 文件在 classpath 中

### 2. UnsupportedClassVersionError
**问题**: HSQLDB 需要 Java 11+
**解决**: 使用项目自带的 HSQLDB 2.5.2（支持 Java 8）

### 3. 连接失败
**问题**: 无法连接到 Excel 目录
**解决**: 
- 检查目录路径是否正确
- 确保目录中包含 Excel 文件（.xls 或 .xlsx）
- 检查文件权限

### 4. 表不存在
**问题**: SQL 查询失败，提示"表不存在"
**解决**:
- 使用正确的表名格式：`"文件名.工作表名"`
- 确保 Excel 文件存在于目录中
- 检查工作表名称拼写（区分大小写）

## 依赖项

- **Apache POI**: 用于读写 Excel 文件（.xls 和 .xlsx 格式）
- **HSQLDB**: 默认数据库引擎（版本 2.5.2，兼容 Java 8）
- **H2 Database**: 可选数据库引擎支持（版本 2.2.224）
- **JUnit 5**: 单元测试框架

## 限制

- 支持 `.xls` 和 `.xlsx` 格式的 Excel 文件
- 部分 JDBC 高级功能（如事务）可能受限
- 表名格式：`"文件名.工作表名"`（注意引号和大小写）

## 扩展性

- 支持自定义数据库引擎（通过 `xlDatabaseFactory` 扩展）
- 支持自定义 SQL 解析器（通过 `ASqlParser` 扩展）

## 贡献

欢迎贡献代码！请遵循以下步骤：

1. Fork 仓库
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 进行修改
4. 为新功能添加测试
5. 确保所有测试通过 (`mvn test`)
6. 提交更改 (`git commit -m '添加新功能'`)
7. 推送到分支 (`git push origin feature/amazing-feature`)
8. 创建 Pull Request

### 代码规范

- 遵循 Java 命名规范
- 使用 4 个空格缩进
- 为公共类和方法添加 JavaDoc
- 为新功能编写单元测试

## 许可证

本项目基于 GNU General Public License (GPL) 发布。

详见 [LICENSE](LICENSE) 文件。

## 文档

- [安装和打包指南](doc/INSTALLATION_GUIDE.md) - 详细的安装和构建说明
- [开发指南](doc/DEVELOPMENT_GUIDE.md) - 开发环境设置和指南
- [命令行使用说明](doc/命令行使用说明.md) - 命令行工具文档
- [DBeaver 集成指南](doc/Dbeaver结合exceljdbc使用文档.md) - DBeaver 集成说明
- [架构设计文档](doc/xlSQL%20架构设计文档V2.md) - 系统架构说明
- [更新日志](doc/CHANGELOG_4.0.md) - 版本历史

## 相关链接

- **GitHub**: https://github.com/daichangya/xlsql
- **博客**: https://blog.jsdiff.com/
- **问题反馈**: https://github.com/daichangya/xlsql/issues