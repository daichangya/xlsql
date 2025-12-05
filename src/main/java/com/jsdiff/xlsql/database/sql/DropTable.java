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

import com.jsdiff.xlsql.database.ADatabase;


/**
 * DropTable - DROP TABLE SQL命令实现
 * 
 * <p>该类实现了ICommand接口，用于处理删除Excel表的操作。
 * 执行后会从数据库中移除指定的表。</p>
 * 
 * @author daichangya
 */
public class DropTable implements ICommand {
    /** 数据库对象 */
    private ADatabase dbs;
    /** 模式名称（Excel文件名） */
    private String sch;
    /** 表名称（Excel工作表名） */
    private String tbl;

    /**
     * 创建DropTable命令对象
     * 
     * @param database 数据库实例
     * @param schema 模式名称（Excel文件名，不含扩展名）
     * @param table 表名称（Excel工作表名）
     * @throws IllegalArgumentException 如果任何参数为null则抛出异常
     */
    DropTable(final ADatabase database, final String schema, 
                                                        final String table) {
        if ((database != null) && (schema != null) && (table != null)) {
            dbs = database;
            sch = schema;
            tbl = table;
        } else {
            throw new IllegalArgumentException("XLSQL: null argument(s)");
        }
    }

    /**
     * 检查命令是否允许执行
     * 
     * <p>DROP TABLE命令始终允许执行，如果表不存在则操作会被忽略。</p>
     *
     * @return 始终返回true
     * @throws SQLException 如果发生意外错误则抛出异常
     */
    public final boolean execAllowed() throws SQLException {
        return true;
    }

    /**
     * 执行DROP TABLE命令
     * 
     * <p>从数据库中移除指定的表，标记为删除状态。实际删除操作在关闭连接时执行。</p>
     *
     * @throws SQLException 如果发生意外错误则抛出异常
     */
    public final void execute() throws SQLException {
        // 从数据库中移除表
        dbs.removeTable(sch, tbl);
    }
}

