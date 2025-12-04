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
package com.jsdiff.xlsql.engine.plan;

/**
 * OrderByItem - ORDER BY项
 * 
 * <p>表示ORDER BY子句中的一个排序项，包括列名和排序方向。</p>
 * 
 * @author daichangya
 */
public class OrderByItem {
    
    /** 排序列（可以是列名或聚合函数别名） */
    private final String column;
    
    /** 排序方向（ASC或DESC） */
    private final SortDirection direction;
    
    /**
     * 排序方向枚举
     */
    public enum SortDirection {
        ASC, DESC
    }
    
    /**
     * 创建OrderByItem实例
     * 
     * @param column 排序列
     * @param direction 排序方向
     */
    public OrderByItem(String column, SortDirection direction) {
        if (column == null || column.isEmpty()) {
            throw new IllegalArgumentException("Column cannot be null or empty");
        }
        this.column = column;
        this.direction = direction != null ? direction : SortDirection.ASC;
    }
    
    /**
     * 创建OrderByItem实例（默认ASC）
     * 
     * @param column 排序列
     */
    public OrderByItem(String column) {
        this(column, SortDirection.ASC);
    }
    
    // Getters
    
    public String getColumn() {
        return column;
    }
    
    public SortDirection getDirection() {
        return direction;
    }
    
    public boolean isAscending() {
        return direction == SortDirection.ASC;
    }
    
    public boolean isDescending() {
        return direction == SortDirection.DESC;
    }
}

