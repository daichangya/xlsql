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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JoinConditionTest - JOIN条件单元测试
 * 
 * <p>测试JoinCondition类的各种功能，包括列引用解析和条件构建。</p>
 * 
 * @author rzy
 */
public class JoinConditionTest {
    
    @Test
    public void testCreateJoinCondition() {
        JoinCondition condition = new JoinCondition("t1.id", "t2.id", "=");
        
        assertNotNull(condition);
        assertEquals("t1.id", condition.getLeftColumn());
        assertEquals("t2.id", condition.getRightColumn());
        assertEquals("=", condition.getOperator());
    }
    
    @Test
    public void testCreateJoinConditionWithNullOperator() {
        JoinCondition condition = new JoinCondition("t1.id", "t2.id", null);
        
        assertEquals("=", condition.getOperator()); // null操作符应该默认为"="
    }
    
    @Test
    public void testCreateJoinConditionWithWhitespace() {
        JoinCondition condition = new JoinCondition("  t1.id  ", "  t2.id  ", "  =  ");
        
        assertEquals("t1.id", condition.getLeftColumn()); // 应该去除空格
        assertEquals("t2.id", condition.getRightColumn());
        assertEquals("=", condition.getOperator());
    }
    
    @Test
    public void testCreateJoinConditionWithNullLeftColumn() {
        assertThrows(NullPointerException.class, () -> {
            new JoinCondition(null, "t2.id", "=");
        });
    }
    
    @Test
    public void testCreateJoinConditionWithNullRightColumn() {
        assertThrows(NullPointerException.class, () -> {
            new JoinCondition("t1.id", null, "=");
        });
    }
    
    @Test
    public void testParseColumnRefWithTableAlias() {
        String[] result = JoinCondition.parseColumnRef("t1.id");
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("t1", result[0]); // 表别名
        assertEquals("id", result[1]); // 列名
    }
    
    @Test
    public void testParseColumnRefWithoutTableAlias() {
        String[] result = JoinCondition.parseColumnRef("id");
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("", result[0]); // 无表别名
        assertEquals("id", result[1]); // 列名
    }
    
    @Test
    public void testParseColumnRefWithWhitespace() {
        String[] result = JoinCondition.parseColumnRef("  t1.id  ");
        
        assertNotNull(result);
        assertEquals("t1", result[0]);
        assertEquals("id", result[1]);
    }
    
    @Test
    public void testParseColumnRefWithNull() {
        String[] result = JoinCondition.parseColumnRef(null);
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("", result[0]);
        assertEquals("", result[1]);
    }
    
    @Test
    public void testParseColumnRefWithEmptyString() {
        String[] result = JoinCondition.parseColumnRef("");
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("", result[0]);
        assertEquals("", result[1]);
    }
    
    @Test
    public void testParseColumnRefWithMultipleDots() {
        // 测试包含多个点的情况（如 schema.table.column）
        String[] result = JoinCondition.parseColumnRef("schema.table.column");
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("schema", result[0]); // 只取第一个点之前的部分作为表别名
        assertEquals("table.column", result[1]); // 第一个点之后的所有内容作为列名
    }
}

