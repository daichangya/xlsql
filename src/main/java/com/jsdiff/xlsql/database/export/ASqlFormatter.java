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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * DOCUMENT ME!
 * 
 * @author daichangya
 *
 * Changed by Csongor Nyulas (csny): adapted to work with TIME cell types
 */
public abstract class ASqlFormatter {
    /**
     * TODO: javadoc
     * 
     * @param schema
     * 
     * @return sql string for 'CREATE SCHEMA'
     */
    abstract public String wCreateSchema(String schema);

    /**
     * TODO: javadoc
     * 
     * @param s
     * @param t
     * @param co
     * @param ty
     * 
     * @return sql string for 'CREATE TABLE'
     */
    public String wCreateTable(String s, String t, String[] co, String[] ty) {
        String sql;
        sql = "CREATE TABLE " + getTableName(s, t) + " ( ";

        boolean firstcolumn = true;

        for (int i = 0; i < co.length; i++) {
            if (firstcolumn) {
                firstcolumn = false;
            } else {
                sql = sql + ",";
            }

            sql = sql +"\"" +co[i] + "\" " + ty[i];
        }

        sql = sql + " );";

        return sql;
    }

    /**
     * TODO: javadoc
     * 
     * @param schema
     * @param table
     * 
     * @return sql string for 'DROP TABLE'
     */
    abstract public String wDropTable(String schema, String table);

    /**
     * TODO: javadoc
     * 
     * @param s
     * @param t
     * @param co
     * @param ty
     * @param va
     * 
     * @return sql string for 'INSERT INTO ... VALUES '
     */
    public String wInsert(String s, String t, String[] co, String[] ty, 
                          String[] va) {
        String sql;
        sql = "INSERT INTO " + getTableName(s, t) + " VALUES (";

        boolean firstcolumn = true;

        for (int i = 0; i < co.length; i++) {
            if (firstcolumn) {
                firstcolumn = false;
            } else {
                sql = sql + ",";
            }

            if (va[i] == null) {
                sql = sql + "null";

                continue;
            }

            if (va[i].equals("")) {
                sql = sql + "null";

                continue;
            }

            // jsdiff...            
            //  VERIFY FOR MySQL
            // Escape the presence of single quotes in varchar columns
            Pattern pattern = Pattern.compile("'");
            Matcher matcher = pattern.matcher(va[i]);
            va[i] = matcher.replaceAll("''");

            // End
            if ("DOUBLE".equals(ty[i]) || "BIT".equals(ty[i])) {
                sql = sql + va[i];
            } else if ("DATE".equals(ty[i])) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    java.util.Date d;
                    d = dateFormat.parse(va[i]);
                    dateFormat.applyPattern("yyyy-MM-dd");
                    sql = sql + "'" + dateFormat.format(d) + "'";
                } catch (ParseException pe) {
                    sql = sql + "null";
                }
            } else if ("TIME".equals(ty[i])) {
            	try {
            		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            		java.util.Date d;
            		d = dateFormat.parse(va[i]);
            		dateFormat.applyPattern("HH:mm:ss");
            		sql = sql + "'" + dateFormat.format(d) + "'";
            	} catch (ParseException pe) {
            		sql = sql + "null";
            	}
            } else {
                sql = sql + "'" + va[i] + "'";
            }
        }

        sql = sql + " );";

        return sql;
    }
    

    abstract protected String getTableName(String schema, String table);

    abstract public String wLast();
}

