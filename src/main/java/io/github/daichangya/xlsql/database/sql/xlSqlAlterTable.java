/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://xlsql.jsdiff.com
   daichangya@163.com

 This program is free software; you can redistribute it and/or modify it under 
 the terms of the GNU General Public License as published by the Free Software 
 Foundation; either version 2 of the License, or (at your option) any later 
 version.

 This program is distributed in the hope that it will be useful, 
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License 
 along with this program; if not, write to the Free Software Foundation, 
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package io.github.daichangya.xlsql.database.sql;


import java.sql.SQLException;


/**
 * xlSqlAlterTable - ALTER TABLE SQL命令实现
 * 
 * <p>该类实现了ICommand接口，用于处理修改Excel表结构的操作。
 * 执行后会标记表和模式为已修改。</p>
 * 
 * @author daichangya
 */
public class xlSqlAlterTable implements ICommand {
    /** 数据库对象 */
    protected io.github.daichangya.xlsql.database.ADatabase db;
    /** 模式名称（Excel文件名） */
    protected String _schema;
    /** 表名称（Excel工作表名） */
    protected String _table;

    /**
     * 创建xlSqlAlterTable命令对象
     * 
     * @param database 数据库对象
     * @param schema 模式名称（Excel文件名，不含扩展名）
     * @param table 表名称（Excel工作表名）
     * @throws IllegalArgumentException 如果任何参数为null则抛出异常
     */
    public xlSqlAlterTable(io.github.daichangya.xlsql.database.ADatabase database,
                           String schema, String table) {
        if ((database != null) && (schema != null) && (table != null)) {
            db = database;
            _schema = schema;
            _table = table;
        }
        else {
            throw new IllegalArgumentException("XLSQL: null argument(s)");
        }
    }

    /**
     * 检查命令是否允许执行
     * 
     * <p>ALTER TABLE命令始终允许执行，因为它是标记操作，不会立即修改数据。</p>
     *
     * @return 始终返回true
     * @throws SQLException 如果发生错误则抛出异常
     */
    public boolean execAllowed() throws SQLException {
        return true;
    }

    /**
     * 执行ALTER TABLE命令
     * 
     * <p>标记模式和表为需要更新，实际修改操作在关闭连接时执行。</p>
     *
     * @throws SQLException 如果执行过程中发生错误则抛出异常
     */
    public void execute() throws SQLException {
        // 标记模式和表为需要更新
        db.touchSchema(_schema);
        db.touchTable(_schema, _table);
    }
}