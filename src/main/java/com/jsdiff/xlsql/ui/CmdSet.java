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
 * Set new values in configuration file.
 * 
 * @author daichangya
 */
public class CmdSet implements IStateCommand {
    private XlUi xldba;

    /**
     * Creates a new instance of this class.
     * 
     * @param dba object
     */
    public CmdSet(final XlUi dba) {
        xldba = dba;
    }

    /**
     * Show (all) configuration parameter(s).
     * 
     * @return state
     * @throws xlException
     */
    public final int execute() {
        try {
            if (xldba.commandline.getOptionValues("set").length != 2) {
                throw new xlException("..? Enter h for help");
            }

            String parameter = xldba.commandline.getOptionValues("set")[0];
            String value = xldba.commandline.getOptionValues("set")[1];

            if ("log".equalsIgnoreCase(parameter)) {
                xldba.instance.setLog(value);
            } else if ("database".equalsIgnoreCase(parameter)) {
                xldba.instance.setDatabase(value);
            } else if ("engine".equalsIgnoreCase(parameter)) {
                xldba.instance.setEngine(value);
            } else if (("driver".equalsIgnoreCase(parameter))) {
                xldba.instance.setDriver(value);
            } else if (("url".equalsIgnoreCase(parameter))) {
                xldba.instance.setUrl(value);
            } else if (("schema".equalsIgnoreCase(parameter))) {
                xldba.instance.setSchema(value);
            } else if (("user".equalsIgnoreCase(parameter))) {
                xldba.instance.setUser(value);
            } else if (("password".equalsIgnoreCase(parameter))) {
                xldba.instance.setPassword(value);
            } else {
                throw new xlException("..? Enter h for help");
            }

            System.out.println(parameter + " set to " + value);
        } catch (xlException xe) {
            System.out.println(xe.getMessage());
        } finally {
            System.out.println("");
        }
        return 0;
    }
}

