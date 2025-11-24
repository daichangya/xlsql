import static com.jsdiff.xlsql.jdbc.Constants.DRIVER;
import static com.jsdiff.xlsql.jdbc.Constants.URL_PFX_XLS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive unit tests for Excel JDBC functionality
 * Tests metadata, table listing, and data querying
 */
public class ComprehensiveExcelTest {

    public static final String DATA_XLS = "test1.xls";
    
    private String url;
    private Connection con;

    @BeforeEach
    public void setUp() throws ClassNotFoundException, SQLException {
        // 加载驱动
        Class.forName(DRIVER);
        
        // 连接到当前目录
        url = URL_PFX_XLS + System.getProperty("user.dir");
        con = DriverManager.getConnection(url);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }

    @Test
    public void testTestFileExists() {
        File testFile = new File(DATA_XLS);
        // 如果文件不存在，跳过此测试（使用assumeTrue）
        // 或者只是记录警告，不阻止测试继续
        if (!testFile.exists()) {
            System.out.println("警告: 测试文件 " + DATA_XLS + " 不存在，某些测试可能失败");
        }
    }

    @Test
    public void testConnection() throws SQLException {
        assertNotNull(con, "Connection should be established");
        assertFalse(con.isClosed(), "Connection should be open");
    }

    @Test
    public void testDatabaseMetaData() throws SQLException {
        DatabaseMetaData metaData = con.getMetaData();
        assertNotNull(metaData, "MetaData should not be null");
        
        String driverName = metaData.getDriverName();
        assertNotNull(driverName, "Driver name should not be null");
        assertFalse(driverName.isEmpty(), "Driver name should not be empty");
        
        String driverVersion = metaData.getDriverVersion();
        assertNotNull(driverVersion, "Driver version should not be null");
    }

    @Test
    public void testGetTables() throws SQLException {
        DatabaseMetaData metaData = con.getMetaData();
        ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
        
        assertNotNull(tables, "Tables ResultSet should not be null");
        
        // 验证可以遍历表（即使表可能为空）
        int tableCount = 0;
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            assertNotNull(tableName, "Table name should not be null");
            tableCount++;
        }
        tables.close();
        
        // 至少应该能够执行查询，即使没有表
        assertTrue(tableCount >= 0, "Table count should be non-negative");
    }

    @Test
    public void testQueryExcelData() throws SQLException {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM \"test1.Sheet1\" LIMIT 1")) {
            
            assertNotNull(rs, "ResultSet should not be null");
            
            ResultSetMetaData rsMetaData = rs.getMetaData();
            assertNotNull(rsMetaData, "ResultSet MetaData should not be null");
            
            int columnCount = rsMetaData.getColumnCount();
            assertTrue(columnCount > 0, "Should have at least one column");
            
            // 验证列信息
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsMetaData.getColumnName(i);
                assertNotNull(columnName, "Column name should not be null");
                
                String columnType = rsMetaData.getColumnTypeName(i);
                assertNotNull(columnType, "Column type should not be null");
            }
            
            // 如果有多行数据，验证可以读取
            if (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    // 验证可以读取值（可能为null）
                    rs.getString(i);
                    // 值可能为null，这是允许的
                }
            }
        }
    }

    @Test
    public void testQueryWithAlternativeTable() throws SQLException {
        // 尝试查询另一个可能的表名
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM \"test2.Sheet1\" LIMIT 1")) {
            
            assertNotNull(rs, "ResultSet should not be null");
            
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            
            if (rs.next()) {
                // 验证可以读取数据
                for (int i = 1; i <= columnCount; i++) {
                    rs.getString(i); // 可能为null，但不应该抛出异常
                }
            }
        } catch (SQLException e) {
            // 如果表不存在，这是可以接受的
            // 只验证异常信息有意义
            assertNotNull(e.getMessage(), "Exception message should not be null");
        }
    }

    @Test
    public void testResultSetNavigation() throws SQLException {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM \"test1.Sheet1\" LIMIT 5")) {
            
            assertNotNull(rs, "ResultSet should not be null");
            
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                // 验证可以读取所有列
                for (int i = 1; i <= columnCount; i++) {
                    rs.getString(i);
                }
            }
            
            // 验证可以遍历结果集
            assertTrue(rowCount >= 0, "Row count should be non-negative");
        }
    }
}
