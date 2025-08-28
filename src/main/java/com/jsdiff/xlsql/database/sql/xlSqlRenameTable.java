/*zthinker.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

package com.jsdiff.xlsql.database.sql;

import java.sql.SQLException;

/**
 * DOCUMENT ME!
 * 
 * @author daichangya
 */
public class xlSqlRenameTable implements ICommand {
    //~ Instance variables �����������������������������������������������������

    protected com.jsdiff.xlsql.database.ADatabase db;
    protected String _schema;
    protected String _table;
    protected String _schema_old;
    protected String _table_old;

    /**
     * Creates a new instance of type xlSqlRenameTable.
     *
     *
     * @param database 	
     * @param schema 	
     * @param table 	
     */
    public xlSqlRenameTable(com.jsdiff.xlsql.database.ADatabase database, String schema, String table,
                            String schema_old, String table_old) {
        if (database == null) {
            throw new NullPointerException("xlSQL: database null");
        } else {
            db = database;
        }

        if (schema == null) {
            throw new NullPointerException("xlSQL: schema null");
        } else {
            _schema = schema;
        }

        if (table == null) {
            throw new NullPointerException("xlSQL: table null");
        } else {
            _table = table;
        }

        if (schema_old == null) {
            throw new NullPointerException("xlSQL: schema_old null");
        } else {
            _schema_old = schema_old;
        }

        if (table_old == null) {
            throw new NullPointerException("xlSQL: table_old null");
        } else {
            _table_old = table_old;
        }
    }

    /**
     * TODO: javadoc
     *
     * @return true if allowed
     *
     * @throws SQLException
     */
    public boolean execAllowed() throws SQLException {
        boolean ret = true;
        if (db.tableExists(_schema, _table)) {
            ret = false;
        }
        return ret;
    }

    /**
     * TODO: javadoc
     *
     * @throws SQLException
     */
    public void execute() throws SQLException {
        db.removeTable(_schema_old, _table_old);            
        db.addSchema(_schema);
        db.addTable(_schema, _table);
    }
}