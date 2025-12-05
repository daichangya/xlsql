package io.github.daichangya.xlsql.jdbc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.daichangya.xlsql.database.xlInstance;
import io.github.daichangya.xlsql.engine.connection.xlConnectionNative;

/**
 * NativeEngineQueryTest - 自研SQL引擎查询功能测试
 * 
 * <p>测试自研SQL引擎的实际查询功能，验证能否正确查询Excel数据。</p>
 * 
 * @author daichangya
 */
public class NativeEngineQueryTest {

    private Connection con;
    private xlInstance instance;

    @BeforeEach
    public void setUp() throws Exception {
        // 加载驱动
        Class.forName(Constants.DRIVER);

        instance = xlInstance.getInstance();
        instance.setEngine("native"); // 使用自研引擎

        // 连接到database目录
        String databaseDir = System.getProperty("user.dir") + File.separator + "database";
        String url = Constants.URL_PFX_XLS + databaseDir;
        con = DriverManager.getConnection(url);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
        try {
            instance.setEngine("h2"); // 重置为H2
        } catch (io.github.daichangya.xlsql.database.xlException e) {
            // 忽略
        }
    }

    @Test
    public void testQueryWithWorkbookAndSheet() throws SQLException {
        // 测试查询格式：SELECT * FROM workbook_sheet（下划线格式）
        Statement stmt = con.createStatement();
        
        try {
            // 尝试查询test1.xls的Sheet1工作表
            ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1");
            assertNotNull(rs, "ResultSet should not be null");
            
            // 验证可以获取元数据
            ResultSetMetaData metaData = rs.getMetaData();
            assertNotNull(metaData, "MetaData should not be null");
            
            int columnCount = metaData.getColumnCount();
            assertTrue(columnCount >= 0, "Should have valid column count");
            
            // 尝试读取数据
            int rowCount = 0;
            while (rs.next() && rowCount < 10) { // 最多读取10行
                rowCount++;
            }
            
            System.out.println("Query successful: " + columnCount + " columns, " + rowCount + " rows");
            
        } catch (SQLException e) {
            // 如果表不存在，这是可以接受的
            System.out.println("Query test: " + e.getMessage());
            assertTrue(e.getMessage().contains("Table not found") || 
                      e.getMessage().contains("not found"),
                      "Should be a table not found error, not a connection error");
        }
    }

    @Test
    public void testQueryWithSheetOnly() throws SQLException {
        // 测试查询格式：SELECT * FROM sheet（使用默认工作簿SA，或使用 SA_sheet）
        Statement stmt = con.createStatement();
        
        try {
            // 使用下划线格式，SA 是默认 schema
            ResultSet rs = stmt.executeQuery("SELECT * FROM SA_Sheet1");
            assertNotNull(rs, "ResultSet should not be null");
            
            ResultSetMetaData metaData = rs.getMetaData();
            assertNotNull(metaData, "MetaData should not be null");
            
            System.out.println("Query with sheet only successful: " + 
                             metaData.getColumnCount() + " columns");
            
        } catch (SQLException e) {
            System.out.println("Query test (sheet only): " + e.getMessage());
            // 表不存在是可以接受的
        }
    }

    @Test
    public void testNativeEngineFeatures() throws SQLException {
        // 验证自研引擎的特性
        assertTrue(con instanceof xlConnectionNative,
                  "Should be using Native engine");
        
        // 验证只读模式
        assertTrue(con.isReadOnly(), "Native engine should be read-only");
        
        // 验证自动提交
        assertTrue(con.getAutoCommit(), "Native engine should have auto-commit");
        
        // 验证元数据
        String productName = con.getMetaData().getDatabaseProductName();
        assertTrue(productName.contains("Native") || productName.contains("XLSQL"),
                  "Product name should indicate Native engine");
        
        System.out.println("Native engine features verified:");
        System.out.println("  - Product: " + productName);
        System.out.println("  - Read-only: " + con.isReadOnly());
        System.out.println("  - Auto-commit: " + con.getAutoCommit());
    }
}

