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
import com.jsdiff.xlsql.util.XlSqlLogger;

/**
 * xlNativeStatement - 自研引擎的Statement实现
 * 
 * <p>该类实现了JDBC Statement接口，使用自研SQL引擎执行SQL语句。
 * 不依赖外部数据库连接，直接基于Excel数据执行查询。</p>
 * 
 * @author daichangya
 */
public class xlNativeStatement implements Statement {
    
    /** 日志记录器（保留用于向后兼容，已迁移到 XlSqlLogger） */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(xlNativeStatement.class.getName());
    
    /** 关联的xlConnectionNative对象 */
    protected xlConnectionNative xlCon;
    
    /** 语句是否已关闭 */
    private boolean closed = false;
    
    /** 当前结果集（用于支持 execute() + getResultSet() 模式，DBeaver 需要此功能） */
    private ResultSet currentResultSet = null;
    
    /** 当前更新计数（用于支持 execute() + getUpdateCount() 模式） */
    private int currentUpdateCount = -1;
    
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
        
        // 关闭之前的结果集
        closeCurrentResultSet();
        
        // 使用自研引擎执行查询
        NativeSqlEngine engine = xlCon.getNativeEngine();
        if (engine == null) {
            throw new SQLException("Native engine not initialized");
        }
        
        long startTime = System.currentTimeMillis();
        try {
            ResultSet resultSet = engine.executeQuery(sql);
            
            // 保存结果集，用于 getResultSet() 调用
            currentResultSet = resultSet;
            currentUpdateCount = -1;
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 获取结果行数（如果可能）
            int rowCount = -1;
            if (resultSet != null) {
                try {
                    resultSet.last();
                    rowCount = resultSet.getRow();
                    resultSet.beforeFirst();
                } catch (SQLException e) {
                    // 无法获取行数，忽略
                }
            }
            
            XlSqlLogger.logSql(xlNativeStatement.class, sql, executionTime, rowCount);
            return resultSet;
        } catch (SQLException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            XlSqlLogger.logError(xlNativeStatement.class, 
                String.format("SQL execution failed: %s | Time: %dms", sql, executionTime), e);
            throw e;
        }
    }
    
    @Override
    public int executeUpdate(String sql) throws SQLException {
        checkClosed();
        
        // 关闭之前的结果集
        closeCurrentResultSet();
        
        // 解析SQL并执行命令
        ICommand cmd = xlCon.xlsql.parseSql(sql);
        if (cmd.execAllowed()) {
            long startTime = System.currentTimeMillis();
            try {
                // 使用自研引擎执行更新
                NativeSqlEngine engine = xlCon.getNativeEngine();
                if (engine == null) {
                    throw new SQLException("Native engine not initialized");
                }
                
                int result = engine.executeUpdate(sql);
                cmd.execute();
                
                // 清除结果集，保存更新计数
                currentResultSet = null;
                currentUpdateCount = result;
                
                long executionTime = System.currentTimeMillis() - startTime;
                XlSqlLogger.logSql(xlNativeStatement.class, sql, executionTime, result);
                return result;
            } catch (SQLException e) {
                long executionTime = System.currentTimeMillis() - startTime;
                XlSqlLogger.logError(xlNativeStatement.class, 
                    String.format("SQL update failed: %s | Time: %dms", sql, executionTime), e);
                throw e;
            }
        } else {
            throw new SQLException("XLSQL: execute not allowed");
        }
    }
    
    @Override
    public boolean execute(String sql) throws SQLException {
        checkClosed();
        
        // 关闭之前的结果集
        closeCurrentResultSet();
        
        String[] sqlCommands = sql.split("[;]");
        boolean ret = false;
        
        for (String sqlCommand : sqlCommands) {
            sqlCommand = sqlCommand.trim();
            if (sqlCommand.isEmpty()) {
                continue;
            }
            
            ICommand cmd = xlCon.xlsql.parseSql(sqlCommand);
            if (cmd.execAllowed()) {
                long startTime = System.currentTimeMillis();
                try {
                    // 判断是查询还是更新
                    String normalizedSql = sqlCommand.trim().toUpperCase();
                    if (normalizedSql.startsWith("SELECT")) {
                        // 查询语句
                        NativeSqlEngine engine = xlCon.getNativeEngine();
                        if (engine == null) {
                            throw new SQLException("Native engine not initialized");
                        }
                        // 保存结果集，用于后续 getResultSet() 调用（DBeaver 需要）
                        ResultSet rs = engine.executeQuery(sqlCommand);
                        currentResultSet = rs;
                        currentUpdateCount = -1;
                        ret = true;
                    } else {
                        // 更新语句
                        NativeSqlEngine engine = xlCon.getNativeEngine();
                        if (engine == null) {
                            throw new SQLException("Native engine not initialized");
                        }
                        int updateCount = engine.executeUpdate(sqlCommand);
                        currentResultSet = null;
                        currentUpdateCount = updateCount;
                        ret = false;
                    }
                    cmd.execute();
                    
                    long executionTime = System.currentTimeMillis() - startTime;
                    XlSqlLogger.logSql(xlNativeStatement.class, sqlCommand, executionTime, -1);
                } catch (SQLException e) {
                    long executionTime = System.currentTimeMillis() - startTime;
                    XlSqlLogger.logError(xlNativeStatement.class, 
                        String.format("SQL execute failed: %s | Time: %dms", sqlCommand, executionTime), e);
                    throw e;
                }
            } else {
                throw new SQLException("XLSQL: execute not allowed");
            }
        }
        
        return ret;
    }
    
    @Override
    public void close() throws SQLException {
        // 关闭当前结果集
        closeCurrentResultSet();
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
    
    /**
     * 关闭当前结果集（如果存在）
     * 用于资源管理和避免内存泄漏
     */
    private void closeCurrentResultSet() {
        if (currentResultSet != null) {
            try {
                currentResultSet.close();
            } catch (SQLException e) {
                // 忽略关闭时的异常，记录日志即可
                XlSqlLogger.logWarning(xlNativeStatement.class, 
                    "Failed to close current ResultSet: " + e.getMessage());
            }
            currentResultSet = null;
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
        checkClosed();
        return currentResultSet;
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        checkClosed();
        return currentUpdateCount;
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

