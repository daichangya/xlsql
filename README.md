# XLSQL - Excel SQL Query Tool

[![Maven Central](https://img.shields.io/maven-central/v/io.github.daichangya/xlsql.svg)](https://search.maven.org/artifact/io.github.daichangya/xlsql)
[![License](https://img.shields.io/badge/license-GPL%20v2-blue.svg)](https://www.gnu.org/licenses/gpl-3.0.html)

XLSQL is a JDBC driver that enables querying Excel files using SQL statements. It treats Excel worksheets as database tables, allowing you to perform queries, inserts, updates, and deletes on Excel data using standard SQL syntax.

**Version 5.1.1** introduces the **Native SQL Engine**, a fully self-developed SQL execution engine with zero external database dependencies.

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
- Updated HSQLDB to version 2.5.2 (compatible with Java 8)
- Removed MySQL engine support, now supports H2, HSQLDB, and Native engines only
- Updated JUnit to version 5.9.3 (JUnit Jupiter)

### 3. New Features

- Modern Excel reader supporting .xlsx format
- Excel streaming utility class using functional programming (ExcelStreamUtils)
- Improved exception handling and logging
- Thread-safe singleton pattern for instance management
- Proper resource management with try-with-resources

### 4. Performance Optimizations

- Optimized resource management to prevent memory leaks
- Efficient Workbook handling with proper lifecycle management
- Stream-based Excel file processing utilities

## Usage

### Dependency Configuration

#### Maven Central (Recommended)

XLSQL 5.1.1 is available on Maven Central. Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.daichangya</groupId>
    <artifactId>xlsql</artifactId>
    <version>5.1.1</version>
</dependency>
```

#### Local Installation

If you need to install from source:

```bash
git clone https://github.com/daichangya/xlsql.git
cd xlsql
mvn clean install
```

Then add the dependency to your `pom.xml` (same as above).

### JDBC Connection

```java
// Register driver (usually not needed explicitly)
Class.forName("jdbc.io.github.daichangya.xlsql.xlDriver");

// Create connection
String url = "jdbc:xlsql:excel:/path/to/excel/files";
Connection conn = DriverManager.getConnection(url);

// Execute query
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM test2_Sheet1");

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

## Requirements

- **Java**: JDK 8 or higher
- **Maven**: 3.6.0 or higher
- **Operating System**: Windows, Linux, macOS

## Building the Project

### Prerequisites

Ensure you have Java 8+ and Maven installed:

```bash
java -version  # Should show Java 8 or higher
mvn -version   # Should show Maven 3.6.0 or higher
```

### Build Commands

#### 1. Clean the project
```bash
mvn clean
```

#### 2. Compile the project
```bash
mvn compile
```

#### 3. Run tests
```bash
mvn test
```

#### 4. Package the project
```bash
mvn package
```

This will create:
- `target/xlsql-5.1.1.jar` - Standard JAR file
- `target/xlsql-5.1.1-shaded.jar` - Fat JAR with all dependencies

#### 5. Install to local Maven repository
```bash
mvn install
```

This installs the artifact to your local Maven repository (`~/.m2/repository/com/jsdiff/xlsql/5.1.1/`), making it available for other projects.

#### 6. Skip tests during build
```bash
mvn package -DskipTests
```

#### 7. Create source JAR
```bash
mvn source:jar
```

#### 8. Create Javadoc JAR
```bash
mvn javadoc:jar
```

### Build Output

After running `mvn package`, you'll find:

- **Standard JAR**: `target/xlsql-5.1.1.jar`
  - Contains only XLSQL classes
  - Requires dependencies to be provided separately

- **Shaded JAR** (Fat JAR): `target/xlsql-5.1.1-shaded.jar`
  - Contains all dependencies bundled
  - Can be used standalone
  - Recommended for distribution

### Using the Built JAR

#### Option 1: Use Standard JAR with Dependencies

Add to your `pom.xml`:
```xml
<dependency>
    <groupId>io.github.daichangya</groupId>
    <artifactId>xlsql</artifactId>
    <version>5.1.1</version>
</dependency>
```

#### Option 2: Use Shaded JAR Directly

1. Copy `xlsql-5.1.1-shaded.jar` to your project
2. Add to classpath manually
3. Or install to local repository:
```bash
mvn install:install-file \
  -Dfile=target/xlsql-5.1.1-shaded.jar \
  -DgroupId=io.github.daichangya \
  -DartifactId=xlsql \
  -Dversion=5.1.1 \
  -Dpackaging=jar
```

## Development

### Project Structure

```
xlsql/
├── src/
│   ├── main/java/          # Main source code
│   └── test/java/           # Test code
├── doc/                     # Documentation
├── pom.xml                  # Maven configuration
└── README.md                # This file
```

### Running Tests

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=TestXlsql
```

Run tests with coverage (requires JaCoCo plugin):
```bash
mvn clean test jacoco:report
```

### IDE Setup

#### IntelliJ IDEA
1. File → Open → Select `pom.xml`
2. Maven will automatically import dependencies
3. Configure JDK: File → Project Structure → Project SDK (Java 8+)

#### Eclipse
1. File → Import → Maven → Existing Maven Projects
2. Select project directory
3. Configure JDK: Project → Properties → Java Build Path

#### VS Code
1. Install Java Extension Pack
2. Open project folder
3. VS Code will detect Maven project automatically

### Debugging

Run with debug output:
```bash
mvn test -X
```

Enable debug logging by setting system property:
```bash
mvn test -Djava.util.logging.config.file=logging.properties
```

## Installation

### Install from Source

1. Clone the repository:
```bash
git clone https://github.com/daichangya/xlsql.git
cd xlsql
```

2. Build and install:
```bash
mvn clean install
```

3. Use in your project:
```xml
<dependency>
    <groupId>io.github.daichangya</groupId>
    <artifactId>xlsql</artifactId>
    <version>5.1.1</version>
</dependency>
```

### Install Shaded JAR Manually

If you have the shaded JAR file:

```bash
mvn install:install-file \
  -Dfile=xlsql-5.1.1-shaded.jar \
  -DgroupId=io.github.daichangya \
  -DartifactId=xlsql \
  -Dversion=5.1.1 \
  -Dpackaging=jar
```

## Configuration

### Environment Variables

- `XLSQL_CONFIG_PATH`: Path to configuration file
- `XLSQL_LOG_PATH`: Path to log file

### System Properties

- `xlsql.config.path`: Configuration file path
- `xlsql.log.path`: Log file path

### Configuration File

Default location: `~/.xlsql/xlsql_config.properties`

Example configuration:
```properties
# Database engine (hsqldb, h2, or native)
engine=hsqldb

# HSQLDB configuration
hsqldb.url=jdbc:hsqldb:mem:xlsql
hsqldb.user=sa
hsqldb.password=

# H2 configuration (if using H2 engine)
h2.url=jdbc:h2:mem:xlsql
h2.user=sa
h2.password=
```

## Troubleshooting

### Common Issues

#### 1. ClassNotFoundException
**Problem**: Cannot find xlDriver class
**Solution**: Ensure the JAR is in your classpath

#### 2. UnsupportedClassVersionError
**Problem**: HSQLDB requires Java 11+
**Solution**: Use HSQLDB 2.5.2 (included) which supports Java 8

#### 3. Connection Failed
**Problem**: Cannot connect to Excel directory
**Solution**: 
- Check that the directory path is correct
- Ensure the directory contains Excel files (.xls or .xlsx)
- Check file permissions

#### 4. Table Not Found
**Problem**: SQL query fails with "table not found"
**Solution**:
- Use correct table name format: `"filename.SheetName"`
- Ensure Excel file exists in the directory
- Check sheet name spelling (case-sensitive)

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass (`mvn test`)
6. Commit your changes (`git commit -m 'Add amazing feature'`)
7. Push to the branch (`git push origin feature/amazing-feature`)
8. Open a Pull Request

### Code Style

- Follow Java naming conventions
- Use 4 spaces for indentation
- Add JavaDoc for public classes and methods
- Write unit tests for new features

## License

This project is released under the GNU General Public License (GPL).

See [LICENSE](LICENSE) file for details.

## Documentation

- [Installation Guide](doc/INSTALLATION_GUIDE.md) - Detailed installation and build instructions
- [Development Guide](doc/DEVELOPMENT_GUIDE.md) - Development setup and guidelines
- [Command Line Usage](doc/命令行使用说明.md) - Command line tool documentation
- [DBeaver Integration](doc/Dbeaver结合exceljdbc使用文档.md) - DBeaver integration guide
- [Architecture Documentation](doc/XLSQL%20架构设计文档V2.md) - System architecture
- [Changelog](doc/CHANGELOG_4.0.md) - Version history

## Links

- **GitHub**: https://github.com/daichangya/xlsql
- **Blog**: https://blog.jsdiff.com/
- **Issues**: https://github.com/daichangya/xlsql/issues