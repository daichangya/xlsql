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
package com.jsdiff.xlsql.ui;

import java.sql.SQLException;


/**
 * Close database.
 * 
 * @author daichangya
 */
public class CmdClose implements IStateCommand {
    private XlUi xldba;

    /**
     * Creates a new instance of this class.
     * 
     * @param dba object
     */
    public CmdClose(final XlUi dba) {
        xldba = dba;
    }

    /**
     * Open database.
     * 
     * @return state connected
     * 
     */
    public final int execute() {
        int ret = 0;
        try {
            // if a JDBC connection was established: commit changes
            if (xldba.con != null) {
                xldba.con.close();
                xldba.con = null;
                System.out.println("JDBC connection closed");
            }
            else {
                System.out.println("Exporter closed");
            }

            
        } catch (SQLException sqe) {
            System.out.println("SQL engine error: " + sqe.getMessage() + ":"
                               + sqe.getSQLState() + " Changes may be lost..?");
        } finally {
            ret = xldba.CONNECTED;
            System.out.println("");
        }
        
        return ret;
    }
}

