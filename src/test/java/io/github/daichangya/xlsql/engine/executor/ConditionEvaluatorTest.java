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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.daichangya.xlsql.engine.model.AggregateFunction;
import io.github.daichangya.xlsql.engine.model.AggregateType;
import io.github.daichangya.xlsql.engine.plan.TableInfo;
import io.github.daichangya.xlsql.engine.plan.WhereCondition;

/**
 * ConditionEvaluatorTest - 条件评估器单元测试
 * 
 * <p>测试WHERE和HAVING条件的评估功能。</p>
 * 
 * @author daichangya
 */
public class ConditionEvaluatorTest {

    private ConditionEvaluator evaluator;
    private Map<String, Integer> columnIndexMap;
    private List<TableInfo> tables;
    private String[] testRow;

    @BeforeEach
    public void setUp() {
        evaluator = new ConditionEvaluator();
        
        // 创建列索引映射
        columnIndexMap = new HashMap<>();
        columnIndexMap.put("ID", 0);
        columnIndexMap.put("NAME", 1);
        columnIndexMap.put("AGE", 2);
        columnIndexMap.put("SALARY", 3);
        
        tables = new ArrayList<>();
        
        // 创建测试行
        testRow = new String[]{"1", "Alice", "25", "5000.0"};
    }

    @Test
    public void testEqualCondition() throws SQLException {
        WhereCondition condition = new WhereCondition("ID", "=", "1");
        
        boolean result = evaluator.evaluate(condition, testRow, columnIndexMap, tables);
        assertTrue(result, "ID = 1 should be true");
        
        condition = new WhereCondition("ID", "=", "2");
        result = evaluator.evaluate(condition, testRow, columnIndexMap, tables);
        assertFalse(result, "ID = 2 should be false");
    }

    @Test
    public void testNotEqualCondition() throws SQLException {
        WhereCondition condition = new WhereCondition("ID", "!=", "2");
        
        boolean result = evaluator.evaluate(condition, testRow, columnIndexMap, tables);
        assertTrue(result, "ID != 2 should be true");
        
        condition = new WhereCondition("ID", "<>", "1");
        result = evaluator.evaluate(condition, testRow, columnIndexMap, tables);
        assertFalse(result, "ID <> 1 should be false");
    }

    @Test
    public void testGreaterThanCondition() throws SQLException {
        WhereCondition condition = new WhereCondition("AGE", ">", "20");
        
        boolean result = evaluator.evaluate(condition, testRow, columnIndexMap, tables);
        assertTrue(result, "AGE > 20 should be true");
        
        condition = new WhereCondition("AGE", ">", "30");
        result = evaluator.evaluate(condition, testRow, columnIndexMap, tables);
        assertFalse(result, "AGE > 30 should be false");
    }

    @Test
    public void testLessThanCondition() throws SQLException {
        WhereCondition condition = new WhereCondition("AGE", "<", "30");
        
        boolean result = evaluator.evaluate(condition, testRow, columnIndexMap, tables);
        assertTrue(result, "AGE < 30 should be true");
        
        condition = new WhereCondition("AGE", "<", "20");
        result = evaluator.evaluate(condition, testRow, columnIndexMap, tables);
        assertFalse(result, "AGE < 20 should be false");
    }

    @Test
    public void testAndCondition() throws SQLException {
        WhereCondition left = new WhereCondition("AGE", ">", "20");
        WhereCondition right = new WhereCondition("SALARY", ">", "4000");
        WhereCondition andCondition = new WhereCondition(left, "AND", right);
        
        boolean result = evaluator.evaluate(andCondition, testRow, columnIndexMap, tables);
        assertTrue(result, "AGE > 20 AND SALARY > 4000 should be true");
        
        right = new WhereCondition("SALARY", ">", "6000");
        andCondition = new WhereCondition(left, "AND", right);
        result = evaluator.evaluate(andCondition, testRow, columnIndexMap, tables);
        assertFalse(result, "AGE > 20 AND SALARY > 6000 should be false");
    }

    @Test
    public void testOrCondition() throws SQLException {
        WhereCondition left = new WhereCondition("AGE", ">", "30");
        WhereCondition right = new WhereCondition("SALARY", ">", "4000");
        WhereCondition orCondition = new WhereCondition(left, "OR", right);
        
        boolean result = evaluator.evaluate(orCondition, testRow, columnIndexMap, tables);
        assertTrue(result, "AGE > 30 OR SALARY > 4000 should be true");
        
        left = new WhereCondition("AGE", ">", "30");
        right = new WhereCondition("SALARY", ">", "6000");
        orCondition = new WhereCondition(left, "OR", right);
        result = evaluator.evaluate(orCondition, testRow, columnIndexMap, tables);
        assertFalse(result, "AGE > 30 OR SALARY > 6000 should be false");
    }

    @Test
    public void testHavingCondition() throws SQLException {
        AggregateFunction countFunc = new AggregateFunction(AggregateType.COUNT, "*", false, "cnt");
        WhereCondition havingCondition = new WhereCondition(countFunc, ">", "10");
        
        Map<String, Object> aggregateValues = new HashMap<>();
        aggregateValues.put("cnt", 15L);
        
        boolean result = evaluator.evaluateHaving(havingCondition, aggregateValues);
        assertTrue(result, "COUNT(*) > 10 should be true when count=15");
        
        aggregateValues.put("cnt", 5L);
        result = evaluator.evaluateHaving(havingCondition, aggregateValues);
        assertFalse(result, "COUNT(*) > 10 should be false when count=5");
    }

    @Test
    public void testNullCondition() throws SQLException {
        // 测试NULL条件
        String[] rowWithNull = new String[]{"1", null, "25", "5000.0"};
        
        WhereCondition condition = new WhereCondition("NAME", "=", "Alice");
        boolean result = evaluator.evaluate(condition, rowWithNull, columnIndexMap, tables);
        assertFalse(result, "NAME = 'Alice' should be false when NAME is NULL");
    }

    @Test
    public void testNullConditionReturnsTrue() throws SQLException {
        // 无条件应该返回true
        boolean result = evaluator.evaluate((WhereCondition) null, testRow, columnIndexMap, tables);
        assertTrue(result, "Null condition should return true");
    }
}

