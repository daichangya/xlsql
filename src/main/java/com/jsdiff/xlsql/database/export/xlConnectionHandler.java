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
package com.jsdiff.xlsql.database.export;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ListIterator;


/**
 * @version $Revision: 1.3 $
 * @author $author$
 */
public class xlConnectionHandler implements IExportHandler {
    private Connection out;

    /**
     * Creates a new xlConnectionHandler object.
     *
     * @param jdbc
     */
    public xlConnectionHandler(Connection jdbc) {
        if (jdbc != null) {
            out = jdbc;
        } else {
            throw new IllegalArgumentException(); //desc
        }
    }

    /**
     *
     * @param export 
     *
     * @throws xlExportException 
     */
    public void write(List export) throws xlExportException {
        ListIterator l = export.listIterator();

        try {
            Statement stm = out.createStatement();

            while (l.hasNext()) {
                String sql;
                sql = (String) l.next();
                stm.execute(sql);
            }

        } catch (SQLException sqe) {
            throw new xlExportException("java.sql package reports: '"
                                        + sqe.getMessage() + ":"
                                        + sqe.getSQLState() + "' ..?");
        }
    }

    /**
     *
     * @throws xlExportException 
     */
    public void close() throws xlExportException {
        try {
            out.close();
        } catch (SQLException sqe) {
            throw new xlExportException("SQL Error while closing exception"
                                        + sqe.getMessage() + ":"
                                        + sqe.getSQLState() + "' ..?");
        }
    }
}

