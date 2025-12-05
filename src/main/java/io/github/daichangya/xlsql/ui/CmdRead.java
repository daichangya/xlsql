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
package io.github.daichangya.xlsql.ui;

import io.github.daichangya.xlsql.database.xlException;


/**
 * Read.
 * 
 * @author daichangya
 */
public class CmdRead implements IStateCommand {
    private XlUi xldba;

    /**
     * Creates a new instance of this class.
     * @param dba object
     */
    public CmdRead(final XlUi dba) {
        xldba = dba;
    }

    /**
     * Read bypass JDBC driver.
     * 
     * @return state connected
     * 
     */
    public final int execute() {
        int ret = 0;
        
        try {
            String database;
            if (xldba.commandline.hasOption("r")) {
                database = xldba.instance.getDatabase();
            } else { // assume "open"
                database = xldba.commandline.getOptionValue("read");
                xldba.instance.setDatabase(database);
            }
            xldba.setExporter(xldba.instance.getExporter(database));
            System.out.println("JDBC Driver bypass: " + database
                               + " opened for read");
            ret = xldba.READ;
        } catch (xlException xle) {
            System.out.println(xle.getMessage());
        } finally {
            System.out.println("");
        }
        return ret;
    }
}

