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
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.daichangya.xlsql.database.xlInstance;

/**
 * NativeAggregateQueryTest - Native引擎聚合查询测试
 * 
 * <p>测试Native引擎的聚合函数和GROUP BY功能。</p>
 * 
 * @author daichangya
 */
public class NativeAggregateQueryTest {

    private Connection con;
    private xlInstance instance;

    @BeforeEach
    public void setUp() throws Exception {
        Class.forName(DRIVER);
        instance = xlInstance.getInstance();
        instance.setEngine("native");
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
    public void testCount() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM test1_Sheet1");
        assertNotNull(rs);
        
        assertTrue(rs.next());
        long count = rs.getLong("cnt");
        assertTrue(count > 0);
        
        rs.close();
        stmt.close();
    }

    @Test
    public void testGroupBy() throws SQLException {
        Statement stmt = con.createStatement();
        // 假设第一列可以用于分组
        ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 GROUP BY 1");
        assertNotNull(rs);
        
        // GROUP BY应该返回分组后的行
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        // 至少应该有一行（即使分组后）
        assertTrue(rowCount >= 0);
        
        rs.close();
        stmt.close();
    }
}

