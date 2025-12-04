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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TableInfoTest - 表信息单元测试
 * 
 * <p>测试TableInfo类的各种功能，包括表信息创建、数据加载、列访问等。</p>
 * 
 * @author rzy
 */
public class TableInfoTest {
    
    private TableInfo table;
    
    @BeforeEach
    public void setUp() {
        table = new TableInfo("test1", "Sheet1", "t1");
    }
    
    @Test
    public void testCreateTableInfo() {
        assertNotNull(table);
        assertEquals("test1", table.getWorkbook());
        assertEquals("Sheet1", table.getSheet());
        assertEquals("t1", table.getAlias());
    }
    
    @Test
    public void testCreateTableInfoWithNullAlias() {
        TableInfo table2 = new TableInfo("test1", "Sheet1", null);
        assertEquals("test1", table2.getWorkbook());
        assertEquals("Sheet1", table2.getSheet());
        assertEquals("", table2.getAlias()); // null别名应该转换为空字符串
    }
    
    @Test
    public void testLoadData() {
        String[] columnNames = {"id", "name", "age"};
        String[] columnTypes = {"INTEGER", "VARCHAR", "INTEGER"};
        String[][] data = {
            {"1", "2", "3"},           // id列
            {"Alice", "Bob", "Charlie"}, // name列
            {"25", "30", "35"}        // age列
        };
        
        table.loadData(columnNames, columnTypes, data, 3);
        
        assertEquals(3, table.getRowCount());
        assertArrayEquals(columnNames, table.getColumnNames());
        assertArrayEquals(columnTypes, table.getColumnTypes());
        assertNotNull(table.getData());
    }
    
    @Test
    public void testGetColumnIndex() {
        String[] columnNames = {"id", "name", "age"};
        String[] columnTypes = {"INTEGER", "VARCHAR", "INTEGER"};
        String[][] data = {
            {"1", "2"}, {"Alice", "Bob"}, {"25", "30"}
        };
        
        table.loadData(columnNames, columnTypes, data, 2);
        
        assertEquals(0, table.getColumnIndex("id"));
        assertEquals(0, table.getColumnIndex("ID")); // 大小写不敏感
        assertEquals(1, table.getColumnIndex("name"));
        assertEquals(2, table.getColumnIndex("age"));
        assertEquals(-1, table.getColumnIndex("nonexistent"));
    }
    
    @Test
    public void testGetColumnIndexBeforeLoadData() {
        // 在加载数据之前，列索引映射应该为null
        assertEquals(-1, table.getColumnIndex("id"));
    }
    
    @Test
    public void testGetRow() {
        String[] columnNames = {"id", "name", "age"};
        String[] columnTypes = {"INTEGER", "VARCHAR", "INTEGER"};
        String[][] data = {
            {"1", "2", "3"},           // id列
            {"Alice", "Bob", "Charlie"}, // name列
            {"25", "30", "35"}        // age列
        };
        
        table.loadData(columnNames, columnTypes, data, 3);
        
        String[] row0 = table.getRow(0);
        assertNotNull(row0);
        assertEquals(3, row0.length);
        assertEquals("1", row0[0]);
        assertEquals("Alice", row0[1]);
        assertEquals("25", row0[2]);
        
        String[] row1 = table.getRow(1);
        assertNotNull(row1);
        assertEquals("2", row1[0]);
        assertEquals("Bob", row1[1]);
        assertEquals("30", row1[2]);
    }
    
    @Test
    public void testGetRowInvalidIndex() {
        String[] columnNames = {"id", "name"};
        String[] columnTypes = {"INTEGER", "VARCHAR"};
        String[][] data = {
            {"1", "2"}, {"Alice", "Bob"}
        };
        
        table.loadData(columnNames, columnTypes, data, 2);
        
        assertNull(table.getRow(-1)); // 负索引
        assertNull(table.getRow(2));  // 超出范围
        assertNull(table.getRow(100)); // 超出范围
    }
    
    @Test
    public void testGetRowBeforeLoadData() {
        // 在加载数据之前，getRow应该返回null
        assertNull(table.getRow(0));
    }
    
    @Test
    public void testGetFullName() {
        assertEquals("test1.Sheet1", table.getFullName());
    }
    
    @Test
    public void testGetFullNameWithSAWorkbook() {
        TableInfo table2 = new TableInfo("SA", "Sheet1", null);
        assertEquals("Sheet1", table2.getFullName()); // SA工作簿应该只显示工作表名
    }
    
    @Test
    public void testGetFullNameWithEmptyWorkbook() {
        TableInfo table2 = new TableInfo("", "Sheet1", null);
        assertEquals("Sheet1", table2.getFullName()); // 空工作簿应该只显示工作表名
    }
    
    @Test
    public void testGetColumnIndexMap() {
        String[] columnNames = {"id", "name", "age"};
        String[] columnTypes = {"INTEGER", "VARCHAR", "INTEGER"};
        String[][] data = {
            {"1", "2"}, {"Alice", "Bob"}, {"25", "30"}
        };
        
        table.loadData(columnNames, columnTypes, data, 2);
        
        assertNotNull(table.getColumnIndexMap());
        assertEquals(3, table.getColumnIndexMap().size());
        assertTrue(table.getColumnIndexMap().containsKey("ID"));
        assertTrue(table.getColumnIndexMap().containsKey("NAME"));
        assertTrue(table.getColumnIndexMap().containsKey("AGE"));
    }
    
    @Test
    public void testEmptyTable() {
        String[] columnNames = {"id", "name"};
        String[] columnTypes = {"INTEGER", "VARCHAR"};
        String[][] data = {
            {}, {} // 空数据
        };
        
        table.loadData(columnNames, columnTypes, data, 0);
        
        assertEquals(0, table.getRowCount());
        assertNull(table.getRow(0));
    }
}

