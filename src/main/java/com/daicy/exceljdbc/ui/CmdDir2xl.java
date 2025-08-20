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
package com.daicy.exceljdbc.ui;

import com.daicy.exceljdbc.database.export.ASqlFormatter;
import com.daicy.exceljdbc.database.export.xlSqlFormatterFactory;
import com.daicy.exceljdbc.database.xlException;
import com.daicy.exceljdbc.jdbc.xlConnection;
import com.daicy.exceljdbc.util.xlFile;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * dir2xl.
 * [ all | files | subdirs ] in [ path ] as [ list | tree ] to  [(sch.)table ]
 * 
 * @author daichangya
 */
public class CmdDir2xl implements IStateCommand {
    private XlUi xldba;
    private int selection;
    private xlFile path;
    private boolean recursive;
    private String dialect;
    private String schema = "";
    private String table = "";

    /**
     * Creates a new instance of this class.
     * 
     * @param dba object
     */
    public CmdDir2xl(final XlUi dba) {
        xldba = dba;
    }

    /**
     * @return state connected
     */
    public final int execute() {
        try {
            validate();

            File[] f = path.listFiles(selection, recursive);
            ASqlFormatter sqlformat = xlSqlFormatterFactory.create(dialect);

            Statement stm = xldba.con.createStatement();
            String[] col = {"PATH", "NAME"};
            String[] typ = {"VARCHAR", "VARCHAR"};
            String[] val = {null, null};
            String sql;


            // BEGIN SQL
            //    CREATE TABLE "BOOK.SHEET" (PATH VARCHAR, NAME VARCHAR);
            sql = sqlformat.wDropTable(schema, table);
            stm.execute(sql);
            sql = sqlformat.wCreateTable(schema, table, col, typ);
            stm.execute(sql);

            // END SQL
            for (int i = 0; i < f.length; i++) {

                // BEGIN SQL
                //    INSERT INTO "BOOK.SHEET" VALUES (?path?, ?name?);
                val[0] = f[i].getPath();
                val[1] = f[i].getName();
                sql = sqlformat.wInsert(schema, table, col, typ, val);
                stm.execute(sql);
            }

            System.out.println("Close database to commit");
        } catch (xlException xe) {
            System.out.println(xe.getMessage());
        } catch (SQLException sqe) {
            System.out.println(sqe.getMessage() + ":" + sqe.getSQLState() 
                                    + "' existing table..??");
        } finally {
            System.out.println("");
        }

        return 0;
    }

    private boolean validate() throws xlException {
        boolean ret = false;

        if (xldba.commandline.getOptionValues("dir2xl").length != 7) {
            throw new xlException("invalid number of parameters");
        }

        String[] args = xldba.commandline.getOptionValues("dir2xl");


        // [all | files | subdirs]
        selection = 0;

        if (args[0].equalsIgnoreCase("all")) {
            selection = 0;
        } else if (args[0].equalsIgnoreCase("files")) {
            selection = 1;
        } else if (args[0].equalsIgnoreCase("subdirs")) {
            selection = -1;
        } else {
            throw new xlException(args[0] + "!...?");
        }


        // [path]
        path = new xlFile(args[2]);

        if (!path.isDirectory()) {
            throw new xlException("is " + args[2] + " a directory...?!");
        }

        // [list|tree]
        if (args[4].equalsIgnoreCase("list")) {
            recursive = false;
        } else if (args[4].equalsIgnoreCase("tree")) {
            recursive = true;
        } else {
            throw new xlException(args[4] + "!...??");
        }


        // to [ (schema.)table ]
        dialect = ((xlConnection) xldba.con).getDialect();

        if (dialect.equals("hsqldb")) {
            if (args[6].indexOf(".") > 0) {
                String[] tmp = args[6].split("[.]");
                schema = tmp[0];
                table = tmp[1];
            } else {
                schema = "SA";
                table = args[6];
            }
        } else if (dialect.equals("mysql")) {
            if (args[6].indexOf(".") > 0) {
                String[] tmp = args[6].split("[.]");
                schema = tmp[0];
                table = tmp[1];
            } else {
                throw new xlException(args[6] + " for MySQL...!?");
            }
        } else {
            assert ret;
        }

        ret = true;

        return ret;
    }
}

