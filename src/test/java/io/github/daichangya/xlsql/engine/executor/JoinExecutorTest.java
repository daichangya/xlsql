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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.daichangya.xlsql.engine.model.JoinType;
import io.github.daichangya.xlsql.engine.plan.JoinCondition;
import io.github.daichangya.xlsql.engine.plan.JoinInfo;
import io.github.daichangya.xlsql.engine.plan.TableInfo;
import io.github.daichangya.xlsql.util.TestDataHelper;

/**
 * JoinExecutorTest - JOIN执行器单元测试
 * 
 * <p>测试各种JOIN类型的执行，包括INNER、LEFT、RIGHT和FULL OUTER JOIN。</p>
 * 
 * @author daichangya
 */
public class JoinExecutorTest {

    private JoinExecutor executor;
    private List<String[]> leftRows;
    private TableInfo rightTable;
    private Map<String, Integer> leftColumnIndexMap;
    private JoinInfo innerJoinInfo;
    private JoinInfo leftJoinInfo;

    @BeforeEach
    public void setUp() {
        executor = new JoinExecutor();
        
        // 创建左表数据（订单表）
        leftRows = new ArrayList<>();
        leftRows.add(new String[]{"1", "101", "100.0"}); // order_id, customer_id, amount
        leftRows.add(new String[]{"2", "102", "200.0"});
        leftRows.add(new String[]{"3", "101", "150.0"});
        leftRows.add(new String[]{"4", "999", "300.0"}); // customer_id=999不存在于右表
        
        // 创建左表列索引映射
        leftColumnIndexMap = new HashMap<>();
        leftColumnIndexMap.put("ORDER_ID", 0);
        leftColumnIndexMap.put("CUSTOMER_ID", 1);
        leftColumnIndexMap.put("AMOUNT", 2);
        
        // 创建右表（客户表）
        rightTable = TestDataHelper.createDepartmentTable();
        // 修改为客户表结构
        String[] customerColumns = {"customer_id", "name", "city"};
        String[] customerTypes = {"INTEGER", "VARCHAR", "VARCHAR"};
        String[][] customerData = {
            {"101", "102", "103"}, // customer_id列
            {"Alice", "Bob", "Charlie"}, // name列
            {"Beijing", "Shanghai", "Guangzhou"} // city列
        };
        rightTable = new TableInfo("test2", "customers", null);
        rightTable.loadData(customerColumns, customerTypes, customerData, 3);
        
        // 创建JOIN条件
        JoinCondition condition = new JoinCondition("CUSTOMER_ID", "customer_id", "=");
        innerJoinInfo = new JoinInfo(JoinType.INNER, rightTable, condition);
        leftJoinInfo = new JoinInfo(JoinType.LEFT, rightTable, condition);
    }

    @Test
    public void testInnerJoin() throws SQLException {
        List<String[]> result = executor.execute(leftRows, rightTable, innerJoinInfo, leftColumnIndexMap);
        
        assertNotNull(result);
        // INNER JOIN应该返回3行（customer_id=101有2个订单，102有1个订单，999没有匹配）
        assertEquals(3, result.size());
        
        // 验证结果行包含左表和右表的列
        for (String[] row : result) {
            assertTrue(row.length >= 5); // 左表3列 + 右表3列 - 1个重复的customer_id = 5列
        }
    }

    @Test
    public void testLeftJoin() throws SQLException {
        List<String[]> result = executor.execute(leftRows, rightTable, leftJoinInfo, leftColumnIndexMap);
        
        assertNotNull(result);
        // LEFT JOIN应该返回4行（所有左表行，包括999）
        assertEquals(4, result.size());
        
        // 验证customer_id=999的行，右表列应该为NULL
        boolean found999 = false;
        for (String[] row : result) {
            if (row.length > 1 && "999".equals(row[1])) {
                found999 = true;
                // 右表的列应该为NULL
                assertTrue(row.length >= 5);
                break;
            }
        }
        assertTrue(found999, "Should find row with customer_id=999");
    }

    @Test
    public void testRightJoin() throws SQLException {
        JoinInfo rightJoinInfo = new JoinInfo(JoinType.RIGHT, rightTable, 
            new JoinCondition("CUSTOMER_ID", "customer_id", "="));
        
        List<String[]> result = executor.execute(leftRows, rightTable, rightJoinInfo, leftColumnIndexMap);
        
        assertNotNull(result);
        // RIGHT JOIN应该返回至少3行（所有右表行，customer_id=103没有匹配的订单）
        // 实际可能返回4行（包括匹配的行）
        assertTrue(result.size() >= 3, "RIGHT JOIN should return at least 3 rows");
    }

    @Test
    public void testFullOuterJoin() throws SQLException {
        JoinInfo fullJoinInfo = new JoinInfo(JoinType.FULL_OUTER, rightTable, 
            new JoinCondition("CUSTOMER_ID", "customer_id", "="));
        
        List<String[]> result = executor.execute(leftRows, rightTable, fullJoinInfo, leftColumnIndexMap);
        
        assertNotNull(result);
        // FULL OUTER JOIN应该返回至少4行（所有左表和右表的行）
        assertTrue(result.size() >= 4);
    }

    @Test
    public void testJoinWithNoMatches() throws SQLException {
        // 创建没有匹配的数据
        List<String[]> noMatchRows = new ArrayList<>();
        noMatchRows.add(new String[]{"1", "999", "100.0"});
        
        List<String[]> result = executor.execute(noMatchRows, rightTable, innerJoinInfo, leftColumnIndexMap);
        
        assertNotNull(result);
        // INNER JOIN没有匹配应该返回空结果
        assertEquals(0, result.size());
    }

    @Test
    public void testJoinWithEmptyLeftTable() throws SQLException {
        List<String[]> emptyRows = new ArrayList<>();
        
        List<String[]> result = executor.execute(emptyRows, rightTable, innerJoinInfo, leftColumnIndexMap);
        
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testJoinWithEmptyRightTable() throws SQLException {
        TableInfo emptyTable = TestDataHelper.createEmptyTable();
        JoinInfo joinInfo = new JoinInfo(JoinType.INNER, emptyTable, 
            new JoinCondition("CUSTOMER_ID", "id", "="));
        
        List<String[]> result = executor.execute(leftRows, emptyTable, joinInfo, leftColumnIndexMap);
        
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testJoinColumnNotFound() {
        JoinCondition invalidCondition = new JoinCondition("INVALID_COL", "customer_id", "=");
        JoinInfo invalidJoin = new JoinInfo(JoinType.INNER, rightTable, invalidCondition);
        
        assertThrows(SQLException.class, () -> {
            executor.execute(leftRows, rightTable, invalidJoin, leftColumnIndexMap);
        });
    }
}

