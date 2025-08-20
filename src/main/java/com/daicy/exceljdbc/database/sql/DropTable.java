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
package com.daicy.exceljdbc.database.sql;

import com.daicy.exceljdbc.database.ADatabase;

import java.sql.SQLException;


/**
 * Native xlSql DROP TABLE.
 * 
 * @author daichangya
 */
public class DropTable implements ICommand {
    private ADatabase dbs;
    private String sch;
    private String tbl;

    /**
     * Creates a new instance of type xlSqlDropTable.
     * @param database database instance
     * @param schema name
     * @param table
     * @throws IllegalArgumentException
     */
    DropTable(final ADatabase database, final String schema, 
                                                        final String table) {
        if ((database != null) && (schema != null) && (table != null)) {
            dbs = database;
            sch = schema;
            tbl = table;
        } else {
            throw new IllegalArgumentException("null argument(s)");
        }
    }

    /**
     * Changelog reader for verifications.
     * @return true if allowed
     * @throws SQLException if an unexpected error occurs
     */
    public final boolean execAllowed() throws SQLException {
        boolean ret = true;

        return ret;
    }

    /**
     * Adds command to queue.
     * @throws SQLException if an unexpected error occurs
     */
    public final void execute() throws SQLException {
        dbs.removeTable(sch, tbl);
    }
}

