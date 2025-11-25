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

import com.jsdiff.xlsql.database.xlException;


/**
 * Engine command.
 * 
 * @author daichangya
 */
public class CmdEngine implements IStateCommand {
    private XlUi xldba;

    /**
     * Creates a new instance of this class.
     * @param dba object
     */
    public CmdEngine(final XlUi dba) {
        xldba = dba;
    }

    /**
     * Engine set, add or remove.
     * 
     * @return state connected
     * @throws xlException
     */
    public final int execute() {
        // assume: commandline not null
        try {
            if (xldba.commandline.getOptionValues("engine").length != 2) {
                throw new xlException("..? Enter h for help");
            }

            String action = xldba.commandline.getOptionValues("engine")[0];
            String name = xldba.commandline.getOptionValues("engine")[1];

            if ("add".equalsIgnoreCase(action)) {
                xldba.instance.addEngine(name);
                System.out.println(name + " added as engine.");
            } else if (("remove".equalsIgnoreCase(action))) {
                xldba.instance.removeEngine(name);
                System.out.println(name + " removed as engine.");
            } else if (("set".equalsIgnoreCase(action))) {
                xldba.instance.setEngine(name);
                System.out.println(name + " set as active engine.");
            } else {
                throw new xlException("..? Enter h for help");
            }
        } catch (xlException xe) {
            System.out.println(xe.getMessage());
        }

        System.out.println("");

        return 0;
    }
}

