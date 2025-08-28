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

import com.jsdiff.xlsql.database.xlDatabaseException;
import com.jsdiff.xlsql.database.xlException;
import com.jsdiff.xlsql.util.xlFile;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;


/**
 * export [ ALL|schema|table ] AS [hsqldb|mysql|XML] TO [out|File|jdbc:].
 * 
 * @author daichangya
 */
public class CmdExport implements IStateCommand {
    private static final String EXPORT = "export";
    private static final int HSQLDB = 1;
    private static final int MYSQL = 2;
    private static final int XML = 3;
    private static final int OUT = 1;
    private static final int FILE = 2;
    private static final int JDBC = 3;

    //
    private XlUi xldba;

    //
    private boolean all;
    private String schema;
    private String table;
    private int format;
    private int handler;
    private PrintStream toOut;
    private File toFile;
    private Connection toCon;

    /**
     * Creates a new instance of this class.
     * 
     * @param dba object
     */
    public CmdExport(final XlUi dba) {
        xldba = dba;
    }

    /**
     *
     * @return  new state
     */
    public final int execute() {
        try {
            validate();

            switch (handler) {
                case OUT:

                    if (all) {
                        xldba.getExporter().export(format, toOut);
                    } else if (table.equals("")) {
                        xldba.getExporter().export(schema, format, toOut);
                    } else {
                        xldba.getExporter().export(schema, table, format, toOut);
                    }

                    break;

                case FILE:

                    if (all) {
                        xldba.getExporter().export(format, toFile);
                    } else if (table.equals("")) {
                        xldba.getExporter().export(schema, format, toFile);
                    } else {
                        xldba.getExporter().export(schema, table, format, toFile);
                    }

                    break;

                case JDBC:

                    if (all) {
                        xldba.getExporter().export(format, toCon);
                    } else if (table.equals("")) {
                        xldba.getExporter().export(schema, format, toCon);
                    } else {
                        xldba.getExporter().export(schema, table, format, toCon);
                    }

                default:
            }
        } catch (xlDatabaseException de) {
            System.out.println(de.getMessage());
        } catch (xlException xe) {
            System.out.println(xe.getMessage());
        } finally {
            System.out.println("");
        }

        return 0;
    }

    /**
     *
     * @throws xlException when invalid arguments
     */
    public final void validate() throws xlException {
        String[] tmp;

        // validate and parse
        if (xldba.commandline.getOptionValues(EXPORT).length != 5) {
            throw new xlException("..? Enter h for help");
        }

        // [ ALL | schema | (schema.table) ]
        String data = xldba.commandline.getOptionValues(EXPORT)[0];

        if ("all".equalsIgnoreCase(data)) {
            all = true;
        } else {
            all = false;

            if (data.indexOf(".") > 0) {
                tmp = data.split("[.]");
                schema = tmp[0];
                table = tmp[1];
            } else {
                schema = data;
                table = "";
            }
        }

        // [ hsqldb | mysql | XML ]
        String as = xldba.commandline.getOptionValues(EXPORT)[2];

        if ("hsqldb".equalsIgnoreCase(as)) {
            format = HSQLDB;
        } else if ("mysql".equalsIgnoreCase(as)) {
            format = MYSQL;
        } else if ("XML".equalsIgnoreCase(as)) {
            format = XML;
        } else {
            throw new xlException("as ..?! " + as);
        }

        // TO [out|File|jdbc:]
        String to = xldba.commandline.getOptionValues(EXPORT)[4];

        if ("out".equalsIgnoreCase(to)) {
            // out
            handler = OUT;
        } else {
            // engine --> jdbc connection
            boolean search = false;
            tmp = xldba.instance.getEngines();

            for (int i = 0; i < tmp.length; i++) {
                if (to.equalsIgnoreCase(tmp[i])) {
                    search = true;

                    break;
                }
            }

            if (search) {
                handler = JDBC;
                tmp[0] = xldba.instance.getEngine();
                xldba.instance.setEngine(to);
                toCon = xldba.instance.connect();
                xldba.instance.setEngine(tmp[0]);
            } else {
                // File
                String[] ext = {"txt"};
                if(format == XML) {
                    ext = new String[]{"xml"};
                }
                File f = xlFile.settle(to, ext);
                handler = FILE;
                toFile = f;
            }
        }
    }
}

