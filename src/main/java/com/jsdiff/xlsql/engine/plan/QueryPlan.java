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

import java.util.ArrayList;
import java.util.List;

import com.jsdiff.xlsql.engine.model.AggregateFunction;

/**
 * QueryPlan - 查询计划
 * 
 * <p>存储解析后的SQL查询计划，包括表信息、JOIN信息、WHERE条件、
 * 聚合函数、GROUP BY等所有查询要素。</p>
 * 
 * @author daichangya
 */
public class QueryPlan {
    
    /** 普通列列表（SELECT col1, col2） */
    private List<String> selectColumns;
    
    /** 聚合函数列表 */
    private List<AggregateFunction> aggregateFunctions;
    
    /** 是否包含聚合函数 */
    private boolean hasAggregation;
    
    /** 主表信息（FROM子句） */
    private TableInfo mainTable;
    
    /** JOIN列表 */
    private List<JoinInfo> joins;
    
    /** WHERE条件 */
    private WhereCondition whereClause;
    
    /** GROUP BY列列表 */
    private List<String> groupByColumns;
    
    /** HAVING条件 */
    private WhereCondition havingClause;
    
    /** ORDER BY列表 */
    private List<OrderByItem> orderBy;
    
    /** LIMIT限制 */
    private Integer limit;
    
    /** OFFSET偏移 */
    private Integer offset;
    
    /**
     * 创建QueryPlan实例
     */
    public QueryPlan() {
        this.selectColumns = new ArrayList<>();
        this.aggregateFunctions = new ArrayList<>();
        this.joins = new ArrayList<>();
        this.groupByColumns = new ArrayList<>();
        this.orderBy = new ArrayList<>();
        this.hasAggregation = false;
    }
    
    // Getters and Setters
    
    public List<String> getSelectColumns() {
        return selectColumns;
    }
    
    public void setSelectColumns(List<String> selectColumns) {
        this.selectColumns = selectColumns != null ? selectColumns : new ArrayList<>();
    }
    
    public List<AggregateFunction> getAggregateFunctions() {
        return aggregateFunctions;
    }
    
    public void setAggregateFunctions(List<AggregateFunction> aggregateFunctions) {
        this.aggregateFunctions = aggregateFunctions != null ? aggregateFunctions : new ArrayList<>();
        this.hasAggregation = !this.aggregateFunctions.isEmpty();
    }
    
    public void addAggregateFunction(AggregateFunction func) {
        if (func != null) {
            this.aggregateFunctions.add(func);
            this.hasAggregation = true;
        }
    }
    
    public boolean hasAggregation() {
        return hasAggregation;
    }
    
    public TableInfo getMainTable() {
        return mainTable;
    }
    
    public void setMainTable(TableInfo mainTable) {
        this.mainTable = mainTable;
    }
    
    public List<JoinInfo> getJoins() {
        return joins;
    }
    
    public void setJoins(List<JoinInfo> joins) {
        this.joins = joins != null ? joins : new ArrayList<>();
    }
    
    public void addJoin(JoinInfo join) {
        if (join != null) {
            this.joins.add(join);
        }
    }
    
    public WhereCondition getWhereClause() {
        return whereClause;
    }
    
    public void setWhereClause(WhereCondition whereClause) {
        this.whereClause = whereClause;
    }
    
    public List<String> getGroupByColumns() {
        return groupByColumns;
    }
    
    public void setGroupByColumns(List<String> groupByColumns) {
        this.groupByColumns = groupByColumns != null ? groupByColumns : new ArrayList<>();
    }
    
    public void addGroupByColumn(String column) {
        if (column != null && !column.isEmpty()) {
            this.groupByColumns.add(column);
        }
    }
    
    public boolean hasGroupBy() {
        return !groupByColumns.isEmpty();
    }
    
    public WhereCondition getHavingClause() {
        return havingClause;
    }
    
    public void setHavingClause(WhereCondition havingClause) {
        this.havingClause = havingClause;
    }
    
    public List<OrderByItem> getOrderBy() {
        return orderBy;
    }
    
    public void setOrderBy(List<OrderByItem> orderBy) {
        this.orderBy = orderBy != null ? orderBy : new ArrayList<>();
    }
    
    public void addOrderBy(OrderByItem item) {
        if (item != null) {
            this.orderBy.add(item);
        }
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public Integer getOffset() {
        return offset;
    }
    
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}

