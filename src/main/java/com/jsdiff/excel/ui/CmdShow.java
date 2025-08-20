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
package com.jsdiff.excel.ui;

import com.jsdiff.excel.database.xlException;


/**
 * Show values in configuration file.
 * 
 * @author daichangya
 */
public class CmdShow implements IStateCommand {
    private XlUi xldba;

    /**
     * Creates a new instance of this class.
     * @param dba object
     */
    public CmdShow(final XlUi dba) {
        xldba = dba;
    }

    /**
     * Show (all) configuration parameter(s).
     * 
     * @return state
     * 
     */
    public final int execute() {
        // assume: commandline not null
        String selection = xldba.commandline.getOptionValue("show");

        try {
            if ("all".equalsIgnoreCase(selection)) {
                String tmp = xldba.instance.getEngine(); //save engine :-(
                System.out.println(" log           : "
                                   + xldba.instance.getLog());
                System.out.println(" database      : "
                                   + xldba.instance.getDatabase());
                System.out.println(" active engine : "
                                   + xldba.instance.getEngine());

                String[] engines = xldba.instance.getEngines();

                for (int i = 0; i < engines.length; i++) {
                    if (!engines[i].equalsIgnoreCase("general")) {
                        xldba.instance.setEngine(engines[i]);
                        System.out.println("\nengine --> " + engines[i]);
                        System.out.println(" driver   : "
                                           + xldba.instance.getDriver());
                        System.out.println(" url      : "
                                           + xldba.instance.getUrl());
                        System.out.println(" schema   : "
                                           + xldba.instance.getSchema());
                        System.out.println(" user     : "
                                           + xldba.instance.getUser());
                        System.out.println(" password : "
                                           + xldba.instance.getPassword()
                                           + "\n");
                    }
                }

                xldba.instance.setEngine(tmp);
            } else if ("engine".equalsIgnoreCase(selection)) {
                System.out.println(" engine   : " + xldba.instance.getEngine());
            } else if ("driver".equalsIgnoreCase(selection)) {
                System.out.println(" driver   : " + xldba.instance.getDriver());
            } else if ("url".equalsIgnoreCase(selection)) {
                System.out.println(" url      : " + xldba.instance.getUrl());
            } else if ("schema".equalsIgnoreCase(selection)) {
                System.out.println(" schema   : " + xldba.instance.getSchema());
            } else if ("user".equalsIgnoreCase(selection)) {
                System.out.println(" user     : " + xldba.instance.getUser());
            } else if ("password".equalsIgnoreCase(selection)) {
                System.out.println(" password : "
                                   + xldba.instance.getPassword());
            } else {
                System.out.println("..? Enter h for help \n");
            }
        } catch (xlException xe) {
            System.out.println(xe.getMessage());
        }

        System.out.println("");

        return 0;
    }
}

