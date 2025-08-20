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
package com.jsdiff.excel.database.sql;


import java.sql.SQLException;


/**
 * DOCUMENT ME!
 * 
 * @author daichangya
 */
public class xlSqlInsert implements ICommand {
    protected com.jsdiff.excel.database.ADatabase db;
    protected String _schema;
    protected String _table;

    /**
     * Creates a new instance of type xlSqlInsert.
     * 
     * @param database
     * @param schema
     * @param table
     * @throws NullPointerException DOCUMENT ME!
     */
    public xlSqlInsert(com.jsdiff.excel.database.ADatabase database,
                       String schema, String table) {
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
     * TODO: javadoc
     * 
     * @return true if allowed
     * 
     * @throws SQLException
     */
    public boolean execAllowed() throws SQLException {
        boolean ret = true;

        if (db.getRows(_schema, _table) > 65535) {
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
        db.addRow(_schema, _table);
        db.touchSchema(_schema);
        db.touchTable(_schema, _table);
    }
}

