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
package com.jsdiff.xlsql.jdbc;

import com.jsdiff.xlsql.database.*;
import com.jsdiff.xlsql.database.export.*;
import com.jsdiff.xlsql.database.sql.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * xlConnection - Base class for Excel SQL connections
 * 
 * @version 2.0
 * @author daichangya
 */
public abstract class xlConnection implements Connection, Constants {
    private static final Logger LOGGER = Logger.getLogger(xlConnection.class.getName());
    
    protected ADatabase datastore;
    protected Connection dbCon;
    protected String URL;
    protected ASqlFormatter w;
    protected ASqlParser xlsql;
    protected ASqlSelect query;
    protected boolean closed;
    protected String dialect;

    /**
     * Shuts down the connection
     * 
     * @throws Exception If shutdown fails
     */
    public abstract void shutdown() throws Exception;

    /**
     * Factory method to create appropriate connection based on database engine
     * 
     * @param url JDBC URL
     * @param c Backend connection
     * @param schema Database schema
     * @return Appropriate xlConnection implementation
     * @throws SQLException If connection creation fails
     */
    public static xlConnection factory(String url, Connection c, String schema)
            throws SQLException {
        if (c == null) {
            throw new SQLException("Backend connection cannot be null");
        }
        
        String engine = c.getMetaData().getDatabaseProductName();
        if (engine.contains("MySQL")) {
            return new xlConnectionMySQL(url, c, schema);
        } else {
            return new xlConnectionHSQLDB(url, c);
        }
    }

    /**
     * Sets auto-commit mode
     * 
     * @param autoCommit Auto-commit flag
     * @throws SQLException If setting fails
     */
    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        dbCon.setAutoCommit(autoCommit);
    }

    /**
     * Gets auto-commit mode
     * 
     * @return Auto-commit flag
     * @throws SQLException If retrieval fails
     */
    @Override
    public boolean getAutoCommit() throws SQLException {
        return dbCon.getAutoCommit();
    }

    /**
     * Gets the SQL dialect for this connection
     * 
     * @return SQL dialect name
     */
    public String getDialect() {
        return dialect;
    }

    /**
     * Sets the catalog
     * 
     * @param catalog Catalog name
     * @throws SQLException If setting fails
     */
    @Override
    public void setCatalog(String catalog) throws SQLException {
        dbCon.setCatalog(catalog);
    }

    /**
     * Gets the catalog
     * 
     * @return Catalog name
     * @throws SQLException If retrieval fails
     */
    @Override
    public String getCatalog() throws SQLException {
        return dbCon.getCatalog();
    }

    /**
     * Checks if connection is closed
     * 
     * @return true if closed
     * @throws SQLException If check fails
     */
    @Override
    public boolean isClosed() throws SQLException {
        return dbCon.isClosed();
    }

    /**
     * Sets result set holdability
     * 
     * @param holdability Holdability constant
     * @throws SQLException If setting fails
     */
    @Override
    public void setHoldability(int holdability) throws SQLException {
        dbCon.setHoldability(holdability);
    }

    /**
     * Gets result set holdability
     * 
     * @return Holdability constant
     * @throws SQLException If retrieval fails
     */
    @Override
    public int getHoldability() throws SQLException {
        return dbCon.getHoldability();
    }

    /**
     * Gets database metadata
     * 
     * @return Database metadata
     * @throws SQLException If retrieval fails
     */
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        DatabaseMetaData dbMeta = dbCon.getMetaData();
        return new xlDatabaseMetaData(this, dbMeta);
    }

    /**
     * Sets read-only mode
     * 
     * @param readOnly Read-only flag
     * @throws SQLException If setting fails
     */
    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        dbCon.setReadOnly(readOnly);
    }

    /**
     * Checks if connection is read-only
     * 
     * @return true if read-only
     * @throws SQLException If check fails
     */
    @Override
    public boolean isReadOnly() throws SQLException {
        return dbCon.isReadOnly();
    }

    /**
     * Creates a savepoint
     * 
     * @return Savepoint
     * @throws SQLException If creation fails
     */
    @Override
    public Savepoint setSavepoint() throws SQLException {
        Savepoint dbSave = dbCon.setSavepoint();
        return new xlSavepoint(this, dbSave);
    }

    /**
     * Creates a named savepoint
     * 
     * @param name Savepoint name
     * @return Savepoint
     * @throws SQLException If creation fails
     */
    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        Savepoint dbSave = dbCon.setSavepoint(name);
        return new xlSavepoint(this, dbSave);
    }

    /**
     * Sets transaction isolation level
     * 
     * @param level Isolation level constant
     * @throws SQLException If setting fails
     */
    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        dbCon.setTransactionIsolation(level);
    }

    /**
     * Gets transaction isolation level
     * 
     * @return Isolation level constant
     * @throws SQLException If retrieval fails
     */
    @Override
    public int getTransactionIsolation() throws SQLException {
        return dbCon.getTransactionIsolation();
    }

    /**
     * Sets type map
     * 
     * @param map Type map
     * @throws SQLException If setting fails
     */
    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        dbCon.setTypeMap(map);
    }

    /**
     * Gets type map
     * 
     * @return Type map
     * @throws SQLException If retrieval fails
     */
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return dbCon.getTypeMap();
    }

    /**
     * Gets warnings
     * 
     * @return SQL warnings
     * @throws SQLException If retrieval fails
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return dbCon.getWarnings();
    }

    /**
     * Clears warnings
     * 
     * @throws SQLException If clearing fails
     */
    @Override
    public void clearWarnings() throws SQLException {
        dbCon.clearWarnings();
    }

    /**
     * Closes the connection
     * 
     * @throws SQLException If closing fails
     */
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
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to close connection", e);
            throw new SQLException("Failed to close connection: " + e.getMessage(), e);
        }
    }

    /**
     * Commits the transaction
     * 
     * @throws SQLException If commit fails
     */
    @Override
    public void commit() throws SQLException {
        dbCon.commit();
    }

    /**
     * Creates a statement
     * 
     * @return Statement
     * @throws SQLException If creation fails
     */
    @Override
    public Statement createStatement() throws SQLException {
        Statement dbStm = dbCon.createStatement();
        return new xlStatement(this, dbStm);
    }

    /**
     * Creates a statement with result set type and concurrency
     * 
     * @param resultSetType Result set type
     * @param resultSetConcurrency Result set concurrency
     * @return Statement
     * @throws SQLException If creation fails
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        Statement dbStm = dbCon.createStatement(resultSetType, resultSetConcurrency);
        return new xlStatement(this, dbStm);
    }

    /**
     * Creates a statement with result set type, concurrency, and holdability
     * 
     * @param resultSetType Result set type
     * @param resultSetConcurrency Result set concurrency
     * @param resultSetHoldability Result set holdability
     * @return Statement
     * @throws SQLException If creation fails
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, 
            int resultSetHoldability) throws SQLException {
        Statement dbStm = dbCon.createStatement(resultSetType, resultSetConcurrency, 
                resultSetHoldability);
        return new xlStatement(this, dbStm);
    }

    /**
     * Gets native SQL
     * 
     * @param sql SQL statement
     * @return Native SQL
     * @throws SQLException If conversion fails
     */
    @Override
    public String nativeSQL(String sql) throws SQLException {
        return dbCon.nativeSQL(sql);
    }

    /**
     * Prepares a callable statement
     * 
     * @param sql SQL statement
     * @return Callable statement
     * @throws SQLException If preparation fails
     */
    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        CallableStatement dbClst = dbCon.prepareCall(sql);
        return new xlCallableStatement(this, dbClst, sql);
    }

    /**
     * Prepares a callable statement with result set type and concurrency
     * 
     * @param sql SQL statement
     * @param resultSetType Result set type
     * @param resultSetConcurrency Result set concurrency
     * @return Callable statement
     * @throws SQLException If preparation fails
     */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, 
            int resultSetConcurrency) throws SQLException {
        CallableStatement dbClst = dbCon.prepareCall(sql, resultSetType, resultSetConcurrency);
        return new xlCallableStatement(this, dbClst, sql);
    }

    /**
     * Prepares a callable statement with result set type, concurrency, and holdability
     * 
     * @param sql SQL statement
     * @param resultSetType Result set type
     * @param resultSetConcurrency Result set concurrency
     * @param resultSetHoldability Result set holdability
     * @return Callable statement
     * @throws SQLException If preparation fails
     */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, 
            int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        CallableStatement dbClst = dbCon.prepareCall(sql, resultSetType, 
                resultSetConcurrency, resultSetHoldability);
        return new xlCallableStatement(this, dbClst, sql);
    }

    /**
     * Prepares a statement
     * 
     * @param sql SQL statement
     * @return Prepared statement
     * @throws SQLException If preparation fails
     */
    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        PreparedStatement dbPstm = dbCon.prepareStatement(sql);
        return new xlPreparedStatement(this, dbPstm, sql);
    }

    /**
     * Prepares a statement with auto-generated keys
     * 
     * @param sql SQL statement
     * @param autoGeneratedKeys Auto-generated keys flag
     * @return Prepared statement
     * @throws SQLException If preparation fails
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        PreparedStatement dbPstm = dbCon.prepareStatement(sql, autoGeneratedKeys);
        return new xlPreparedStatement(this, dbPstm, sql);
    }

    /**
     * Prepares a statement with column indexes
     * 
     * @param sql SQL statement
     * @param columnIndexes Column indexes
     * @return Prepared statement
     * @throws SQLException If preparation fails
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
            throws SQLException {
        PreparedStatement dbPstm = dbCon.prepareStatement(sql, columnIndexes);
        return new xlPreparedStatement(this, dbPstm, sql);
    }

    /**
     * Prepares a statement with result set type and concurrency
     * 
     * @param sql SQL statement
     * @param resultSetType Result set type
     * @param resultSetConcurrency Result set concurrency
     * @return Prepared statement
     * @throws SQLException If preparation fails
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, 
            int resultSetConcurrency) throws SQLException {
        PreparedStatement dbPstm = dbCon.prepareStatement(sql, resultSetType, 
                resultSetConcurrency);
        return new xlPreparedStatement(this, dbPstm, sql);
    }

    /**
     * Prepares a statement with result set type, concurrency, and holdability
     * 
     * @param sql SQL statement
     * @param resultSetType Result set type
     * @param resultSetConcurrency Result set concurrency
     * @param resultSetHoldability Result set holdability
     * @return Prepared statement
     * @throws SQLException If preparation fails
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, 
            int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        PreparedStatement dbPstm = dbCon.prepareStatement(sql, resultSetType, 
                resultSetConcurrency, resultSetHoldability);
        return new xlPreparedStatement(this, dbPstm, sql);
    }

    /**
     * Prepares a statement with column names
     * 
     * @param sql SQL statement
     * @param columnNames Column names
     * @return Prepared statement
     * @throws SQLException If preparation fails
     */
    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames)
            throws SQLException {
        PreparedStatement dbPstm = dbCon.prepareStatement(sql, columnNames);
        return new xlPreparedStatement(this, dbPstm, sql);
    }

    /**
     * Releases a savepoint
     * 
     * @param savepoint Savepoint to release
     * @throws SQLException If release fails
     */
    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        dbCon.releaseSavepoint(savepoint);
    }

    /**
     * Rolls back the transaction
     * 
     * @throws SQLException If rollback fails
     */
    @Override
    public void rollback() throws SQLException {
        dbCon.rollback();
    }

    /**
     * Rolls back the transaction to a savepoint
     * 
     * @param savepoint Savepoint to roll back to
     * @throws SQLException If rollback fails
     */
    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        dbCon.rollback(savepoint);
    }

    /**
     * Initializes the connection by loading Excel data into the database
     * 
     * @throws SQLException If initialization fails
     */
    void startup() throws SQLException {
        try {
            // Extract directory path from URL
            String dir = URL.substring(URL_PFX_XLS.length());
            LOGGER.info("Mounting: " + dir + " using jdbc:jsdiff:excel");
            
            // Create database from directory
            datastore = xlDatabaseFactory.create(new File(dir), "xls");
            
            // Prepare SQL statements
            List<String> sqlStatements = new ArrayList<>();
            String[] schemas = datastore.getSchemas();
            
            // Process each schema
            for (String schema : schemas) {
                String[] tables = datastore.getTables(schema);
                
                // Process each table
                for (String table : tables) {
                    String[] columnNames = datastore.getColumnNames(schema, table);
                    String[] columnTypes = datastore.getColumnTypes(schema, table);
                    
                    // Drop table if it exists
                    String dropTableSql = w.wDropTable(schema, table);
                    if (dropTableSql != null) {
                        sqlStatements.add(dropTableSql);
                    }
                    
                    // Create table
                    sqlStatements.add(w.wCreateTable(schema, table, columnNames, columnTypes));
                    
                    // Insert data
                    int rowCount = datastore.getRows(schema, table);
                    int columnCount = columnNames.length;
                    String[][] dataMatrix = datastore.getValues(schema, table);
                    
                    if (columnNames != null && columnTypes != null) {
                        for (int row = 0; row < rowCount; row++) {
                            String[] values = new String[columnCount];
                            for (int col = 0; col < columnCount; col++) {
                                values[col] = dataMatrix[col][row];
                            }
                            sqlStatements.add(w.wInsert(schema, table, columnNames, columnTypes, values));
                        }
                    }
                }
            }
            
            // Execute all SQL statements
            try (Statement statement = dbCon.createStatement()) {
                for (String sql : sqlStatements) {
                    LOGGER.fine("Executing SQL: " + sql);
                    statement.executeUpdate(sql);
                }
            }
            
            LOGGER.info("Successfully loaded " + sqlStatements.size() + " SQL statements");
            
        } catch (xlDatabaseException e) {
            LOGGER.log(Level.SEVERE, "Database initialization failed", e);
            throw new SQLException("Failed to initialize database: " + e.getMessage(), e);
        }
    }
    
    /**
     * JDBC 4.1 methods - implemented with default behavior
     */
    
    @Override
    public void abort(java.util.concurrent.Executor executor) throws SQLException {
        throw new SQLFeatureNotSupportedException("abort not supported");
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("getNetworkTimeout not supported");
    }
    
    @Override
    public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) 
            throws SQLException {
        throw new SQLFeatureNotSupportedException("setNetworkTimeout not supported");
    }
    
    @Override
    public String getSchema() throws SQLException {
        throw new SQLFeatureNotSupportedException("getSchema not supported");
    }
    
    @Override
    public void setSchema(String schema) throws SQLException {
        throw new SQLFeatureNotSupportedException("setSchema not supported");
    }
}
