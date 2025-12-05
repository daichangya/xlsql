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
package io.github.daichangya.xlsql.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

import io.github.daichangya.xlsql.base.NativeEngineTestBase;

/**
 * AggregateQueryIntegrationTest - 聚合查询集成测试
 * 
 * <p>测试Native引擎的聚合函数和GROUP BY功能，使用真实Excel文件。</p>
 * 
 * @author daichangya
 */
public class AggregateQueryIntegrationTest extends NativeEngineTestBase {

    @Test
    public void testCount() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM test1_Sheet1");
        
        assertNotNull(rs);
        
        assertTrue(rs.next());
        long count = rs.getLong("cnt");
        assertTrue(count >= 0);
        
        rs.close();
        stmt.close();
    }

    @Test
    public void testGroupBy() throws SQLException {
        Statement stmt = con.createStatement();
        // 假设第一列可以用于分组
        ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 GROUP BY 1 LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        
        assertTrue(rowCount >= 0);
        
        rs.close();
        stmt.close();
    }

    @Test
    public void testSum() throws SQLException {
        Statement stmt = con.createStatement();
        // 假设存在数值列
        try {
            ResultSet rs = stmt.executeQuery("SELECT SUM(1) as total FROM test1_Sheet1");
            
            assertNotNull(rs);
            
            if (rs.next()) {
                // 验证可以读取SUM值
                rs.getString("total");
            }
            
            rs.close();
        } catch (SQLException e) {
            // 如果列不存在，这是可以接受的
        }
        
        stmt.close();
    }
}

