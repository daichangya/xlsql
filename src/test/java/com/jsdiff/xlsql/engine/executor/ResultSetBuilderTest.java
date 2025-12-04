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
package com.jsdiff.xlsql.engine.executor;

import com.jsdiff.xlsql.engine.model.AggregateFunction;
import com.jsdiff.xlsql.engine.model.AggregateType;
import com.jsdiff.xlsql.engine.plan.OrderByItem;
import com.jsdiff.xlsql.engine.plan.QueryPlan;
import com.jsdiff.xlsql.engine.plan.TableInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ResultSetBuilderTest - 结果集构建器单元测试
 * 
 * <p>测试ResultSetBuilder类的各种功能，包括列选择、排序、LIMIT处理等。</p>
 * 
 * @author rzy
 */
public class ResultSetBuilderTest {
    
    private ResultSetBuilder builder;
    private QueryPlan plan;
    private TableInfo table;
    private Map<String, Integer> columnIndexMap;
    
    @BeforeEach
    public void setUp() {
        builder = new ResultSetBuilder();
        plan = new QueryPlan();
        
        // 创建测试表
        String[] columnNames = {"id", "name", "age", "salary"};
        String[] columnTypes = {"INTEGER", "VARCHAR", "INTEGER", "DOUBLE"};
        String[][] data = {
            {"1", "2", "3", "4", "5"},                    // id列
            {"Alice", "Bob", "Charlie", "David", "Eve"}, // name列
            {"25", "30", "35", "28", "32"},              // age列
            {"5000.0", "6000.0", "7000.0", "5500.0", "6500.0"} // salary列
        };
        
        table = new TableInfo("test1", "Sheet1", null);
        table.loadData(columnNames, columnTypes, data, 5);
        
        // 创建列索引映射
        columnIndexMap = new HashMap<>();
        columnIndexMap.put("ID", 0);
        columnIndexMap.put("NAME", 1);
        columnIndexMap.put("AGE", 2);
        columnIndexMap.put("SALARY", 3);
    }
    
    @Test
    public void testBuildWithSelectAll() throws SQLException {
        // SELECT * - 不设置selectColumns
        plan.setMainTable(table);
        
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rows.add(table.getRow(i));
        }
        
        ResultSet rs = builder.build(rows, plan, columnIndexMap, Arrays.asList(table));
        
        assertNotNull(rs);
        ResultSetMetaData metaData = rs.getMetaData();
        assertEquals(4, metaData.getColumnCount());
        assertEquals("id", metaData.getColumnName(1));
        assertEquals("name", metaData.getColumnName(2));
        assertEquals("age", metaData.getColumnName(3));
        assertEquals("salary", metaData.getColumnName(4));
        
        // 验证数据
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            assertNotNull(rs.getString(1));
            assertNotNull(rs.getString(2));
        }
        assertEquals(5, rowCount);
    }
    
    @Test
    public void testBuildWithSelectColumns() throws SQLException {
        // SELECT id, name
        plan.setSelectColumns(Arrays.asList("id", "name"));
        plan.setMainTable(table);
        
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rows.add(table.getRow(i));
        }
        
        ResultSet rs = builder.build(rows, plan, columnIndexMap, Arrays.asList(table));
        
        assertNotNull(rs);
        ResultSetMetaData metaData = rs.getMetaData();
        assertEquals(2, metaData.getColumnCount());
        assertEquals("id", metaData.getColumnName(1));
        assertEquals("name", metaData.getColumnName(2));
        
        // 验证第一行数据
        assertTrue(rs.next());
        assertEquals("1", rs.getString(1));
        assertEquals("Alice", rs.getString(2));
    }
    
    @Test
    public void testBuildWithOrderBy() throws SQLException {
        // SELECT * ORDER BY age ASC
        plan.setMainTable(table);
        plan.addOrderBy(new OrderByItem("age", OrderByItem.SortDirection.ASC));
        
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rows.add(table.getRow(i));
        }
        
        ResultSet rs = builder.build(rows, plan, columnIndexMap, Arrays.asList(table));
        
        assertNotNull(rs);
        
        // 验证排序：age应该是25, 28, 30, 32, 35
        assertTrue(rs.next());
        assertEquals("25", rs.getString("age")); // Alice, age=25
        
        assertTrue(rs.next());
        assertEquals("28", rs.getString("age")); // David, age=28
        
        assertTrue(rs.next());
        assertEquals("30", rs.getString("age")); // Bob, age=30
        
        assertTrue(rs.next());
        assertEquals("32", rs.getString("age")); // Eve, age=32
        
        assertTrue(rs.next());
        assertEquals("35", rs.getString("age")); // Charlie, age=35
    }
    
    @Test
    public void testBuildWithOrderByDesc() throws SQLException {
        // SELECT * ORDER BY age DESC
        plan.setMainTable(table);
        plan.addOrderBy(new OrderByItem("age", OrderByItem.SortDirection.DESC));
        
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rows.add(table.getRow(i));
        }
        
        ResultSet rs = builder.build(rows, plan, columnIndexMap, Arrays.asList(table));
        
        assertNotNull(rs);
        
        // 验证降序排序：age应该是35, 32, 30, 28, 25
        assertTrue(rs.next());
        assertEquals("35", rs.getString("age")); // Charlie, age=35
        
        assertTrue(rs.next());
        assertEquals("32", rs.getString("age")); // Eve, age=32
        
        assertTrue(rs.next());
        assertEquals("30", rs.getString("age")); // Bob, age=30
        
        assertTrue(rs.next());
        assertEquals("28", rs.getString("age")); // David, age=28
        
        assertTrue(rs.next());
        assertEquals("25", rs.getString("age")); // Alice, age=25
    }
    
    @Test
    public void testBuildWithLimit() throws SQLException {
        // SELECT * LIMIT 3
        plan.setMainTable(table);
        plan.setLimit(3);
        
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rows.add(table.getRow(i));
        }
        
        ResultSet rs = builder.build(rows, plan, columnIndexMap, Arrays.asList(table));
        
        assertNotNull(rs);
        
        // 验证只返回3行
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertEquals(3, rowCount);
    }
    
    @Test
    public void testBuildWithLimitAndOffset() throws SQLException {
        // SELECT * LIMIT 3 OFFSET 2
        plan.setMainTable(table);
        plan.setLimit(3);
        plan.setOffset(2);
        
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rows.add(table.getRow(i));
        }
        
        ResultSet rs = builder.build(rows, plan, columnIndexMap, Arrays.asList(table));
        
        assertNotNull(rs);
        
        // 验证跳过前2行，返回3行
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertEquals(3, rowCount);
    }
    
    @Test
    public void testBuildWithOrderByAndLimit() throws SQLException {
        // SELECT * ORDER BY age ASC LIMIT 2
        plan.setMainTable(table);
        plan.addOrderBy(new OrderByItem("age", OrderByItem.SortDirection.ASC));
        plan.setLimit(2);
        
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rows.add(table.getRow(i));
        }
        
        ResultSet rs = builder.build(rows, plan, columnIndexMap, Arrays.asList(table));
        
        assertNotNull(rs);
        
        // 验证排序后取前2行（age最小的2个）
        assertTrue(rs.next());
        assertEquals("25", rs.getString("age")); // Alice
        
        assertTrue(rs.next());
        assertEquals("28", rs.getString("age")); // David
        
        assertFalse(rs.next()); // 应该只有2行
    }
    
    @Test
    public void testBuildWithAggregateFunctions() throws SQLException {
        // SELECT COUNT(*), SUM(salary)
        AggregateFunction countFunc = new AggregateFunction(AggregateType.COUNT, "*", false, "total_count");
        AggregateFunction sumFunc = new AggregateFunction(AggregateType.SUM, "salary", false, "total_salary");
        
        plan.addAggregateFunction(countFunc);
        plan.addAggregateFunction(sumFunc);
        plan.setMainTable(table);
        
        // 聚合后的数据（只有一行）
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"5", "30000.0"}); // COUNT=5, SUM=30000.0
        
        ResultSet rs = builder.build(rows, plan, columnIndexMap, Arrays.asList(table));
        
        assertNotNull(rs);
        ResultSetMetaData metaData = rs.getMetaData();
        assertEquals(2, metaData.getColumnCount());
        assertEquals("total_count", metaData.getColumnName(1));
        assertEquals("total_salary", metaData.getColumnName(2));
        
        assertTrue(rs.next());
        assertEquals("5", rs.getString(1));
        assertEquals("30000.0", rs.getString(2));
    }
    
    @Test
    public void testBuildWithEmptyResultSet() throws SQLException {
        // SELECT * - 空结果集
        plan.setMainTable(table);
        
        List<String[]> rows = new ArrayList<>();
        
        ResultSet rs = builder.build(rows, plan, columnIndexMap, Arrays.asList(table));
        
        assertNotNull(rs);
        assertFalse(rs.next()); // 应该没有行
    }
    
    @Test
    public void testBuildWithLargeResultSet() throws SQLException {
        // SELECT * - 大数据集
        plan.setMainTable(table);
        
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            rows.add(new String[]{"id" + i, "name" + i, "age" + i, "salary" + i});
        }
        
        // 更新列索引映射以匹配新数据
        Map<String, Integer> largeColumnIndexMap = new HashMap<>();
        largeColumnIndexMap.put("ID", 0);
        largeColumnIndexMap.put("NAME", 1);
        largeColumnIndexMap.put("AGE", 2);
        largeColumnIndexMap.put("SALARY", 3);
        
        ResultSet rs = builder.build(rows, plan, largeColumnIndexMap, Arrays.asList(table));
        
        assertNotNull(rs);
        
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertEquals(1000, rowCount);
    }
    
    @Test
    public void testBuildWithOffsetBeyondSize() throws SQLException {
        // SELECT * LIMIT 10 OFFSET 100 (offset超出数据范围)
        plan.setMainTable(table);
        plan.setLimit(10);
        plan.setOffset(100);
        
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rows.add(table.getRow(i));
        }
        
        ResultSet rs = builder.build(rows, plan, columnIndexMap, Arrays.asList(table));
        
        assertNotNull(rs);
        assertFalse(rs.next()); // 应该没有行（offset超出范围）
    }
    
    @Test
    public void testBuildWithMultipleOrderBy() throws SQLException {
        // SELECT * ORDER BY age ASC, salary DESC
        plan.setMainTable(table);
        plan.addOrderBy(new OrderByItem("age", OrderByItem.SortDirection.ASC));
        plan.addOrderBy(new OrderByItem("salary", OrderByItem.SortDirection.DESC));
        
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rows.add(table.getRow(i));
        }
        
        ResultSet rs = builder.build(rows, plan, columnIndexMap, Arrays.asList(table));
        
        assertNotNull(rs);
        
        // 验证多列排序（先按age升序，age相同时按salary降序）
        // 由于age都不同，所以主要按age排序
        assertTrue(rs.next());
        assertEquals("25", rs.getString("age")); // Alice, age=25
        
        assertTrue(rs.next());
        assertEquals("28", rs.getString("age")); // David, age=28
    }
}

