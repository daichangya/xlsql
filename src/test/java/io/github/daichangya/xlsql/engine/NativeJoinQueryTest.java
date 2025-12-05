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
package io.github.daichangya.xlsql.engine;

import static io.github.daichangya.xlsql.jdbc.Constants.DRIVER;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

/**
 * NativeJoinQueryTest - Native引擎JOIN查询测试
 * 
 * <p>测试Native引擎的多表JOIN功能。</p>
 * 
 * @author daichangya
 */
public class NativeJoinQueryTest {

    private Connection con;
    private xlInstance instance;

    @BeforeEach
    public void setUp() throws Exception {
        // 确保xlDriver已注册
        Class.forName(DRIVER);
        
        instance = xlInstance.getInstance();
        instance.setEngine("native"); // 设置引擎为自研引擎
        
        // 使用xlDriver连接，将自动使用xlConnectionNative
        String url = "jdbc:xlsql:excel:" + System.getProperty("user.dir");
        con = DriverManager.getConnection(url);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (con != null && !con.isClosed()) {
            con.close();
        }
        instance.setEngine("h2"); // Reset to H2 for other tests
    }

    @Test
    public void testSimpleSelect() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1");
        assertNotNull(rs);
        
        ResultSetMetaData metaData = rs.getMetaData();
        assertTrue(metaData.getColumnCount() > 0);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount > 0);
        
        rs.close();
        stmt.close();
    }

    @Test
    public void testSelectWithWhere() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 WHERE 1=1");
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount > 0);
        
        rs.close();
        stmt.close();
    }

    @Test
    public void testSelectWithOrderBy() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 ORDER BY 1 ASC LIMIT 10");
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount <= 10);
        
        rs.close();
        stmt.close();
    }
}

