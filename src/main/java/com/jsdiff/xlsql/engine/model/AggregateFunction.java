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
package com.jsdiff.xlsql.engine.model;

/**
 * AggregateFunction - 聚合函数
 * 
 * <p>表示SQL中的聚合函数，如COUNT(*), SUM(amount), AVG(price)等。</p>
 * 
 * @author daichangya
 */
public class AggregateFunction {
    
    /** 聚合函数类型 */
    private final AggregateType type;
    
    /** 聚合的列名（* 表示所有行，null表示所有行） */
    private final String column;
    
    /** 是否去重（COUNT(DISTINCT col)） */
    private final boolean distinct;
    
    /** 列别名 */
    private final String alias;
    
    /**
     * 创建AggregateFunction实例
     * 
     * @param type 聚合函数类型
     * @param column 聚合的列名（* 或具体列名）
     * @param distinct 是否去重
     * @param alias 列别名
     */
    public AggregateFunction(AggregateType type, String column, boolean distinct, String alias) {
        if (type == null) {
            throw new NullPointerException("AggregateType cannot be null");
        }
        this.type = type;
        this.column = column;
        this.distinct = distinct;
        this.alias = alias;
    }
    
    /**
     * 创建AggregateFunction实例（默认不去重，无别名）
     * 
     * @param type 聚合函数类型
     * @param column 聚合的列名
     */
    public AggregateFunction(AggregateType type, String column) {
        this(type, column, false, null);
    }
    
    // Getters
    
    public AggregateType getType() {
        return type;
    }
    
    public String getColumn() {
        return column;
    }
    
    public boolean isDistinct() {
        return distinct;
    }
    
    public String getAlias() {
        return alias;
    }
    
    /**
     * 获取显示名称（用于结果集列名）
     * 
     * @return 显示名称（别名或函数表达式）
     */
    public String getDisplayName() {
        if (alias != null && !alias.isEmpty()) {
            return alias;
        }
        
        return getFunctionExpression();
    }
    
    /**
     * 获取函数表达式（不管是否有别名）
     * 
     * @return 函数表达式，如 COUNT(*), SUM(column), AVG(column) 等
     */
    public String getFunctionExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.name());
        sb.append("(");
        if (distinct) {
            sb.append("DISTINCT ");
        }
        sb.append(column != null ? column : "*");
        sb.append(")");
        return sb.toString();
    }
}

