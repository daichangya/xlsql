# XLSQL Maven Central 发布指南

本文档详细说明如何将 XLSQL 发布到 Maven Central（中央仓库）。

## 目录

- [前置条件](#前置条件)
- [准备工作](#准备工作)
- [发布步骤](#发布步骤)
- [验证发布](#验证发布)
- [常见问题](#常见问题)

## 前置条件

### 1. OSSRH 账号

1. 访问 https://issues.sonatype.org/
2. 注册账号（如果还没有）
3. 创建新 Issue，申请发布权限
   - **Project**: Community Support - Open Source Project Repository Hosting (OSSRH)
   - **Issue Type**: New Project
   - **Group Id**: `io.github.daichangya`
   - **Project Name**: XLSQL
   - **Project URL**: https://github.com/daichangya/xlsql
   - **SCM URL**: https://github.com/daichangya/xlsql.git

4. 等待审核通过（通常需要 1-2 个工作日）

### 2. GPG 密钥

#### 生成 GPG 密钥

```bash
# 安装 GPG（如果还没有）
# macOS: brew install gnupg
# Linux: sudo apt-get install gnupg
# Windows: 下载 Gpg4win

# 生成密钥
gpg --gen-key

# 选择选项：
# - RSA and RSA (default)
# - 4096 bits
# - 永不过期（或设置合适的过期时间）
# - 输入姓名和邮箱
# - 设置密码
```

#### 查看密钥 ID

```bash
gpg --list-keys
# 找到你的密钥，复制密钥 ID（例如：ABC123DEF456）
```

#### 发布公钥到密钥服务器

```bash
# 发布到 keyserver.ubuntu.com
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# 或者发布到 keys.openpgp.org
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

### 3. Maven settings.xml 配置

编辑 `~/.m2/settings.xml`（如果不存在则创建）：

```xml
<settings>
    <servers>
        <server>
            <id>central</id>
            <username>YOUR_OSSRH_USERNAME</username>
            <password>YOUR_OSSRH_PASSWORD</password>
        </server>
    </servers>
    
    <profiles>
        <profile>
            <id>gpg</id>
            <properties>
                <gpg.executable>gpg</gpg.executable>
                <gpg.passphrase>YOUR_GPG_PASSPHRASE</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
</settings>
```

**安全提示**：不要在 `settings.xml` 中硬编码密码，可以使用 Maven 的密码加密功能。

## 准备工作

### 1. 更新版本号

确保 `pom.xml` 中的版本号已更新为正式版本（不是 SNAPSHOT）：

```xml
<version>5.1.0</version>
```

### 2. 验证项目配置

检查 `pom.xml` 中的必要配置：

- ✅ `groupId`: `io.github.daichangya`
- ✅ `artifactId`: `xlsql`
- ✅ `version`: `5.1.0`（非 SNAPSHOT）
- ✅ `name`: `XLSQL`
- ✅ `description`: 项目描述
- ✅ `url`: 项目 URL
- ✅ `licenses`: GPL 许可证
- ✅ `developers`: 开发者信息
- ✅ `scm`: SCM 信息

### 3. 运行测试

确保所有测试通过：

```bash
mvn clean test
```

### 4. 生成源码和 Javadoc

```bash
mvn clean package source:jar javadoc:jar
```

验证生成的文件：
- `target/xlsql-5.1.0.jar`
- `target/xlsql-5.1.0-sources.jar`
- `target/xlsql-5.1.0-javadoc.jar`

## 发布步骤

### 方法 1: 使用 central-publishing-maven-plugin（推荐）

XLSQL 已配置 `central-publishing-maven-plugin`，这是发布到 Maven Central 的推荐方式。

#### 步骤 1: 构建并发布

```bash
# 清理并构建
mvn clean package

# 发布到 OSSRH（staging 仓库）
mvn -P release deploy
```

这个命令会：
1. 编译代码
2. 运行测试
3. 生成源码和 Javadoc
4. GPG 签名所有文件
5. 上传到 OSSRH staging 仓库

#### 步骤 2: 在 OSSRH 中关闭和发布

1. 访问 https://oss.sonatype.org/
2. 使用 OSSRH 账号登录
3. 进入 "Staging Repositories"
4. 找到你的仓库（`comjsdiff-xxxx`）
5. 点击 "Close" 按钮
   - 系统会验证所有文件
   - 等待验证完成（可能需要几分钟）
6. 验证通过后，点击 "Release" 按钮
7. 确认发布

#### 步骤 3: 等待同步

发布后，需要等待同步到 Maven Central：
- 通常需要 **1-2 小时** 同步到中央仓库
- 可以在 https://repo1.maven.org/maven2/com/jsdiff/xlsql/ 查看

### 方法 2: 使用 maven-release-plugin

如果需要自动化版本管理：

```bash
# 准备发布（会更新版本号、创建 tag）
mvn release:prepare

# 执行发布
mvn release:perform
```

### 方法 3: 手动发布

如果上述方法有问题，可以手动发布：

```bash
# 1. 构建项目
mvn clean package source:jar javadoc:jar

# 2. GPG 签名
mvn gpg:sign

# 3. 部署到 OSSRH
mvn deploy:deploy-file \
  -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ \
  -DrepositoryId=central \
  -Dfile=target/xlsql-5.1.0.jar \
  -DpomFile=pom.xml \
  -Dfiles=target/xlsql-5.1.0.jar,target/xlsql-5.1.0-sources.jar,target/xlsql-5.1.0-javadoc.jar \
  -Dclassifiers=sources,javadoc \
  -Dtypes=jar,jar,jar
```

## 验证发布

### 1. 检查 OSSRH Staging 仓库

在 OSSRH 中检查 staging 仓库：
- 所有文件都已上传
- GPG 签名正确
- 源码和 Javadoc 已包含

### 2. 检查 Maven Central

等待同步后，检查：
- https://repo1.maven.org/maven2/com/jsdiff/xlsql/5.1.0/
- 应该包含：
  - `xlsql-5.1.0.pom`
  - `xlsql-5.1.0.jar`
  - `xlsql-5.1.0.jar.asc` (GPG 签名)
  - `xlsql-5.1.0-sources.jar`
  - `xlsql-5.1.0-sources.jar.asc`
  - `xlsql-5.1.0-javadoc.jar`
  - `xlsql-5.1.0-javadoc.jar.asc`

### 3. 测试依赖

创建一个测试项目，验证依赖可以正常下载：

```xml
<dependency>
    <groupId>io.github.daichangya</groupId>
    <artifactId>xlsql</artifactId>
    <version>5.1.0</version>
</dependency>
```

```bash
mvn dependency:resolve
```

## 常见问题

### 1. GPG 签名失败

**问题**：`gpg: signing failed: No secret key`

**解决**：
- 检查 GPG 密钥是否正确配置
- 确认 `gpg.passphrase` 在 `settings.xml` 中正确设置
- 尝试手动签名：`gpg -ab target/xlsql-5.1.0.jar`

### 2. 401 Unauthorized

**问题**：上传时提示 401 错误

**解决**：
- 检查 `settings.xml` 中的用户名和密码
- 确认 OSSRH 账号已激活
- 确认项目已获得发布权限

### 3. 验证失败

**问题**：在 OSSRH 中关闭 staging 仓库时验证失败

**解决**：
- 检查所有必需文件是否已上传
- 检查 GPG 签名是否正确
- 检查 POM 文件中的元数据是否完整
- 查看错误日志了解具体问题

### 4. 同步延迟

**问题**：发布后 Maven Central 中找不到

**解决**：
- 等待 1-2 小时（同步需要时间）
- 检查 OSSRH 中是否已成功发布
- 使用搜索功能：https://search.maven.org/

### 5. 版本号冲突

**问题**：版本号已存在

**解决**：
- Maven Central 不允许覆盖已发布的版本
- 必须使用新的版本号
- 更新 `pom.xml` 中的版本号

## 发布检查清单

发布前确认：

- [ ] 版本号已更新（非 SNAPSHOT）
- [ ] 所有测试通过
- [ ] 源码和 Javadoc 已生成
- [ ] GPG 密钥已配置
- [ ] OSSRH 账号已激活
- [ ] `settings.xml` 已配置
- [ ] POM 文件元数据完整
- [ ] README 和文档已更新
- [ ] CHANGELOG 已更新

发布后确认：

- [ ] OSSRH staging 仓库已关闭和发布
- [ ] Maven Central 中可以看到新版本
- [ ] 依赖可以正常下载
- [ ] GPG 签名正确
- [ ] 源码和 Javadoc 可用

## 参考资源

- **OSSRH 指南**: https://central.sonatype.org/publish/publish-guide/
- **Maven Central 要求**: https://central.sonatype.org/publish/requirements/
- **GPG 指南**: https://central.sonatype.org/publish/requirements/gpg/
- **常见问题**: https://central.sonatype.org/publish/publish-faq/

## 支持

如果遇到问题：

1. 查看 OSSRH 文档
2. 检查项目 Issue：https://github.com/daichangya/xlsql/issues
3. 联系 OSSRH 支持：https://issues.sonatype.org/

