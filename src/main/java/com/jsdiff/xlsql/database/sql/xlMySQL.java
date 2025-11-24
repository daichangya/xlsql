/*zthinker.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

package com.jsdiff.xlsql.database.sql;



/**
 * xlMySQL class translates MySQL to native xlSQL
 * 
 * @author daichangya
 */
public class xlMySQL extends ASqlParser {
    //~ Instance variables �����������������������������������������������������

    private final int USE = 7;
    private String context;

    /**
     * Creates a new instance of type xlMySQL.
     *
     * @param xlsqldb xlSQL instance
     * @param mysqldb initial context ( sql.database in xlsql.properties )
     */
    public xlMySQL(com.jsdiff.xlsql.database.ADatabase xlsqldb,
                   String mysqldb) {
        super(xlsqldb);

        if (mysqldb == null) {
            throw new NullPointerException("xlSQL: mysql dbname null");
        }

        context = mysqldb;

        cmd = new String[8];
        cmd[INSERT] = "\\s*[Ii][Nn][Ss][Ee][Rr][Tt].*[Ii][Nn][Tt][Oo].*[Vv][Aa][Ll][Uu][Ee][Ss].*";        
        cmd[UPDATE] = "\\s*[Uu][Pp][Dd][Aa][Tt][Ee].*";
        cmd[DELETE] = "\\s*[Dd][Ee][Ll][Ee][Tt][Ee].*[Ff][Rr][Oo][Mm].*";
        cmd[CREATE_TABLE] = "\\s*[Cc][Rr][Ee][Aa][Tt][eE].*[Tt][Aa][Bb][Ll][Ee].*";
        cmd[DROP_TABLE] = "\\s*[Dd][Rr][Oo][Pp].*[Tt][Aa][Bb][Ll][Ee].*";
        cmd[RENAME_TABLE] = "\\s*[Aa][Ll][Tt][Ee][Rr].*[Tt][Aa][Bb][Ll][Ee].*" + 
                 "[Rr][Ee][Nn][Aa][Mm][Ee].*";
        cmd[ALTER_TABLE] = "\\s*[Aa][Ll][Tt][Ee][Rr].*[Tt][Aa][Bb][Ll][Ee].*" + 
                 "[Cc][Oo][Ll][Uu][Mm][Nn].*";
        cmd[USE] = "\\s*[Uu][Ss][Ee].*";
    }

    //~ Methods ����������������������������������������������������������������

    protected String[] getVars(int cmd, String sql) {
        String[] v = new String[4];
        String tmp = "";
        
        // 根据命令类型提取表名
        switch (cmd) {
        case INSERT:
            // INSERT INTO table (...) VALUES (...)
            tmp = sql.replaceAll(
                          "\\s*[Ii][Nn][Ss][Ee][Rr][Tt].*[Ii][Nn][Tt][Oo]\\s*", 
                          "");
            tmp = tmp.substring(0, tmp.indexOf("("));
            tmp = tmp.replaceAll("\\s*[Vv][Aa][Ll][Uu][Ee][Ss].*", "");
            break;

        case UPDATE:
            // UPDATE table SET ...
            tmp = sql.replaceAll("\\s*[Uu][Pp][Dd][Aa][Tt][Ee]\\s*", "");
            tmp = tmp.replaceAll("\\s*[Ss][Ee][Tt].*", "");
            break;

        case DELETE:
            // DELETE FROM table WHERE ...
            tmp = sql.replaceAll(
                          "\\s*[Dd][Ee][Ll][Ee][Tt][Ee].*[Ff][Rr][Oo][Mm]\\s*", 
                          "");
            tmp = tmp.replaceAll("\\s*[Ww][Hh][Ee][Rr][Ee].*", "");
            break;

        case CREATE_TABLE:
            // CREATE TABLE table (...)
            tmp = sql.replaceAll("\\s*[Cc][Rr][Ee][Aa][Tt][Ee]\\s*", "");
            tmp = tmp.substring(6); // 跳过"TABLE"
            tmp = tmp.replaceAll("\\s*[(].*", "");
            break;

        case DROP_TABLE:
            // DROP TABLE table IF EXISTS
            tmp = sql.replaceAll("\\s*[Dd][Rr][Oo][Pp]\\s*", "");
            tmp = tmp.substring(6); // 跳过"TABLE"
            tmp = tmp.replaceAll("\\s*[Ii][Ff].*[Ee][Xx][Ii][Ss][Tt][Ss]\\s*", "");
            break;

        case RENAME_TABLE:
            // ALTER TABLE old_table RENAME [TO] new_table
            // 提取旧表名和新表名
            tmp = sql.replaceAll("\\s*[Aa][Ll][Tt][Ee][Rr]\\s*", "");
            tmp = tmp.trim();
            tmp = tmp.substring(6); // 跳过"TABLE"
            String[] s = tmp.split("[Rr][Ee][Nn][Aa][Mm][Ee].*[Tt][Oo]");
            if (s.length < 2) {
                // MySQL可能没有TO关键字
                s = tmp.split("[Rr][Ee][Nn][Aa][Mm][Ee]");
            }
            String tmp_old = s[0].trim();
            tmp_old = tmp_old.replaceAll("[\"]", "");
            tmp = s[1].trim();
            tmp = tmp.replaceAll("[\"]", "");
            // 解析旧表名：如果包含点号则拆分，否则使用当前上下文
            if (tmp_old.indexOf(DOT) == -1) {
                v[2] = context.toUpperCase();
                v[3] = tmp_old.toUpperCase();
            } else {
                v[2] = tmp_old.substring(0, tmp_old.indexOf(DOT)).toUpperCase();
                v[3] = tmp_old.substring(1 + tmp_old.indexOf(DOT), 
                                                tmp_old.length()).toUpperCase();
            }
            break;

        case ALTER_TABLE:
            // ALTER TABLE table ...
            String[] words = sql.split("[\\s*]");
            tmp = words[2];
            break;

        case USE:
            // USE database - 切换数据库上下文
            tmp = sql.replaceAll("\\s*[Uu][Ss][Ee]\\s*", "");
            tmp = tmp.replaceAll("[;]", "");
            tmp.trim();
            // 更新当前数据库上下文
            context = tmp;
            // USE命令不需要返回表名
            return v;

        default:
            break;
        }

        // 清理提取的表名：移除分号和引号
        tmp = tmp.trim();
        tmp = tmp.replaceAll("[;]", "");
        tmp = tmp.replaceAll("[\"]", "");
        
        // 解析表名：如果包含点号则拆分，否则使用当前上下文
        if (tmp.indexOf(DOT) == -1) {
            // 没有点号，使用当前数据库上下文作为工作簿名
            v[0] = context.toUpperCase();
            v[1] = tmp.toUpperCase();
        } else {
            // 包含点号，拆分为工作簿名和工作表名
            v[0] = tmp.substring(0, tmp.indexOf(DOT)).toUpperCase();
            v[1] = tmp.substring(1 + tmp.indexOf(DOT), tmp.length()).toUpperCase();
        }
        return v;
    }
}