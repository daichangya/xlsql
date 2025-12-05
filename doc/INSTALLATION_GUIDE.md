# xlSQL 安装和打包指南

本文档详细说明如何构建、打包和安装 xlSQL 项目。

## 目录

- [系统要求](#系统要求)
- [快速开始](#快速开始)
- [详细构建步骤](#详细构建步骤)
- [打包选项](#打包选项)
- [安装到本地仓库](#安装到本地仓库)
- [发布到 Maven 仓库](#发布到-maven-仓库)
- [使用构建的 JAR](#使用构建的-jar)
- [常见问题](#常见问题)

## 系统要求

### 必需软件

- **Java Development Kit (JDK)**: 版本 8 或更高
  - 下载地址: https://adoptium.net/
  - 验证安装: `java -version`

- **Apache Maven**: 版本 3.6.0 或更高
  - 下载地址: https://maven.apache.org/download.cgi
  - 验证安装: `mvn -version`

### 可选工具

- **Git**: 用于克隆仓库
- **IDE**: IntelliJ IDEA、Eclipse 或 VS Code（推荐）

## 快速开始

### 1. 获取源代码

```bash
# 克隆仓库
git clone https://github.com/daichangya/xlsql.git
cd xlsql
```

### 2. 构建项目

```bash
# 清理并打包
mvn clean package
```

### 3. 安装到本地仓库

```bash
# 安装到本地 Maven 仓库
mvn install
```

### 4. 在项目中使用

在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.jsdiff</groupId>
    <artifactId>xlsql</artifactId>
    <version>5.1-SNAPSHOT</version>
</dependency>
```

## 详细构建步骤

### 步骤 1: 清理项目

删除之前的构建产物：

```bash
mvn clean
```

这会删除 `target/` 目录。

### 步骤 2: 编译源代码

编译主代码和测试代码：

```bash
mvn compile
```

只编译主代码（跳过测试）：

```bash
mvn compile -DskipTests
```

### 步骤 3: 运行测试

运行所有单元测试：

```bash
mvn test
```

运行特定测试类：

```bash
mvn test -Dtest=TestXlsql
```

运行测试并生成报告：

```bash
mvn test surefire-report:report
```

查看报告：`target/site/surefire-report.html`

### 步骤 4: 打包项目

#### 标准打包

```bash
mvn package
```

生成的文件：
- `target/xlsql-5.1-SNAPSHOT.jar` - 标准 JAR（不包含依赖）
- `target/xlsql-5.1-SNAPSHOT-shaded.jar` - Fat JAR（包含所有依赖）

#### 跳过测试打包

```bash
mvn package -DskipTests
```

#### 生成源码 JAR

```bash
mvn source:jar
```

生成文件：`target/xlsql-5.1-SNAPSHOT-sources.jar`

#### 生成 Javadoc JAR

```bash
mvn javadoc:jar
```

生成文件：`target/xlsql-5.1-SNAPSHOT-javadoc.jar`

#### 生成所有 JAR（源码、文档、主 JAR）

```bash
mvn package source:jar javadoc:jar
```

## 打包选项

### 1. 标准 JAR

**文件**: `target/xlsql-5.1-SNAPSHOT.jar`

**特点**:
- 只包含 xlSQL 的类文件
- 不包含依赖库
- 需要在使用时提供依赖

**使用场景**:
- 通过 Maven 依赖管理使用
- 依赖已由其他方式提供

### 2. Shaded JAR (Fat JAR)

**文件**: `target/xlsql-5.1-SNAPSHOT-shaded.jar`

**特点**:
- 包含所有依赖库
- 可以独立运行
- 文件较大（约 10-15 MB）

**使用场景**:
- 独立部署
- 不通过 Maven 管理依赖
- 命令行工具使用

**配置说明**:
Shaded JAR 由 `maven-shade-plugin` 生成，配置在 `pom.xml` 中：
- 合并所有依赖到单个 JAR
- 排除签名文件（.SF, .DSA, .RSA）
- 设置主类为 `com.jsdiff.xlsql.ui.XlUi`

### 3. 源码 JAR

**文件**: `target/xlsql-5.1-SNAPSHOT-sources.jar`

**用途**:
- IDE 中查看源代码
- 调试时使用

### 4. Javadoc JAR

**文件**: `target/xlsql-5.1-SNAPSHOT-javadoc.jar`

**用途**:
- IDE 中显示 API 文档
- 离线查看文档

## 安装到本地仓库

### 标准安装

```bash
mvn install
```

这会：
1. 编译项目
2. 运行测试
3. 打包 JAR
4. 安装到本地 Maven 仓库：`~/.m2/repository/com/jsdiff/xlsql/5.1-SNAPSHOT/`

### 跳过测试安装

```bash
mvn install -DskipTests
```

### 安装 Shaded JAR

如果需要将 Shaded JAR 安装为独立版本：

```bash
mvn install:install-file \
  -Dfile=target/xlsql-5.1-SNAPSHOT-shaded.jar \
  -DgroupId=com.jsdiff \
  -DartifactId=xlsql \
  -Dversion=5.1-SNAPSHOT \
  -Dpackaging=jar \
  -Dclassifier=shaded
```

然后在使用时指定 classifier：

```xml
<dependency>
    <groupId>com.jsdiff</groupId>
    <artifactId>xlsql</artifactId>
    <version>5.1-SNAPSHOT</version>
    <classifier>shaded</classifier>
</dependency>
```

## 发布到 Maven 仓库

### 发布到本地仓库（测试）

```bash
mvn deploy -DaltDeploymentRepository=local::default::file://${HOME}/.m2/repository
```

### 发布到 Maven Central（需要配置）

1. 配置 `settings.xml` 中的服务器凭据
2. 执行发布：

```bash
mvn clean deploy -P release
```

### 发布到私有仓库

```bash
mvn deploy -DaltDeploymentRepository=myrepo::default::http://your-repo-url/repository/maven-releases/
```

## 使用构建的 JAR

### 方式一：Maven 依赖（推荐）

在 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.jsdiff</groupId>
    <artifactId>xlsql</artifactId>
    <version>5.1-SNAPSHOT</version>
</dependency>
```

### 方式二：直接使用 JAR 文件

#### 使用标准 JAR

1. 复制 JAR 到项目：
```bash
cp target/xlsql-5.1-SNAPSHOT.jar /path/to/your/project/lib/
```

2. 添加依赖 JAR（Apache POI、HSQLDB 等）

3. 编译时包含在 classpath：
```bash
javac -cp "lib/xlsql-5.1-SNAPSHOT.jar:lib/poi-5.2.3.jar:..." YourClass.java
```

#### 使用 Shaded JAR（推荐用于独立应用）

1. 复制 Shaded JAR：
```bash
cp target/xlsql-5.1-SNAPSHOT-shaded.jar /path/to/your/project/lib/
```

2. 编译和运行：
```bash
# 编译
javac -cp "lib/xlsql-5.1-SNAPSHOT-shaded.jar" YourClass.java

# 运行
java -cp "lib/xlsql-5.1-SNAPSHOT-shaded.jar:." YourClass
```

### 方式三：作为命令行工具

Shaded JAR 可以作为命令行工具运行：

```bash
java -jar xlsql-5.1-SNAPSHOT-shaded.jar [options]
```

## 验证安装

### 验证 Maven 安装

```bash
# 检查本地仓库
ls ~/.m2/repository/com/jsdiff/xlsql/5.1-SNAPSHOT/

# 应该看到：
# - xlsql-5.1-SNAPSHOT.jar
# - xlsql-5.1-SNAPSHOT.pom
# - xlsql-5.1-SNAPSHOT-sources.jar (如果生成了)
# - xlsql-5.1-SNAPSHOT-javadoc.jar (如果生成了)
```

### 验证 JAR 内容

```bash
# 查看 JAR 内容
jar -tf target/xlsql-5.1-SNAPSHOT.jar | head -20

# 查看 Shaded JAR 大小
ls -lh target/xlsql-5.1-SNAPSHOT-shaded.jar
```

### 测试连接

创建测试类：

```java
import java.sql.*;

public class TestConnection {
    public static void main(String[] args) throws Exception {
        Class.forName("com.jsdiff.xlsql.jdbc.xlDriver");
        String url = "jdbc:xlsql:excel:/path/to/excel/files";
        Connection conn = DriverManager.getConnection(url);
        System.out.println("连接成功！");
        conn.close();
    }
}
```

编译和运行：

```bash
javac -cp "target/xlsql-5.1-SNAPSHOT-shaded.jar" TestConnection.java
java -cp "target/xlsql-5.1-SNAPSHOT-shaded.jar:." TestConnection
```

## 常见问题

### Q1: 构建失败，提示找不到依赖

**原因**: Maven 无法下载依赖

**解决**:
1. 检查网络连接
2. 检查 Maven 配置（`~/.m2/settings.xml`）
3. 尝试强制更新：`mvn clean install -U`

### Q2: 测试失败

**原因**: 测试环境问题或测试数据缺失

**解决**:
1. 检查测试输出日志
2. 确保测试所需的 Excel 文件存在
3. 跳过测试：`mvn package -DskipTests`

### Q3: Shaded JAR 太大

**原因**: 包含了所有依赖

**解决**:
- 这是正常的，Shaded JAR 设计为包含所有依赖
- 如果只需要特定功能，可以使用标准 JAR + 选择性依赖

### Q4: 运行时找不到类

**原因**: classpath 配置不正确

**解决**:
1. 确保 JAR 在 classpath 中
2. 使用 Shaded JAR 可以避免依赖问题
3. 检查 Maven 依赖是否正确下载

### Q5: 版本冲突

**原因**: 项目中已有相同依赖的不同版本

**解决**:
1. 检查依赖树：`mvn dependency:tree`
2. 使用 `<exclusions>` 排除冲突依赖
3. 统一依赖版本

## 构建优化

### 并行构建

```bash
mvn -T 4 clean package  # 使用 4 个线程
```

### 离线构建

```bash
mvn -o clean package  # 使用本地缓存
```

### 只编译变更的文件

```bash
mvn compile  # Maven 会自动检测变更
```

## 持续集成

### GitHub Actions 示例

```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 8
        uses: actions/setup-java@v2
        with:
          java-version: '8'
      - name: Build with Maven
        run: mvn clean package
      - name: Run tests
        run: mvn test
```

### Jenkins Pipeline 示例

```groovy
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Archive') {
            steps {
                archiveArtifacts 'target/*.jar'
            }
        }
    }
}
```

## 相关文档

- [README.md](../README.md) - 项目概述
- [README-zh.md](../README-zh.md) - 中文说明
- [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) - 开发指南
- [命令行使用说明.md](命令行使用说明.md) - 命令行工具使用

## 支持

如有问题，请：
1. 查看本文档的常见问题部分
2. 查看 GitHub Issues: https://github.com/daichangya/xlsql/issues
3. 提交新的 Issue 描述问题

