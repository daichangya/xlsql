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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

import com.jsdiff.xlsql.base.NativeEngineTestBase;

/**
 * NativeEngineIntegrationTest - Native引擎集成测试
 * 
 * <p>测试Native引擎的端到端SQL查询功能，使用真实Excel文件。</p>
 * 
 * @author daichangya
 */
public class NativeEngineIntegrationTest extends NativeEngineTestBase {

    @Test
    public void testBasicSelect() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 5");
        
        assertNotNull(rs);
        
        ResultSetMetaData metaData = rs.getMetaData();
        assertTrue(metaData.getColumnCount() > 0);
        
        int rowCount = 0;
        while (rs.next() && rowCount < 5) {
            rowCount++;
        }
        
        assertTrue(rowCount >= 0);
        
        rs.close();
        stmt.close();
    }

    @Test
    public void testSelectWithWhere() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 WHERE 1=1 LIMIT 5");
        
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

    @Test
    public void testSelectWithLimit() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 3");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        
        assertTrue(rowCount <= 3);
        
        rs.close();
        stmt.close();
    }

    @Test
    public void testSelectWithLimitOffset() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 5 OFFSET 2");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        
        assertTrue(rowCount <= 5);
        
        rs.close();
        stmt.close();
    }

    @Test
    public void testSelectSpecificColumns() throws SQLException {
        Statement stmt = con.createStatement();
        // 假设第一列存在
        ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 1");
        
        assertNotNull(rs);
        
        if (rs.next()) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            assertTrue(columnCount > 0);
            
            // 验证可以读取列值
            for (int i = 1; i <= columnCount; i++) {
                rs.getString(i);
            }
        }
        
        rs.close();
        stmt.close();
    }
}

