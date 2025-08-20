/*zthinker.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

package com.jsdiff.excel.database.sql;

/**
 * xlHsqldb class translates HSQL to native xlSQL
 * 
 * @author daichangya
 */
public class xlHsqldb extends ASqlParser {

    /**
     * Creates a new instance of type xlHsqldb.
     *
     * @param database xlSQL instance
     */
    public xlHsqldb(com.jsdiff.excel.database.ADatabase database) {
        super(database); 

        cmd = new String[7];
        cmd[INSERT] = "\\s*[Ii][Nn][Ss][Ee][Rr][Tt].*[Ii][Nn][Tt][Oo].*[Vv][Aa][Ll][Uu][Ee][Ss].*";
        cmd[UPDATE] = "\\s*[Uu][Pp][Dd][Aa][Tt][Ee].*";
        cmd[DELETE] = "\\s*[Dd][Ee][Ll][Ee][Tt][Ee].*[Ff][Rr][Oo][Mm].*";
        cmd[CREATE_TABLE] = "\\s*[Cc][Rr][Ee][Aa][Tt][eE].*[Tt][Aa][Bb][Ll][Ee].*";
        cmd[DROP_TABLE] = "\\s*[Dd][Rr][Oo][Pp].*[Tt][Aa][Bb][Ll][Ee].*";
        cmd[RENAME_TABLE] = "\\s*[Aa][Ll][Tt][Ee][Rr].*[Tt][Aa][Bb][Ll][Ee].*" + 
                 "[Rr][Ee][Nn][Aa][Mm][Ee].*[Tt][Oo].*";
        cmd[ALTER_TABLE] = "\\s*[Aa][Ll][Tt][Ee][Rr].*[Tt][Aa][Bb][Ll][Ee].*" + 
                 "[Cc][Oo][Ll][Uu][Mm][Nn].*";
    }

    //~ Methods ����������������������������������������������������������������

    protected String[] getVars(int cmd, 
                               String sql) {
        String[] v = new String[4];
        String tmp = "";

        switch (cmd) {
        case INSERT:
            tmp = sql.replaceAll(
                          "\\s*[Ii][Nn][Ss][Ee][Rr][Tt].*[Ii][Nn][Tt][Oo]\\s*", 
                          "");
            tmp = tmp.substring(0, tmp.indexOf("("));
            tmp = tmp.replaceAll("\\s*[Vv][Aa][Ll][Uu][Ee][Ss].*", "");
            break;

        case UPDATE:
            tmp = sql.replaceAll("\\s*[Uu][Pp][Dd][Aa][Tt][Ee]\\s*", "");
            tmp = tmp.replaceAll("\\s*[Ss][Ee][Tt].*", "");
            break;

        case DELETE:
            tmp = sql.replaceAll(
                          "\\s*[Dd][Ee][Ll][Ee][Tt][Ee].*[Ff][Rr][Oo][Mm]\\s*", 
                          "");
            tmp = tmp.replaceAll("\\s*[Ww][Hh][Ee][Rr][Ee].*", "");
            break;

        case CREATE_TABLE:
            tmp = sql.replaceAll("\\s*[Cc][Rr][Ee][Aa][Tt][Ee]\\s*", "");
            tmp = tmp.substring(6);
            tmp = tmp.replaceAll("\\s*[(].*", "");
            break;

        case DROP_TABLE:
            tmp = sql.replaceAll("\\s*[Dd][Rr][Oo][Pp]\\s*", "");
            tmp = tmp.substring(6);
            tmp = tmp.replaceAll("\\s*[Ii][Ff].*[Ee][Xx][Ii][Ss][Tt][Ss]\\s*","");
            break;

        case RENAME_TABLE:
            tmp = sql.replaceAll("\\s*[Aa][Ll][Tt][Ee][Rr]\\s*", "");
            tmp = tmp.trim();
            tmp = tmp.substring(6);

            String[] s = tmp.split("[Rr][Ee][Nn][Aa][Mm][Ee].*[Tt][Oo]");
            String tmp_old = s[0].trim();
            tmp_old = tmp_old.replaceAll("[\"]", "");
            tmp_old = tmp_old.replaceAll("[']", "");
            tmp = s[1].trim();
            tmp = tmp.replaceAll("[\"]", "");

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
            String[] words = sql.split("[\\s*]");
            tmp = words[2];
            break;

        default:
            break;

        }
        tmp = tmp.trim();
        tmp = tmp.replaceAll("[;]", "");
        tmp = tmp.replaceAll("[']", "");
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