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

import com.jsdiff.xlsql.engine.model.AggregateFunction;

/**
 * WhereCondition - WHERE条件
 * 
 * <p>表示SQL WHERE或HAVING子句中的条件表达式。
 * 支持简单条件（col = value）和复合条件（AND/OR）。</p>
 * 
 * @author daichangya
 */
public class WhereCondition {
    
    /** 左操作数（通常是列名） */
    private String leftOperand;
    
    /** 操作符（=, !=, <>, >, <, >=, <=, LIKE, IN） */
    private String operator;
    
    /** 右操作数（通常是值或另一个列名） */
    private String rightOperand;
    
    /** 逻辑运算符（AND, OR），用于连接多个条件 */
    private String logicalOperator;
    
    /** 左子条件（用于复合条件） */
    private WhereCondition leftCondition;
    
    /** 右子条件（用于复合条件） */
    private WhereCondition rightCondition;
    
    /** 是否为聚合函数条件（用于HAVING） */
    private boolean isAggregate;
    
    /** 聚合函数（如果isAggregate为true） */
    private AggregateFunction aggregateFunction;
    
    /**
     * 创建简单条件（col operator value）
     * 
     * @param leftOperand 左操作数（列名）
     * @param operator 操作符
     * @param rightOperand 右操作数（值）
     */
    public WhereCondition(String leftOperand, String operator, String rightOperand) {
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
        this.isAggregate = false;
    }
    
    /**
     * 创建复合条件（condition1 AND/OR condition2）
     * 
     * @param leftCondition 左子条件
     * @param logicalOperator 逻辑运算符（AND/OR）
     * @param rightCondition 右子条件
     */
    public WhereCondition(WhereCondition leftCondition, String logicalOperator, 
                         WhereCondition rightCondition) {
        this.leftCondition = leftCondition;
        this.logicalOperator = logicalOperator;
        this.rightCondition = rightCondition;
        this.isAggregate = false;
    }
    
    /**
     * 创建聚合函数条件（用于HAVING，如 COUNT(*) > 10）
     * 
     * @param aggregateFunction 聚合函数
     * @param operator 操作符
     * @param rightOperand 右操作数（值）
     */
    public WhereCondition(AggregateFunction aggregateFunction, String operator, String rightOperand) {
        this.aggregateFunction = aggregateFunction;
        this.operator = operator;
        this.rightOperand = rightOperand;
        this.isAggregate = true;
    }
    
    /**
     * 判断是否为简单条件
     * 
     * @return 如果是简单条件返回true
     */
    public boolean isSimple() {
        return leftCondition == null && rightCondition == null;
    }
    
    /**
     * 判断是否为复合条件
     * 
     * @return 如果是复合条件返回true
     */
    public boolean isCompound() {
        return leftCondition != null || rightCondition != null;
    }
    
    // Getters
    
    public String getLeftOperand() {
        return leftOperand;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public String getRightOperand() {
        return rightOperand;
    }
    
    public String getLogicalOperator() {
        return logicalOperator;
    }
    
    public WhereCondition getLeftCondition() {
        return leftCondition;
    }
    
    public WhereCondition getRightCondition() {
        return rightCondition;
    }
    
    public boolean isAggregate() {
        return isAggregate;
    }
    
    public AggregateFunction getAggregateFunction() {
        return aggregateFunction;
    }
}

