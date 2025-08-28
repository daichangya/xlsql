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
package com.jsdiff.xlsql.database.export;


/**
 * DOCUMENT ME!
 * 
 * @author daichangya
 */
public class xlHsqldbFormatter extends ASqlFormatter {
    /**
     * TODO: javadoc
     * 
     * @param s
     * 
     * @return sql string for 'CREATE SCHEMA'
     */
    public String wCreateSchema(String s) {
        return "--";
    }

    /**
     * TODO: javadoc
     * 
     * @param s
     * @param t
     * 
     * @return sql string for 'DROP TABLE'
     */
    public String wDropTable(String s, String t) {
        String sql;
        sql = "DROP TABLE " + getTableName(s, t) + " IF EXISTS;";

        return sql;
    }

    protected String getTableName(String s, String t) {
        String tablename;

        if (s.equalsIgnoreCase("sa")) {
            tablename = "\"" + t + "\"";
        } else {
            tablename = "\"" + s + "." + t + "\"";
        }

        return tablename;
    }
    
    public String wLast() {
        return "";
    }
    
}

