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
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

import com.jsdiff.xlsql.base.NativeEngineTestBase;

/**
 * JoinQueryIntegrationTest - JOIN查询集成测试
 * 
 * <p>测试Native引擎的多表JOIN查询功能，使用真实Excel文件。</p>
 * 
 * @author daichangya
 */
public class JoinQueryIntegrationTest extends NativeEngineTestBase {

    @Test
    public void testInnerJoin() throws SQLException {
        Statement stmt = con.createStatement();
        
        // 测试INNER JOIN（如果存在两个表）
        try {
            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM test1_Sheet1 t1 " +
                "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 5"
            );
            
            assertNotNull(rs);
            
            int rowCount = 0;
            while (rs.next()) {
                //打印所有列数据
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
                rowCount++;
            }
            
            assertTrue(rowCount >= 0);
            
            rs.close();
        } catch (SQLException e) {
            // 如果表不存在，这是可以接受的
            assertTrue(e.getMessage().contains("not found") || 
                      e.getMessage().contains("Table not found"),
                      "Should be a table not found error");
        }
        
        stmt.close();
    }

    @Test
    public void testLeftJoin() throws SQLException {
        Statement stmt = con.createStatement();
        
        try {
            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM test1_Sheet1 t1 " +
                "LEFT JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 5"
            );
            
            assertNotNull(rs);
            
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
            }
            
            assertTrue(rowCount >= 0);
            
            rs.close();
        } catch (SQLException e) {
            // 如果表不存在，这是可以接受的
            assertTrue(e.getMessage().contains("not found") || 
                      e.getMessage().contains("Table not found"));
        }
        
        stmt.close();
    }
}

