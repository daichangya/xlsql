# XLSQL 日志系统使用指南

## 日志输出位置

### 1. 控制台输出

XLSQL 的日志会同时输出到**控制台（标准错误流）**和**日志文件**：

- **命令行运行**：直接在终端/控制台显示
- **IDE 运行**：在 IDE 的运行窗口或控制台标签页中显示
- **第三方应用**：输出到宿主应用的日志系统（如 Tomcat、Spring Boot 等）

### 2. 日志文件输出（默认启用）

**默认情况下，日志会自动输出到用户目录的日志文件：**

- **默认路径**：`~/.xlsql/xlsql.log`（用户主目录下的 `.xlsql` 文件夹）
- **Windows**：`C:\Users\用户名\.xlsql\xlsql.log`
- **Linux/Mac**：`/home/用户名/.xlsql/xlsql.log` 或 `/Users/用户名/.xlsql/xlsql.log`

日志文件会自动创建，如果目录不存在会自动创建。日志会追加到文件末尾，不会覆盖已有日志。

## 日志级别配置

### 默认日志级别

- **默认级别**：`INFO`（记录连接、SQL执行等重要信息）
- 适合开发和调试，可以看到 SQL 解析和执行情况
- 如果需要减少日志，可以设置为 `WARNING` 级别

### 配置日志级别

#### 方式1：系统属性（推荐）

```bash
# 设置为 INFO 级别（记录连接、SQL 执行等信息）
-Dxlsql.log.level=INFO

# 设置为 FINE 级别（详细调试信息）
-Dxlsql.log.level=FINE

# 设置为 WARNING 级别（只记录警告和错误，适合生产环境）
-Dxlsql.log.level=WARNING

# 完全关闭日志
-Dxlsql.log.level=OFF
```

**示例：**
```bash
java -Dxlsql.log.level=INFO -cp xlsql.jar YourApplication
```

#### 方式2：环境变量

```bash
# Linux/Mac
export XLSQL_LOG_LEVEL=INFO

# Windows
set XLSQL_LOG_LEVEL=INFO
```

#### 方式3：在代码中配置

```java
// 在应用启动时设置
System.setProperty("xlsql.log.level", "INFO");
```

## 支持的日志级别

| 级别 | 说明 | 使用场景 |
|------|------|----------|
| `OFF` | 关闭所有日志 | 生产环境，完全静默 |
| `SEVERE` | 严重错误 | 只记录致命错误 |
| `WARNING` | 警告和错误 | 生产环境推荐 |
| `INFO` | 一般信息（默认） | 开发/测试环境 |
| `INFO` | 一般信息 | 开发/测试环境 |
| `FINE` | 详细调试信息 | 调试问题 |
| `ALL` | 所有日志 | 完整调试 |

## 日志分类

XLSQL 的日志使用结构化格式，包含以下分类：

- `[CONNECTION]` - 连接相关日志（建立、关闭、错误）
- `[SQL]` - SQL 执行日志（SQL 语句、执行时间、结果行数）
- `[ERROR]` - 错误日志（异常堆栈）
- `[PERFORMANCE]` - 性能日志（慢查询警告）
- `[WARNING]` - 警告日志
- `[INFO]` - 一般信息日志
- `[DEBUG]` - 调试日志

## 日志格式示例

```
[XLSQL] [CONNECTION] Connection established to Native SQL Engine
[XLSQL] [SQL] SQL: SELECT * FROM table1 | Time: 50ms | Rows: 100
[XLSQL] [ERROR] SQL parsing failed: Invalid SQL syntax
[XLSQL] [PERFORMANCE] Slow query detected | Time: 5000ms
```

## 在第三方应用中使用

### Spring Boot 应用

在 `application.properties` 或 `application.yml` 中配置：

```properties
# application.properties
# 通过 JVM 参数传递
# 或在启动脚本中设置
```

启动脚本：
```bash
java -Dxlsql.log.level=INFO -jar your-application.jar
```

### Tomcat 应用

在 `setenv.sh` 或 `setenv.bat` 中配置：

```bash
# setenv.sh (Linux/Mac)
export JAVA_OPTS="$JAVA_OPTS -Dxlsql.log.level=INFO"

# setenv.bat (Windows)
set JAVA_OPTS=%JAVA_OPTS% -Dxlsql.log.level=INFO
```

### DBeaver / 其他 JDBC 工具

**重要：在 DBeaver 中使用时，日志主要输出到日志文件，而不是 DBeaver 的控制台。**

#### 查看日志文件

日志文件默认位置：
- **Windows**: `C:\Users\用户名\.xlsql\xlsql.log`
- **Linux/Mac**: `~/.xlsql/xlsql.log` 或 `/Users/用户名/.xlsql/xlsql.log`

#### 在 DBeaver 中配置日志级别

1. **通过 DBeaver 的驱动配置**：
   - 打开 DBeaver → 数据库 → 驱动管理器
   - 找到 XLSQL 驱动，点击"编辑"
   - 在"驱动属性"中添加：
     - 属性名：`xlsql.log.level`
     - 属性值：`INFO`（或其他级别）

2. **通过系统属性**（如果 DBeaver 支持）：
   - 在 DBeaver 的启动配置中添加 JVM 参数：
     ```
     -Dxlsql.log.level=INFO
     ```

3. **查看日志文件**：
   - 打开日志文件：`~/.xlsql/xlsql.log`
   - 使用文本编辑器或命令行工具查看：
     ```bash
     # Linux/Mac
     tail -f ~/.xlsql/xlsql.log
     
     # Windows
     type %USERPROFILE%\.xlsql\xlsql.log
     ```

#### 为什么在 DBeaver 中看不到控制台日志？

DBeaver 使用自己的日志系统，可能会拦截或重定向标准输出。因此：
- **控制台日志**：可能不会显示在 DBeaver 的日志窗口中
- **文件日志**：始终会写入 `~/.xlsql/xlsql.log`，这是最可靠的查看方式

## 配置日志文件路径

### 默认行为

默认情况下，日志会自动输出到 `~/.xlsql/xlsql.log` 文件。

### 自定义日志文件路径

#### 方式1：系统属性（推荐）

```bash
# 指定自定义日志文件路径
-Dxlsql.log.file=/path/to/your/xlsql.log

# 示例
java -Dxlsql.log.file=/var/log/xlsql.log -cp xlsql.jar YourApp
```

#### 方式2：环境变量

```bash
# Linux/Mac
export XLSQL_LOG_FILE=/var/log/xlsql.log

# Windows
set XLSQL_LOG_FILE=C:\logs\xlsql.log
```

#### 方式3：禁用文件日志

如果只需要控制台输出，可以禁用文件日志：

```bash
-Dxlsql.log.file.disable=true
```

### 日志文件位置总结

| 配置方式 | 日志文件路径 |
|---------|------------|
| 默认 | `~/.xlsql/xlsql.log` |
| 系统属性 `xlsql.log.file` | 指定的路径 |
| 环境变量 `XLSQL_LOG_FILE` | 指定的路径 |
| 禁用文件日志 | 无文件输出（仅控制台） |

## 敏感信息过滤

XLSQL 日志系统会自动过滤敏感信息：

- **密码过滤**：`password=xxx` → `password=***`
- **URL 凭证过滤**：`jdbc:xlsql://user:pass@host` → `jdbc:xlsql://***:***@host`

## 性能优化

- 日志级别检查：使用 `isLoggable()` 避免不必要的字符串拼接
- 延迟求值：支持 `Supplier` 延迟求值，只在需要时计算日志消息
- 默认级别：生产环境默认 WARNING，减少日志开销

## 常见问题

### Q: 为什么看不到日志？

A: 默认日志级别是 WARNING，只记录警告和错误。如果需要看到更多日志，请设置：
```bash
-Dxlsql.log.level=INFO
```

### Q: 日志输出到哪里？

A: 日志会同时输出到：
1. **控制台**（标准错误流）- 在控制台或 IDE 的运行窗口中可以看到
2. **日志文件**（默认：`~/.xlsql/xlsql.log`）- 自动创建在用户目录下

### Q: 如何自定义日志文件路径？

A: 使用系统属性或环境变量：
```bash
# 系统属性
-Dxlsql.log.file=/path/to/your/xlsql.log

# 环境变量
export XLSQL_LOG_FILE=/var/log/xlsql.log
```

### Q: 如何禁用文件日志，只输出到控制台？

A: 设置系统属性：
```bash
-Dxlsql.log.file.disable=true
```

### Q: 在第三方服务中如何使用？

A: 日志会自动输出到宿主应用的日志系统。可以通过系统属性或环境变量配置日志级别。

## 示例

### 示例1：开发环境（详细日志）

```bash
java -Dxlsql.log.level=FINE -cp xlsql.jar YourApp
```

### 示例2：生产环境（只记录错误）

```bash
java -Dxlsql.log.level=WARNING -cp xlsql.jar YourApp
```

### 示例3：完全静默

```bash
java -Dxlsql.log.level=OFF -cp xlsql.jar YourApp
```

