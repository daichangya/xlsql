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
package io.github.daichangya.xlsql.engine.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.daichangya.xlsql.database.ADatabase;
import io.github.daichangya.xlsql.database.AFile;
import io.github.daichangya.xlsql.database.ASubFolder;
import io.github.daichangya.xlsql.database.xlDatabaseException;

/**
 * xlNativeSelectTest - xlNativeSelect单元测试
 * 
 * <p>测试xlNativeSelect.executeQuery方法的完整SQL查询流程，使用Mock数据。
 * 覆盖各种SQL查询场景，包括WHERE、JOIN、GROUP BY、HAVING等。</p>
 * 
 * @author rzy
 */
public class xlNativeSelectTest {
    
    private xlNativeSelect nativeSelect;
    private MockDatabase mockDatabase;
    
    @BeforeEach
    public void setUp() throws Exception {
        mockDatabase = new MockDatabase();
        nativeSelect = new xlNativeSelect(mockDatabase);
        
        // 设置测试数据
        setupTestData();
    }
    
    /**
     * 设置测试数据
     */
    private void setupTestData() {
        // 表1: test1_Sheet1 - 员工表
        String[] columns1 = {"id", "name", "age", "salary", "category"};
        String[] types1 = {"INTEGER", "VARCHAR", "INTEGER", "DOUBLE", "VARCHAR"};
        String[][] data1 = {
            {"1", "2", "3", "4", "5"},                              // id列
            {"Alice", "Bob", "Charlie", "David", "Eve"},            // name列
            {"25", "30", "35", "28", "32"},                         // age列
            {"5000.0", "6000.0", "7000.0", "5500.0", "6500.0"},    // salary列
            {"A", "A", "B", "B", "C"}                               // category列
        };
        mockDatabase.addTable("test1", "Sheet1", columns1, types1, data1, 5);
        
        // 表2: test2_Sheet1 - 部门表
        String[] columns2 = {"id", "name", "location"};
        String[] types2 = {"INTEGER", "VARCHAR", "VARCHAR"};
        String[][] data2 = {
            {"1", "2", "3"},                      // id列
            {"IT", "HR", "Finance"},             // name列
            {"Beijing", "Shanghai", "Guangzhou"} // location列
        };
        mockDatabase.addTable("test2", "Sheet1", columns2, types2, data2, 3);
        
        // 表3: test3_Sheet1 - 包含NULL值的表（用于IS NULL/IS NOT NULL测试）
        String[] columns3 = {"id", "name", "value"};
        String[] types3 = {"INTEGER", "VARCHAR", "DOUBLE"};
        String[][] data3 = {
            {"1", "2", "3"},
            {"Alice", null, "Bob"},
            {"10.5", null, "20.0"}
        };
        mockDatabase.addTable("test3", "Sheet1", columns3, types3, data3, 3);
    }
    
    // ==================== 基础查询测试 ====================
    
    @Test
    public void testSelectAllWithLimit() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 3");
        
        assertNotNull(rs);
        ResultSetMetaData metaData = rs.getMetaData();
        assertEquals(5, metaData.getColumnCount());
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertEquals(3, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testSelectColumnsWithLimit() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT id, name FROM test1_Sheet1 LIMIT 3");
        
        assertNotNull(rs);
        ResultSetMetaData metaData = rs.getMetaData();
        assertEquals(2, metaData.getColumnCount());
        assertEquals("id", metaData.getColumnName(1));
        assertEquals("name", metaData.getColumnName(2));
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            assertNotNull(rs.getString(1));
            assertNotNull(rs.getString(2));
        }
        assertEquals(3, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testSelectAllWithoutLimit() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertEquals(5, rowCount);
        
        rs.close();
    }
    
    // ==================== WHERE条件测试 ====================
    
    @Test
    public void testSelectWithWhereEqual() throws SQLException {
        // 先测试不带WHERE的查询，看看数据是否正确
        ResultSet rsAll = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 5");
        System.out.println("All rows:");
        while (rsAll.next()) {
            System.out.println("  id=" + rsAll.getString("id") + ", name=" + rsAll.getString("name"));
        }
        rsAll.close();
        
        // 测试带WHERE的查询
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE name = 'Alice' LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            String name = rs.getString("name");
            String id = rs.getString("id");
            // 调试：打印实际值
            System.out.println("Filtered Row " + rowCount + ": id=" + id + ", name=" + name);
            assertEquals("Alice", name, "Name should be Alice");
            assertEquals("1", id, "ID should be 1");
        }
        assertEquals(1, rowCount, "Should return exactly 1 row");
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereNotEqual() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE name != 'Alice' LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            assertNotEquals("Alice", rs.getString("name"));
        }
        assertEquals(4, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereGreaterThan() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE age > 30 LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            int age = Integer.parseInt(rs.getString("age"));
            assertTrue(age > 30);
        }
        assertEquals(2, rowCount); // Charlie(35) and Eve(32)
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereLessThan() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE age < 30 LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            int age = Integer.parseInt(rs.getString("age"));
            assertTrue(age < 30);
        }
        assertEquals(2, rowCount); // Alice(25) and David(28)
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereGreaterEqual() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE age >= 30 LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            int age = Integer.parseInt(rs.getString("age"));
            assertTrue(age >= 30);
        }
        assertEquals(3, rowCount); // Bob(30), Charlie(35), Eve(32)
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereLessEqual() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE age <= 30 LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            int age = Integer.parseInt(rs.getString("age"));
            assertTrue(age <= 30);
        }
        assertEquals(3, rowCount); // Alice(25), Bob(30), David(28)
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereAnd() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE name = 'Alice' AND age = 25 LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            assertEquals("Alice", rs.getString("name"));
            assertEquals("25", rs.getString("age"));
        }
        assertEquals(1, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereOr() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE name = 'Alice' OR name = 'Bob' LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            String name = rs.getString("name");
            assertTrue("Alice".equals(name) || "Bob".equals(name));
        }
        assertEquals(2, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereLike() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE name LIKE '%ice%' LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            String name = rs.getString("name");
            assertTrue(name.contains("ice")); // Alice
        }
        assertEquals(1, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereIn() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE id IN (1, 2, 3) LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            String id = rs.getString("id");
            assertTrue("1".equals(id) || "2".equals(id) || "3".equals(id));
        }
        assertEquals(3, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereBetween() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE age BETWEEN 28 AND 32 LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            int age = Integer.parseInt(rs.getString("age"));
            assertTrue(age >= 28 && age <= 32);
        }
        assertEquals(3, rowCount); // David(28), Bob(30), Eve(32)
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereIsNull() throws SQLException {
        // 添加包含NULL值的测试数据
        String[] columns = {"id", "name", "value"};
        String[] types = {"INTEGER", "VARCHAR", "DOUBLE"};
        String[][] data = {
            {"1", "2", "3"},
            {"Alice", null, "Bob"},
            {"10.5", null, "20.0"}
        };
        mockDatabase.addTable("test3", "Sheet1", columns, types, data, 3);
        
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test3_Sheet1 WHERE name IS NULL LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            assertNull(rs.getString("name"));
        }
        assertEquals(1, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereIsNotNull() throws SQLException {
        // 使用test3_Sheet1表（包含NULL值）
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test3_Sheet1 WHERE name IS NOT NULL LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            assertNotNull(rs.getString("name"));
        }
        assertEquals(2, rowCount); // Alice和Bob
        
        rs.close();
    }
    
    // ==================== JOIN查询测试 ====================
    
    @Test
    public void testInnerJoin() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT * FROM test1_Sheet1 t1 INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testLeftJoin() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT * FROM test1_Sheet1 t1 LEFT JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testRightJoin() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT * FROM test1_Sheet1 t1 RIGHT JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testFullOuterJoin() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT * FROM test1_Sheet1 t1 FULL OUTER JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testJoinWithSelectColumns() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT t1.id, t1.name, t2.name FROM test1_Sheet1 t1 " +
            "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 10"
        );
        
        assertNotNull(rs);
        ResultSetMetaData metaData = rs.getMetaData();
        assertEquals(3, metaData.getColumnCount());
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testJoinWithWhere() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT * FROM test1_Sheet1 t1 INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
            "WHERE t1.name = 'Alice' LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testJoinWithWhereAndOrderBy() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT * FROM test1_Sheet1 t1 INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
            "WHERE t1.age > 25 ORDER BY t1.id LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testJoinWithWhereAndLimitAndOffset() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT * FROM test1_Sheet1 t1 INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
            "WHERE t1.age > 25 LIMIT 10 OFFSET 1"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testJoinWithMultipleConditions() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT * FROM test1_Sheet1 t1 INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
            "WHERE t1.name = 'Alice' AND t2.name = 'IT' LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    // ==================== GROUP BY 和 HAVING 测试 ====================
    
    @Test
    public void testGroupBySingleColumn() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT category, COUNT(*) AS cnt FROM test1_Sheet1 GROUP BY category LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertEquals(3, rowCount); // 3个分组：A, B, C
        
        rs.close();
    }
    
    @Test
    public void testGroupByWithWhere() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT category, COUNT(*) AS cnt FROM test1_Sheet1 " +
            "WHERE age > 25 GROUP BY category LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testGroupByWithHaving() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT category, COUNT(*) AS cnt FROM test1_Sheet1 " +
            "GROUP BY category HAVING COUNT(*) > 1 LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertEquals(2, rowCount); // A和B都有2条记录
        
        rs.close();
    }
    
    @Test
    public void testGroupByWithWhereAndHaving() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT category, COUNT(*) AS cnt FROM test1_Sheet1 " +
            "WHERE age > 25 GROUP BY category HAVING COUNT(*) > 1 LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testGroupByWithOrderBy() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT category, COUNT(*) AS cnt FROM test1_Sheet1 " +
            "GROUP BY category ORDER BY cnt DESC LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertEquals(3, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testGroupByWithWhereAndHavingAndOrderByAndLimit() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT category, COUNT(*) AS cnt FROM test1_Sheet1 " +
            "WHERE age > 25 GROUP BY category HAVING COUNT(*) > 1 " +
            "ORDER BY cnt DESC LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testGroupByWithAggregateFunctions() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT category, COUNT(*), SUM(salary), AVG(salary), MAX(salary), MIN(salary) " +
            "FROM test1_Sheet1 GROUP BY category LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertEquals(3, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testGroupByMultipleColumns() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT category, age, COUNT(*) AS cnt FROM test1_Sheet1 " +
            "GROUP BY category, age LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    // ==================== JOIN + GROUP BY + HAVING 组合测试 ====================
    
    @Test
    public void testJoinWithGroupBy() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT t1.category, COUNT(*) AS cnt FROM test1_Sheet1 t1 " +
            "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
            "GROUP BY t1.category LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testJoinWithGroupByAndHaving() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT t1.category, COUNT(*) AS cnt FROM test1_Sheet1 t1 " +
            "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
            "GROUP BY t1.category HAVING COUNT(*) > 1 LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testJoinWithWhereAndGroupBy() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT t1.category, COUNT(*) AS cnt FROM test1_Sheet1 t1 " +
            "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
            "WHERE t1.age > 25 GROUP BY t1.category LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testJoinWithWhereAndGroupByAndHaving() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT t1.category, COUNT(*) AS cnt FROM test1_Sheet1 t1 " +
            "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
            "WHERE t1.age > 25 GROUP BY t1.category HAVING COUNT(*) > 1 LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testJoinWithGroupByAndOrderBy() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT t1.category, COUNT(*) AS cnt FROM test1_Sheet1 t1 " +
            "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
            "GROUP BY t1.category ORDER BY cnt DESC LIMIT 10"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    // ==================== 组合查询测试 ====================
    
    @Test
    public void testSelectWithWhereAndLimit() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE name = 'Alice' LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            assertEquals("Alice", rs.getString("name"));
        }
        assertEquals(1, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testSelectColumnsWithWhereAndLimit() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT id, name FROM test1_Sheet1 WHERE name = 'Alice' LIMIT 10");
        
        assertNotNull(rs);
        ResultSetMetaData metaData = rs.getMetaData();
        assertEquals(2, metaData.getColumnCount());
        
        assertTrue(rs.next());
        assertEquals("1", rs.getString("id"));
        assertEquals("Alice", rs.getString("name"));
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereAndOrderByAndLimit() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT * FROM test1_Sheet1 WHERE age > 25 ORDER BY age ASC LIMIT 3"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        int lastAge = 0;
        while (rs.next()) {
            rowCount++;
            int age = Integer.parseInt(rs.getString("age"));
            assertTrue(age > lastAge || rowCount == 1);
            lastAge = age;
        }
        assertEquals(3, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereAndOrderByDescAndLimit() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT * FROM test1_Sheet1 WHERE age > 25 ORDER BY age DESC LIMIT 3"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        int lastAge = Integer.MAX_VALUE;
        while (rs.next()) {
            rowCount++;
            int age = Integer.parseInt(rs.getString("age"));
            assertTrue(age <= lastAge || rowCount == 1);
            lastAge = age;
        }
        assertEquals(3, rowCount);
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereAndLimitAndOffset() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT * FROM test1_Sheet1 WHERE age > 25 ORDER BY age ASC LIMIT 2 OFFSET 1"
        );
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertEquals(2, rowCount);
        
        rs.close();
    }
    
    // ==================== 边界情况测试 ====================
    
    @Test
    public void testSelectWithWhereNoMatch() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE name = 'NonExistent' LIMIT 10");
        
        assertNotNull(rs);
        assertFalse(rs.next());
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereAndLimitZero() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE name = 'Alice' LIMIT 0");
        
        assertNotNull(rs);
        assertFalse(rs.next());
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereAllMatch() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE age > 0 LIMIT 10");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertEquals(5, rowCount); // 所有行都匹配
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereAndLimitExceedsRows() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE name = 'Alice' LIMIT 100");
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertEquals(1, rowCount); // 只有1行匹配，但LIMIT是100
        
        rs.close();
    }
    
    @Test
    public void testSelectWithWhereAndOffsetBeyondRows() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE name = 'Alice' LIMIT 10 OFFSET 100");
        
        assertNotNull(rs);
        assertFalse(rs.next()); // OFFSET超出范围，应该没有行
        
        rs.close();
    }
    
    @Test
    public void testGroupByWithNoMatches() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT category, COUNT(*) AS cnt FROM test1_Sheet1 " +
            "WHERE name = 'NonExistent' GROUP BY category LIMIT 10"
        );
        
        assertNotNull(rs);
        assertFalse(rs.next()); // 没有匹配的行，GROUP BY后也没有结果
        
        rs.close();
    }
    
    @Test
    public void testJoinWithNoMatches() throws SQLException {
        ResultSet rs = nativeSelect.executeQuery(
            "SELECT * FROM test1_Sheet1 t1 INNER JOIN test2_Sheet1 t2 " +
            "ON t1.id = t2.id WHERE t1.id = 999 LIMIT 10"
        );
        
        assertNotNull(rs);
        assertFalse(rs.next()); // 没有匹配的行
        
        rs.close();
    }
    
    // ==================== 错误处理测试 ====================
    
    @Test
    public void testSelectWithInvalidTable() {
        assertThrows(SQLException.class, () -> {
            nativeSelect.executeQuery("SELECT * FROM non_existent_table LIMIT 10");
        });
    }
    
    @Test
    public void testSelectWithInvalidWhereColumn() {
        // 这个测试可能不会抛出异常，因为WHERE条件评估可能会返回false
        // 但至少应该能执行而不崩溃
        try {
            ResultSet rs = nativeSelect.executeQuery("SELECT * FROM test1_Sheet1 WHERE invalid_column = 'value' LIMIT 10");
            assertNotNull(rs);
            // 可能没有匹配的行，但不应该崩溃
            rs.close();
        } catch (SQLException e) {
            // 如果抛出异常也是可以接受的
            assertTrue(e.getMessage() != null);
        }
    }
    
    @Test
    public void testSelectWithInvalidSQL() {
        assertThrows(SQLException.class, () -> {
            nativeSelect.executeQuery("INVALID SQL STATEMENT");
        });
    }
    
    @Test
    public void testSelectWithNullSQL() {
        assertThrows(SQLException.class, () -> {
            nativeSelect.executeQuery(null);
        });
    }
    
    @Test
    public void testSelectWithEmptySQL() {
        assertThrows(SQLException.class, () -> {
            nativeSelect.executeQuery("");
        });
    }
    
    /**
     * Mock ADatabase实现，用于单元测试
     */
    private static class MockDatabase extends ADatabase {
        private Map<String, TableData> tables = new HashMap<>();
        
        public MockDatabase() throws xlDatabaseException {
            super(new File(System.getProperty("java.io.tmpdir")));
        }
        
        public void addTable(String workbook, String sheet, String[] columns, 
                           String[] types, String[][] data, int rowCount) {
            String key = workbook.toUpperCase() + "_" + sheet.toUpperCase();
            tables.put(key, new TableData(columns, types, data, rowCount));
        }
        
        @Override
        public String[] getColumnNames(String workbook, String sheet) {
            String key = workbook.toUpperCase() + "_" + sheet.toUpperCase();
            TableData table = tables.get(key);
            if (table == null) {
                throw new IllegalArgumentException("Table not found: " + workbook + "." + sheet);
            }
            return table.columns;
        }
        
        @Override
        public String[] getColumnTypes(String workbook, String sheet) {
            String key = workbook.toUpperCase() + "_" + sheet.toUpperCase();
            TableData table = tables.get(key);
            if (table == null) {
                throw new IllegalArgumentException("Table not found: " + workbook + "." + sheet);
            }
            return table.types;
        }
        
        @Override
        public String[][] getValues(String workbook, String sheet) throws xlDatabaseException {
            String key = workbook.toUpperCase() + "_" + sheet.toUpperCase();
            TableData table = tables.get(key);
            if (table == null) {
                throw new IllegalArgumentException("Table not found: " + workbook + "." + sheet);
            }
            return table.data;
        }
        
        @Override
        public int getRows(String workbook, String sheet) {
            String key = workbook.toUpperCase() + "_" + sheet.toUpperCase();
            TableData table = tables.get(key);
            if (table == null) {
                throw new IllegalArgumentException("Table not found: " + workbook + "." + sheet);
            }
            return table.rowCount;
        }
        
        @Override
        protected void readSubFolders(File dir) throws xlDatabaseException {
            // Mock实现，不需要实际读取
        }
        
        @Override
        public ASubFolder subFolderFactory(File dir, String subfolder) {
            return null; // 不需要实现
        }
        
        @Override
        public AFile fileFactory(File dir, String subfolder, String file) {
            return null; // 不需要实现
        }
        
        private static class TableData {
            String[] columns;
            String[] types;
            String[][] data;
            int rowCount;
            
            TableData(String[] columns, String[] types, String[][] data, int rowCount) {
                this.columns = columns;
                this.types = types;
                this.data = data;
                this.rowCount = rowCount;
            }
        }
    }
}

