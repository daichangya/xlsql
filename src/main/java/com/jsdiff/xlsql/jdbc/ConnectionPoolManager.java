/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://excel.jsdiff.com
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
package com.jsdiff.xlsql.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connection pool manager for efficient database connection handling
 * 
 * @author daichangya
 */
public class ConnectionPoolManager {
    private static final Logger LOGGER = Logger.getLogger(ConnectionPoolManager.class.getName());
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final Map<String, ConnectionPool> pools = new ConcurrentHashMap<>();
    
    /**
     * Gets or creates a connection pool for the specified URL
     * 
     * @param url JDBC URL
     * @param info Connection properties
     * @param poolSize Maximum pool size
     * @return Connection pool
     */
    public static synchronized ConnectionPool getPool(String url, Properties info, int poolSize) {
        return pools.computeIfAbsent(url, k -> new ConnectionPool(url, info, poolSize));
    }
    
    /**
     * Gets or creates a connection pool with default size
     * 
     * @param url JDBC URL
     * @param info Connection properties
     * @return Connection pool
     */
    public static ConnectionPool getPool(String url, Properties info) {
        return getPool(url, info, DEFAULT_POOL_SIZE);
    }
    
    /**
     * Closes all connection pools
     */
    public static void closeAllPools() {
        pools.values().forEach(ConnectionPool::close);
        pools.clear();
    }
    
    /**
     * Connection pool implementation
     */
    public static class ConnectionPool {
        private final String url;
        private final Properties info;
        private final int maxSize;
        private final AtomicInteger activeConnections = new AtomicInteger(0);
        private final LinkedBlockingQueue<PooledConnection> availableConnections;
        private boolean closed = false;
        
        /**
         * Creates a new connection pool
         * 
         * @param url JDBC URL
         * @param info Connection properties
         * @param maxSize Maximum pool size
         */
        public ConnectionPool(String url, Properties info, int maxSize) {
            this.url = url;
            this.info = new Properties();
            this.info.putAll(info);
            this.maxSize = maxSize;
            this.availableConnections = new LinkedBlockingQueue<>(maxSize);
            
            // Pre-create some connections
            int initialSize = Math.min(3, maxSize);
            for (int i = 0; i < initialSize; i++) {
                try {
                    Connection conn = createRealConnection();
                    availableConnections.offer(new PooledConnection(conn, this));
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Failed to create initial connection", e);
                    break;
                }
            }
            
            LOGGER.info("Created connection pool for " + url + " with max size " + maxSize);
        }
        
        /**
         * Gets a connection from the pool
         * 
         * @return Pooled connection
         * @throws SQLException If getting connection fails
         */
        public synchronized Connection getConnection() throws SQLException {
            if (closed) {
                throw new SQLException("Connection pool is closed");
            }
            
            PooledConnection conn = availableConnections.poll();
            
            if (conn != null) {
                // Validate the connection
                if (isConnectionValid(conn.getRealConnection())) {
                    activeConnections.incrementAndGet();
                    return conn;
                } else {
                    // Connection is invalid, close it and create a new one
                    closeRealConnection(conn.getRealConnection());
                }
            }
            
            // Create a new connection if below max size
            if (activeConnections.get() < maxSize) {
                Connection realConn = createRealConnection();
                activeConnections.incrementAndGet();
                return new PooledConnection(realConn, this);
            }
            
            // Wait for a connection to become available
            try {
                conn = availableConnections.poll(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (conn != null) {
                    activeConnections.incrementAndGet();
                    return conn;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            throw new SQLException("Connection pool exhausted");
        }
        
        /**
         * Returns a connection to the pool
         * 
         * @param conn Connection to return
         */
        void returnConnection(PooledConnection conn) {
            if (closed) {
                closeRealConnection(conn.getRealConnection());
                return;
            }
            
            if (isConnectionValid(conn.getRealConnection())) {
                boolean returned = availableConnections.offer(conn);
                if (!returned) {
                    closeRealConnection(conn.getRealConnection());
                }
            } else {
                closeRealConnection(conn.getRealConnection());
            }
            
            activeConnections.decrementAndGet();
        }
        
        /**
         * Creates a real database connection
         * 
         * @return Real connection
         * @throws SQLException If creation fails
         */
        private Connection createRealConnection() throws SQLException {
            return DriverManager.getConnection(url, info);
        }
        
        /**
         * Checks if a connection is valid
         * 
         * @param conn Connection to check
         * @return true if valid
         */
        private boolean isConnectionValid(Connection conn) {
            try {
                return conn != null && !conn.isClosed() && conn.isValid(2);
            } catch (SQLException e) {
                return false;
            }
        }
        
        /**
         * Closes a real connection
         * 
         * @param conn Connection to close
         */
        private void closeRealConnection(Connection conn) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection", e);
                }
            }
        }
        
        /**
         * Closes the connection pool
         */
        public synchronized void close() {
            if (closed) {
                return;
            }
            
            closed = true;
            
            // Close all available connections
            PooledConnection conn;
            while ((conn = availableConnections.poll()) != null) {
                closeRealConnection(conn.getRealConnection());
            }
            
            LOGGER.info("Closed connection pool for " + url);
        }
    }
    
    /**
     * Pooled connection wrapper
     */
    private static class PooledConnection implements Connection {
        private final Connection realConnection;
        private final ConnectionPool pool;
        private boolean closed = false;
        
        /**
         * Creates a new pooled connection
         * 
         * @param realConnection Real connection
         * @param pool Connection pool
         */
        public PooledConnection(Connection realConnection, ConnectionPool pool) {
            this.realConnection = realConnection;
            this.pool = pool;
        }
        
        /**
         * Gets the real connection
         * 
         * @return Real connection
         */
        public Connection getRealConnection() {
            return realConnection;
        }
        
        /**
         * Closes the connection (returns it to the pool)
         */
        @Override
        public void close() throws SQLException {
            if (closed) {
                return;
            }
            
            closed = true;
            pool.returnConnection(this);
        }
        
        // Delegate all other methods to the real connection
        
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return realConnection.isWrapperFor(iface);
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return realConnection.unwrap(iface);
        }

        @Override
        public void abort(java.util.concurrent.Executor executor) throws SQLException {
            realConnection.abort(executor);
        }

        @Override
        public void clearWarnings() throws SQLException {
            realConnection.clearWarnings();
        }

        @Override
        public void commit() throws SQLException {
            realConnection.commit();
        }

        @Override
        public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return realConnection.createArrayOf(typeName, elements);
        }

        @Override
        public java.sql.Blob createBlob() throws SQLException {
            return realConnection.createBlob();
        }

        @Override
        public java.sql.Clob createClob() throws SQLException {
            return realConnection.createClob();
        }

        @Override
        public java.sql.NClob createNClob() throws SQLException {
            return realConnection.createNClob();
        }

        @Override
        public java.sql.SQLXML createSQLXML() throws SQLException {
            return realConnection.createSQLXML();
        }

        @Override
        public java.sql.Statement createStatement() throws SQLException {
            return realConnection.createStatement();
        }

        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return realConnection.createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return realConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return realConnection.createStruct(typeName, attributes);
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return realConnection.getAutoCommit();
        }

        @Override
        public String getCatalog() throws SQLException {
            return realConnection.getCatalog();
        }

        @Override
        public java.util.Properties getClientInfo() throws SQLException {
            return realConnection.getClientInfo();
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            return realConnection.getClientInfo(name);
        }

        @Override
        public int getHoldability() throws SQLException {
            return realConnection.getHoldability();
        }

        @Override
        public java.sql.DatabaseMetaData getMetaData() throws SQLException {
            return realConnection.getMetaData();
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            return realConnection.getNetworkTimeout();
        }

        @Override
        public String getSchema() throws SQLException {
            return realConnection.getSchema();
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            return realConnection.getTransactionIsolation();
        }

        @Override
        public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
            return realConnection.getTypeMap();
        }

        @Override
        public java.sql.SQLWarning getWarnings() throws SQLException {
            return realConnection.getWarnings();
        }

        @Override
        public boolean isClosed() throws SQLException {
            return closed || realConnection.isClosed();
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            return realConnection.isReadOnly();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            return !closed && realConnection.isValid(timeout);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            return realConnection.nativeSQL(sql);
        }

        @Override
        public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
            return realConnection.prepareCall(sql);
        }

        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return realConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return realConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
            return realConnection.prepareStatement(sql);
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return realConnection.prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return realConnection.prepareStatement(sql, columnIndexes);
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return realConnection.prepareStatement(sql, columnNames);
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return realConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return realConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
            realConnection.releaseSavepoint(savepoint);
        }

        @Override
        public void rollback() throws SQLException {
            realConnection.rollback();
        }

        @Override
        public void rollback(java.sql.Savepoint savepoint) throws SQLException {
            realConnection.rollback(savepoint);
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            realConnection.setAutoCommit(autoCommit);
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            realConnection.setCatalog(catalog);
        }

        @Override
        public void setClientInfo(java.util.Properties properties) throws java.sql.SQLClientInfoException {
            realConnection.setClientInfo(properties);
        }

        @Override
        public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException {
            realConnection.setClientInfo(name, value);
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            realConnection.setHoldability(holdability);
        }

        @Override
        public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException {
            realConnection.setNetworkTimeout(executor, milliseconds);
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            realConnection.setReadOnly(readOnly);
        }

        @Override
        public java.sql.Savepoint setSavepoint() throws SQLException {
            return realConnection.setSavepoint();
        }

        @Override
        public java.sql.Savepoint setSavepoint(String name) throws SQLException {
            return realConnection.setSavepoint(name);
        }

        @Override
        public void setSchema(String schema) throws SQLException {
            realConnection.setSchema(schema);
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            realConnection.setTransactionIsolation(level);
        }

        @Override
        public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {
            realConnection.setTypeMap(map);
        }
    }
}