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
package com.jsdiff.excel.database.export;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * DOCUMENT ME!
 * 
 * @author daichangya
 *
 * Changed by Csongor Nyulas (csny): adapted to work with TIME cell types
 */
public class XmlFormatter extends ASqlFormatter {
    /**
     * TODO: javadoc
     * 
     * @param s
     * 
     * @return sql string for 'CREATE SCHEMA'
     */
    public String wCreateSchema(String schema) {
        return "";
    }

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
        return "<resultset>";
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
        return "";
    }

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
        String sql = "<row>";
        for (int i = 0; i < co.length; i++) {
            sql = sql + "<column";
            
            sql = sql + " name=\""+ co[i] + "\"";
            sql = sql + " type=\"" + ty[i] + "\">";
            
            if ("DOUBLE".equals(ty[i])) {
                sql = sql + va[i];
            } else if ("BIT".equals(ty[i])) {
                sql = sql + "Y";
            } else if ("DATE".equals(ty[i])) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                                                          "dd/MM/yyyy");
                    Date d;
                    d = dateFormat.parse(va[i]);
                    dateFormat.applyPattern("yyyy-MM-dd");
                    sql = sql + dateFormat.format(d);
                } catch (ParseException pe) {
                    sql = sql + "null";
                }
            } else if ("TIME".equals(ty[i])) {
            	try {
            		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            		Date d;
            		d = dateFormat.parse(va[i]);
            		dateFormat.applyPattern("HH:mm:ss");
            		sql = sql + "'" + dateFormat.format(d) + "'";
            	} catch (ParseException pe) {
            		sql = sql + "null";
            	}
            } else {
                sql = sql + va[i];
            }
            
            sql = sql + "</column>";
        }

        sql = sql + "</row>";

        return sql;
    }

    protected String getTableName(String schema, String table) {
        return null;
    }
    
    public String wLast() {
        return "</resultset>";
    }
    
}
