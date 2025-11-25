/*jsdiff.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

package com.jsdiff.xlsql.database.sql;

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
    protected com.jsdiff.xlsql.database.ADatabase db;
    protected String[] cmd;
    
    /**
     * Creates a new instance of type xlSql.
     *
     * @param database xlSQL instance
     */
    public ASqlParser(com.jsdiff.xlsql.database.ADatabase database) {
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

        // 移除换行符和回车符，统一为空格
        String sqlLine = sql.replaceAll("\\n|\\r", " ");
        // 识别SQL命令类型
        int cmd = getCmd(sqlLine);
        // 提取命令参数（工作簿名、工作表名等）
        String[] v = getVars(cmd, sqlLine);

        // 根据命令类型创建相应的命令对象
        switch (cmd) {
            case INSERT:
                // 创建INSERT命令：v[0]=工作簿名, v[1]=工作表名
                command = new xlSqlInsert(db, v[0], v[1]);
                break;

            case UPDATE:
                // 创建UPDATE命令：v[0]=工作簿名, v[1]=工作表名
                command = new xlSqlUpdate(db, v[0], v[1]);
                break;

            case DELETE:
                // 创建DELETE命令：v[0]=工作簿名, v[1]=工作表名
                command = new xlSqlDelete(db, v[0], v[1]);
                break;

            case CREATE_TABLE:
                // 创建CREATE TABLE命令：v[0]=工作簿名, v[1]=工作表名
                command = new xlSqlCreateTable(db, v[0], v[1]);
                break;

            case DROP_TABLE:
                // 创建DROP TABLE命令：v[0]=工作簿名, v[1]=工作表名
                command = new DropTable(db, v[0], v[1]);
                break;

            case RENAME_TABLE:
                // 创建RENAME TABLE命令：v[0]=旧工作簿名, v[1]=旧工作表名, v[2]=新工作簿名, v[3]=新工作表名
                command = new xlSqlRenameTable(db, v[0], v[1], v[2], v[3]);
                break;

            case ALTER_TABLE:
                // 创建ALTER TABLE命令：v[0]=工作簿名, v[1]=工作表名
                command = new xlSqlAlterTable(db, v[0], v[1]);
                break;

            default:
                // 无法识别的SQL语句，返回空命令
                command = new xlSqlNull();
                break;
        }
        return command;
    }

    /**
     * 识别SQL命令类型
     * 
     * <p>使用正则表达式匹配SQL语句，返回匹配的命令类型索引。
     * 如果没有匹配的命令，则返回cmd.length（表示未知命令）。</p>
     *
     * @param sql SQL语句字符串
     * @return 命令类型索引（INSERT=0, UPDATE=1, DELETE=2等），如果未匹配则返回cmd.length
     */
    protected int getCmd(String sql) {
        int i;
        // 遍历所有命令模式，找到匹配的模式
        for (i = 0; i < cmd.length; i++) {
            if (Pattern.compile(cmd[i]).matcher(sql).matches()) {
                break;
            }
        }
        return i;
    }

    /**
     * 从SQL语句中提取变量（工作簿名和工作表名）
     * 
     * <p>抽象方法，由子类实现具体的提取逻辑。
     * 不同数据库的SQL语法可能略有不同，因此需要子类实现。</p>
     *
     * @param cmd 命令类型（INSERT、UPDATE、DELETE等）
     * @param sql SQL语句字符串
     * @return 变量数组，v[0]=工作簿名, v[1]=工作表名, v[2]=旧工作簿名（RENAME时）, v[3]=旧工作表名（RENAME时）
     */
    protected abstract String[] getVars(int cmd, String sql);
    
}