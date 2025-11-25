/*jsdiff.com

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
package com.jsdiff.xlsql.ui;

import java.sql.Connection;

/**
 * Ping command.
 * 
 * @author daichangya
 */
public class CmdPing implements IStateCommand {
    private XlUi xldba;

    /**
     * Creates a new instance of this class.
     * @param dba object
     */
    public CmdPing(final XlUi dba) {
        xldba = dba;
    }

    /**
     * Ping SQL engine.
     * 
     * @return state connected
     * @throws xlException
     */
    public final int execute() {
        Connection c = xldba.instance.connect();

        if (c != null) {
            System.out.println("Connection established\n");
        } else {
            System.out.println("NO REPLY\n");
        }        
        return 0;
    }
}

