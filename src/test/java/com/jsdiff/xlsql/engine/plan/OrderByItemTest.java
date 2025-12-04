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
package com.jsdiff.xlsql.engine.plan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderByItemTest - ORDER BY项单元测试
 * 
 * <p>测试OrderByItem类的各种功能，包括列名和排序方向。</p>
 * 
 * @author rzy
 */
public class OrderByItemTest {
    
    @Test
    public void testCreateOrderByItemWithDirection() {
        OrderByItem item = new OrderByItem("age", OrderByItem.SortDirection.ASC);
        
        assertNotNull(item);
        assertEquals("age", item.getColumn());
        assertEquals(OrderByItem.SortDirection.ASC, item.getDirection());
        assertTrue(item.isAscending());
        assertFalse(item.isDescending());
    }
    
    @Test
    public void testCreateOrderByItemWithDesc() {
        OrderByItem item = new OrderByItem("age", OrderByItem.SortDirection.DESC);
        
        assertEquals("age", item.getColumn());
        assertEquals(OrderByItem.SortDirection.DESC, item.getDirection());
        assertFalse(item.isAscending());
        assertTrue(item.isDescending());
    }
    
    @Test
    public void testCreateOrderByItemWithoutDirection() {
        OrderByItem item = new OrderByItem("age");
        
        assertEquals("age", item.getColumn());
        assertEquals(OrderByItem.SortDirection.ASC, item.getDirection()); // 默认ASC
        assertTrue(item.isAscending());
    }
    
    @Test
    public void testCreateOrderByItemWithNullDirection() {
        OrderByItem item = new OrderByItem("age", null);
        
        assertEquals("age", item.getColumn());
        assertEquals(OrderByItem.SortDirection.ASC, item.getDirection()); // null应该默认为ASC
        assertTrue(item.isAscending());
    }
    
    @Test
    public void testCreateOrderByItemWithNullColumn() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderByItem(null);
        });
    }
    
    @Test
    public void testCreateOrderByItemWithEmptyColumn() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderByItem("");
        });
    }
    
    @Test
    public void testGetColumn() {
        OrderByItem item = new OrderByItem("salary", OrderByItem.SortDirection.DESC);
        
        assertEquals("salary", item.getColumn());
    }
    
    @Test
    public void testGetDirection() {
        OrderByItem item1 = new OrderByItem("age", OrderByItem.SortDirection.ASC);
        assertEquals(OrderByItem.SortDirection.ASC, item1.getDirection());
        
        OrderByItem item2 = new OrderByItem("age", OrderByItem.SortDirection.DESC);
        assertEquals(OrderByItem.SortDirection.DESC, item2.getDirection());
    }
    
    @Test
    public void testIsAscending() {
        OrderByItem item1 = new OrderByItem("age", OrderByItem.SortDirection.ASC);
        assertTrue(item1.isAscending());
        
        OrderByItem item2 = new OrderByItem("age", OrderByItem.SortDirection.DESC);
        assertFalse(item2.isAscending());
    }
    
    @Test
    public void testIsDescending() {
        OrderByItem item1 = new OrderByItem("age", OrderByItem.SortDirection.ASC);
        assertFalse(item1.isDescending());
        
        OrderByItem item2 = new OrderByItem("age", OrderByItem.SortDirection.DESC);
        assertTrue(item2.isDescending());
    }
}

