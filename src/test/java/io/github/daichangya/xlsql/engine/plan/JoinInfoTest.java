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

import io.github.daichangya.xlsql.engine.model.JoinType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * JoinInfoTest - JOIN信息单元测试
 * 
 * <p>测试JoinInfo类的各种功能，包括JOIN类型、表信息和连接条件。</p>
 * 
 * @author rzy
 */
public class JoinInfoTest {
    
    private TableInfo table;
    private JoinCondition condition;
    
    @BeforeEach
    public void setUp() {
        table = new TableInfo("test2", "Sheet1", "t2");
        condition = new JoinCondition("t1.id", "t2.id", "=");
    }
    
    @Test
    public void testCreateInnerJoin() {
        JoinInfo join = new JoinInfo(JoinType.INNER, table, condition);
        
        assertNotNull(join);
        assertEquals(JoinType.INNER, join.getType());
        assertEquals(table, join.getTable());
        assertEquals(condition, join.getCondition());
    }
    
    @Test
    public void testCreateLeftJoin() {
        JoinInfo join = new JoinInfo(JoinType.LEFT, table, condition);
        
        assertEquals(JoinType.LEFT, join.getType());
    }
    
    @Test
    public void testCreateRightJoin() {
        JoinInfo join = new JoinInfo(JoinType.RIGHT, table, condition);
        
        assertEquals(JoinType.RIGHT, join.getType());
    }
    
    @Test
    public void testCreateFullOuterJoin() {
        JoinInfo join = new JoinInfo(JoinType.FULL_OUTER, table, condition);
        
        assertEquals(JoinType.FULL_OUTER, join.getType());
    }
    
    @Test
    public void testCreateJoinWithNullType() {
        assertThrows(NullPointerException.class, () -> {
            new JoinInfo(null, table, condition);
        });
    }
    
    @Test
    public void testCreateJoinWithNullTable() {
        assertThrows(NullPointerException.class, () -> {
            new JoinInfo(JoinType.INNER, null, condition);
        });
    }
    
    @Test
    public void testCreateJoinWithNullCondition() {
        assertThrows(NullPointerException.class, () -> {
            new JoinInfo(JoinType.INNER, table, null);
        });
    }
    
    @Test
    public void testGetTable() {
        JoinInfo join = new JoinInfo(JoinType.INNER, table, condition);
        
        TableInfo retrievedTable = join.getTable();
        assertNotNull(retrievedTable);
        assertEquals("test2", retrievedTable.getWorkbook());
        assertEquals("Sheet1", retrievedTable.getSheet());
    }
    
    @Test
    public void testGetCondition() {
        JoinInfo join = new JoinInfo(JoinType.INNER, table, condition);
        
        JoinCondition retrievedCondition = join.getCondition();
        assertNotNull(retrievedCondition);
        assertEquals("t1.id", retrievedCondition.getLeftColumn());
        assertEquals("t2.id", retrievedCondition.getRightColumn());
        assertEquals("=", retrievedCondition.getOperator());
    }
}

