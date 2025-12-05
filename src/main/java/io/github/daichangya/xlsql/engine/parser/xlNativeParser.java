/*jsdiff.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

package io.github.daichangya.xlsql.engine.parser;

import io.github.daichangya.xlsql.database.sql.ASqlParser;

/**
 * xlNativeParser - 自研SQL引擎的解析器实现
 * 
 * <p>该类实现了ASqlParser抽象类，用于解析自研引擎的SQL语句。
 * 由于自研引擎与HSQLDB使用相同的SQL语法（双引号表名），
 * 因此可以复用HSQLDB的解析逻辑。</p>
 * 
 * @author daichangya
 */
public class xlNativeParser extends ASqlParser {

    /**
     * 创建xlNativeParser解析器实例
     * 
     * <p>初始化SQL命令正则表达式模式，与HSQLDB相同。</p>
     *
     * @param database xlSQL数据库实例
     */
    public xlNativeParser(io.github.daichangya.xlsql.database.ADatabase database) {
        super(database); 

        // 初始化SQL命令正则表达式模式数组（与HSQLDB相同）
        cmd = new String[7];
        // INSERT INTO ... VALUES 模式
        cmd[INSERT] = "\\s*[Ii][Nn][Ss][Ee][Rr][Tt].*[Ii][Nn][Tt][Oo].*[Vv][Aa][Ll][Uu][Ee][Ss].*";
        // UPDATE 模式
        cmd[UPDATE] = "\\s*[Uu][Pp][Dd][Aa][Tt][Ee].*";
        // DELETE FROM 模式
        cmd[DELETE] = "\\s*[Dd][Ee][Ll][Ee][Tt][Ee].*[Ff][Rr][Oo][Mm].*";
        // CREATE TABLE 模式
        cmd[CREATE_TABLE] = "\\s*[Cc][Rr][Ee][Aa][Tt][eE].*[Tt][Aa][Bb][Ll][Ee].*";
        // DROP TABLE 模式
        cmd[DROP_TABLE] = "\\s*[Dd][Rr][Oo][Pp].*[Tt][Aa][Bb][Ll][Ee].*";
        // ALTER TABLE ... RENAME TO 模式
        cmd[RENAME_TABLE] = "\\s*[Aa][Ll][Tt][Ee][Rr].*[Tt][Aa][Bb][Ll][Ee].*" + 
                 "[Rr][Ee][Nn][Aa][Mm][Ee].*[Tt][Oo].*";
        // ALTER TABLE ... COLUMN 模式
        cmd[ALTER_TABLE] = "\\s*[Aa][Ll][Tt][Ee][Rr].*[Tt][Aa][Bb][Ll][Ee].*" + 
                 "[Cc][Oo][Ll][Uu][Mm][Nn].*";
    }

    /**
     * 从SQL语句中提取变量（工作簿名和工作表名）
     * 
     * <p>实现逻辑与HSQLDB相同，因为自研引擎使用相同的表名格式（双引号）。</p>
     *
     * @param cmd 命令类型（INSERT、UPDATE、DELETE等）
     * @param sql SQL语句字符串
     * @return 变量数组，v[0]=工作簿名, v[1]=工作表名
     */
    @Override
    protected String[] getVars(int cmd, String sql) {
        String[] v = new String[4];
        String tmp = "";

        // 根据命令类型提取表名（与HSQLDB逻辑相同）
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
            // ALTER TABLE old_table RENAME TO new_table
            tmp = sql.replaceAll("\\s*[Aa][Ll][Tt][Ee][Rr]\\s*", "");
            tmp = tmp.trim();
            tmp = tmp.substring(6); // 跳过"TABLE"

            String[] s = tmp.split("[Rr][Ee][Nn][Aa][Mm][Ee].*[Tt][Oo]");
            String tmp_old = s[0].trim();
            tmp_old = tmp_old.replaceAll("[\"]", "");
            tmp_old = tmp_old.replaceAll("[']", "");
            tmp = s[1].trim();
            tmp = tmp.replaceAll("[\"]", "");

            // 解析旧表名
            if (tmp_old.indexOf(DOT) == -1) {
                v[2] = "SA";
                v[3] = tmp_old;
            } else {
                v[2] = tmp_old.substring(0, tmp_old.indexOf(DOT));
                v[3] = tmp_old.substring(1 + tmp_old.indexOf(DOT), 
                                         tmp_old.length());
            }
            break;

        case ALTER_TABLE:
            // ALTER TABLE table ...
            String[] words = sql.split("[\\s*]");
            tmp = words[2];
            break;

        default:
            break;
        }
        
        // 清理提取的表名
        tmp = tmp.trim();
        tmp = tmp.replaceAll("[;]", "");
        tmp = tmp.replaceAll("[']", "");
        
        // 解析表名：如果包含点号则拆分，否则使用默认模式"SA"
        if (tmp.indexOf(DOT) == -1) {
            if (tmp.indexOf(QUOTE) == -1) {
                v[0] = "SA";
                v[1] = tmp.toUpperCase();
            }
            else {
                tmp = tmp.replaceAll("[\"]", "");
                v[0] = "SA";
                v[1] = tmp;
            }
        } else {
            tmp = tmp.replaceAll("[\"]", "");            
            v[0] = tmp.substring(0, tmp.indexOf(DOT));
            v[1] = tmp.substring(1 + tmp.indexOf(DOT), tmp.length());
        }
        return v;
    }
}

