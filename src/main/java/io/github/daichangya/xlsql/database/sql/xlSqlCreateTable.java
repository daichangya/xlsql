/*jsdiff.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

package io.github.daichangya.xlsql.database.sql;

import java.sql.SQLException;

/**
 * Adds a table to an xlDatabase.
 * 
 * @author daichangya
 */
public class xlSqlCreateTable implements ICommand {
    //~ Instance variables �����������������������������������������������������

    protected io.github.daichangya.xlsql.database.ADatabase db;
    protected String _schema;
    protected String _table;

    /**
     * 创建xlSqlCreateTable命令对象
     * 
     * @param database 数据库对象
     * @param schema 模式名称（Excel文件名，不含扩展名）
     * @param table 表名称（Excel工作表名）
     * @throws NullPointerException 如果任何参数为null则抛出异常
     */
    public xlSqlCreateTable(io.github.daichangya.xlsql.database.ADatabase database, String schema, String table) {
        // 验证并设置数据库对象
        if (database == null) {
            throw new NullPointerException("XLSQL: database null");
        } else {
            db = database;
        }

        // 验证并设置模式名称
        if (schema == null) {
            throw new NullPointerException("XLSQL: schema null");
        } else {
            _schema = schema;
        }

        // 验证并设置表名称
        if (table == null) {
            throw new NullPointerException("XLSQL: table null");
        } else {
            _table = table;
        }
    }

    /**
     * 检查命令是否允许执行
     * 
     * <p>检查以下条件：</p>
     * <ul>
     *   <li>表是否已存在（如果存在则不允许创建）</li>
     *   <li>是否存在大小写冲突的模式名称</li>
     * </ul>
     *
     * @return 如果允许执行则返回true，否则返回false
     * @throws SQLException 如果存在大小写冲突则抛出异常
     */
    public boolean execAllowed() throws SQLException {
        boolean ret = true;
        // 检查表是否已存在
        if (db.tableExists(_schema, _table)) {
            ret = false;
        }
        // 检查是否存在大小写冲突的模式
        if (db.schemaOtherCaseExists(_schema)) {
            ret = false;
            throw new SQLException("(NATIVE:)schema will cause case conflict");
        }
        return ret;
    }

    /**
     * 执行CREATE TABLE命令
     * 
     * <p>在数据库中创建新表，并标记模式和表为已修改。</p>
     *
     * @throws SQLException 如果执行过程中发生错误则抛出异常
     */
    public void execute() throws SQLException {
        db.addSchema(_schema);
        db.addTable(_schema, _table);
    }
}