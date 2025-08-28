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

/**
 * Creates command objects.
 * 
 * @version $Revision: 1.3 $
 * @author $author$
 */
public class XlUiParser {
    /**
     * Creates a new xlDbaParser object.
     */
    XlUiParser() { }

    /**
     * Creates an executable command for its requestor.
     * 
     * @param xldba Apache Commandline object
     * 
     * @return Executable command
     */
    final IStateCommand parseC(final XlUi xldba) {
        IStateCommand ret = null;

        // now lets interrogate the options and create command objects
        if (xldba.commandline.hasOption("cat")) {
            ret = new CmdCat(xldba.getExporter());
        } else if (xldba.commandline.hasOption("close")) {
            ret = new CmdClose(xldba);
        } else if (xldba.commandline.hasOption("c")) {
            ret = new CmdConnect(xldba);
        } else if (xldba.commandline.hasOption("d")) {
            ret = new CmdDisconnect(xldba);
        } else if (xldba.commandline.hasOption("dir2xl")) {
            ret = new CmdDir2xl(xldba);
        } else if (xldba.commandline.hasOption("engine")) {
            ret = new CmdEngine(xldba);
        } else if (xldba.commandline.hasOption("export")) {
            ret = new CmdExport(xldba);
        } else if (xldba.commandline.hasOption("h")) {
            ret = new CmdUsage(xldba);
        } else if ((xldba.commandline.hasOption("o"))
                       || (xldba.commandline.hasOption("open"))) {
            ret = new CmdOpen(xldba);
        } else if (xldba.commandline.hasOption("ping")) {
            ret = new CmdPing(xldba);
        } else if ((xldba.commandline.hasOption("r")) 
                        || (xldba.commandline.hasOption("read"))) {
            ret = new CmdRead(xldba);
        } else if (xldba.commandline.hasOption("set")) {
            ret = new CmdSet(xldba);
        } else if (xldba.commandline.hasOption("script")) {
            ret = new CmdScript(xldba);
        } else if (xldba.commandline.hasOption("show")) {
            ret = new CmdShow(xldba);
        } else if (xldba.commandline.hasOption("sql")) {
            ret = new CmdSql(xldba);
        } else if (xldba.commandline.hasOption("t")) {
            ret = new CmdTime();
        }

        return ret;
    }
}

