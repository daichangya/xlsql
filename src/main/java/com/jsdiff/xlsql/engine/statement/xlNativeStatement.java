/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://excel.jsdiff.com
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
package com.jsdiff.xlsql.engine.statement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.logging.Logger;

import com.jsdiff.xlsql.database.sql.ICommand;
import com.jsdiff.xlsql.engine.connection.xlConnectionNative;
import com.jsdiff.xlsql.engine.core.NativeSqlEngine;

/**
 * xlNativeStatement - 自研引擎的Statement实现
 * 
 * <p>该类实现了JDBC Statement接口，使用自研SQL引擎执行SQL语句。
 * 不依赖外部数据库连接，直接基于Excel数据执行查询。</p>
 * 
 * @author daichangya
 */
public class xlNativeStatement implements Statement {
    
    /** 日志记录器 */
    private static final Logger log = Logger.getLogger(xlNativeStatement.class.getName());
    
    /** 关联的xlConnectionNative对象 */
    protected xlConnectionNative xlCon;
    
    /** 语句是否已关闭 */
    private boolean closed = false;
    
    /**
     * 创建xlNativeStatement实例
     * 
     * @param con 关联的xlConnectionNative对象
     */
    public xlNativeStatement(xlConnectionNative con) {
        if (con == null) {
            throw new NullPointerException("Connection cannot be null");
        }
        this.xlCon = con;
    }
    
    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        checkClosed();
        
        // 使用自研引擎执行查询
        NativeSqlEngine engine = xlCon.getNativeEngine();
        if (engine == null) {
            throw new SQLException("Native engine not initialized");
        }
        
        log.info("xlSQL: executeQuery " + sql);
        return engine.executeQuery(sql);
    }
    
    @Override
    public int executeUpdate(String sql) throws SQLException {
        checkClosed();
        
        // 解析SQL并执行命令
        ICommand cmd = xlCon.xlsql.parseSql(sql);
        if (cmd.execAllowed()) {
            log.info("xlSQL: executeUpdate " + sql);
            
            // 使用自研引擎执行更新
            NativeSqlEngine engine = xlCon.getNativeEngine();
            if (engine == null) {
                throw new SQLException("Native engine not initialized");
            }
            
            int result = engine.executeUpdate(sql);
            cmd.execute();
            return result;
        } else {
            throw new SQLException("xlSQL: execute not allowed");
        }
    }
    
    @Override
    public boolean execute(String sql) throws SQLException {
        checkClosed();
        
        String[] sqlCommands = sql.split("[;]");
        boolean ret = false;
        
        for (String sqlCommand : sqlCommands) {
            sqlCommand = sqlCommand.trim();
            if (sqlCommand.isEmpty()) {
                continue;
            }
            
            ICommand cmd = xlCon.xlsql.parseSql(sqlCommand);
            if (cmd.execAllowed()) {
                log.info("xlSQL: execute " + sqlCommand);
                
                // 判断是查询还是更新
                String normalizedSql = sqlCommand.trim().toUpperCase();
                if (normalizedSql.startsWith("SELECT")) {
                    // 查询语句
                    NativeSqlEngine engine = xlCon.getNativeEngine();
                    if (engine == null) {
                        throw new SQLException("Native engine not initialized");
                    }
                    engine.executeQuery(sqlCommand);
                    ret = true;
                } else {
                    // 更新语句
                    NativeSqlEngine engine = xlCon.getNativeEngine();
                    if (engine == null) {
                        throw new SQLException("Native engine not initialized");
                    }
                    engine.executeUpdate(sqlCommand);
                    ret = false;
                }
                cmd.execute();
            } else {
                throw new SQLException("xlSQL: execute not allowed");
            }
        }
        
        return ret;
    }
    
    @Override
    public void close() throws SQLException {
        closed = true;
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }
    
    private void checkClosed() throws SQLException {
        if (closed) {
            throw new SQLException("Statement is closed");
        }
    }
    
    // 其他Statement接口方法的实现（简化实现）
    
    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }
    
    @Override
    public void setMaxFieldSize(int max) throws SQLException {
    }
    
    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }
    
    @Override
    public void setMaxRows(int max) throws SQLException {
    }
    
    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
    }
    
    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }
    
    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
    }
    
    @Override
    public void cancel() throws SQLException {
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }
    
    @Override
    public void clearWarnings() throws SQLException {
    }
    
    @Override
    public void setCursorName(String name) throws SQLException {
        throw new SQLException("Cursors not supported");
    }
    
    @Override
    public ResultSet getResultSet() throws SQLException {
        return null;
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        return -1;
    }
    
    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }
    
    @Override
    public void setFetchDirection(int direction) throws SQLException {
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }
    
    @Override
    public void setFetchSize(int rows) throws SQLException {
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }
    
    @Override
    public int getResultSetConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }
    
    @Override
    public int getResultSetType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }
    
    @Override
    public void addBatch(String sql) throws SQLException {
        throw new SQLException("Batch operations not supported");
    }
    
    @Override
    public void clearBatch() throws SQLException {
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLException("Batch operations not supported");
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return xlCon;
    }
    
    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }
    
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new SQLException("Generated keys not supported");
    }
    
    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return executeUpdate(sql);
    }
    
    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeUpdate(sql);
    }
    
    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeUpdate(sql);
    }
    
    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return execute(sql);
    }
    
    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return execute(sql);
    }
    
    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return execute(sql);
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }
    
    @Override
    public void setPoolable(boolean poolable) throws SQLException {
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }
    
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
    
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}

