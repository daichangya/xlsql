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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * MySQLSqlParserTest - MySQL SQL解析器单元测试
 * 
 * <p>测试MySQLSqlParser的SQL解析功能，验证：
 * <ul>
 *   <li>基本SELECT语句解析</li>
 *   <li>复杂SQL语句解析</li>
 *   <li>错误SQL语句处理</li>
 *   <li>表名格式解析（下划线格式）</li>
 * </ul>
 * </p>
 * 
 * @author rzy
 */
@Tag("unit")
@Tag("parser")
public class MySQLSqlParserTest {

    private MySQLSqlParser parser;

    @BeforeEach
    public void setUp() {
        parser = new MySQLSqlParser();
    }

    @Test
    public void testParseSimpleSelect() throws SQLException {
        String sql = "SELECT * FROM test1_Sheet1";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect, "解析结果不应该为null");
        assertNotNull(plainSelect.getFromItem(), "FROM子句不应该为null");
    }

    @Test
    public void testParseSelectWithColumns() throws SQLException {
        String sql = "SELECT a, b, id FROM test1_Sheet1";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getSelectItems());
        assertTrue(plainSelect.getSelectItems().size() == 3, "应该解析出3个列");
    }

    @Test
    public void testParseSelectWithWhere() throws SQLException {
        String sql = "SELECT * FROM test1_Sheet1 WHERE id > 10";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getWhere(), "WHERE子句不应该为null");
    }

    @Test
    public void testParseSelectWithJoin() throws SQLException {
        String sql = "SELECT t1.a, t2.b FROM test1_Sheet1 t1 " +
                    "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getJoins());
        assertTrue(plainSelect.getJoins().size() == 1, "应该解析出1个JOIN");
    }

    @Test
    public void testParseSelectWithGroupBy() throws SQLException {
        String sql = "SELECT a, COUNT(*) FROM test1_Sheet1 GROUP BY a";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getGroupBy(), "GROUP BY子句不应该为null");
    }

    @Test
    public void testParseSelectWithOrderBy() throws SQLException {
        String sql = "SELECT * FROM test1_Sheet1 ORDER BY id DESC";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getOrderByElements(), "ORDER BY子句不应该为null");
    }

    @Test
    public void testParseSelectWithLimit() throws SQLException {
        String sql = "SELECT * FROM test1_Sheet1 LIMIT 10";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getLimit(), "LIMIT子句不应该为null");
    }

    @Test
    public void testParseSelectWithLimitOffset() throws SQLException {
        String sql = "SELECT * FROM test1_Sheet1 LIMIT 10 OFFSET 5";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getLimit());
    }

    @Test
    public void testParseSelectWithAggregateFunctions() throws SQLException {
        String sql = "SELECT COUNT(*), SUM(id), AVG(age), MAX(salary), MIN(salary) " +
                    "FROM test1_Sheet1";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getSelectItems());
    }

    @Test
    public void testParseSelectWithHaving() throws SQLException {
        String sql = "SELECT a, COUNT(*) FROM test1_Sheet1 " +
                    "GROUP BY a HAVING COUNT(*) > 1";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getHaving(), "HAVING子句不应该为null");
    }

    @Test
    public void testParseUnderscoreTableName() throws SQLException {
        String sql = "SELECT * FROM test1_Sheet1";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        // 验证表名格式（下划线分隔）
        assertNotNull(plainSelect.getFromItem());
    }

    @Test
    public void testParseComplexQuery() throws SQLException {
        String sql = "SELECT t1.a, t2.b, COUNT(*) AS cnt " +
                    "FROM test1_Sheet1 t1 " +
                    "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
                    "WHERE t1.id > 10 " +
                    "GROUP BY t1.a, t2.b " +
                    "HAVING COUNT(*) > 5 " +
                    "ORDER BY cnt DESC " +
                    "LIMIT 20";
        PlainSelect plainSelect = parser.parse(sql);
        
        assertNotNull(plainSelect);
        assertNotNull(plainSelect.getFromItem());
        assertNotNull(plainSelect.getJoins());
        assertNotNull(plainSelect.getWhere());
        assertNotNull(plainSelect.getGroupBy());
        assertNotNull(plainSelect.getHaving());
        assertNotNull(plainSelect.getOrderByElements());
        assertNotNull(plainSelect.getLimit());
    }

    @Test
    public void testParseNullSql() {
        assertThrows(SQLException.class, () -> {
            parser.parse(null);
        }, "null SQL应该抛出异常");
    }

    @Test
    public void testParseEmptySql() {
        assertThrows(SQLException.class, () -> {
            parser.parse("");
        }, "空SQL应该抛出异常");
    }

    @Test
    public void testParseWhitespaceSql() {
        assertThrows(SQLException.class, () -> {
            parser.parse("   ");
        }, "空白SQL应该抛出异常");
    }

    @Test
    public void testParseInvalidSql() {
        assertThrows(SQLException.class, () -> {
            parser.parse("INVALID SQL STATEMENT");
        }, "无效SQL应该抛出异常");
    }

    @Test
    public void testParseNonSelectStatement() {
        assertThrows(SQLException.class, () -> {
            parser.parse("INSERT INTO test1_Sheet1 VALUES (1, 'value')");
        }, "非SELECT语句应该抛出异常");
    }
}

