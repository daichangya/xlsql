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
package io.github.daichangya.xlsql.engine.executor;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

import io.github.daichangya.xlsql.engine.model.AggregateFunction;
import io.github.daichangya.xlsql.engine.plan.JoinCondition;
import io.github.daichangya.xlsql.engine.plan.TableInfo;
import io.github.daichangya.xlsql.engine.plan.WhereCondition;

import net.sf.jsqlparser.expression.Expression;

/**
 * ConditionEvaluator - 条件评估器
 * 
 * <p>评估WHERE和HAVING条件，判断行是否满足条件。
 * 支持简单条件（col operator value）和复合条件（AND/OR）。
 * 也支持聚合函数条件（用于HAVING）。</p>
 * 
 * @author daichangya
 */
public class ConditionEvaluator {
    
    /**
     * 评估WHERE条件（使用JSqlParser Expression）
     * 
     * @param expression WHERE表达式
     * @param row 数据行（列值数组）
     * @param columnIndexMap 列名到索引的映射
     * @param tables 表信息列表（用于解析表别名）
     * @return 如果行满足条件返回true
     * @throws SQLException 如果评估失败则抛出异常
     */
    public boolean evaluate(Expression expression, String[] row,
                           Map<String, Integer> columnIndexMap,
                           java.util.List<TableInfo> tables) throws SQLException {
        if (expression == null) {
            return true; // 无条件，返回true
        }
        
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        return evaluator.evaluateAsBoolean(expression, row, columnIndexMap, tables);
    }
    
    /**
     * 评估WHERE条件（使用WhereCondition，保持向后兼容）
     * 
     * @param condition WHERE条件对象
     * @param row 数据行（列值数组）
     * @param columnIndexMap 列名到索引的映射
     * @param tables 表信息列表（用于解析表别名）
     * @return 如果行满足条件返回true
     * @throws SQLException 如果评估失败则抛出异常
     */
    public boolean evaluate(WhereCondition condition, String[] row, 
                           Map<String, Integer> columnIndexMap,
                           java.util.List<TableInfo> tables) throws SQLException {
        if (condition == null) {
            return true; // 无条件，返回true
        }
        
        if (condition.isSimple()) {
            return evaluateSimpleCondition(condition, row, columnIndexMap, tables);
        } else if (condition.isCompound()) {
            return evaluateCompoundCondition(condition, row, columnIndexMap, tables);
        } else if (condition.isAggregate()) {
            // 聚合函数条件用于HAVING，不在WHERE中使用
            throw new SQLException("Aggregate condition cannot be used in WHERE clause");
        }
        
        return false;
    }
    
    /**
     * 评估HAVING条件（支持聚合函数）
     * 
     * @param condition HAVING条件对象
     * @param aggregateValues 聚合函数值映射（函数别名 -> 值）
     * @return 如果分组满足条件返回true
     * @throws SQLException 如果评估失败则抛出异常
     */
    public boolean evaluateHaving(WhereCondition condition, 
                                  Map<String, Object> aggregateValues) throws SQLException {
        if (condition == null) {
            return true; // 无条件，返回true
        }
        
        if (condition.isAggregate()) {
            return evaluateAggregateCondition(condition, aggregateValues);
        } else if (condition.isSimple()) {
            // 简单条件也可以用于HAVING（比较聚合函数值）
            return evaluateSimpleHavingCondition(condition, aggregateValues);
        } else if (condition.isCompound()) {
            boolean left = evaluateHaving(condition.getLeftCondition(), aggregateValues);
            boolean right = evaluateHaving(condition.getRightCondition(), aggregateValues);
            
            String op = condition.getLogicalOperator();
            if ("AND".equalsIgnoreCase(op)) {
                return left && right;
            } else if ("OR".equalsIgnoreCase(op)) {
                return left || right;
            }
        }
        
        return false;
    }
    
    /**
     * 评估简单条件
     * 
     * @param condition 简单条件
     * @param row 数据行
     * @param columnIndexMap 列名到索引的映射
     * @param tables 表信息列表
     * @return 如果满足条件返回true
     * @throws SQLException 如果评估失败则抛出异常
     */
    private boolean evaluateSimpleCondition(WhereCondition condition, String[] row,
                                            Map<String, Integer> columnIndexMap,
                                            java.util.List<TableInfo> tables) throws SQLException {
        String leftOperand = condition.getLeftOperand();
        String operator = condition.getOperator();
        String rightOperand = condition.getRightOperand();
        
        // 解析左操作数（列引用）
        String leftValue = getColumnValue(leftOperand, row, columnIndexMap, tables);
        
        // 解析右操作数（可能是列引用或字面量）
        String rightValue = getRightOperandValue(rightOperand, row, columnIndexMap, tables);
        
        return compareValues(leftValue, operator, rightValue);
    }
    
    /**
     * 评估复合条件
     * 
     * @param condition 复合条件
     * @param row 数据行
     * @param columnIndexMap 列名到索引的映射
     * @param tables 表信息列表
     * @return 如果满足条件返回true
     * @throws SQLException 如果评估失败则抛出异常
     */
    private boolean evaluateCompoundCondition(WhereCondition condition, String[] row,
                                             Map<String, Integer> columnIndexMap,
                                             java.util.List<TableInfo> tables) throws SQLException {
        boolean left = evaluate(condition.getLeftCondition(), row, columnIndexMap, tables);
        boolean right = evaluate(condition.getRightCondition(), row, columnIndexMap, tables);
        
        String op = condition.getLogicalOperator();
        if ("AND".equalsIgnoreCase(op)) {
            return left && right;
        } else if ("OR".equalsIgnoreCase(op)) {
            return left || right;
        }
        
        return false;
    }
    
    /**
     * 评估聚合函数条件
     * 
     * @param condition 聚合函数条件
     * @param aggregateValues 聚合函数值映射
     * @return 如果满足条件返回true
     * @throws SQLException 如果评估失败则抛出异常
     */
    private boolean evaluateAggregateCondition(WhereCondition condition,
                                              Map<String, Object> aggregateValues) throws SQLException {
        AggregateFunction func = condition.getAggregateFunction();
        String operator = condition.getOperator();
        String rightOperand = condition.getRightOperand();
        
        // 获取聚合函数值
        String funcKey = func.getDisplayName();
        Object funcValue = aggregateValues.get(funcKey);
        if (funcValue == null) {
            // 尝试使用别名
            if (func.getAlias() != null) {
                funcValue = aggregateValues.get(func.getAlias());
            }
        }
        
        if (funcValue == null) {
            throw new SQLException("Aggregate function value not found: " + funcKey);
        }
        
        String leftValue = funcValue.toString();
        String rightValue = rightOperand;
        
        return compareValues(leftValue, operator, rightValue);
    }
    
    /**
     * 评估简单HAVING条件
     * 
     * @param condition 简单条件
     * @param aggregateValues 聚合函数值映射
     * @return 如果满足条件返回true
     * @throws SQLException 如果评估失败则抛出异常
     */
    private boolean evaluateSimpleHavingCondition(WhereCondition condition,
                                                 Map<String, Object> aggregateValues) throws SQLException {
        String leftOperand = condition.getLeftOperand();
        String operator = condition.getOperator();
        String rightOperand = condition.getRightOperand();
        
        // 左操作数应该是聚合函数别名或列名
        // 尝试多种键名匹配：原始字符串、大写、去掉空格等
        Object leftValueObj = aggregateValues.get(leftOperand);
        if (leftValueObj == null) {
            // 尝试大写匹配
            leftValueObj = aggregateValues.get(leftOperand.toUpperCase());
        }
        if (leftValueObj == null) {
            // 尝试去掉空格匹配
            leftValueObj = aggregateValues.get(leftOperand.replaceAll("\\s+", ""));
        }
        if (leftValueObj == null) {
            // 尝试匹配所有键（包含leftOperand的键）
            String normalizedLeft = leftOperand.replaceAll("\\s+", "").toUpperCase();
            for (Map.Entry<String, Object> entry : aggregateValues.entrySet()) {
                String key = entry.getKey();
                if (key != null) {
                    String normalizedKey = key.replaceAll("\\s+", "").toUpperCase();
                    // 检查是否是聚合函数表达式匹配（如COUNT(*)匹配COUNT(*))
                    if (normalizedKey.equals(normalizedLeft) || 
                        key.equalsIgnoreCase(leftOperand) ||
                        // 如果leftOperand是聚合函数表达式（如COUNT(*))，尝试匹配所有值
                        (leftOperand.toUpperCase().startsWith("COUNT") && normalizedKey.contains("COUNT"))) {
                        leftValueObj = entry.getValue();
                        break;
                    }
                }
            }
        }
        if (leftValueObj == null) {
            // 如果leftOperand是聚合函数表达式（如COUNT(*))，尝试从所有值中查找
            // 因为可能有多个聚合函数，需要找到匹配的那个
            String normalizedLeft = leftOperand.replaceAll("\\s+", "").toUpperCase();
            for (Map.Entry<String, Object> entry : aggregateValues.entrySet()) {
                String key = entry.getKey();
                if (key != null) {
                    String normalizedKey = key.replaceAll("\\s+", "").toUpperCase();
                    // 检查是否是聚合函数表达式匹配
                    if (normalizedKey.contains(normalizedLeft) || normalizedLeft.contains(normalizedKey)) {
                        leftValueObj = entry.getValue();
                        break;
                    }
                }
            }
        }
        if (leftValueObj == null) {
            throw new SQLException("Column or aggregate not found in HAVING: " + leftOperand + 
                                 ". Available keys: " + aggregateValues.keySet());
        }
        
        String leftValue = leftValueObj.toString();
        String rightValue = rightOperand;
        
        return compareValues(leftValue, operator, rightValue);
    }
    
    /**
     * 获取列值
     * 
     * @param columnRef 列引用（table.column 或 column）
     * @param row 数据行
     * @param columnIndexMap 列名到索引的映射
     * @param tables 表信息列表
     * @return 列值字符串
     * @throws SQLException 如果列不存在则抛出异常
     */
    private String getColumnValue(String columnRef, String[] row,
                                 Map<String, Integer> columnIndexMap,
                                 java.util.List<TableInfo> tables) throws SQLException {
        String[] parts = JoinCondition.parseColumnRef(columnRef);
        String tableAlias = parts[0];
        String columnName = parts[1].toUpperCase();
        
        // 如果没有表别名，直接查找列名
        if (tableAlias == null || tableAlias.isEmpty()) {
            Integer index = columnIndexMap.get(columnName);
            if (index == null) {
                throw new SQLException("Column not found: " + columnName);
            }
            if (index < 0 || index >= row.length) {
                return null;
            }
            return row[index];
        }
        
        // 有表别名，需要找到对应的表
        // 简化处理：假设列名在columnIndexMap中已经包含了表别名信息
        // 或者直接查找列名（忽略表别名）
        Integer index = columnIndexMap.get(columnName);
        if (index == null) {
            // 尝试不带表别名查找
            index = columnIndexMap.get(columnName);
        }
        
        if (index == null || index < 0 || index >= row.length) {
            return null;
        }
        
        return row[index];
    }
    
    /**
     * 获取右操作数值（可能是列引用或字面量）
     * 
     * @param rightOperand 右操作数
     * @param row 数据行
     * @param columnIndexMap 列名到索引的映射
     * @param tables 表信息列表
     * @return 值字符串
     * @throws SQLException 如果解析失败则抛出异常
     */
    private String getRightOperandValue(String rightOperand, String[] row,
                                       Map<String, Integer> columnIndexMap,
                                       java.util.List<TableInfo> tables) throws SQLException {
        // 检查是否是列引用（包含点号或看起来像列名）
        if (rightOperand.contains(".") || 
            (rightOperand.length() > 0 && Character.isLetter(rightOperand.charAt(0)))) {
            // 可能是列引用，尝试解析
            try {
                return getColumnValue(rightOperand, row, columnIndexMap, tables);
            } catch (SQLException e) {
                // 不是列引用，当作字面量处理
            }
        }
        
        // 字面量，去掉引号
        if (rightOperand.startsWith("'") && rightOperand.endsWith("'")) {
            return rightOperand.substring(1, rightOperand.length() - 1);
        }
        if (rightOperand.startsWith("\"") && rightOperand.endsWith("\"")) {
            return rightOperand.substring(1, rightOperand.length() - 1);
        }
        
        return rightOperand;
    }
    
    /**
     * 比较两个值
     * 
     * @param leftValue 左值
     * @param operator 操作符
     * @param rightValue 右值
     * @return 如果比较结果为true返回true
     */
    private boolean compareValues(String leftValue, String operator, String rightValue) {
        if (leftValue == null && rightValue == null) {
            return "=".equals(operator) || "<>".equals(operator) || "!=".equals(operator) ? 
                   "=".equals(operator) : false;
        }
        if (leftValue == null || rightValue == null) {
            return false; // NULL比较总是false（除了IS NULL）
        }
        
        // 尝试数值比较
        try {
            BigDecimal leftNum = new BigDecimal(leftValue);
            BigDecimal rightNum = new BigDecimal(rightValue);
            
            int cmp = leftNum.compareTo(rightNum);
            
            switch (operator) {
                case "=":
                    return cmp == 0;
                case "!=":
                case "<>":
                    return cmp != 0;
                case ">":
                    return cmp > 0;
                case ">=":
                    return cmp >= 0;
                case "<":
                    return cmp < 0;
                case "<=":
                    return cmp <= 0;
                default:
                    return false;
            }
        } catch (NumberFormatException e) {
            // 不是数字，进行字符串比较
            int cmp = leftValue.compareTo(rightValue);
            
            switch (operator) {
                case "=":
                    return cmp == 0;
                case "!=":
                case "<>":
                    return cmp != 0;
                case ">":
                    return cmp > 0;
                case ">=":
                    return cmp >= 0;
                case "<":
                    return cmp < 0;
                case "<=":
                    return cmp <= 0;
                case "LIKE":
                    // 简单的LIKE匹配（支持%通配符）
                    return likeMatch(leftValue, rightValue);
                default:
                    return false;
            }
        }
    }
    
    /**
     * LIKE模式匹配（简单实现，支持%通配符）
     * 
     * @param value 值
     * @param pattern 模式
     * @return 如果匹配返回true
     */
    private boolean likeMatch(String value, String pattern) {
        if (value == null || pattern == null) {
            return false;
        }
        
        // 将SQL LIKE模式转换为正则表达式
        String regex = pattern.replace("%", ".*").replace("_", ".");
        return value.matches(regex);
    }
}

