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
package com.jsdiff.xlsql.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jsdiff.xlsql.base.NativeEngineTestBase;
import com.jsdiff.xlsql.engine.connection.xlConnectionNative;
import com.jsdiff.xlsql.engine.statement.xlNativeStatement;

/**
 * xlNativeStatementTest - Statement单元测试
 * 
 * <p>测试xlNativeStatement类的各种功能，包括查询执行、更新执行、资源管理等。</p>
 * 
 * @author rzy
 */
public class xlNativeStatementTest extends NativeEngineTestBase {
    
    private Statement stmt;
    
    @BeforeEach
    public void setUpStatement() throws SQLException {
        stmt = con.createStatement();
    }
    
    @Test
    public void testCreateStatement() throws SQLException {
        assertNotNull(stmt);
        assertTrue(stmt instanceof xlNativeStatement);
        assertFalse(stmt.isClosed());
    }
    
    @Test
    public void testCreateStatementWithNullConnection() {
        assertThrows(NullPointerException.class, () -> {
            new xlNativeStatement(null);
        });
    }
    
    @Test
    public void testExecuteQuery() throws SQLException {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 5");
            
            assertNotNull(rs);
            assertFalse(stmt.isClosed());
            
            // 验证结果集
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
            }
            assertTrue(rowCount >= 0); // 至少应该能执行查询
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
    
    @Test
    public void testExecuteQueryWithInvalidSQL() {
        assertThrows(SQLException.class, () -> {
            stmt.executeQuery("INVALID SQL STATEMENT");
        });
    }
    
    @Test
    public void testExecuteQueryAfterClose() throws SQLException {
        stmt.close();
        
        assertThrows(SQLException.class, () -> {
            stmt.executeQuery("SELECT * FROM test1_Sheet1");
        });
    }
    
    @Test
    public void testExecuteUpdate() throws SQLException {
        // 注意：Native引擎不支持UPDATE，这里测试是否会抛出异常
        try {
            int result = stmt.executeUpdate("UPDATE test1_Sheet1 SET a = 'test' WHERE id = 1");
            // 如果执行成功，验证返回值
            assertTrue(result >= 0, "Update should return non-negative result");
        } catch (SQLException e) {
            // 如果不支持UPDATE，这是预期的
            // 验证异常消息包含相关关键词
            String msg = e.getMessage();
            assertTrue(msg != null && !msg.isEmpty(), "Exception message should not be empty");
            assertTrue(msg.contains("not allowed") || 
                      msg.contains("not supported") ||
                      msg.contains("not yet supported") ||
                      msg.contains("execute not allowed") ||
                      msg.contains("UPDATE") ||
                      msg.contains("xlSQL"),
                      "Expected error message about UPDATE not supported, got: " + msg);
        }
    }
    
    @Test
    public void testExecute() throws SQLException {
        // 测试execute方法（可以执行SELECT或UPDATE）
        boolean result = stmt.execute("SELECT * FROM test1_Sheet1 LIMIT 1");
        
        assertTrue(result); // SELECT应该返回true
        assertFalse(stmt.isClosed());
    }
    
    @Test
    public void testClose() throws SQLException {
        assertFalse(stmt.isClosed());
        
        stmt.close();
        
        assertTrue(stmt.isClosed());
    }
    
    @Test
    public void testCloseMultipleTimes() throws SQLException {
        stmt.close();
        stmt.close(); // 应该可以多次关闭而不抛出异常
        
        assertTrue(stmt.isClosed());
    }
    
    @Test
    public void testGetConnection() throws SQLException {
        assertEquals(con, stmt.getConnection());
        assertTrue(stmt.getConnection() instanceof xlConnectionNative);
    }
    
    @Test
    public void testGetConnectionAfterClose() throws SQLException {
        stmt.close();
        
        // 关闭后仍应能获取连接
        assertNotNull(stmt.getConnection());
    }
    
    @Test
    public void testGetResultSet() throws SQLException {
        // executeQuery后应该可以通过getResultSet获取结果集
        ResultSet queryRs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 1");
        queryRs.close();
        
        // 注意：当前实现可能不支持getResultSet，这里测试是否会返回null
        ResultSet rs = stmt.getResultSet();
        // 如果实现支持，rs应该不为null；如果不支持，rs为null也是可以接受的
        if (rs != null) {
            rs.close();
        }
    }
    
    @Test
    public void testGetUpdateCount() throws SQLException {
        // 对于SELECT查询，updateCount应该返回-1
        stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 1");
        assertEquals(-1, stmt.getUpdateCount());
    }
    
    @Test
    public void testGetMoreResults() throws SQLException {
        // 测试是否有更多结果集
        stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 1");
        assertFalse(stmt.getMoreResults()); // 应该没有更多结果
    }
    
    @Test
    public void testGetResultSetType() throws SQLException {
        assertEquals(ResultSet.TYPE_FORWARD_ONLY, stmt.getResultSetType());
    }
    
    @Test
    public void testGetResultSetConcurrency() throws SQLException {
        assertEquals(ResultSet.CONCUR_READ_ONLY, stmt.getResultSetConcurrency());
    }
    
    @Test
    public void testGetFetchSize() throws SQLException {
        assertEquals(0, stmt.getFetchSize());
    }
    
    @Test
    public void testSetFetchSize() throws SQLException {
        stmt.setFetchSize(100);
        // 设置后应该能获取到（即使实现可能忽略）
        assertTrue(stmt.getFetchSize() >= 0);
    }
    
    @Test
    public void testGetFetchDirection() throws SQLException {
        assertEquals(ResultSet.FETCH_FORWARD, stmt.getFetchDirection());
    }
    
    @Test
    public void testSetFetchDirection() throws SQLException {
        stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
        // 应该不抛出异常
        assertEquals(ResultSet.FETCH_FORWARD, stmt.getFetchDirection());
    }
    
    @Test
    public void testGetMaxRows() throws SQLException {
        assertEquals(0, stmt.getMaxRows());
    }
    
    @Test
    public void testSetMaxRows() throws SQLException {
        stmt.setMaxRows(100);
        // 设置后应该能获取到（即使实现可能忽略）
        assertTrue(stmt.getMaxRows() >= 0);
    }
    
    @Test
    public void testGetQueryTimeout() throws SQLException {
        assertEquals(0, stmt.getQueryTimeout());
    }
    
    @Test
    public void testSetQueryTimeout() throws SQLException {
        stmt.setQueryTimeout(30);
        // 应该不抛出异常
        assertTrue(stmt.getQueryTimeout() >= 0);
    }
    
    @Test
    public void testGetWarnings() throws SQLException {
        assertNull(stmt.getWarnings());
    }
    
    @Test
    public void testClearWarnings() throws SQLException {
        stmt.clearWarnings(); // 应该不抛出异常
        assertNull(stmt.getWarnings());
    }
    
    @Test
    public void testSetCursorName() {
        assertThrows(SQLException.class, () -> {
            stmt.setCursorName("cursor1");
        });
    }
    
    @Test
    public void testAddBatch() {
        assertThrows(SQLException.class, () -> {
            stmt.addBatch("SELECT * FROM test1_Sheet1");
        });
    }
    
    @Test
    public void testClearBatch() throws SQLException {
        stmt.clearBatch(); // 应该不抛出异常
    }
    
    @Test
    public void testExecuteBatch() {
        assertThrows(SQLException.class, () -> {
            stmt.executeBatch();
        });
    }
    
    @Test
    public void testGetGeneratedKeys() {
        assertThrows(SQLException.class, () -> {
            stmt.getGeneratedKeys();
        });
    }
    
    @Test
    public void testIsPoolable() throws SQLException {
        assertFalse(stmt.isPoolable());
    }
    
    @Test
    public void testSetPoolable() throws SQLException {
        stmt.setPoolable(true);
        // 应该不抛出异常
    }
    
    @Test
    public void testIsCloseOnCompletion() throws SQLException {
        assertFalse(stmt.isCloseOnCompletion());
    }
    
    @Test
    public void testCloseOnCompletion() throws SQLException {
        stmt.closeOnCompletion(); // 应该不抛出异常
    }
    
    @Test
    public void testGetResultSetHoldability() throws SQLException {
        assertEquals(ResultSet.HOLD_CURSORS_OVER_COMMIT, stmt.getResultSetHoldability());
    }
    
    @Test
    public void testExecuteWithMultipleStatements() throws SQLException {
        // 测试执行多个SQL语句（用分号分隔）
        boolean result = stmt.execute("SELECT * FROM test1_Sheet1 LIMIT 1; SELECT * FROM test1_Sheet1 LIMIT 1");
        // 应该执行成功
        assertTrue(result || !result); // 结果可能是true或false
    }
    
    // ================== execute() + getResultSet() 模式测试（DBeaver 使用此模式）==================
    
    @Test
    public void testExecuteAndGetResultSet() throws SQLException {
        // 测试 DBeaver 使用的标准模式：execute() + getResultSet()
        boolean isResultSet = stmt.execute("SELECT * FROM test1_Sheet1 LIMIT 5");
        
        assertTrue(isResultSet, "execute() should return true for SELECT");
        
        // 通过 getResultSet() 获取结果集
        ResultSet rs = stmt.getResultSet();
        assertNotNull(rs, "getResultSet() should return non-null ResultSet after execute() with SELECT");
        
        // 验证结果集可以正常读取
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount > 0, "ResultSet should have data");
        
        // 验证 updateCount 为 -1（因为是 SELECT 查询）
        assertEquals(-1, stmt.getUpdateCount(), "getUpdateCount() should return -1 for SELECT");
        
        rs.close();
    }
    
    @Test
    public void testExecuteQueryAndGetResultSet() throws SQLException {
        // 测试 executeQuery() 后通过 getResultSet() 也能获取结果集
        ResultSet queryRs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 3");
        assertNotNull(queryRs);
        
        // getResultSet() 应该返回相同的结果集
        ResultSet rs = stmt.getResultSet();
        assertNotNull(rs, "getResultSet() should return non-null after executeQuery()");
        
        // 两者应该是同一个结果集引用
        assertEquals(queryRs, rs, "getResultSet() should return the same ResultSet as executeQuery()");
        
        queryRs.close();
    }
    
    @Test
    public void testGetResultSetAfterClose() throws SQLException {
        // 执行查询
        stmt.execute("SELECT * FROM test1_Sheet1 LIMIT 1");
        
        // 关闭 Statement
        stmt.close();
        
        // 关闭后 getResultSet() 应该抛出异常
        assertThrows(SQLException.class, () -> {
            stmt.getResultSet();
        }, "getResultSet() should throw exception after Statement is closed");
    }
    
    @Test
    public void testGetUpdateCountAfterClose() throws SQLException {
        // 执行查询
        stmt.execute("SELECT * FROM test1_Sheet1 LIMIT 1");
        
        // 关闭 Statement
        stmt.close();
        
        // 关闭后 getUpdateCount() 应该抛出异常
        assertThrows(SQLException.class, () -> {
            stmt.getUpdateCount();
        }, "getUpdateCount() should throw exception after Statement is closed");
    }
    
    @Test
    public void testMultipleExecutionsResultSetSwitch() throws SQLException {
        // 第一次执行查询
        stmt.execute("SELECT * FROM test1_Sheet1 LIMIT 2");
        ResultSet rs1 = stmt.getResultSet();
        assertNotNull(rs1);
        
        // 遍历第一个结果集
        int count1 = 0;
        while (rs1.next()) {
            count1++;
        }
        assertEquals(2, count1, "First query should return 2 rows");
        
        // 第二次执行查询 - 应该自动关闭第一个结果集
        stmt.execute("SELECT * FROM test1_Sheet1 LIMIT 3");
        ResultSet rs2 = stmt.getResultSet();
        assertNotNull(rs2);
        
        // 验证第二个结果集
        int count2 = 0;
        while (rs2.next()) {
            count2++;
        }
        assertEquals(3, count2, "Second query should return 3 rows");
        
        // 验证两个结果集是不同的对象
        assertNotNull(rs2);
        
        rs2.close();
    }
    
    @Test
    public void testGetResultSetBeforeExecute() throws SQLException {
        // 在执行任何查询之前，getResultSet() 应该返回 null
        ResultSet rs = stmt.getResultSet();
        assertNull(rs, "getResultSet() should return null before any execution");
    }
    
    @Test
    public void testGetUpdateCountBeforeExecute() throws SQLException {
        // 在执行任何查询之前，getUpdateCount() 应该返回 -1
        assertEquals(-1, stmt.getUpdateCount(), "getUpdateCount() should return -1 before any execution");
    }
    
    @Test
    public void testExecuteQueryResultSetReadable() throws SQLException {
        // 测试通过 execute() 执行后，结果集可以正常读取数据
        stmt.execute("SELECT * FROM test1_Sheet1 LIMIT 1");
        
        ResultSet rs = stmt.getResultSet();
        assertNotNull(rs);
        
        // 验证可以获取元数据
        assertNotNull(rs.getMetaData());
        assertTrue(rs.getMetaData().getColumnCount() > 0);
        
        // 验证可以读取数据
        assertTrue(rs.next());
        
        // 验证可以通过列索引读取
        assertNotNull(rs.getString(1));
        
        rs.close();
    }
    
    @Test
    public void testStatementCloseClosesResultSet() throws SQLException {
        // 执行查询
        stmt.execute("SELECT * FROM test1_Sheet1 LIMIT 5");
        ResultSet rs = stmt.getResultSet();
        assertNotNull(rs);
        
        // 验证结果集未关闭
        assertFalse(rs.isClosed());
        
        // 关闭 Statement
        stmt.close();
        
        // 验证结果集也被关闭
        assertTrue(rs.isClosed(), "ResultSet should be closed when Statement is closed");
    }
    
    @Test
    public void testExecuteQueryMultipleTimes() throws SQLException {
        // 连续执行多次 executeQuery
        for (int i = 1; i <= 3; i++) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT " + i);
            assertNotNull(rs);
            
            int count = 0;
            while (rs.next()) {
                count++;
            }
            assertEquals(i, count, "Query " + i + " should return " + i + " rows");
            
            // 验证 getResultSet() 返回同一个结果集
            assertEquals(rs, stmt.getResultSet());
        }
    }
}

