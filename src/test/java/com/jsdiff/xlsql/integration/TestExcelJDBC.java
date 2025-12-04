/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://xlsql.jsdiff.com
   daichangya@163.com

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by the Free 
 Software Foundation; either version 2 of the License, or (at your option) 
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public 
 License along with this program; if not, write to the Free Software 
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package com.jsdiff.xlsql.integration;

import static com.jsdiff.xlsql.jdbc.Constants.DRIVER;
import static com.jsdiff.xlsql.jdbc.Constants.URL_PFX_XLS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TestExcelJDBC - Excel JDBC基础功能测试
 * 
 * <p>测试驱动加载、连接和基本查询操作。
 * 这是一个集成测试，使用真实的Excel文件。</p>
 * 
 * @author daichangya
 */
public class TestExcelJDBC {

    private String url;
    private Connection conn;

    @BeforeEach
    public void setUp() throws ClassNotFoundException, SQLException {
        // 加载驱动
        Class.forName(DRIVER);
        
        // 建立连接（指向database目录）
        String databaseDir = System.getProperty("user.dir") + File.separator + "database";
        url = URL_PFX_XLS + databaseDir;
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
        // 使用下划线分隔工作簿和工作表名称
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 1")) {

            assertNotNull(rs, "ResultSet should not be null");

            ResultSetMetaData metaData = rs.getMetaData();
            assertNotNull(metaData, "MetaData should not be null");

            int columnCount = metaData.getColumnCount();
            assertTrue(columnCount > 0, "Column count should be greater than 0");

            if (rs.next()) {
                // 验证可以读取数据
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    // 值可能为null，这是允许的
                    assertNotNull(value, "Column " + i + " should have a value");
                }
            }
        }
    }

    @Test
    public void testResultSetMetaData() throws SQLException {
        // 使用下划线分隔工作簿和工作表名称
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 1")) {

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
        // 使用下划线分隔工作簿和工作表名称
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 1")) {

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

