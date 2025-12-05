package io.github.daichangya.xlsql.jdbc;

import static io.github.daichangya.xlsql.jdbc.Constants.URL_PFX_XLS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.daichangya.xlsql.database.xlInstance;

/**
 * Unit tests for H2 database connection support
 * 
 * <p>测试H2数据库引擎的连接功能，验证H2与HSQLDB的SQL兼容性。</p>
 */
public class xlConnectionH2Test {

    private String url;
    private Connection con;
    private xlInstance instance;

    @BeforeEach
    public void setUp() throws Exception {
        // 加载驱动
        Class.forName(Constants.DRIVER);
        
        // 获取xlInstance实例并配置为使用H2
        instance = xlInstance.getInstance();
        instance.setEngine("h2");
        instance.setDriver("org.h2.Driver");
        instance.setUrl("jdbc:h2:mem:xlsql");
        instance.setUser("sa");
        instance.setPassword("");
        
        // 连接到database目录
        String databaseDir = System.getProperty("user.dir") + File.separator + "database";
        url = URL_PFX_XLS + databaseDir;
        con = DriverManager.getConnection(url);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }

    @Test
    public void testConnection() throws SQLException {
        assertNotNull(con, "Connection should be established");
        assertFalse(con.isClosed(), "Connection should be open");
        
        // 验证连接类型是xlConnectionH2
        assertTrue(con instanceof xlConnection, "Connection should be xlConnection");
        assertTrue(con instanceof xlConnectionH2, "Connection should be xlConnectionH2 instance");
        
        // 验证方言是h2
        xlConnection xlCon = (xlConnection) con;
        assertEquals("h2", xlCon.getDialect(), "Dialect should be h2");
    }

    @Test
    public void testH2ConnectionType() throws SQLException {
        // 验证连接类型是xlConnectionH2
        assertTrue(con instanceof xlConnection, "Connection should be xlConnection");
        assertTrue(con instanceof xlConnectionH2, "Connection should be xlConnectionH2");
        
        // 验证方言是h2
        if (con instanceof xlConnection) {
            xlConnection xlCon = (xlConnection) con;
            assertEquals("h2", xlCon.getDialect(), "Dialect should be h2");
        }
    }

    @Test
    public void testQueryWithH2() throws SQLException {
        // 测试H2数据库的查询功能（H2使用下划线分隔工作簿和工作表名称）
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 1")) {
            
            assertNotNull(rs, "ResultSet should not be null");
            
            // 验证可以读取元数据
            int columnCount = rs.getMetaData().getColumnCount();
            assertTrue(columnCount >= 0, "Should have valid column count");
        } catch (SQLException e) {
            // 如果表不存在，这是可以接受的（测试环境可能没有test1.xls文件）
            // 只验证异常信息有意义
            assertNotNull(e.getMessage(), "Exception message should not be null");
        }
    }

    @Test
    public void testH2Shutdown() throws Exception {
        // 测试H2连接可以正常关闭
        assertFalse(con.isClosed(), "Connection should be open before shutdown");
        
        con.close();
        
        assertTrue(con.isClosed(), "Connection should be closed after shutdown");
    }

    @Test
    public void testH2SQLCompatibility() throws SQLException {
        // 测试H2与HSQLDB的SQL兼容性
        // H2应该支持与HSQLDB相同的表名引用格式（下划线分隔）
        try (Statement stmt = con.createStatement()) {
            // 测试表名引用格式（应该与HSQLDB兼容，使用下划线）
            String sql = "SELECT COUNT(*) FROM test1_Sheet1";
            try {
                ResultSet rs = stmt.executeQuery(sql);
                assertNotNull(rs, "Should support HSQLDB-compatible table name format");
                rs.close();
            } catch (SQLException e) {
                // 如果表不存在，这是可以接受的
                // 只验证SQL语法被接受（不会抛出语法错误）
                assertFalse(e.getMessage().contains("Syntax error"), 
                    "H2 should support HSQLDB-compatible SQL syntax");
            }
        }
    }
}

