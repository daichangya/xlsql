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
 * ComplexQueryIntegrationTest - 复杂查询集成测试
 * 
 * <p>测试Native引擎的复杂SQL查询，包括完整的SELECT语句组合。</p>
 * 
 * @author daichangya
 */
public class ComplexQueryIntegrationTest extends NativeEngineTestBase {

    @Test
    public void testComplexQuery() throws SQLException {
        Statement stmt = con.createStatement();
        
        // 测试完整的SQL查询：SELECT + FROM + WHERE + ORDER BY + LIMIT
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM test1_Sheet1 " +
            "WHERE 1=1 " +
            "ORDER BY 1 ASC " +
            "LIMIT 10"
        );
        
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

