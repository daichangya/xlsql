/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://excel.jsdiff.com
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
package com.jsdiff.xlsql.engine.plan;

import com.jsdiff.xlsql.engine.model.JoinType;

/**
 * JoinInfo - JOIN信息
 * 
 * <p>存储JOIN操作的元数据，包括JOIN类型、被连接的表和连接条件。</p>
 * 
 * @author daichangya
 */
public class JoinInfo {
    
    /** JOIN类型（INNER, LEFT, RIGHT, FULL_OUTER） */
    private final JoinType type;
    
    /** 被连接的表信息 */
    private final TableInfo table;
    
    /** 连接条件（ON子句） */
    private final JoinCondition condition;
    
    /**
     * 创建JoinInfo实例
     * 
     * @param type JOIN类型
     * @param table 被连接的表信息
     * @param condition 连接条件
     */
    public JoinInfo(JoinType type, TableInfo table, JoinCondition condition) {
        if (type == null) {
            throw new NullPointerException("JoinType cannot be null");
        }
        if (table == null) {
            throw new NullPointerException("TableInfo cannot be null");
        }
        if (condition == null) {
            throw new NullPointerException("JoinCondition cannot be null");
        }
        this.type = type;
        this.table = table;
        this.condition = condition;
    }
    
    // Getters
    
    public JoinType getType() {
        return type;
    }
    
    public TableInfo getTable() {
        return table;
    }
    
    public JoinCondition getCondition() {
        return condition;
    }
}

