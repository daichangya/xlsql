# xlSQL

## WeChat Official Account

Scan the QR code to follow our WeChat official account, "Java码界探秘" (Java Code World Exploration).
![Java码界探秘](http://images.jsdiff.com/qrcode_for_gh_1e2587cc42b1_258_1587996055777.jpg)

[https://blog.jsdiff.com/](https://blog.jsdiff.com/)

## Project Overview

xlSQL is a Java-based JDBC driver that allows users to query and manipulate Excel files using SQL. It treats Excel files as database tables and supports standard JDBC interfaces, making it easy for developers to integrate into existing projects.

## Features

- **SQL Query Support**: Execute SQL queries on Excel data through JDBC interfaces
- **Multi-engine Support**: Supports HSQLDB and MySQL as underlying database engines
- **Excel File Read/Write**: Supports read and write operations for `.xls` format Excel files
- **Metadata Query**: Provides metadata information for databases and tables (such as table structure, column information, etc.)

## Quick Start

### 1. Dependency Configuration

Add the following dependency to your [pom.xml](https://github.com/daichangya/xlsql/blob/main/pom.xml):

```xml
<dependency>
    <groupId>com.jsdiff</groupId>
    <artifactId>xlsql</artifactId>
    <version>2.0-SNAPSHOT</version>
</dependency>
```


#### 1. Clean Project
```bash
mvn clean
```


#### 2. Force Update and Compile
```bash
mvn compile -U
```


#### 3. Package
```bash
mvn package
```


### 2. Connect to Database

```java
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) throws Exception {
        // Register driver
        Class.forName("com.jsdiff.excel.jdbc.xlDriver");
        
        // Create connection
        String url = "jdbc:jsdiff:excel::/path";
        Connection conn = DriverManager.getConnection(url);
        System.out.println("Connection successful!");
    }
}
```


### 3. Execute Query

```java
import java.sql.ResultSet;
import java.sql.Statement;

// Create Statement
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM \"test2.Sheet1\" LIMIT 10");

// Iterate through result set
while (rs.next()) {
    System.out.println(rs.getString(1));
}
```


## Dependencies

- **jxl**: For reading and writing Excel files
- **HSQLDB/MySQL**: Optional database engine support

## Limitations

- supports `.xls`  `.xlsx` format Excel files
- Some advanced JDBC features (such as transactions) may be limited

## Extensibility

- Supports custom database engines (via [xlDatabaseFactory](https://github.com/daichangya/xlsql/blob/main/src/main/java/com/jsdiff/excel/database/xlDatabaseFactory.java#L36-L58) extension)
- Supports custom SQL parsers (via [ASqlParser](https://github.com/daichangya/xlsql/blob/main/src/main/java/com/jsdiff/excel/database/sql/ASqlParser.java#L20-L115) extension)

## License

This project is released under the GNU General Public License.

## Project Link

Project Link: [https://github.com/daichangya/xlsql](https://github.com/daichangya/xlsql)