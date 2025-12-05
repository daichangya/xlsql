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

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.daichangya.xlsql.database.ADatabase;
import io.github.daichangya.xlsql.database.xlDatabaseException;
import io.github.daichangya.xlsql.database.xlDatabaseFactory;
import io.github.daichangya.xlsql.database.export.ASqlFormatter;
import io.github.daichangya.xlsql.database.sql.ASqlParser;
import io.github.daichangya.xlsql.database.sql.ASqlSelect;
import io.github.daichangya.xlsql.engine.connection.xlConnectionNative;

/**
 * xlConnection - Excel SQL连接的基类
 * 
 * <p>该类是xlSQL连接实现的抽象基类，实现了JDBC Connection接口。
 * 它负责管理Excel文件到数据库的映射，包括：</p>
 * <ul>
 *   <li>管理Excel数据存储（datastore）</li>
 *   <li>维护后端数据库连接（HSQLDB或H2）</li>
 *   <li>处理SQL语句的解析和执行</li>
 *   <li>在连接启动时将Excel数据加载到后端数据库</li>
 * </ul>
 * 
 * <p>子类需要实现shutdown()方法来清理资源。</p>
 * 
 * @version 2.0
 * @author daichangya
 */
public abstract class xlConnection implements Connection, Constants {
    /** 日志记录器 */
    private static final Logger LOGGER = Logger.getLogger(xlConnection.class.getName());
    
    /** Excel数据存储对象，管理Excel文件和表结构 */
    protected ADatabase datastore;
    /** 后端数据库连接（HSQLDB或H2） */
    protected Connection dbCon;
    /** JDBC连接URL */
    protected String URL;
    /** SQL格式化器，用于生成特定数据库的SQL语句 */
    protected ASqlFormatter w;
    /** SQL解析器，用于解析xlSQL语句 */
    public ASqlParser xlsql;
    /** SQL查询对象 */
    protected ASqlSelect query;
    /** 连接是否已关闭的标志 */
    protected boolean closed;
    /** SQL方言名称（如"hsqldb"、"h2"或"native"） */
    protected String dialect;

    /**
     * 关闭连接并清理资源
     * 
     * <p>子类需要实现此方法来执行特定的清理操作，如关闭后端连接等。</p>
     * 
     * @throws Exception 如果关闭失败则抛出异常
     */
    public abstract void shutdown() throws Exception;

    /**
     * 工厂方法：根据数据库引擎创建相应的连接实现
     * 
     * <p>该方法会检查后端数据库类型，创建相应的连接实现。
     * 支持HSQLDB、H2和自研NATIVE引擎。</p>
     * 
     * @param url JDBC连接URL
     * @param c 后端数据库连接对象（NATIVE引擎时可以为null）
     * @param schema 数据库模式名称
     * @return 相应的xlConnection实现
     * @throws SQLException 如果连接创建失败则抛出异常
     */
    public static xlConnection factory(String url, Connection c, String schema)
            throws SQLException {
        // 检查URL中是否指定了native引擎
        // 如果URL包含native标识或连接为null，使用自研引擎
        if (c == null || url.contains("native") || url.contains("NATIVE")) {
            // 自研引擎不需要外部数据库连接
            return new xlConnectionNative(url);
        }
        
        // 根据数据库产品名称判断使用哪个连接实现
        String engine = c.getMetaData().getDatabaseProductName();
        DatabaseType dbType = DatabaseType.fromEngineName(engine);
        switch (dbType) {
            case HSQLDB:
                return new xlConnectionHSQLDB(url, c);
            case H2:
            default:
                // H2数据库使用xlConnectionH2
                return new xlConnectionH2(url, c);
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
     * 初始化连接：将Excel数据加载到数据库
     * 
     * <p>该方法执行以下操作：</p>
     * <ol>
     *   <li>从URL中提取Excel文件目录路径</li>
     *   <li>创建数据存储对象，扫描Excel文件</li>
     *   <li>为每个Excel工作表生成SQL语句（DROP TABLE、CREATE TABLE、INSERT）</li>
     *   <li>在后端数据库中执行所有SQL语句，将Excel数据导入数据库</li>
     * </ol>
     * 
     * @throws SQLException 如果初始化失败则抛出异常
     */
    void startup() throws SQLException {
        try {
            // 从URL中提取目录路径（去掉"jdbc:xlsql:excel:"前缀）
            String dir = URL.substring(URL_PFX_XLS.length());
            LOGGER.info("Mounting: " + dir + " using jdbc:xlsql:excel");
            
            // 从目录创建数据库对象，扫描Excel文件
            datastore = xlDatabaseFactory.create(new File(dir), "xls");
            
            // 准备SQL语句列表
            List<String> sqlStatements = new ArrayList<>();
            String[] schemas = datastore.getSchemas();
            
            // 处理每个模式（对应Excel文件名）
            for (String schema : schemas) {
                String[] tables = datastore.getTables(schema);
                
                // 处理每个表（对应Excel工作表）
                for (String table : tables) {
                    String[] columnNames = datastore.getColumnNames(schema, table);
                    String[] columnTypes = datastore.getColumnTypes(schema, table);
                    
                    // 如果表已存在则先删除
                    String dropTableSql = w.wDropTable(schema, table);
                    if (dropTableSql != null) {
                        sqlStatements.add(dropTableSql);
                    }
                    
                    // 创建表结构
                    sqlStatements.add(w.wCreateTable(schema, table, columnNames, columnTypes));
                    
                    // 插入数据行
                    int rowCount = datastore.getRows(schema, table);
                    int columnCount = columnNames.length;
                    String[][] dataMatrix = datastore.getValues(schema, table);
                    
                    if (columnNames != null && columnTypes != null) {
                        // 为每一行数据生成INSERT语句
                        for (int row = 0; row < rowCount; row++) {
                            String[] values = new String[columnCount];
                            // 提取该行的所有列值
                            for (int col = 0; col < columnCount; col++) {
                                values[col] = dataMatrix[col][row];
                            }
                            // 生成INSERT语句并添加到列表
                            sqlStatements.add(w.wInsert(schema, table, columnNames, columnTypes, values));
                        }
                    }
                }
            }
            
            // 在后端数据库中执行所有SQL语句
            try (Statement statement = dbCon.createStatement()) {
                for (String sql : sqlStatements) {
                    if(!sql.contains("INSERT")){
                        LOGGER.info("Executing SQL: " + sql);
                    }
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
