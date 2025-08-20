# ExcelJDBC
## 微信公众号

扫码关注微信公众号，Java码界探秘。
![Java码界探秘](http://images.jsdiff.com/qrcode_for_gh_1e2587cc42b1_258_1587996055777.jpg)

[https://jsdiff.com/](https://jsdiff.com/)

## 项目概述
ExcelJDBC 是一个基于 Java 的 JDBC 驱动，允许用户通过 SQL 查询和操作 Excel 文件。它将 Excel 文件视为数据库表，支持标准的 JDBC 接口，方便开发者集成到现有项目中。

## 功能特性
- **支持 SQL 查询**：通过 JDBC 接口执行 SQL 查询，操作 Excel 数据。
- **多引擎支持**：支持 HSQLDB 和 MySQL 作为底层数据库引擎。
- **Excel 文件读写**：支持 `.xls` 格式的 Excel 文件读写操作。
- **元数据查询**：提供数据库和表的元数据信息（如表结构、列信息等）。

## 快速开始
### 1. 依赖配置
在 `pom.xml` 中添加以下依赖：
```xml
<dependency>
    <groupId>com.jsdiff</groupId>
    <artifactId>exceljdbc</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### 1. 清理项目
mvn clean

#### 2. 强制更新并编译
mvn compile -U

#### 3. 打包
mvn package

### 2. 连接数据库
```java
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) throws Exception {
        // 注册驱动
        Class.forName("com.jsdiff.excel.jdbc.xlDriver");
        
        // 创建连接
        String url = "jdbc:jsdiff:excel::/path";
        Connection conn = DriverManager.getConnection(url);
        System.out.println("连接成功！");
    }
}
```

### 3. 执行查询
```java
import java.sql.ResultSet;
import java.sql.Statement;

// 创建 Statement
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM Sheet1");

// 遍历结果集
while (rs.next()) {
    System.out.println(rs.getString(1));
}
```

## 依赖项
- **jxl**: 用于读写 Excel 文件。
- **HSQLDB/MySQL**: 可选数据库引擎支持。

## 限制
- 仅支持 `.xls` 格式的 Excel 文件。
- 部分 JDBC 高级功能（如事务）可能受限。

## 扩展性
- 支持自定义数据库引擎（通过 `xlDatabaseFactory` 扩展）。
- 支持自定义 SQL 解析器（通过 `ASqlParser` 扩展）。

## 许可证
本项目基于 GNU General Public License 发布。