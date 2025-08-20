/*zthinker.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

package com.daicy.exceljdbc.database.sql;

import java.util.regex.Pattern;

/**
 * xlSql class translates SQL to native xlSQL
 * 
 * @author daichangya
 */
public abstract class ASqlParser {
    //~ Instance variables �����������������������������������������������������

    protected final String DOT = ".";
    protected final String QUOTE = "\"";
    protected final int INSERT = 0;
    protected final int UPDATE = 1;
    protected final int DELETE = 2;
    protected final int CREATE_TABLE = 3;
    protected final int DROP_TABLE = 4;
    protected final int RENAME_TABLE = 5;
    protected final int ALTER_TABLE = 6;
    protected com.daicy.exceljdbc.database.ADatabase db;
    protected String[] cmd;
    
    /**
     * Creates a new instance of type xlSql.
     *
     * @param database xlSQL instance
     */
    public ASqlParser(com.daicy.exceljdbc.database.ADatabase database) {
        if (database == null) {
            throw new NullPointerException("xlSQL: database null");
        } else {
            db = database;
        }
    }

    /**
     * Translate foreign SQL string to xlSQL Command
     *
     * @param sql foreign SQL string
     *
     * @return native xlSQL command object
     */
    public ICommand parseSql(String sql) {
        if (sql == null) {
            throw new NullPointerException("xlSQL: sql string null");
        }

        ICommand command;

        String sqlLine=sql.replaceAll("\\n|\\r"," "); // add [Tab] \t ?        
        int cmd = getCmd(sqlLine);
        String[] v = getVars(cmd, sqlLine);

        switch (cmd) {

            case INSERT:
                command = new xlSqlInsert(db, v[0], v[1]);
                break;

            case UPDATE:
                command = new xlSqlUpdate(db, v[0], v[1]);
                break;

            case DELETE:
                command = new xlSqlDelete(db, v[0], v[1]);
                break;

            case CREATE_TABLE:
                command = new xlSqlCreateTable(db, v[0], v[1]);
                break;

            case DROP_TABLE:
                command = new DropTable(db, v[0], v[1]);
                break;

            case RENAME_TABLE:
                command = new xlSqlRenameTable(db, v[0], v[1], v[2], v[3]);
                break;

            case ALTER_TABLE:
                command = new xlSqlAlterTable(db, v[0], v[1]);
                break;

            default:
                command = new xlSqlNull();
                break;
        }
        return command;
    }

    protected int getCmd(String sql) {
        int i;
        for (i = 0; i < cmd.length; i++) {
            if (Pattern.compile(cmd[i]).matcher(sql).matches()) {
                break;
            }
        }
        return i;
    }

    protected abstract String[] getVars(int cmd, String sql);
    
}