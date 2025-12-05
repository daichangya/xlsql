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
 * Represents a DELETE SQL command.
 * Handles deletion of rows from Excel tables.
 * 
 * @author daichangya
 */
public class xlSqlDelete implements ICommand {
    //~ Instance variables �����������������������������������������������������

    protected io.github.daichangya.xlsql.database.ADatabase db;
    protected String _schema;
    protected String _table;

    /**
     * 创建xlSqlDelete命令对象
     * 
     * @param database 数据库对象
     * @param schema 模式名称（Excel文件名，不含扩展名）
     * @param table 表名称（Excel工作表名）
     * @throws NullPointerException 如果任何参数为null则抛出异常
     */
    public xlSqlDelete(io.github.daichangya.xlsql.database.ADatabase database, String schema, String table) {
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
     * 检查DELETE操作是否允许执行
     * 
     * <p>DELETE操作始终允许执行，因为删除操作不会导致表超出限制。</p>
     *
     * @return 始终返回true
     * @throws SQLException 如果发生错误则抛出异常
     */
    public boolean execAllowed() throws SQLException {
        return true;
    }

    /**
     * 执行DELETE命令
     * 
     * <p>标记模式和表为已修改状态，表示需要更新。
     * 实际的Excel文件删除操作在关闭连接时执行。</p>
     *
     * @throws SQLException 如果执行过程中发生错误则抛出异常
     */
    public void execute() throws SQLException {
        // 标记模式和表为需要更新
        db.touchSchema(_schema);
        db.touchTable(_schema, _table);
    }
}