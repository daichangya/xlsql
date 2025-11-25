package com.jsdiff.xlsql.jdbc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jsdiff.xlsql.engine.connection.xlConnectionNative;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jsdiff.xlsql.database.xlInstance;

/**
 * xlConnectionNativeTest - 自研SQL引擎连接测试
 * 
 * <p>测试自研SQL引擎的连接和基本查询功能。</p>
 * 
 * @author daichangya
 */
public class xlConnectionNativeTest {

    private Connection con;
    private xlInstance instance;

    @BeforeEach
    public void setUp() throws ClassNotFoundException, SQLException, com.jsdiff.xlsql.database.xlException {
        // 确保xlDriver已注册
        Class.forName(Constants.DRIVER);

        instance = xlInstance.getInstance();
        instance.setEngine("native"); // 设置引擎为自研引擎
        // 自研引擎不需要驱动和URL配置

        // 使用xlDriver连接，将自动使用xlConnectionNative
        String url = Constants.URL_PFX_XLS + System.getProperty("user.dir");
        con = DriverManager.getConnection(url);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
        // 重置xlInstance为默认引擎
        try {
            instance.setEngine("h2"); // 重置为H2，避免影响其他测试
        } catch (com.jsdiff.xlsql.database.xlException e) {
            // 忽略重置引擎时的异常
        }
    }

    @Test
    public void testConnection() throws SQLException {
        assertNotNull(con, "Connection should be established");
        assertFalse(con.isClosed(), "Connection should be open");
        // 验证使用的是自研引擎连接实现
        assertTrue(con instanceof xlConnectionNative,
                  "Should be using Native connection implementation");
    }

    @Test
    public void testNativeDatabaseMetaData() throws SQLException {
        assertNotNull(con.getMetaData(), "DatabaseMetaData should not be null");
        String productName = con.getMetaData().getDatabaseProductName();
        assertTrue(productName.contains("Native") || productName.contains("xlSQL"), 
                  "Database product name should contain Native or xlSQL");
    }

    @Test
    public void testSimpleQuery() throws SQLException {
        // 测试简单查询（如果有Excel文件）
        // 注意：这个测试需要当前目录下有Excel文件
        Statement stmt = con.createStatement();
        
        try {
            // 尝试查询（如果目录中有Excel文件）
            // 这里只是测试连接是否正常工作，不保证有数据
            ResultSet rs = stmt.executeQuery("SELECT * FROM \"SA\".\"Sheet1\"");
            assertNotNull(rs, "ResultSet should not be null");
            // 如果查询成功，说明自研引擎正常工作
        } catch (SQLException e) {
            // 如果没有Excel文件或表不存在，这是预期的
            // 只要不是连接错误，就认为测试通过
            assertFalse(e.getMessage().contains("Connection"), 
                       "Should not be a connection error: " + e.getMessage());
        }
    }

    @Test
    public void testConnectionIsReadOnly() throws SQLException {
        assertTrue(con.isReadOnly(), "Native engine connection should be read-only");
    }

    @Test
    public void testAutoCommit() throws SQLException {
        assertTrue(con.getAutoCommit(), "Native engine should have auto-commit enabled");
    }
}

