/*zthinker.com

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
/*
 * xlConnectionHSQL.java
 *
 * Created on 5 juli 2025, 23:59
 *
 * Changed by Csongor Nyulas (csny): safe shutdown of HSQLDB connection
 */
package com.jsdiff.xlsql.jdbc;

import com.jsdiff.xlsql.database.export.xlSqlFormatterFactory;
import com.jsdiff.xlsql.database.sql.xlSqlParserFactory;
import com.jsdiff.xlsql.database.sql.xlSqlSelectFactory;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;


public class xlConnectionHSQLDB extends xlConnection {

    private static final Logger logger = Logger.getAnonymousLogger();

    private static final String HSQLDB = "hsqldb";
    
    public xlConnectionHSQLDB(String url, Connection c) throws SQLException {
        dialect = HSQLDB;
        w = xlSqlFormatterFactory.create(HSQLDB);
        URL = url;
        dbCon = c;
        query = xlSqlSelectFactory.create(HSQLDB, dbCon);
        startup();
        xlsql = xlSqlParserFactory.create(HSQLDB, datastore, null);
    }

    @Override
    public void shutdown() throws Exception {
    	//csny safe shutdown
    	if (! closed) {
	        logger.info("Shutting down HSQLDB ...");
	    	dbCon.createStatement().execute("SHUTDOWN");
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