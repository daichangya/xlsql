/*zthinker.com

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
package com.jsdiff.excel.jdbc;

//import com.jsdiff.excel.database.export.ASqlFormatter;
//import com.jsdiff.excel.database.sql.ASqlParser;
//import com.jsdiff.excel.database.sql.ASqlSelect;
//import com.jsdiff.excel.database.xlDatabaseException;
//import com.jsdiff.excel.database.xlDatabaseFactory;

import com.jsdiff.excel.database.*;
import com.jsdiff.excel.database.export.*;
import com.jsdiff.excel.database.sql.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;


/**
 * Class
 * 
 * @version $Revision: 1.31 $
 * @author daichangya
 *
 * Changed by Csongor Nyulas (csny): no unnecessary schemas are created for each datasource (Excel file)
 */
public abstract class xlConnection implements Connection, Constants {
    /** DOCUMENT ME! */
    public static final Logger logger = Logger.getAnonymousLogger();
    protected com.jsdiff.excel.database.ADatabase datastore;
    protected Connection dbCon;
    protected String URL;
    protected ASqlFormatter w;
    protected ASqlParser xlsql;
    protected ASqlSelect query;
    protected boolean closed;
    protected String dialect;

    /**
     * DOCUMENT ME!
     * 
     * @throws Exception DOCUMENT ME!
     */
    public abstract void shutdown() throws Exception;

    /**
     * DOCUMENT ME!
     * 
     * @param url DOCUMENT ME!
     * @param c DOCUMENT ME!
     * @param schema DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SQLException DOCUMENT ME!
     */
    public static xlConnection factory(String url, Connection c, String schema)
                                throws SQLException {
        String engine = c.getMetaData().getDatabaseProductName();
        if (engine.indexOf("MySQL") >= 0) {
            return new xlConnectionMySQL(url, c, schema);
        } else {
            return new xlConnectionHSQLDB(url, c);
        }
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#rollback
     */
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        dbCon.setAutoCommit(autoCommit);
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#getAutoCommit
     */
    public boolean getAutoCommit() throws SQLException {
        return dbCon.getAutoCommit();
    }

    /**
     * returns the sql dialect which should be sent to this connection
     * possible values are hsql and mysql
     */
    public String getDialect() {
        return dialect;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#setCatalog
     */
    public void setCatalog(String catalog) throws SQLException {
        dbCon.setCatalog(catalog);
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#getCatalog
     */
    public String getCatalog() throws SQLException {
        return dbCon.getCatalog();
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#isClosed
     */
    public boolean isClosed() throws SQLException {
        return dbCon.isClosed();
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#setHoldability
     */
    public void setHoldability(int holdability) throws SQLException {
        dbCon.setHoldability(holdability);
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#getHoldability
     */
    public int getHoldability() throws SQLException {
        return dbCon.getHoldability();
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#getMetaData
     */
    public DatabaseMetaData getMetaData() throws SQLException {
        DatabaseMetaData dbMeta = dbCon.getMetaData();
        DatabaseMetaData meta = new xlDatabaseMetaData(this, dbMeta);

        return meta;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#setReadOnly
     */
    public void setReadOnly(boolean readOnly) throws SQLException {
        //jsdiff...
        //  [ ISSUE ]        
        dbCon.setReadOnly(readOnly);

        //End        
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#isReadOnly
     */
    public boolean isReadOnly() throws SQLException {
        return dbCon.isReadOnly();
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#setSavepoint
     */
    public Savepoint setSavepoint() throws SQLException {
        Savepoint dbSave = dbCon.setSavepoint();
        Savepoint save = new xlSavepoint(this, dbSave);

        return save;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#setSavepoint
     */
    public Savepoint setSavepoint(String name) throws SQLException {
        Savepoint dbSave = dbCon.setSavepoint(name);
        Savepoint save = new xlSavepoint(this, dbSave);

        return save;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#setTransactionIsolation
     */
    public void setTransactionIsolation(int level) throws SQLException {
        dbCon.setTransactionIsolation(level);
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#getTransactionIsolation
     */
    public int getTransactionIsolation() throws SQLException {
        return dbCon.getTransactionIsolation();
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#setTypeMap
     */
    public void setTypeMap(java.util.Map map) throws SQLException {
        dbCon.setTypeMap(map);
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#getTypeMap
     */
    public java.util.Map getTypeMap() throws SQLException {
        return dbCon.getTypeMap();
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#getWarnings
     */
    public SQLWarning getWarnings() throws SQLException {
        return dbCon.getWarnings();
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#clearWarnings
     */
    public void clearWarnings() throws SQLException {
        dbCon.clearWarnings();
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#close
     */
    public void close() throws SQLException {
        try {
            datastore.close(query);
            shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#commit
     */
    public void commit() throws SQLException {
        dbCon.commit();
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#createStatement
     */
    public Statement createStatement() throws SQLException {
        Statement dbStm = dbCon.createStatement();
        Statement stmt = new xlStatement(this, dbStm);

        return stmt;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#createStatement
     */
    public Statement createStatement(int resultSetType, 
                                     int resultSetConcurrency)
                              throws SQLException {
        Statement dbStm = dbCon.createStatement(resultSetType, 
                                                resultSetConcurrency);
        Statement stmt = new xlStatement(this, dbStm);

        return stmt;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#createStatement
     */
    public Statement createStatement(int resultSetType, 
                                     int resultSetConcurrency, 
                                     int resultSetHoldability)
                              throws SQLException {
        Statement dbStm = dbCon.createStatement(resultSetType, 
                                                resultSetConcurrency);
        Statement stmt = new xlStatement(this, dbStm);

        return stmt;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#nativeSQL
     */
    public String nativeSQL(String sql) throws SQLException {
        return dbCon.nativeSQL(sql);
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#prepareCall
     */
    public CallableStatement prepareCall(String sql) throws SQLException {
        CallableStatement dbClst = dbCon.prepareCall(sql);
        CallableStatement clst = new xlCallableStatement(this, dbClst, sql);

        return clst;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#prepareCall
     */
    public CallableStatement prepareCall(String sql, int resultSetType, 
                                         int resultSetConcurrency)
                                  throws SQLException {
        CallableStatement dbClst = dbCon.prepareCall(sql, resultSetType, 
                                                     resultSetConcurrency);
        CallableStatement clst = new xlCallableStatement(this, dbClst, sql);

        return clst;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#prepareCall
     */
    public CallableStatement prepareCall(String sql, int resultSetType, 
                                         int resultSetConcurrency, 
                                         int resultSetHoldability)
                                  throws SQLException {
        CallableStatement dbClst = dbCon.prepareCall(sql, resultSetType, 
                                                     resultSetConcurrency, 
                                                     resultSetHoldability);
        CallableStatement clst = new xlCallableStatement(this, dbClst, sql);

        return clst;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#prepareStatement
     */
    public PreparedStatement prepareStatement(String sql)
                                       throws SQLException {
        PreparedStatement dbPstm = dbCon.prepareStatement(sql);
        PreparedStatement pstm = new xlPreparedStatement(this, dbPstm, sql);

        return pstm;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#prepareStatement
     */
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
                                       throws SQLException {
        PreparedStatement dbPstm = dbCon.prepareStatement(sql, 
                                                          autoGeneratedKeys);
        PreparedStatement pstm = new xlPreparedStatement(this, dbPstm, sql);

        return pstm;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#prepareStatement
     */
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
                                       throws SQLException {
        PreparedStatement dbPstm = dbCon.prepareStatement(sql, columnIndexes);
        PreparedStatement pstm = new xlPreparedStatement(this, dbPstm, sql);

        return pstm;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#prepareStatement
     */
    public PreparedStatement prepareStatement(String sql, int resultSetType, 
                                              int resultSetConcurrency)
                                       throws SQLException {
        PreparedStatement dbPstm = dbCon.prepareStatement(sql, resultSetType, 
                                                          resultSetConcurrency);
        PreparedStatement pstm = new xlPreparedStatement(this, dbPstm, sql);

        return pstm;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#prepareStatement
     */
    public PreparedStatement prepareStatement(String sql, int resultSetType, 
                                              int resultSetConcurrency, 
                                              int resultSetHoldability)
                                       throws SQLException {
        PreparedStatement dbPstm = dbCon.prepareStatement(sql, resultSetType, 
                                                          resultSetConcurrency, 
                                                          resultSetHoldability);
        PreparedStatement pstm = new xlPreparedStatement(this, dbPstm, sql);

        return pstm;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#prepareStatement
     */
    public PreparedStatement prepareStatement(String sql, String[] columnNames)
                                       throws SQLException {
        PreparedStatement dbPstm = dbCon.prepareStatement(sql, columnNames);
        PreparedStatement pstm = new xlPreparedStatement(this, dbPstm, sql);

        return pstm;
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#releaseSavepoint
     */
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        dbCon.releaseSavepoint(savepoint);
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#rollback
     */
    public void rollback() throws SQLException {
        dbCon.rollback();
    }

    /**
     * Implements method in interface java.sql.Connection
     * 
     * @see Connection#rollback
     */
    public void rollback(Savepoint savepoint) throws SQLException {
        dbCon.rollback(savepoint);
    }

    /**
     * TODO: javadoc
     * 
     * @throws SQLException DOCUMENT ME!
     */
    void startup() throws SQLException {
        try {
            String dir;

            dir = URL.substring(URL_PFX_XLS.length());
            logger.info("mounting: " + dir + " using jdbc:jsdiff:excel");
            datastore = xlDatabaseFactory.create(new File(dir), "xls");

            List<String> list = new ArrayList<>();

            String[] s = datastore.getSchemas();

            for (int i = 0; i < s.length; i++) {
                // -->
            	//csny
            	//No need for this since for each sheet in each Excel table 
            	//we create tables in the current schema
                //list.add(w.wCreateSchema(s[i]));

                String[] t = datastore.getTables(s[i]);

                for (int j = 0; j < t.length; j++) {
                    String[] cn = datastore.getColumnNames(s[i], t[j]);
                    String[] ty = datastore.getColumnTypes(s[i], t[j]);

                    // -->
                    //jsdiff...
                    //workaround for IF EXISTS known bug in McKoi 1.0.2
                    //list.add(w.wDropTable(s[i], t[j]));
                    if (w.wDropTable(s[i], t[j]) != null) {
                        list.add(w.wDropTable(s[i], t[j]));
                    }


                    //End                    
                    list.add(w.wCreateTable(s[i], t[j], cn, ty));

                    int R = datastore.getRows(s[i], t[j]);
                    int C = cn.length;
                    String[][] matrix = datastore.getValues(s[i], t[j]);
                    String[] va = new String[C];

                    if ((cn != null) && (ty != null)) {
                        for (int k = 0; k < R; k++) {
                            for (int m = 0; m < C; m++) {
                                va[m] = matrix[m][k];
                            }

                            list.add(w.wInsert(s[i], t[j], cn, ty, va));
                        }
                    }
                }
            }

            Statement stm = dbCon.createStatement();
            ListIterator l = list.listIterator();
            int i = 0;

            while (l.hasNext()) {
                String sql = (String) l.next();
                logger.info("sql=" + sql);
                stm.executeUpdate(sql);
//                stm.executeUpdate((String) l.next());
                i++;
            }
        } catch (xlDatabaseException xe) {
            xe.printStackTrace();
            throw new SQLException(xe.getMessage());
        }
    }
}

