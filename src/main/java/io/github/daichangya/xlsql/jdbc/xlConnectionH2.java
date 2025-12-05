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
 * xlConnectionH2.java
 *
 * Created on 2025-01-XX
 *
 * H2数据库引擎的连接实现，与HSQLDB高度兼容
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
 * xlConnectionH2 - H2数据库引擎的连接实现
 * 
 * <p>该类是xlConnection的H2实现，用于使用H2作为后端数据库引擎。
 * H2与HSQLDB在SQL语法上高度兼容，支持相同的表名引用格式和数据类型。
 * 在关闭连接时会安全地关闭H2数据库。</p>
 * 
 * @author daichangya
 */
public class xlConnectionH2 extends xlConnection {

    /** 日志记录器 */
    private static final Logger logger = Logger.getAnonymousLogger();

    /** H2数据库引擎标识符 */
    private static final String H2 = DatabaseType.H2.getName();;
    
    /**
     * 创建H2连接实例
     * 
     * <p>初始化H2特定的组件（SQL格式化器、解析器、查询对象），
     * 并启动连接（加载Excel数据到H2）。</p>
     * 
     * @param url JDBC连接URL
     * @param c H2后端数据库连接
     * @throws SQLException 如果连接创建失败则抛出异常
     */
    public xlConnectionH2(String url, Connection c) throws SQLException {
        dialect = H2;
        // 创建H2特定的SQL格式化器
        w = xlSqlFormatterFactory.create(H2);
        URL = url;
        dbCon = c;
        // 创建H2特定的查询对象
        query = xlSqlSelectFactory.create(H2, dbCon);
        // 启动连接，加载Excel数据
        startup();
        // 创建H2特定的SQL解析器（H2与HSQLDB语法兼容，可以复用hsqldb解析器）
        xlsql = xlSqlParserFactory.create("hsqldb", datastore, null);
    }

    /**
     * 安全关闭H2连接
     * 
     * <p>执行H2的SHUTDOWN命令来安全关闭数据库，然后关闭连接。
     * 如果连接已经关闭则直接返回。</p>
     * 
     * @throws Exception 如果关闭失败则抛出异常
     */
    @Override
    public void shutdown() throws Exception {
    	// 安全关闭，避免重复关闭
    	if (! closed) {
	        logger.info("Shutting down H2 ...");
	    	// 执行H2的SHUTDOWN命令（与HSQLDB相同）
	    	dbCon.createStatement().execute("SHUTDOWN");
	        // 关闭数据库连接
	        dbCon.close();
	        closed = true;
            logger.info("H2 is shut down");
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

