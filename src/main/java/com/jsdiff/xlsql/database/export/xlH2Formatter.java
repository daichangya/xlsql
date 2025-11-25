/*jsdiff.com

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
 * SQL formatter for H2 database engine.
 * Generates H2-compatible SQL statements for schema, table, and data operations.
 * 
 * <p>H2与HSQLDB在SQL语法上高度兼容，支持相同的表名引用格式（双引号）和数据类型。
 * 因此大部分SQL生成逻辑与HSQLDB相同。</p>
 *
 * @author daichangya
 */
public class xlH2Formatter extends ASqlFormatter {
    /**
     * Generates SQL string for creating a schema in H2.
     * H2 doesn't require explicit schema creation in memory mode, so returns a comment.
     *
     * @param s the schema name
     * @return SQL comment string (H2 doesn't need CREATE SCHEMA in memory mode)
     */
    @Override
    public String wCreateSchema(String s) {
        return "--";
    }

    /**
     * Generates SQL string for creating a table in H2.
     * H2与HSQLDB使用相同的数据类型和表名引用格式。
     *
     * @param s schema name
     * @param t table name
     * @param co column names array
     * @param ty column types array
     * @return SQL string for 'CREATE TABLE' statement
     */
    @Override
    public String wCreateTable(String s, String t, String[] co, String[] ty) {
        String sql = "CREATE TABLE " + getTableName(s, t) + " ( ";
        boolean firstcolumn = true;

        for (int i = 0; i < co.length; i++) {
            if (firstcolumn) {
                firstcolumn = false;
            } else {
                sql = sql + ",";
            }

            sql = sql + "\"" + co[i] + "\" ";

            // 为 H2 处理 VARCHAR 类型，指定默认长度（与HSQLDB相同）
            if (ty[i].equalsIgnoreCase("VARCHAR")) {
                sql = sql + "VARCHAR(2048)";
            } else if (ty[i].equalsIgnoreCase("BIT")) {
                sql = sql + "CHAR(1)";
            } else {
                sql = sql + ty[i];
            }
        }

        sql = sql + " );";

        return sql;
    }


    /**
     * Generates SQL string for dropping a table in H2.
     *
     * @param s schema name
     * @param t table name
     * @return SQL string for 'DROP TABLE IF EXISTS' statement
     */
    @Override
    public String wDropTable(String s, String t) {
        String sql;
        sql = "DROP TABLE IF EXISTS " + getTableName(s, t) + ";";

        return sql;
    }

    /**
     * Generates table name for H2 with double quote escaping.
     * H2与HSQLDB使用相同的表名引用格式（双引号）。
     * 
     * @param s schema name
     * @param t table name
     * @return formatted table name with double quotes: "schema.table" or "table"
     */
    @Override
    protected String getTableName(String s, String t) {
        String tablename;

        if (s.equalsIgnoreCase("sa")) {
            tablename = "\"" + t + "\"";
        } else {
            tablename = "\"" + s + "." + t + "\"";
        }

        return tablename;
    }

    /**
     * Generates SQL string for inserting data into a table in H2.
     * H2与HSQLDB使用相同的数据类型和值格式。
     *
     * @param s schema name
     * @param t table name
     * @param co column names array
     * @param ty column types array
     * @param va column values array
     * @return SQL string for 'INSERT INTO ... VALUES' statement
     */
    @Override
    public String wInsert(String s, String t, String[] co, String[] ty,
                          String[] va) {
        String sql = "INSERT INTO " + getTableName(s, t) + " VALUES (";
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

            // 处理单引号转义
            Pattern pattern = Pattern.compile("'");
            Matcher matcher = pattern.matcher(va[i]);
            va[i] = matcher.replaceAll("''");

            // 对于所有类型，除了明确的数值和布尔类型，都应该用引号包围
            if ("DOUBLE".equals(ty[i]) || "BIT".equals(ty[i]) || "INTEGER".equals(ty[i]) || "BIGINT".equals(ty[i])) {
                // 数值类型不加引号
                sql = sql + va[i];
            } else if ("DATE".equals(ty[i])) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    java.util.Date d = dateFormat.parse(va[i]);
                    dateFormat.applyPattern("yyyy-MM-dd");
                    sql = sql + "'" + dateFormat.format(d) + "'";
                } catch (ParseException pe) {
                    sql = sql + "null";
                }
            } else if ("TIME".equals(ty[i])) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    java.util.Date d = dateFormat.parse(va[i]);
                    dateFormat.applyPattern("HH:mm:ss");
                    sql = sql + "'" + dateFormat.format(d) + "'";
                } catch (ParseException pe) {
                    sql = sql + "null";
                }
            } else {
                // 所有其他类型（包括VARCHAR等）都用单引号包围
                sql = sql + "'" + va[i] + "'";
            }
        }

        sql = sql + " );";
        return sql;
    }



    /**
     * Generates the last SQL statement for H2.
     * H2 doesn't require a final statement, so returns empty string.
     * 
     * @return empty string
     */
    public String wLast() {
        return "";
    }

}

