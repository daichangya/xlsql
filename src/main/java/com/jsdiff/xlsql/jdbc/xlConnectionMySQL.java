/*zthinker.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

/*
 * xlConnectionHSQL.java
 *
 * Created on 5 juli 2025, 23:59
 */
package com.jsdiff.xlsql.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import com.jsdiff.xlsql.database.export.xlSqlFormatterFactory;
import com.jsdiff.xlsql.database.sql.xlSqlParserFactory;
import com.jsdiff.xlsql.database.sql.xlSqlSelectFactory;

/**
 * xlConnectionMySQL - MySQL数据库引擎的连接实现
 * 
 * <p>该类是xlConnection的MySQL实现，用于使用MySQL作为后端数据库引擎。
 * 支持指定数据库模式（schema），在关闭连接时会安全地清理资源。</p>
 * 
 * @author daichangya
 *
 * Changed by Csongor Nyulas (csny): safe shutdown of the MySQL connection
 */
public class xlConnectionMySQL extends xlConnection {

    /** 日志记录器 */
    private static final Logger logger = Logger.getAnonymousLogger();

    /** MySQL数据库引擎标识符 */
    private static final String MYSQL = "mysql";
    /** MySQL数据库模式（schema）名称 */
    private String context;

    /**
     * 创建MySQL连接实例
     * 
     * <p>初始化MySQL特定的组件（SQL格式化器、解析器、查询对象），
     * 并启动连接（加载Excel数据到MySQL）。</p>
     * 
     * @param url JDBC连接URL
     * @param c MySQL后端数据库连接
     * @param schema MySQL数据库模式名称
     * @throws SQLException 如果连接创建失败则抛出异常
     */
    public xlConnectionMySQL(String url, Connection c, 
                             String schema) throws SQLException {
        dialect = "mysql";
        context = schema;
        URL = url;
        // 创建MySQL特定的SQL格式化器
        w = xlSqlFormatterFactory.create(MYSQL);
        dbCon = c;
        // 创建MySQL特定的查询对象
        query = xlSqlSelectFactory.create(MYSQL, dbCon);
        // 启动连接，加载Excel数据
        startup();
        // 创建MySQL特定的SQL解析器
        xlsql = xlSqlParserFactory.create(MYSQL, datastore, context);
    }

    //~ Methods ����������������������������������������������������������������

    @Override
    public void shutdown() throws Exception {
        if (!closed) {
            logger.info("Executing MySQL clean-up...");
            String[] schemas = datastore.getSchemas();
            Statement stm = dbCon.createStatement();

            //csny
            //Make this command safe. Since usually we will be not using context 
            //during the whole connection we want to be sure to avoid seeing an ugly 
            //exception at the time of closing the connection, even if the user
            //specified an invalid schema name
        	try {
        		stm.execute("USE " + context);
        	}
        	catch (SQLException e) {
        		logger.warning("Invalid schema name: " + context);
        	}
            for (int i = 0; i < schemas.length; i++) {
            	//csny
                //next command is useless because we commented out the 
            	//"v.add(w.wCreateSchema(s[i]));" command in jdbc.xlConnection. 
            	//We kept a safe version of it only for the case if we will later 
            	//revert the code to use the old datasource representation, as schemas
            	try {
            		stm.execute("DROP DATABASE " + schemas[i]);
            	}
            	catch (SQLException e) {
            	}
            	
                String[] t = datastore.getTables(schemas[i]);

                for (int j = 0; j < t.length; j++) {
                    if (w.wDropTable(schemas[i], t[j]) != null) {
                    	stm.execute(w.wDropTable(schemas[i], t[j]));
                    }
                }
            }
            dbCon.close();
            closed = true;
            logger.info("MySQL clean-up done");
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