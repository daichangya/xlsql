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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

import io.github.daichangya.xlsql.base.NativeEngineTestBase;

/**
 * ErrorHandlingTest - 错误处理测试
 * 
 * <p>测试Native引擎的错误处理功能，包括无效SQL、表不存在等场景。</p>
 * 
 * @author daichangya
 */
public class ErrorHandlingTest extends NativeEngineTestBase {

    @Test
    public void testInvalidSQL() {
        assertThrows(SQLException.class, () -> {
            Statement stmt = con.createStatement();
            stmt.executeQuery("INVALID SQL STATEMENT");
            stmt.close();
        });
    }

    @Test
    public void testTableNotFound() {
        assertThrows(SQLException.class, () -> {
            Statement stmt = con.createStatement();
            stmt.executeQuery("SELECT * FROM nonexistent_table");
            stmt.close();
        });
    }

    @Test
    public void testEmptySQL() {
        assertThrows(SQLException.class, () -> {
            Statement stmt = con.createStatement();
            stmt.executeQuery("");
            stmt.close();
        });
    }

    @Test
    public void testNullSQL() {
        assertThrows(SQLException.class, () -> {
            Statement stmt = con.createStatement();
            stmt.executeQuery(null);
            stmt.close();
        });
    }
}

