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

/**
 * JoinCondition - JOIN条件
 * 
 * <p>表示JOIN的ON条件，通常是等值连接（如 t1.id = t2.foreign_id）。</p>
 * 
 * @author daichangya
 */
public class JoinCondition {
    
    /** 左表列引用（格式：table.column 或 column） */
    private final String leftColumn;
    
    /** 右表列引用（格式：table.column 或 column） */
    private final String rightColumn;
    
    /** 比较操作符（通常是 =） */
    private final String operator;
    
    /**
     * 创建JoinCondition实例
     * 
     * @param leftColumn 左表列引用
     * @param rightColumn 右表列引用
     * @param operator 比较操作符
     */
    public JoinCondition(String leftColumn, String rightColumn, String operator) {
        if (leftColumn == null || rightColumn == null) {
            throw new NullPointerException("Column references cannot be null");
        }
        this.leftColumn = leftColumn.trim();
        this.rightColumn = rightColumn.trim();
        this.operator = operator != null ? operator.trim() : "=";
    }
    
    /**
     * 解析列引用，提取表别名和列名
     * 
     * @param columnRef 列引用（格式：table.column 或 column）
     * @return 数组，[0]=表别名（可能为空），[1]=列名
     */
    public static String[] parseColumnRef(String columnRef) {
        if (columnRef == null || columnRef.isEmpty()) {
            return new String[]{"", ""};
        }
        
        String ref = columnRef.trim();
        int dotIndex = ref.indexOf(".");
        
        if (dotIndex == -1) {
            // 没有表别名，只有列名
            return new String[]{"", ref};
        } else {
            // 有表别名
            String tableAlias = ref.substring(0, dotIndex).trim();
            String columnName = ref.substring(dotIndex + 1).trim();
            return new String[]{tableAlias, columnName};
        }
    }
    
    // Getters
    
    public String getLeftColumn() {
        return leftColumn;
    }
    
    public String getRightColumn() {
        return rightColumn;
    }
    
    public String getOperator() {
        return operator;
    }
}

