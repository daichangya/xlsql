import static com.jsdiff.xlsql.jdbc.Constants.DRIVER;
import static com.jsdiff.xlsql.jdbc.Constants.URL_PFX_XLS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Excel JDBC basic functionality
 * Tests driver loading, connection, and basic query operations
 */
public class TestExcelJDBC {

    private String url;
    private Connection conn;

    @BeforeEach
    public void setUp() throws ClassNotFoundException, SQLException {
        // 加载驱动
        Class.forName(DRIVER);
        
        // 建立连接
        url = URL_PFX_XLS + System.getProperty("user.dir");
        conn = DriverManager.getConnection(url);
    }

    @Test
    public void testDriverLoading() {
        assertNotNull(conn, "Connection should be established");
    }

    @Test
    public void testConnection() throws SQLException {
        assertNotNull(conn, "Connection should not be null");
        assertFalse(conn.isClosed(), "Connection should be open");
    }

    @Test
    public void testBasicQuery() throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM \"test.Sheet1\" LIMIT 1")) {

            assertNotNull(rs, "ResultSet should not be null");

            ResultSetMetaData metaData = rs.getMetaData();
            assertNotNull(metaData, "MetaData should not be null");

            int columnCount = metaData.getColumnCount();
            assertTrue(columnCount > 0, "Column count should be greater than 0");

            if (rs.next()) {
                // 验证可以读取数据
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    assertNotNull(value, "Column " + i + " should have a value");
                }
            }
        }
    }

    @Test
    public void testResultSetMetaData() throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM \"test.Sheet1\" LIMIT 1")) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            assertTrue(columnCount > 0, "Should have at least one column");

            // 验证列信息
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                assertNotNull(columnName, "Column name should not be null");
                assertFalse(columnName.isEmpty(), "Column name should not be empty");
            }
        }
    }

    @Test
    public void testQueryWithData() throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM \"test.Sheet1\" LIMIT 1")) {

            boolean hasData = rs.next();
            // 如果表存在且有数据，验证数据读取
            if (hasData) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    // 验证可以读取列值（可能为null，但不应该抛出异常）
                    rs.getString(i);
                }
            }
        }
    }
}
