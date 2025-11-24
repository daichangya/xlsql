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

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import static com.jsdiff.xlsql.jdbc.Constants.DRIVER;
import static com.jsdiff.xlsql.jdbc.Constants.URL_PFX_XLS;


/**
 * Open command.
 * 
 * @author daichangya
 */
public class CmdOpen implements IStateCommand {
    private XlUi xldba;

    /**
     * Creates a new instance of this class.
     * 
     * @param dba object
     */
    public CmdOpen(final XlUi dba) {
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

        // assume: commandline not null
        try {
            String database;

            if (xldba.commandline.hasOption("o")) {
                database = xldba.instance.getDatabase();
            } else { // assume "open"
                database = xldba.commandline.getOptionValue("open");
                xldba.instance.setDatabase(database);
            }

            // 使用 getDeclaredConstructor().newInstance() 替代已废弃的 newInstance()
            Driver d = (Driver) Class.forName(DRIVER).getDeclaredConstructor().newInstance();
            ExcelDriver ed = new ExcelDriver(d);
            String url = URL_PFX_XLS + database;
            xldba.con = ed.connect(url, new Properties());
            ret = xldba.OPEN;
            System.out.println(database + " open");
        } catch (ClassNotFoundException nfe) {
            System.out.println("driver not found. Classpath set ?");
        } catch (InstantiationException ie) {
            System.out.println("ERR: while instantiating. ???");
        } catch (IllegalAccessException iae) {
            System.out.println("ERR: illegal access. Privileges?");
        } catch (NoSuchMethodException nsme) {
            System.out.println("ERR: driver constructor not found: " + nsme.getMessage());
        } catch (java.lang.reflect.InvocationTargetException ite) {
            System.out.println("ERR: error invoking driver constructor: " + ite.getMessage());
        } catch (SQLException sqe) {
            System.out.println(sqe.getMessage() + " :" + sqe.getSQLState());
        } catch (Exception xe) {
            System.out.println(xe.getMessage()
                               + "'... use xldba `ping` to establish cause");
        } finally {
            System.out.println("");
        }
        return ret;
    }
}

