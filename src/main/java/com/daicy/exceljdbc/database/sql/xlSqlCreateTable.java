/*zthinker.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

package com.daicy.exceljdbc.database.sql;

import java.sql.SQLException;

/**
 * Adds a table to an xlDatabase.
 * 
 * @author daichangya
 */
public class xlSqlCreateTable implements ICommand {
    //~ Instance variables �����������������������������������������������������

    protected com.daicy.exceljdbc.database.ADatabase db;
    protected String _schema;
    protected String _table;

    /**
     * Creates a new instance of type xlSqlCreateTable.
     *
     *
     * @param database 	xlDatabase
     * @param schema 	schema name
     * @param table 	table name
     */
    public xlSqlCreateTable(com.daicy.exceljdbc.database.ADatabase database, String schema, String table) {
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
    }

    /**
     * Verifies if this command can be executed.
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
        if (db.schemaOtherCaseExists(_schema)) {
            ret = false;
            throw new SQLException("(NATIVE:)schema will cause case conflict");
        }
        return ret;
    }

    /**
     * Executes the command.
     *
     * @throws SQLException if an unexpected error occurs.
     */
    public void execute() throws SQLException {
        db.addSchema(_schema);
        db.addTable(_schema, _table);
    }
}