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

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import net.sf.jsqlparser.statement.select.PlainSelect;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MySQLSqlParserTest - MySQL SQL解析器测试
 * 
 * <p>测试基于JSqlParser的MySQL SQL解析器功能。</p>
 * 
 * @author daichangya
 */
public class MySQLSqlParserTest {

    @Test
    public void testSimpleSelect() throws SQLException {
        MySQLSqlParser parser = new MySQLSqlParser();
        PlainSelect plainSelect = parser.parse("SELECT * FROM `test1`.`Sheet1`");
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getFromItem());
    }

    @Test
    public void testSelectWithWhere() throws SQLException {
        MySQLSqlParser parser = new MySQLSqlParser();
        PlainSelect plainSelect = parser.parse("SELECT * FROM `test1`.`Sheet1` WHERE id = 1");
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getWhere());
    }

    @Test
    public void testSelectWithJoin() throws SQLException {
        MySQLSqlParser parser = new MySQLSqlParser();
        PlainSelect plainSelect = parser.parse(
            "SELECT * FROM `test1`.`Sheet1` t1 " +
            "INNER JOIN `test2`.`Sheet2` t2 ON t1.id = t2.foreign_id"
        );
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getJoins());
        assertTrue(plainSelect.getJoins().size() > 0);
    }

    @Test
    public void testSelectWithGroupBy() throws SQLException {
        MySQLSqlParser parser = new MySQLSqlParser();
        PlainSelect plainSelect = parser.parse(
            "SELECT category, COUNT(*) FROM `test1`.`Sheet1` GROUP BY category"
        );
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getGroupBy());
    }

    @Test
    public void testSelectWithOrderBy() throws SQLException {
        MySQLSqlParser parser = new MySQLSqlParser();
        PlainSelect plainSelect = parser.parse(
            "SELECT * FROM `test1`.`Sheet1` ORDER BY id DESC"
        );
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getOrderByElements());
    }

    @Test
    public void testSelectWithLimit() throws SQLException {
        MySQLSqlParser parser = new MySQLSqlParser();
        PlainSelect plainSelect = parser.parse(
            "SELECT * FROM `test1`.`Sheet1` LIMIT 10"
        );
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getLimit());
    }

    @Test
    public void testSelectWithLimitOffset() throws SQLException {
        MySQLSqlParser parser = new MySQLSqlParser();
        PlainSelect plainSelect = parser.parse(
            "SELECT * FROM `test1`.`Sheet1` LIMIT 10 OFFSET 5"
        );
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getLimit());
    }

    @Test
    public void testMySQLBacktickSyntax() throws SQLException {
        MySQLSqlParser parser = new MySQLSqlParser();
        PlainSelect plainSelect = parser.parse("SELECT * FROM `table`");
        assertNotNull(plainSelect);
    }

    @Test
    public void testInvalidSQL() {
        MySQLSqlParser parser = new MySQLSqlParser();
        assertThrows(SQLException.class, () -> {
            parser.parse("INVALID SQL");
        });
    }
}

