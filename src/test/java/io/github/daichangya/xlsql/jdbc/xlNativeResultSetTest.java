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
package io.github.daichangya.xlsql.jdbc;

import io.github.daichangya.xlsql.engine.resultset.xlNativeResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * xlNativeResultSetTest - ResultSet单元测试
 * 
 * <p>测试xlNativeResultSet类的各种功能，包括遍历、数据类型转换、元数据访问等。</p>
 * 
 * @author rzy
 */
public class xlNativeResultSetTest {
    
    private xlNativeResultSet resultSet;
    private String[] columnNames;
    private String[] columnTypes;
    private String[][] values;
    
    @BeforeEach
    public void setUp() {
        columnNames = new String[]{"id", "name", "age", "salary"};
        columnTypes = new String[]{"INTEGER", "VARCHAR", "INTEGER", "DOUBLE"};
        
        // 数据矩阵：String[列][行]
        values = new String[][]{
            {"1", "2", "3", "4", "5"},                    // id列
            {"Alice", "Bob", "Charlie", "David", "Eve"}, // name列
            {"25", "30", "35", "28", "32"},              // age列
            {"5000.0", "6000.0", "7000.0", "5500.0", "6500.0"} // salary列
        };
        
        resultSet = new xlNativeResultSet(columnNames, columnTypes, values, 5);
    }
    
    @Test
    public void testCreateResultSet() throws SQLException {
        assertNotNull(resultSet);
        assertFalse(resultSet.isClosed());
    }
    
    @Test
    public void testNext() throws SQLException {
        // 初始位置在首行之前
        assertTrue(resultSet.next()); // 移动到第一行
        assertTrue(resultSet.next()); // 移动到第二行
        assertTrue(resultSet.next()); // 移动到第三行
        assertTrue(resultSet.next()); // 移动到第四行
        assertTrue(resultSet.next()); // 移动到第五行
        assertFalse(resultSet.next()); // 没有更多行了
    }
    
    @Test
    public void testGetStringByIndex() throws SQLException {
        assertTrue(resultSet.next());
        
        assertEquals("1", resultSet.getString(1));
        assertEquals("Alice", resultSet.getString(2));
        assertEquals("25", resultSet.getString(3));
        assertEquals("5000.0", resultSet.getString(4));
    }
    
    @Test
    public void testGetStringByLabel() throws SQLException {
        assertTrue(resultSet.next());
        
        assertEquals("1", resultSet.getString("id"));
        assertEquals("Alice", resultSet.getString("name"));
        assertEquals("25", resultSet.getString("age"));
        assertEquals("5000.0", resultSet.getString("salary"));
    }
    
    @Test
    public void testGetStringCaseInsensitive() throws SQLException {
        assertTrue(resultSet.next());
        
        // 列名应该大小写不敏感
        assertEquals("1", resultSet.getString("ID"));
        assertEquals("Alice", resultSet.getString("NAME"));
        assertEquals("25", resultSet.getString("Age"));
    }
    
    @Test
    public void testFindColumn() throws SQLException {
        assertEquals(1, resultSet.findColumn("id"));
        assertEquals(2, resultSet.findColumn("name"));
        assertEquals(3, resultSet.findColumn("age"));
        assertEquals(4, resultSet.findColumn("salary"));
    }
    
    @Test
    public void testFindColumnCaseInsensitive() throws SQLException {
        assertEquals(1, resultSet.findColumn("ID"));
        assertEquals(2, resultSet.findColumn("NAME"));
        assertEquals(3, resultSet.findColumn("Age"));
    }
    
    @Test
    public void testFindColumnNotFound() {
        assertThrows(SQLException.class, () -> {
            resultSet.findColumn("nonexistent");
        });
    }
    
    @Test
    public void testGetStringInvalidIndex() throws SQLException {
        assertTrue(resultSet.next());
        
        assertThrows(SQLException.class, () -> {
            resultSet.getString(0); // 索引从1开始
        });
        
        assertThrows(SQLException.class, () -> {
            resultSet.getString(10); // 超出范围
        });
    }
    
    @Test
    public void testGetStringBeforeNext() {
        // 在调用next()之前访问数据应该抛出异常
        assertThrows(SQLException.class, () -> {
            resultSet.getString(1);
        });
    }
    
    @Test
    public void testGetMetaData() throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        
        assertNotNull(metaData);
        assertEquals(4, metaData.getColumnCount());
        assertEquals("id", metaData.getColumnName(1));
        assertEquals("name", metaData.getColumnName(2));
        assertEquals("age", metaData.getColumnName(3));
        assertEquals("salary", metaData.getColumnName(4));
    }
    
    @Test
    public void testClose() throws SQLException {
        assertFalse(resultSet.isClosed());
        
        resultSet.close();
        
        assertTrue(resultSet.isClosed());
    }
    
    @Test
    public void testOperationsAfterClose() throws SQLException {
        resultSet.close();
        
        // 关闭后操作应该抛出异常
        assertThrows(SQLException.class, () -> {
            resultSet.next();
        });
        
        assertThrows(SQLException.class, () -> {
            resultSet.getString(1);
        });
        
        assertThrows(SQLException.class, () -> {
            resultSet.findColumn("id");
        });
        
        assertThrows(SQLException.class, () -> {
            resultSet.getMetaData();
        });
    }
    
    @Test
    public void testTraverseAllRows() throws SQLException {
        int rowCount = 0;
        while (resultSet.next()) {
            rowCount++;
            assertNotNull(resultSet.getString("id"));
            assertNotNull(resultSet.getString("name"));
        }
        
        assertEquals(5, rowCount);
    }
    
    @Test
    public void testGetStringWithNull() throws SQLException {
        // 创建包含null值的结果集
        String[][] valuesWithNull = new String[][]{
            {"1", "2", null},           // id列
            {"Alice", null, "Charlie"}, // name列
        };
        
        xlNativeResultSet rs = new xlNativeResultSet(
            new String[]{"id", "name"},
            new String[]{"INTEGER", "VARCHAR"},
            valuesWithNull,
            3
        );
        
        try {
            assertTrue(rs.next());
            assertEquals("1", rs.getString(1));
            assertEquals("Alice", rs.getString(2));
            
            assertTrue(rs.next());
            assertEquals("2", rs.getString(1));
            assertNull(rs.getString(2)); // null值
            
            assertTrue(rs.next());
            assertNull(rs.getString(1)); // null值
            assertEquals("Charlie", rs.getString(2));
        } finally {
            rs.close();
        }
    }
    
    @Test
    public void testGetStringWithEmptyString() throws SQLException {
        // 创建包含空字符串的结果集
        String[][] valuesWithEmpty = new String[][]{
            {"1", ""}, // id列
            {"", "Bob"}, // name列
        };
        
        xlNativeResultSet rs = new xlNativeResultSet(
            new String[]{"id", "name"},
            new String[]{"INTEGER", "VARCHAR"},
            valuesWithEmpty,
            2
        );
        
        try {
            assertTrue(rs.next());
            assertEquals("1", rs.getString(1));
            assertNull(rs.getString(2)); // 空字符串应该返回null
            
            assertTrue(rs.next());
            assertNull(rs.getString(1)); // 空字符串应该返回null
            assertEquals("Bob", rs.getString(2));
        } finally {
            rs.close();
        }
    }
    
    @Test
    public void testEmptyResultSet() throws SQLException {
        xlNativeResultSet emptyRs = new xlNativeResultSet(
            new String[]{"id", "name"},
            new String[]{"INTEGER", "VARCHAR"},
            new String[2][0], // 空数据
            0
        );
        
        try {
            assertFalse(emptyRs.next());
            
            ResultSetMetaData metaData = emptyRs.getMetaData();
            assertNotNull(metaData);
            assertEquals(2, metaData.getColumnCount());
        } finally {
            emptyRs.close();
        }
    }
    
    @Test
    public void testSingleRowResultSet() throws SQLException {
        String[][] singleRow = new String[][]{
            {"1"}, // id列
            {"Alice"}, // name列
        };
        
        xlNativeResultSet rs = new xlNativeResultSet(
            new String[]{"id", "name"},
            new String[]{"INTEGER", "VARCHAR"},
            singleRow,
            1
        );
        
        try {
            assertTrue(rs.next());
            assertEquals("1", rs.getString(1));
            assertEquals("Alice", rs.getString(2));
            assertFalse(rs.next());
        } finally {
            rs.close();
        }
    }
    
    @Test
    public void testLargeResultSet() throws SQLException {
        // 创建1000行的结果集
        String[][] largeValues = new String[2][1000];
        for (int i = 0; i < 1000; i++) {
            largeValues[0][i] = String.valueOf(i + 1);
            largeValues[1][i] = "name" + (i + 1);
        }
        
        xlNativeResultSet rs = new xlNativeResultSet(
            new String[]{"id", "name"},
            new String[]{"INTEGER", "VARCHAR"},
            largeValues,
            1000
        );
        
        try {
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                assertNotNull(rs.getString(1));
                assertNotNull(rs.getString(2));
            }
            
            assertEquals(1000, rowCount);
        } finally {
            rs.close();
        }
    }
    
    @Test
    public void testGetStringWithTableAlias() throws SQLException {
        // 测试带表别名的列名（如 t1.id）
        // 注意：xlNativeResultSet本身不处理表别名，这应该在查询计划中处理
        // 这里只测试基本的列名访问
        assertTrue(resultSet.next());
        assertEquals("1", resultSet.getString("id"));
    }
    
    @Test
    public void testResultSetWithNullColumnNames() {
        // 测试null列名数组
        xlNativeResultSet rs = new xlNativeResultSet(
            null,
            new String[]{"INTEGER"},
            new String[][]{{"1"}},
            1
        );
        
        assertNotNull(rs);
        // 列名数组为null时，应该使用空数组
    }
    
    @Test
    public void testResultSetWithNullColumnTypes() {
        // 测试null列类型数组
        xlNativeResultSet rs = new xlNativeResultSet(
            new String[]{"id"},
            null,
            new String[][]{{"1"}},
            1
        );
        
        assertNotNull(rs);
        // 列类型数组为null时，应该使用空数组
    }
    
    @Test
    public void testResultSetWithNullValues() {
        // 测试null数据矩阵
        xlNativeResultSet rs = new xlNativeResultSet(
            new String[]{"id"},
            new String[]{"INTEGER"},
            null,
            0
        );
        
        assertNotNull(rs);
        // 数据矩阵为null时，应该使用空矩阵
    }
}

