/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://xlsql.jsdiff.com
   daichangya@163.com

 This program is free software; you can redistribute it and/or modify it under 
 the terms of the GNU General Public License as published by the Free Software 
 Foundation; either version 2 of the License, or (at your option) any later 
 version.

 This program is distributed in the hope that it will be useful, 
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License 
 along with this program; if not, write to the Free Software Foundation, 
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
/*
 * xlConnectionNative.java
 *
 * Created on 2025-01-XX
 *
 * 自研SQL引擎的连接实现，不依赖外部数据库
 */
package io.github.daichangya.xlsql.engine.connection;

import java.io.File;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import io.github.daichangya.xlsql.database.xlDatabaseException;
import io.github.daichangya.xlsql.database.xlDatabaseFactory;
import io.github.daichangya.xlsql.database.sql.xlSqlParserFactory;
import io.github.daichangya.xlsql.engine.core.NativeSqlEngine;
import io.github.daichangya.xlsql.engine.statement.xlNativePreparedStatement;
import io.github.daichangya.xlsql.engine.statement.xlNativeStatement;
import io.github.daichangya.xlsql.jdbc.DatabaseType;
import io.github.daichangya.xlsql.jdbc.xlConnection;
import io.github.daichangya.xlsql.jdbc.xlDatabaseMetaData;


/**
 * xlConnectionNative - 自研SQL引擎的连接实现
 * 
 * <p>该类是xlConnection的自研引擎实现，不依赖外部数据库（HSQLDB/H2）。
 * 直接基于Excel数据执行SQL查询，提供完全的控制和优化能力。</p>
 * 
 * <p>特点：</p>
 * <ul>
 *   <li>零外部依赖：不依赖任何外部数据库引擎</li>
 *   <li>按需加载：不需要预加载所有数据到内存</li>
 *   <li>完全控制：可以针对Excel场景优化查询</li>
 *   <li>SQL兼容：支持与HSQLDB/H2相同的SQL语法</li>
 * </ul>
 * 
 * @author daichangya
 */
public class xlConnectionNative extends xlConnection {

    /** 日志记录器 */
    private static final Logger logger = Logger.getLogger(xlConnectionNative.class.getName());

    /** 自研SQL引擎标识符 */
    private static final String NATIVE = DatabaseType.NATIVE.getName();
    
    /** 自研SQL执行引擎 */
    private NativeSqlEngine nativeEngine;
    
    /**
     * 创建自研引擎连接实例
     * 
     * <p>初始化自研SQL引擎，扫描Excel文件，但不预加载数据到外部数据库。
     * 数据将在查询时按需读取。</p>
     * 
     * @param url JDBC连接URL
     * @throws SQLException 如果连接创建失败则抛出异常
     */
    public xlConnectionNative(String url) throws SQLException {
        dialect = NATIVE;
        URL = url;
        
        try {
            // 从URL中提取目录路径（去掉"jdbc:xlsql:excel:"前缀）
            String dir = URL.substring(URL_PFX_XLS.length());
            logger.info(() -> "Mounting: " + dir + " using native SQL engine");
            
            // 从目录创建数据库对象，扫描Excel文件
            datastore = xlDatabaseFactory.create(new File(dir), "xls");
            
            // 创建自研SQL引擎（不需要外部数据库连接）
            nativeEngine = new NativeSqlEngine();
            nativeEngine.initialize(datastore);
            
            // 创建自研SQL解析器（与HSQLDB语法兼容）
            xlsql = xlSqlParserFactory.create(NATIVE, datastore, null);
            
            // 自研引擎不需要SQL格式化器和查询对象（这些用于外部数据库）
            w = null;
            query = null;
            dbCon = null; // 没有外部数据库连接
            
            logger.info(() -> "Native SQL engine initialized successfully");
            
        } catch (xlDatabaseException e) {
            logger.severe("Failed to initialize native engine: " + e.getMessage());
            throw new SQLException("Failed to initialize native engine: " + e.getMessage(), e);
        }
    }

    /**
     * 关闭自研引擎连接
     * 
     * <p>关闭自研SQL引擎并清理资源。</p>
     * 
     * @throws Exception 如果关闭失败则抛出异常
     */
    @Override
    public void shutdown() throws Exception {
    	// 安全关闭，避免重复关闭
    	if (! closed) {
	        logger.info("Shutting down native SQL engine ...");
	        try {
	            if (nativeEngine != null) {
	                nativeEngine.shutdown();
	                nativeEngine = null;
	            }
	            closed = true;
	            logger.info("Native SQL engine is shut down");
	        } catch (SQLException e) {
	            logger.severe(() -> "Error shutting down native engine: " + e.getMessage());
	            throw new Exception("Failed to shutdown native engine", e);
	        }
    	}
    }
    
    /**
     * 重写executeQuery方法，使用自研引擎执行查询
     */
    @Override
    public java.sql.Statement createStatement() throws SQLException {
        return new xlNativeStatement(this);
    }
    
    /**
     * 获取自研SQL引擎
     * 
     * @return 自研SQL执行引擎
     */
    public NativeSqlEngine getNativeEngine() {
        return nativeEngine;
    }

    // 实现xlConnection的抽象方法（这些方法在自研引擎中不需要外部数据库连接）
    
    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        // 自研引擎默认自动提交
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return true; // 自研引擎默认自动提交
    }

    @Override
    public void commit() throws SQLException {
        // 自研引擎自动提交，无需操作
    }

    @Override
    public void rollback() throws SQLException {
        throw new SQLException("Rollback not supported in native engine");
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        // 返回自研引擎的元数据
        // 传入datastore以便获取表、列等信息
        return new xlDatabaseMetaData(this, new xlNativeDatabaseMetaData(datastore));
    }

    // 其他JDBC Connection接口方法的实现（简化实现）
    
    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return !closed;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return "";
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return new Properties();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
    }

    @Override
    public String getSchema() throws SQLException {
        return "";
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        try {
            shutdown();
        } catch (Exception e) {
            throw new SQLException("Failed to abort connection", e);
        }
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
    
    // 其他xlConnection的方法需要重写以支持自研引擎
    // 由于自研引擎不需要外部数据库连接，很多方法需要特殊处理
    
    @Override
    public void setCatalog(String catalog) throws SQLException {
        // 不支持
    }

    @Override
    public String getCatalog() throws SQLException {
        return "";
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        // 自研引擎默认只读
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return true;
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        // 不支持
    }

    @Override
    public int getHoldability() throws SQLException {
        return java.sql.ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public java.sql.Savepoint setSavepoint() throws SQLException {
        throw new SQLException("Savepoints not supported in native engine");
    }

    @Override
    public java.sql.Savepoint setSavepoint(String name) throws SQLException {
        throw new SQLException("Savepoints not supported in native engine");
    }

    @Override
    public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
        throw new SQLException("Savepoints not supported in native engine");
    }

    @Override
    public void rollback(java.sql.Savepoint savepoint) throws SQLException {
        throw new SQLException("Savepoints not supported in native engine");
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        // 不支持
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        // 不支持
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return new java.util.HashMap<>();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
    }

    @Override
    public void close() throws SQLException {
        if (closed) {
            return;
        }
        
        try {
            if (datastore != null && query != null) {
                datastore.close(query);
            }
            shutdown();
            closed = true;
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Failed to close connection", e);
            throw e;
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Failed to close connection", e);
            throw new SQLException("Failed to close connection: " + e.getMessage(), e);
        }
    }

    @Override
    public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return new xlNativeStatement(this);
    }

    @Override
    public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, 
            int resultSetHoldability) throws SQLException {
        return new xlNativeStatement(this);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return sql; // 自研引擎使用原生SQL
    }

    @Override
    public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
        throw new SQLException("CallableStatement not supported in native engine");
    }

    @Override
    public java.sql.CallableStatement prepareCall(String sql, int resultSetType, 
            int resultSetConcurrency) throws SQLException {
        throw new SQLException("CallableStatement not supported in native engine");
    }

    @Override
    public java.sql.CallableStatement prepareCall(String sql, int resultSetType, 
            int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new SQLException("CallableStatement not supported in native engine");
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
        return new xlNativePreparedStatement(this, sql);
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        return new xlNativePreparedStatement(this, sql);
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes)
            throws SQLException {
        return new xlNativePreparedStatement(this, sql);
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, 
            int resultSetConcurrency) throws SQLException {
        return new xlNativePreparedStatement(this, sql);
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, 
            int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new xlNativePreparedStatement(this, sql);
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames)
            throws SQLException {
        return new xlNativePreparedStatement(this, sql);
    }
}

