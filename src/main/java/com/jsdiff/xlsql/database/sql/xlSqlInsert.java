/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://xlsql.jsdiff.com
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
package com.jsdiff.xlsql.database.sql;


import java.sql.SQLException;


/**
 * xlSqlInsert - INSERT SQL命令实现
 * 
 * <p>该类实现了ICommand接口，用于处理向Excel表中插入行的操作。
 * 在执行插入前会检查表是否已满（Excel最大行数限制为65535行）。</p>
 * 
 * @author daichangya
 */
public class xlSqlInsert implements ICommand {
    /** 数据库对象 */
    protected com.jsdiff.xlsql.database.ADatabase db;
    /** 模式名称（Excel文件名） */
    protected String _schema;
    /** 表名称（Excel工作表名） */
    protected String _table;

    /**
     * 创建xlSqlInsert命令对象
     * 
     * @param database 数据库对象
     * @param schema 模式名称（Excel文件名，不含扩展名）
     * @param table 表名称（Excel工作表名）
     * @throws NullPointerException 如果任何参数为null则抛出异常
     */
    public xlSqlInsert(com.jsdiff.xlsql.database.ADatabase database,
                       String schema, String table) {
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

    /** Excel最大行数限制（65535是Excel的硬限制） */
    private static final int MAX_EXCEL_ROWS = 65535;
    
    /**
     * 检查INSERT操作是否允许执行
     * 
     * <p>如果表已达到Excel的最大行数限制（65535行），则不允许插入新行。</p>
     * 
     * @return 如果允许插入则返回true，如果表已满则返回false
     * @throws SQLException 如果检查过程中发生错误则抛出异常
     */
    public boolean execAllowed() throws SQLException {
        boolean ret = true;

        // 检查当前表的行数是否超过Excel限制
        if (db.getRows(_schema, _table) > MAX_EXCEL_ROWS) {
            ret = false;
        }

        return ret;
    }

    /**
     * 执行INSERT命令
     * 
     * <p>向指定表添加新行，并标记模式和表为已修改状态。
     * 实际的Excel文件写入操作在关闭连接时执行。</p>
     * 
     * @throws SQLException 如果执行过程中发生错误则抛出异常
     */
    public void execute() throws SQLException {
        // 添加新行
        db.addRow(_schema, _table);
        // 标记模式和表为需要更新
        db.touchSchema(_schema);
        db.touchTable(_schema, _table);
    }
}

