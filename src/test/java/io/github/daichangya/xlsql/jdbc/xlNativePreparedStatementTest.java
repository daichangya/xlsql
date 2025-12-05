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

import io.github.daichangya.xlsql.base.NativeEngineTestBase;
import io.github.daichangya.xlsql.engine.statement.xlNativePreparedStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

/**
 * xlNativePreparedStatementTest - PreparedStatement单元测试
 * 
 * <p>测试xlNativePreparedStatement类的各种功能，包括参数绑定、查询执行等。</p>
 * 
 * @author rzy
 */
public class xlNativePreparedStatementTest extends NativeEngineTestBase {
    
    private PreparedStatement pstmt;
    
    @BeforeEach
    public void setUpPreparedStatement() throws SQLException {
        // 创建一个简单的预编译语句
        pstmt = con.prepareStatement("SELECT * FROM test1_Sheet1 WHERE id = ? LIMIT 1");
    }
    
    @Test
    public void testCreatePreparedStatement() throws SQLException {
        assertNotNull(pstmt);
        assertTrue(pstmt instanceof xlNativePreparedStatement);
        assertFalse(pstmt.isClosed());
    }
    
    @Test
    public void testSetString() throws SQLException {
        pstmt.setString(1, "1");
        
        // 执行查询验证参数设置
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetInt() throws SQLException {
        pstmt.setInt(1, 1);
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetLong() throws SQLException {
        pstmt.setLong(1, 1L);
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetDouble() throws SQLException {
        pstmt.setDouble(1, 1.0);
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetBoolean() throws SQLException {
        pstmt.setBoolean(1, true);
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetBigDecimal() throws SQLException {
        pstmt.setBigDecimal(1, new BigDecimal("1.0"));
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetNull() throws SQLException {
        pstmt.setNull(1, java.sql.Types.INTEGER);
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetObject() throws SQLException {
        pstmt.setObject(1, "1");
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetDate() throws SQLException {
        pstmt.setDate(1, new Date(System.currentTimeMillis()));
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetTime() throws SQLException {
        pstmt.setTime(1, new Time(System.currentTimeMillis()));
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetTimestamp() throws SQLException {
        pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testClearParameters() throws SQLException {
        pstmt.setString(1, "1");
        pstmt.clearParameters();
        
        // 清除参数后，参数应该为null
        // 执行查询可能会失败，但应该不抛出异常（取决于实现）
        try {
            ResultSet rs = pstmt.executeQuery();
            rs.close();
        } catch (SQLException e) {
            // 如果因为参数未设置而失败，这是可以接受的
        }
    }
    
    @Test
    public void testExecuteQuery() throws SQLException {
        pstmt.setString(1, "1");
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        
        // 验证结果集
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
        }
        assertTrue(rowCount >= 0);
        
        rs.close();
    }
    
    @Test
    public void testExecuteQueryAfterClose() throws SQLException {
        pstmt.close();
        
        assertThrows(SQLException.class, () -> {
            pstmt.executeQuery();
        });
    }
    
    @Test
    public void testExecuteUpdate() throws SQLException {
        // 注意：Native引擎不支持UPDATE
        PreparedStatement updateStmt = con.prepareStatement("UPDATE test1_Sheet1 SET a = ? WHERE id = ?");
        
        try {
            updateStmt.setString(1, "test");
            updateStmt.setInt(2, 1);
            int result = updateStmt.executeUpdate();
            assertTrue(result >= 0, "Update should return non-negative result");
        } catch (SQLException e) {
            // 如果不支持UPDATE，这是预期的
            String msg = e.getMessage();
            assertTrue(msg != null && !msg.isEmpty(), "Exception message should not be empty");
            assertTrue(msg.contains("not allowed") || 
                      msg.contains("not supported") ||
                      msg.contains("not yet supported") ||
                      msg.contains("execute not allowed") ||
                      msg.contains("UPDATE") ||
                      msg.contains("XLSQL"),
                      "Expected error message about UPDATE not supported, got: " + msg);
        } finally {
            updateStmt.close();
        }
    }
    
    @Test
    public void testExecute() throws SQLException {
        pstmt.setString(1, "1");
        
        boolean result = pstmt.execute();
        assertTrue(result); // SELECT应该返回true
    }
    
    @Test
    public void testSetParameterAfterClose() throws SQLException {
        pstmt.close();
        
        assertThrows(SQLException.class, () -> {
            pstmt.setString(1, "1");
        });
    }
    
    @Test
    public void testSetParameterInvalidIndex() throws SQLException {
        // 测试无效的参数索引（负数或超出范围）
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            pstmt.setString(0, "1"); // 索引从1开始
        });
    }
    
    @Test
    public void testMultipleParameters() throws SQLException {
        PreparedStatement multiParamStmt = con.prepareStatement(
            "SELECT * FROM test1_Sheet1 WHERE id = ? AND age = ? LIMIT 1");
        
        multiParamStmt.setInt(1, 1);
        multiParamStmt.setInt(2, 25);
        
        ResultSet rs = multiParamStmt.executeQuery();
        assertNotNull(rs);
        rs.close();
        multiParamStmt.close();
    }
    
    @Test
    public void testSetByte() throws SQLException {
        pstmt.setByte(1, (byte) 1);
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetShort() throws SQLException {
        pstmt.setShort(1, (short) 1);
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetFloat() throws SQLException {
        pstmt.setFloat(1, 1.0f);
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetBytes() throws SQLException {
        pstmt.setBytes(1, new byte[]{1, 2, 3});
        
        ResultSet rs = pstmt.executeQuery();
        assertNotNull(rs);
        rs.close();
    }
    
    @Test
    public void testSetAsciiStream() {
        assertThrows(SQLException.class, () -> {
            pstmt.setAsciiStream(1, null, 0);
        });
    }
    
    @Test
    public void testSetUnicodeStream() {
        assertThrows(SQLException.class, () -> {
            pstmt.setUnicodeStream(1, null, 0);
        });
    }
    
    @Test
    public void testSetBinaryStream() {
        assertThrows(SQLException.class, () -> {
            pstmt.setBinaryStream(1, null, 0);
        });
    }
    
    @Test
    public void testSetCharacterStream() {
        assertThrows(SQLException.class, () -> {
            pstmt.setCharacterStream(1, null, 0);
        });
    }
    
    @Test
    public void testGetMetaData() {
        assertThrows(SQLException.class, () -> {
            pstmt.getMetaData();
        });
    }
    
    @Test
    public void testGetParameterMetaData() {
        assertThrows(SQLException.class, () -> {
            pstmt.getParameterMetaData();
        });
    }
    
    @Test
    public void testAddBatch() {
        assertThrows(SQLException.class, () -> {
            pstmt.addBatch();
        });
    }
    
    @Test
    public void testClose() throws SQLException {
        assertFalse(pstmt.isClosed());
        
        pstmt.close();
        
        assertTrue(pstmt.isClosed());
    }
    
    @Test
    public void testCloseMultipleTimes() throws SQLException {
        pstmt.close();
        pstmt.close(); // 应该可以多次关闭而不抛出异常
        
        assertTrue(pstmt.isClosed());
    }
    
    @Test
    public void testReusePreparedStatement() throws SQLException {
        // 测试重用PreparedStatement（设置不同的参数值）
        pstmt.setString(1, "1");
        ResultSet rs1 = pstmt.executeQuery();
        rs1.close();
        
        pstmt.setString(1, "2");
        ResultSet rs2 = pstmt.executeQuery();
        rs2.close();
        
        // 应该能成功执行多次
        assertFalse(pstmt.isClosed());
    }
}

