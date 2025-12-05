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
package io.github.daichangya.xlsql.engine.plan;

import io.github.daichangya.xlsql.engine.model.AggregateFunction;
import io.github.daichangya.xlsql.engine.model.AggregateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QueryPlanTest - 查询计划单元测试
 * 
 * <p>测试QueryPlan类的各种功能，包括查询要素的设置和获取。</p>
 * 
 * @author rzy
 */
public class QueryPlanTest {
    
    private QueryPlan plan;
    
    @BeforeEach
    public void setUp() {
        plan = new QueryPlan();
    }
    
    @Test
    public void testCreateQueryPlan() {
        assertNotNull(plan);
        assertNotNull(plan.getSelectColumns());
        assertNotNull(plan.getAggregateFunctions());
        assertNotNull(plan.getJoins());
        assertNotNull(plan.getGroupByColumns());
        assertNotNull(plan.getOrderBy());
        assertFalse(plan.hasAggregation());
        assertFalse(plan.hasGroupBy());
    }
    
    @Test
    public void testSelectColumns() {
        List<String> columns = Arrays.asList("id", "name", "age");
        plan.setSelectColumns(columns);
        
        assertEquals(3, plan.getSelectColumns().size());
        assertEquals("id", plan.getSelectColumns().get(0));
        assertEquals("name", plan.getSelectColumns().get(1));
        assertEquals("age", plan.getSelectColumns().get(2));
    }
    
    @Test
    public void testSelectColumnsWithNull() {
        plan.setSelectColumns(null);
        assertNotNull(plan.getSelectColumns());
        assertTrue(plan.getSelectColumns().isEmpty());
    }
    
    @Test
    public void testAggregateFunctions() {
        AggregateFunction countFunc = new AggregateFunction(AggregateType.COUNT, "*");
        AggregateFunction sumFunc = new AggregateFunction(AggregateType.SUM, "salary");
        
        plan.addAggregateFunction(countFunc);
        plan.addAggregateFunction(sumFunc);
        
        assertTrue(plan.hasAggregation());
        assertEquals(2, plan.getAggregateFunctions().size());
        assertEquals(AggregateType.COUNT, plan.getAggregateFunctions().get(0).getType());
        assertEquals(AggregateType.SUM, plan.getAggregateFunctions().get(1).getType());
    }
    
    @Test
    public void testSetAggregateFunctions() {
        List<AggregateFunction> funcs = new ArrayList<>();
        funcs.add(new AggregateFunction(AggregateType.COUNT, "*"));
        funcs.add(new AggregateFunction(AggregateType.AVG, "age"));
        
        plan.setAggregateFunctions(funcs);
        
        assertTrue(plan.hasAggregation());
        assertEquals(2, plan.getAggregateFunctions().size());
    }
    
    @Test
    public void testSetAggregateFunctionsWithNull() {
        plan.setAggregateFunctions(null);
        assertNotNull(plan.getAggregateFunctions());
        assertTrue(plan.getAggregateFunctions().isEmpty());
        assertFalse(plan.hasAggregation());
    }
    
    @Test
    public void testAddAggregateFunctionWithNull() {
        plan.addAggregateFunction(null);
        assertFalse(plan.hasAggregation());
        assertEquals(0, plan.getAggregateFunctions().size());
    }
    
    @Test
    public void testMainTable() {
        TableInfo table = new TableInfo("test1", "Sheet1", null);
        plan.setMainTable(table);
        
        assertNotNull(plan.getMainTable());
        assertEquals("test1", plan.getMainTable().getWorkbook());
        assertEquals("Sheet1", plan.getMainTable().getSheet());
    }
    
    @Test
    public void testJoins() {
        TableInfo table1 = new TableInfo("test1", "Sheet1", "t1");
        TableInfo table2 = new TableInfo("test2", "Sheet1", "t2");
        JoinCondition condition = new JoinCondition("t1.id", "t2.id", "=");
        JoinInfo join = new JoinInfo(io.github.daichangya.xlsql.engine.model.JoinType.INNER, table2, condition);
        
        plan.addJoin(join);
        
        assertEquals(1, plan.getJoins().size());
        assertEquals(io.github.daichangya.xlsql.engine.model.JoinType.INNER, plan.getJoins().get(0).getType());
    }
    
    @Test
    public void testSetJoins() {
        List<JoinInfo> joins = new ArrayList<>();
        TableInfo table2 = new TableInfo("test2", "Sheet1", "t2");
        JoinCondition condition = new JoinCondition("t1.id", "t2.id", "=");
        joins.add(new JoinInfo(io.github.daichangya.xlsql.engine.model.JoinType.LEFT, table2, condition));
        
        plan.setJoins(joins);
        
        assertEquals(1, plan.getJoins().size());
        assertEquals(io.github.daichangya.xlsql.engine.model.JoinType.LEFT, plan.getJoins().get(0).getType());
    }
    
    @Test
    public void testSetJoinsWithNull() {
        plan.setJoins(null);
        assertNotNull(plan.getJoins());
        assertTrue(plan.getJoins().isEmpty());
    }
    
    @Test
    public void testAddJoinWithNull() {
        plan.addJoin(null);
        assertEquals(0, plan.getJoins().size());
    }
    
    @Test
    public void testWhereClause() {
        WhereCondition where = new WhereCondition("age", ">", "30");
        plan.setWhereClause(where);
        
        assertNotNull(plan.getWhereClause());
        assertEquals("age", plan.getWhereClause().getLeftOperand());
        assertEquals(">", plan.getWhereClause().getOperator());
        assertEquals("30", plan.getWhereClause().getRightOperand());
    }
    
    @Test
    public void testGroupByColumns() {
        plan.addGroupByColumn("dept_id");
        plan.addGroupByColumn("status");
        
        assertTrue(plan.hasGroupBy());
        assertEquals(2, plan.getGroupByColumns().size());
        assertEquals("dept_id", plan.getGroupByColumns().get(0));
        assertEquals("status", plan.getGroupByColumns().get(1));
    }
    
    @Test
    public void testSetGroupByColumns() {
        List<String> groupBy = Arrays.asList("dept_id", "status");
        plan.setGroupByColumns(groupBy);
        
        assertTrue(plan.hasGroupBy());
        assertEquals(2, plan.getGroupByColumns().size());
    }
    
    @Test
    public void testSetGroupByColumnsWithNull() {
        plan.setGroupByColumns(null);
        assertNotNull(plan.getGroupByColumns());
        assertTrue(plan.getGroupByColumns().isEmpty());
        assertFalse(plan.hasGroupBy());
    }
    
    @Test
    public void testAddGroupByColumnWithNull() {
        plan.addGroupByColumn(null);
        plan.addGroupByColumn("");
        assertFalse(plan.hasGroupBy());
        assertEquals(0, plan.getGroupByColumns().size());
    }
    
    @Test
    public void testHavingClause() {
        AggregateFunction countFunc = new AggregateFunction(AggregateType.COUNT, "*");
        WhereCondition having = new WhereCondition(countFunc, ">", "10");
        plan.setHavingClause(having);
        
        assertNotNull(plan.getHavingClause());
        assertTrue(plan.getHavingClause().isAggregate());
    }
    
    @Test
    public void testOrderBy() {
        OrderByItem item1 = new OrderByItem("age", OrderByItem.SortDirection.ASC);
        OrderByItem item2 = new OrderByItem("salary", OrderByItem.SortDirection.DESC);
        
        plan.addOrderBy(item1);
        plan.addOrderBy(item2);
        
        assertEquals(2, plan.getOrderBy().size());
        assertEquals("age", plan.getOrderBy().get(0).getColumn());
        assertTrue(plan.getOrderBy().get(0).isAscending());
        assertEquals("salary", plan.getOrderBy().get(1).getColumn());
        assertTrue(plan.getOrderBy().get(1).isDescending());
    }
    
    @Test
    public void testSetOrderBy() {
        List<OrderByItem> orderBy = new ArrayList<>();
        orderBy.add(new OrderByItem("age"));
        orderBy.add(new OrderByItem("name", OrderByItem.SortDirection.DESC));
        
        plan.setOrderBy(orderBy);
        
        assertEquals(2, plan.getOrderBy().size());
    }
    
    @Test
    public void testSetOrderByWithNull() {
        plan.setOrderBy(null);
        assertNotNull(plan.getOrderBy());
        assertTrue(plan.getOrderBy().isEmpty());
    }
    
    @Test
    public void testAddOrderByWithNull() {
        plan.addOrderBy(null);
        assertEquals(0, plan.getOrderBy().size());
    }
    
    @Test
    public void testLimit() {
        plan.setLimit(10);
        assertEquals(Integer.valueOf(10), plan.getLimit());
    }
    
    @Test
    public void testOffset() {
        plan.setOffset(5);
        assertEquals(Integer.valueOf(5), plan.getOffset());
    }
    
    @Test
    public void testLimitAndOffset() {
        plan.setLimit(10);
        plan.setOffset(5);
        
        assertEquals(Integer.valueOf(10), plan.getLimit());
        assertEquals(Integer.valueOf(5), plan.getOffset());
    }
    
    @Test
    public void testComplexQueryPlan() {
        // 构建一个复杂的查询计划
        plan.setSelectColumns(Arrays.asList("id", "name", "age"));
        
        AggregateFunction countFunc = new AggregateFunction(AggregateType.COUNT, "*", false, "total");
        plan.addAggregateFunction(countFunc);
        
        TableInfo mainTable = new TableInfo("test1", "Sheet1", "t1");
        plan.setMainTable(mainTable);
        
        TableInfo joinTable = new TableInfo("test2", "Sheet1", "t2");
        JoinCondition condition = new JoinCondition("t1.id", "t2.id", "=");
        JoinInfo join = new JoinInfo(io.github.daichangya.xlsql.engine.model.JoinType.INNER, joinTable, condition);
        plan.addJoin(join);
        
        WhereCondition where = new WhereCondition("age", ">", "30");
        plan.setWhereClause(where);
        
        plan.addGroupByColumn("dept_id");
        
        AggregateFunction sumFunc = new AggregateFunction(AggregateType.SUM, "salary");
        WhereCondition having = new WhereCondition(sumFunc, ">", "10000");
        plan.setHavingClause(having);
        
        plan.addOrderBy(new OrderByItem("age", OrderByItem.SortDirection.DESC));
        
        plan.setLimit(10);
        plan.setOffset(5);
        
        // 验证所有设置
        assertEquals(3, plan.getSelectColumns().size());
        assertTrue(plan.hasAggregation());
        assertEquals(1, plan.getAggregateFunctions().size());
        assertNotNull(plan.getMainTable());
        assertEquals(1, plan.getJoins().size());
        assertNotNull(plan.getWhereClause());
        assertTrue(plan.hasGroupBy());
        assertNotNull(plan.getHavingClause());
        assertEquals(1, plan.getOrderBy().size());
        assertEquals(Integer.valueOf(10), plan.getLimit());
        assertEquals(Integer.valueOf(5), plan.getOffset());
    }
}

