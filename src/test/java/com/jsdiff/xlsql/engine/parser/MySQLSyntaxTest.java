/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://excel.jsdiff.com
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
package com.jsdiff.xlsql.engine.parser;

import com.jsdiff.xlsql.database.xlInstance;
import com.jsdiff.xlsql.jdbc.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MySQLSyntaxTest - MySQL语法支持测试
 * 
 * <p>测试Native引擎对MySQL语法的支持，包括反引号、LIMIT等MySQL特有语法。</p>
 * 
 * @author daichangya
 */
public class MySQLSyntaxTest {

    private Connection con;
    private xlInstance instance;

    @BeforeEach
    public void setUp() throws Exception {
        // 确保xlDriver已注册
        Class.forName(Constants.DRIVER);
        
        instance = xlInstance.getInstance();
        instance.setEngine("native"); // 设置引擎为自研引擎
        
        // 使用xlDriver连接，将自动使用xlConnectionNative
        String url = Constants.URL_PFX_XLS + System.getProperty("user.dir");
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
    public void testBacktickTableName() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM `test1`.`Sheet1`");
        assertNotNull(rs);
        
        ResultSetMetaData metaData = rs.getMetaData();
        assertTrue(metaData.getColumnCount() > 0);
        
        rs.close();
        stmt.close();
    }

    @Test
    public void testMySQLLimitSyntax() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM `test1`.`Sheet1` LIMIT 10");
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount <= 10);
        
        rs.close();
        stmt.close();
    }

    @Test
    public void testMySQLLimitOffsetSyntax() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM `test1`.`Sheet1` LIMIT 10 OFFSET 5");
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount <= 10);
        
        rs.close();
        stmt.close();
    }

    @Test
    public void testMySQLFunction() throws SQLException {
        Statement stmt = con.createStatement();
        // 测试MySQL函数（如CONCAT、UPPER等）
        ResultSet rs = stmt.executeQuery("SELECT UPPER(name) FROM `test1`.`Sheet1` LIMIT 1");
        assertNotNull(rs);
        
        if (rs.next()) {
            // 验证函数执行
            assertNotNull(rs.getString(1));
        }
        
        rs.close();
        stmt.close();
    }
}

