# xlSQL - Excel SQL Query Tool

xlSQL is a JDBC driver that enables querying Excel files using SQL statements. It treats Excel worksheets as database tables, allowing you to perform queries, inserts, updates, and deletes on Excel data using standard SQL syntax.

## Project Modernization

This project has been upgraded from JDK 1.4 codebase to modern Java 8+, with major optimizations including:

### 1. Code Modernization

- Java 8 Stream API for collection processing
- Lambda expressions for code simplification
- Optional for null-safe operations
- Try-with-resources for automatic resource management
- Parallel streams for improved performance

### 2. Dependency Updates

- Upgraded to latest Apache POI (5.2.3) supporting modern Excel formats
- Updated HSQLDB to version 2.7.2
- Updated MySQL Connector to version 8.0.33
- Updated JUnit to version 5.9.3

### 3. New Features

- Connection pool management for better performance and resource utilization
- Modern Excel reader supporting .xlsx format and parallel processing
- Excel streaming utility class using functional programming
- Improved exception handling and logging

### 4. Performance Optimizations

- Parallel processing for large Excel files
- Connection pooling to reduce database overhead
- Optimized resource management to prevent memory leaks
- Caching to reduce redundant operations

## Usage

### Dependency Configuration
Add the following to your `pom.xml`:
```xml
<dependency>
    <groupId>com.jsdiff</groupId>
    <artifactId>xlsql</artifactId>
    <version>3.0-SNAPSHOT</version>
</dependency>
```

### JDBC Connection

```java
// Register driver (usually not needed explicitly)
Class.forName("com.jsdiff.xlsql.jdbc.xlDriver");

// Create connection
String url = "jdbc:jsdiff:excel:/path/to/excel/files";
Connection conn = DriverManager.getConnection(url);

// Execute query
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM \"test2.Sheet1\"");

// Process results
while (rs.next()) {
    System.out.println(rs.getString(1));
}

// Close resources
rs.close();
stmt.close();
conn.close();
```

### Using the New Excel Utilities

```java
// Find all Excel files
Path directory = Paths.get("/path/to/excel/files");
List<Path> excelFiles = ExcelStreamUtils.findExcelFiles(directory)
    .collect(Collectors.toList());

// Read Excel data
File excelFile = excelFiles.get(0).toFile();
try (Workbook workbook = ExcelStreamUtils.openWorkbook(excelFile)) {
    Sheet sheet = workbook.getSheetAt(0);
    
    // Get headers
    List<String> headers = ExcelStreamUtils.getHeaderRow(sheet);
    
    // Get data rows
    List<Map<String, String>> dataRows = ExcelStreamUtils.getDataRowsAsMaps(sheet);
    
    // Filter data
    List<Map<String, String>> filteredRows = ExcelStreamUtils.filterRows(
        sheet, 
        row -> "Active".equals(row.get("Status"))
    );
    
    // Map data
    List<Customer> customers = ExcelStreamUtils.mapRows(
        sheet,
        row -> new Customer(row.get("Name"), row.get("Email"))
    );
}
```

## Building the Project

```bash
mvn clean package
```

## License

This project is released under the GNU General Public License (GPL).