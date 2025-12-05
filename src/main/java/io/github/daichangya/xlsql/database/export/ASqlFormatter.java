/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://xlsql.jsdiff.com
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
package io.github.daichangya.xlsql.database.export;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Abstract base class for SQL formatters.
 * Provides methods to generate SQL statements for different database engines.
 * 
 * @author daichangya
 * @version 2.0
 * 
 * Changed by Csongor Nyulas (csny): adapted to work with TIME cell types
 */
public abstract class ASqlFormatter {
    /**
     * Generates SQL string for creating a schema.
     * 
     * @param schema the schema name to create
     * @return SQL string for 'CREATE SCHEMA' statement
     */
    abstract public String wCreateSchema(String schema);

    /**
     * Generates SQL string for creating a table.
     * 
     * @param s schema name
     * @param t table name
     * @param co column names array
     * @param ty column types array
     * @return SQL string for 'CREATE TABLE' statement
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

            sql = sql + co[i]  + ty[i];
        }

        sql = sql + " );";

        return sql;
    }

    /**
     * Generates SQL string for dropping a table.
     * 
     * @param schema the schema name
     * @param table the table name to drop
     * @return SQL string for 'DROP TABLE' statement
     */
    abstract public String wDropTable(String schema, String table);

    /**
     * Generates SQL string for inserting data into a table.
     * 
     * @param s schema name
     * @param t table name
     * @param co column names array
     * @param ty column types array
     * @param va values array
     * @return SQL string for 'INSERT INTO ... VALUES' statement
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
    

    /**
     * Generates table name with optional schema prefix.
     * 
     * @param schema the schema name (can be null or empty)
     * @param table the table name
     * @return formatted table name (schema.table or just table)
     */
    abstract protected String getTableName(String schema, String table);

    /**
     * Generates the last SQL statement (typically a commit or similar).
     * 
     * @return SQL string for the final statement
     */
    abstract public String wLast();
}

