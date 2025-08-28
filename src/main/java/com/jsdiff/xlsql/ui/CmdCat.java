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
package com.jsdiff.xlsql.ui;

import com.jsdiff.xlsql.database.AExporter;

/**
 * xlCat command.
 * 
 * @author daichangya
 */
public class CmdCat implements IStateCommand {
    private AExporter exporter;

    /**
     * Creates a new instance of xlCmdCat.
     * 
     * @param exp database exporter object
     * @throws IllegalArgumentException
     */
    public CmdCat(final AExporter exp) {
        if (exp != null) {
            exporter = exp;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Prints database catalog.
     * 
     * @return next state
     */
    public final int execute() {
        String[] sch;
        String[] tab;

        System.out.println("CATALOG\n-------");
        sch = exporter.getSchemas();

        for (int i = 0; i < sch.length; i++) {
            System.out.println("schema --> " + sch[i]);
            tab = exporter.getTables(sch[i]);

            for (int j = 0; j < tab.length; j++) {
                System.out.println(" " + tab[j]);
            }
        }
        System.out.println("");
        return 0;
    }
}

