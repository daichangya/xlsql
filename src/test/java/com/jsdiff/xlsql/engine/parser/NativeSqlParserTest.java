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
package com.jsdiff.xlsql.engine.parser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * NativeSqlParserTest - Native SQL解析器单元测试
 * 
 * <p>测试NativeSqlParser的SQL解析功能，验证其正确委托给MySQLSqlParser。</p>
 * 
 * @author rzy
 */
@Tag("unit")
@Tag("parser")
public class NativeSqlParserTest {

    private NativeSqlParser parser;

    @BeforeEach
    public void setUp() {
        parser = new NativeSqlParser();
    }

    @Test
    public void testParseSimpleSelect() throws SQLException {
        String sql = "SELECT * FROM test1_Sheet1";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect, "解析结果不应该为null");
    }

    @Test
    public void testParseSelectWithWhere() throws SQLException {
        String sql = "SELECT * FROM test1_Sheet1 WHERE id > 10";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getWhere());
    }

    @Test
    public void testParseSelectWithJoin() throws SQLException {
        String sql = "SELECT t1.a, t2.b FROM test1_Sheet1 t1 " +
                    "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getJoins());
    }

    @Test
    public void testParseNullSql() {
        assertThrows(SQLException.class, () -> {
            parser.parse(null);
        }, "null SQL应该抛出异常");
    }

    @Test
    public void testParseInvalidSql() {
        assertThrows(SQLException.class, () -> {
            parser.parse("INVALID SQL");
        }, "无效SQL应该抛出异常");
    }
}

