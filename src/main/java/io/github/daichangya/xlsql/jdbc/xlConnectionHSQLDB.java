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
 * xlConnectionHSQL.java
 *
 * Created on 5 juli 2025, 23:59
 *
 * Changed by Csongor Nyulas (csny): safe shutdown of HSQLDB connection
 */
package io.github.daichangya.xlsql.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Struct;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import io.github.daichangya.xlsql.database.export.xlSqlFormatterFactory;
import io.github.daichangya.xlsql.database.sql.xlSqlParserFactory;
import io.github.daichangya.xlsql.database.sql.xlSqlSelectFactory;


/**
 * xlConnectionHSQLDB - HSQLDB数据库引擎的连接实现
 * 
 * <p>该类是xlConnection的HSQLDB实现，用于使用HSQLDB作为后端数据库引擎。
 * 在关闭连接时会安全地关闭HSQLDB数据库。</p>
 * 
 * @author daichangya
 * 
 * Changed by Csongor Nyulas (csny): safe shutdown of HSQLDB connection
 */
public class xlConnectionHSQLDB extends xlConnection {

    /** 日志记录器 */
    private static final Logger logger = Logger.getAnonymousLogger();

    /** HSQLDB数据库引擎标识符 */
    private static final String HSQLDB = DatabaseType.HSQLDB.getName();;
    
    /**
     * 创建HSQLDB连接实例
     * 
     * <p>初始化HSQLDB特定的组件（SQL格式化器、解析器、查询对象），
     * 并启动连接（加载Excel数据到HSQLDB）。</p>
     * 
     * @param url JDBC连接URL
     * @param c HSQLDB后端数据库连接
     * @throws SQLException 如果连接创建失败则抛出异常
     */
    public xlConnectionHSQLDB(String url, Connection c) throws SQLException {
        dialect = HSQLDB;
        // 创建HSQLDB特定的SQL格式化器
        w = xlSqlFormatterFactory.create(HSQLDB);
        URL = url;
        dbCon = c;
        // 创建HSQLDB特定的查询对象
        query = xlSqlSelectFactory.create(HSQLDB, dbCon);
        // 启动连接，加载Excel数据
        startup();
        // 创建HSQLDB特定的SQL解析器
        xlsql = xlSqlParserFactory.create(HSQLDB, datastore, null);
    }

    /**
     * 安全关闭HSQLDB连接
     * 
     * <p>执行HSQLDB的SHUTDOWN命令来安全关闭数据库，然后关闭连接。
     * 如果连接已经关闭则直接返回。</p>
     * 
     * @throws Exception 如果关闭失败则抛出异常
     */
    @Override
    public void shutdown() throws Exception {
    	// csny: 安全关闭，避免重复关闭
    	if (! closed) {
	        logger.info("Shutting down HSQLDB ...");
	    	// 执行HSQLDB的SHUTDOWN命令
	    	dbCon.createStatement().execute("SHUTDOWN");
	        // 关闭数据库连接
	        dbCon.close();
	        closed = true;
            logger.info("HSQLDB is shut down");
    	}
    }

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
        return false;
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
        return null;
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
}