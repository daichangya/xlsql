/*zthinker.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

package com.jsdiff.xlsql.database.sql;

import java.sql.SQLException;

/**
 * Represents a RENAME TABLE SQL command.
 * Handles renaming of tables in Excel databases.
 * 
 * @author daichangya
 */
public class xlSqlRenameTable implements ICommand {
    //~ Instance variables �����������������������������������������������������

    protected com.jsdiff.xlsql.database.ADatabase db;
    protected String _schema;
    protected String _table;
    protected String _schema_old;
    protected String _table_old;

    /**
     * Creates a new instance of type xlSqlRenameTable.
     *
     *
     * @param database 	
     * @param schema 	
     * @param table 	
     */
    public xlSqlRenameTable(com.jsdiff.xlsql.database.ADatabase database, String schema, String table,
                            String schema_old, String table_old) {
        if (database == null) {
            throw new NullPointerException("xlSQL: database null");
        } else {
            db = database;
        }

        if (schema == null) {
            throw new NullPointerException("xlSQL: schema null");
        } else {
            _schema = schema;
        }

        if (table == null) {
            throw new NullPointerException("xlSQL: table null");
        } else {
            _table = table;
        }

        if (schema_old == null) {
            throw new NullPointerException("xlSQL: schema_old null");
        } else {
            _schema_old = schema_old;
        }

        if (table_old == null) {
            throw new NullPointerException("xlSQL: table_old null");
        } else {
            _table_old = table_old;
        }
    }

    /**
     * 检查RENAME TABLE操作是否允许执行
     * 
     * <p>如果新表名已存在，则不允许重命名，避免覆盖现有表。</p>
     *
     * @return 如果允许重命名则返回true，如果目标表已存在则返回false
     * @throws SQLException 如果检查过程中发生错误则抛出异常
     */
    public boolean execAllowed() throws SQLException {
        // 检查新表名是否已存在
        if (db.tableExists(_schema, _table)) {
            return false;
        }
        return true;
    }

    /**
     * 执行RENAME TABLE命令
     * 
     * <p>删除旧表并添加新表，实现重命名操作。
     * 实际的Excel文件重命名操作在关闭连接时执行。</p>
     *
     * @throws SQLException 如果执行过程中发生错误则抛出异常
     */
    public void execute() throws SQLException {
        // 删除旧表
        db.removeTable(_schema_old, _table_old);
        // 添加新模式（如果不存在）
        db.addSchema(_schema);
        // 添加新表
        db.addTable(_schema, _table);
    }
}