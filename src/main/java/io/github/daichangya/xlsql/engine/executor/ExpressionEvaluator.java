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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import io.github.daichangya.xlsql.engine.plan.TableInfo;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

/**
 * ExpressionEvaluator - 表达式评估器
 * 
 * <p>使用JSqlParser的访问者模式评估SQL表达式。
 * 支持所有JSqlParser支持的表达式类型，包括：
 * - 列引用
 * - 字面量（字符串、数字、NULL）
 * - 算术运算
 * - 比较运算
 * - 逻辑运算
 * - 函数调用
 * - CASE表达式
 * 等。</p>
 * 
 * @author daichangya
 */
public class ExpressionEvaluator extends ExpressionVisitorAdapter {
    
    /** 日志记录器 */
    private static final Logger logger = Logger.getLogger(ExpressionEvaluator.class.getName());
    
    /** 当前数据行 */
    private String[] row;
    
    /** 列名到索引的映射 */
    private Map<String, Integer> columnIndexMap;
    
    /** 表信息列表（用于解析表别名） */
    private List<TableInfo> tables;
    
    /** 评估结果 */
    private Object result;
    
    /**
     * 评估表达式
     * 
     * @param expression 要评估的表达式
     * @param row 数据行
     * @param columnIndexMap 列名到索引的映射
     * @param tables 表信息列表
     * @return 评估结果（String、Number或Boolean）
     * @throws SQLException 如果评估失败则抛出异常
     */
    public Object evaluate(Expression expression, String[] row, 
                          Map<String, Integer> columnIndexMap,
                          List<TableInfo> tables) throws SQLException {
        this.row = row;
        this.columnIndexMap = columnIndexMap;
        this.tables = tables;
        this.result = null;
        
        if (expression != null) {
            expression.accept(this);
        }
        
        return result;
    }
    
    /**
     * 评估表达式（返回字符串）
     * 
     * @param expression 要评估的表达式
     * @param row 数据行
     * @param columnIndexMap 列名到索引的映射
     * @param tables 表信息列表
     * @return 评估结果字符串
     * @throws SQLException 如果评估失败则抛出异常
     */
    public String evaluateAsString(Expression expression, String[] row,
                                  Map<String, Integer> columnIndexMap,
                                  List<TableInfo> tables) throws SQLException {
        Object value = evaluate(expression, row, columnIndexMap, tables);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 评估表达式（返回布尔值，用于WHERE条件）
     * 
     * @param expression 要评估的表达式
     * @param row 数据行
     * @param columnIndexMap 列名到索引的映射
     * @param tables 表信息列表
     * @return 评估结果布尔值
     * @throws SQLException 如果评估失败则抛出异常
     */
    public boolean evaluateAsBoolean(Expression expression, String[] row,
                                    Map<String, Integer> columnIndexMap,
                                    List<TableInfo> tables) throws SQLException {
        Object value = evaluate(expression, row, columnIndexMap, tables);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value == null) {
            return false;
        }
        // 非零值视为true
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0.0;
        }
        // 非空字符串视为true
        return !value.toString().isEmpty();
    }
    
    // ========== 字面量 ==========
    
    public void visit(StringValue stringValue) {
        result = stringValue.getValue();
    }
    
    public void visit(LongValue longValue) {
        result = longValue.getValue();
    }
    
    public void visit(DoubleValue doubleValue) {
        result = doubleValue.getValue();
    }
    
    public void visit(NullValue nullValue) {
        result = null;
    }
    
    public void visit(HexValue hexValue) {
        try {
            result = Long.parseLong(hexValue.getValue(), 16);
        } catch (NumberFormatException e) {
            result = hexValue.getValue();
        }
    }
    
    // ========== 列引用 ==========
    
    public void visit(Column column) {
        String columnName = column.getColumnName();
        if (columnName == null) {
            result = null;
            return;
        }
        
        // 处理表别名
        Table table = column.getTable();
        String tableAlias = null;
        if (table != null) {
            tableAlias = table.getAlias() != null ? table.getAlias().getName() : 
                        table.getName() != null ? table.getName() : null;
        }
        
        // 构建完整的列引用键
        String key = columnName.toUpperCase();
        if (tableAlias != null && !tableAlias.isEmpty()) {
            key = tableAlias.toUpperCase() + "." + key;
        }
        
        // 查找列索引
        Integer index = columnIndexMap.get(key);
        if (index == null) {
            // 尝试不带表别名
            index = columnIndexMap.get(columnName.toUpperCase());
        }
        
        if (index != null && index >= 0 && index < row.length) {
            result = row[index];
        } else {
            result = null;
        }
    }
    
    // ========== 算术运算 ==========
    
    public void visit(Addition addition) {
        Object left = evaluateOperand(addition.getLeftExpression());
        Object right = evaluateOperand(addition.getRightExpression());
        result = performArithmetic(left, right, "+");
    }
    
    public void visit(Subtraction subtraction) {
        Object left = evaluateOperand(subtraction.getLeftExpression());
        Object right = evaluateOperand(subtraction.getRightExpression());
        result = performArithmetic(left, right, "-");
    }
    
    public void visit(Multiplication multiplication) {
        Object left = evaluateOperand(multiplication.getLeftExpression());
        Object right = evaluateOperand(multiplication.getRightExpression());
        result = performArithmetic(left, right, "*");
    }
    
    public void visit(Division division) {
        Object left = evaluateOperand(division.getLeftExpression());
        Object right = evaluateOperand(division.getRightExpression());
        result = performArithmetic(left, right, "/");
    }
    
    public void visit(Modulo modulo) {
        Object left = evaluateOperand(modulo.getLeftExpression());
        Object right = evaluateOperand(modulo.getRightExpression());
        result = performArithmetic(left, right, "%");
    }
    
    public void visit(Concat concat) {
        String left = evaluateOperandAsString(concat.getLeftExpression());
        String right = evaluateOperandAsString(concat.getRightExpression());
        result = (left != null ? left : "") + (right != null ? right : "");
    }
    
    // ========== 比较运算 ==========
    
    public void visit(EqualsTo equalsTo) {
        Object left = evaluateOperand(equalsTo.getLeftExpression());
        Object right = evaluateOperand(equalsTo.getRightExpression());
        boolean equals = compareValues(left, right) == 0;
        result = Boolean.valueOf(equals); // 确保返回Boolean类型
        // 调试日志
        logger.info("EqualsTo: left=" + left + ", right=" + right + ", result=" + equals);
    }
    
    public void visit(NotEqualsTo notEqualsTo) {
        Object left = evaluateOperand(notEqualsTo.getLeftExpression());
        Object right = evaluateOperand(notEqualsTo.getRightExpression());
        result = compareValues(left, right) != 0;
    }
    
    public void visit(GreaterThan greaterThan) {
        Object left = evaluateOperand(greaterThan.getLeftExpression());
        Object right = evaluateOperand(greaterThan.getRightExpression());
        result = compareValues(left, right) > 0;
    }
    
    public void visit(GreaterThanEquals greaterThanEquals) {
        Object left = evaluateOperand(greaterThanEquals.getLeftExpression());
        Object right = evaluateOperand(greaterThanEquals.getRightExpression());
        result = compareValues(left, right) >= 0;
    }
    
    public void visit(MinorThan minorThan) {
        Object left = evaluateOperand(minorThan.getLeftExpression());
        Object right = evaluateOperand(minorThan.getRightExpression());
        result = compareValues(left, right) < 0;
    }
    
    public void visit(MinorThanEquals minorThanEquals) {
        Object left = evaluateOperand(minorThanEquals.getLeftExpression());
        Object right = evaluateOperand(minorThanEquals.getRightExpression());
        result = compareValues(left, right) <= 0;
    }
    
    public void visit(LikeExpression likeExpression) {
        String value = evaluateOperandAsString(likeExpression.getLeftExpression());
        String pattern = evaluateOperandAsString(likeExpression.getRightExpression());
        
        if (value == null || pattern == null) {
            result = false;
            return;
        }
        
        // 简单的LIKE匹配（支持%和_通配符）
        String regex = pattern.replace("%", ".*").replace("_", ".");
        result = value.matches(regex);
    }
    
    public void visit(InExpression inExpression) {
        Object left = evaluateOperand(inExpression.getLeftExpression());
        
        // 检查是否是子查询（暂不支持）
        if (inExpression.getRightExpression() != null) {
            // 检查是否是子查询（通常子查询是Select类型的表达式）
            Expression rightExpr = inExpression.getRightExpression();
            if (rightExpr.toString().toUpperCase().startsWith("SELECT")) {
                // IN (subquery) - 暂不支持
                result = false;
                return;
            }
            // 如果不是子查询，可能是值列表，继续处理
        }
        
        // JSqlParser 4.7的API：使用反射获取getRightItemsList方法
        try {
            // 尝试使用反射获取getRightItemsList方法
            java.lang.reflect.Method getRightItemsList = inExpression.getClass().getMethod("getRightItemsList");
            Object rightItemsList = getRightItemsList.invoke(inExpression);
            
            if (rightItemsList != null) {
                // 如果返回的是ExpressionList，直接使用
                if (rightItemsList instanceof ExpressionList) {
                    ExpressionList exprList = (ExpressionList) rightItemsList;
                    List<Expression> items = exprList.getExpressions();
                    if (items != null) {
                        for (Expression item : items) {
                            Object right = evaluateOperand(item);
                            if (compareValues(left, right) == 0) {
                                result = true;
                                return;
                            }
                        }
                    }
                } else {
                    // 尝试使用反射获取expressions（用于其他ItemsList实现）
                    try {
                        java.lang.reflect.Method getExpressions = rightItemsList.getClass().getMethod("getExpressions");
                        @SuppressWarnings("unchecked")
                        List<Expression> items = (List<Expression>) getExpressions.invoke(rightItemsList);
                        if (items != null) {
                            for (Expression item : items) {
                                Object right = evaluateOperand(item);
                                if (compareValues(left, right) == 0) {
                                    result = true;
                                    return;
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 如果方法不存在，记录警告
                        logger.warning("Cannot get expressions from ItemsList: " + rightItemsList.getClass().getName() + ", error: " + e.getMessage());
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            // getRightItemsList方法不存在，尝试解析getRightExpression
            // 如果getRightExpression返回的是Parenthesis，尝试解析内部表达式
            Expression rightExpr = inExpression.getRightExpression();
            if (rightExpr instanceof Parenthesis) {
                Parenthesis paren = (Parenthesis) rightExpr;
                Expression innerExpr = paren.getExpression();
                // 如果内部表达式是ExpressionList，解析它
                if (innerExpr instanceof ExpressionList) {
                    ExpressionList exprList = (ExpressionList) innerExpr;
                    List<Expression> items = exprList.getExpressions();
                    if (items != null) {
                        for (Expression item : items) {
                            Object right = evaluateOperand(item);
                            if (compareValues(left, right) == 0) {
                                result = true;
                                return;
                            }
                        }
                    }
                }
            } else if (rightExpr instanceof ExpressionList) {
                // 直接是ExpressionList
                ExpressionList exprList = (ExpressionList) rightExpr;
                List<Expression> items = exprList.getExpressions();
                if (items != null) {
                    for (Expression item : items) {
                        Object right = evaluateOperand(item);
                        if (compareValues(left, right) == 0) {
                            result = true;
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 如果反射失败，记录警告
            logger.warning("Cannot get right items list from InExpression: " + e.getMessage());
        }
        
        result = false;
    }
    
    public void visit(Between between) {
        Object value = evaluateOperand(between.getLeftExpression());
        Object min = evaluateOperand(between.getBetweenExpressionStart());
        Object max = evaluateOperand(between.getBetweenExpressionEnd());
        
        int cmp1 = compareValues(value, min);
        int cmp2 = compareValues(value, max);
        result = cmp1 >= 0 && cmp2 <= 0;
    }
    
    public void visit(IsNullExpression isNullExpression) {
        Object value = evaluateOperand(isNullExpression.getLeftExpression());
        result = (value == null) != isNullExpression.isNot();
    }
    
    // ========== 逻辑运算 ==========
    
    public void visit(AndExpression andExpression) {
        boolean left = evaluateOperandAsBoolean(andExpression.getLeftExpression());
        boolean right = evaluateOperandAsBoolean(andExpression.getRightExpression());
        result = left && right;
    }
    
    public void visit(OrExpression orExpression) {
        boolean left = evaluateOperandAsBoolean(orExpression.getLeftExpression());
        boolean right = evaluateOperandAsBoolean(orExpression.getRightExpression());
        result = left || right;
    }
    
    // ========== 其他表达式 ==========
    
    public void visit(Parenthesis parenthesis) {
        result = evaluateOperand(parenthesis.getExpression());
    }
    
    public void visit(Function function) {
        // 函数调用 - 在AggregationExecutor中处理聚合函数
        // 这里处理非聚合函数
        String functionName = function.getName().toUpperCase();
        
        if (function.getParameters() == null || function.getParameters().getExpressions().isEmpty()) {
            result = null;
            return;
        }
        
        // JSQLParser 4.9: getExpressions() 返回 List<?>，需要类型转换
        @SuppressWarnings("unchecked")
        List<Expression> params = (List<Expression>) (List<?>) function.getParameters().getExpressions();
        
        // 处理一些简单的函数
        switch (functionName) {
            case "UPPER":
            case "UCASE":
                if (params.size() == 1) {
                    String str = evaluateOperandAsString(params.get(0));
                    result = str != null ? str.toUpperCase() : null;
                }
                break;
            case "LOWER":
            case "LCASE":
                if (params.size() == 1) {
                    String str = evaluateOperandAsString(params.get(0));
                    result = str != null ? str.toLowerCase() : null;
                }
                break;
            case "TRIM":
                if (params.size() == 1) {
                    String str = evaluateOperandAsString(params.get(0));
                    result = str != null ? str.trim() : null;
                }
                break;
            case "LENGTH":
            case "CHAR_LENGTH":
                if (params.size() == 1) {
                    String str = evaluateOperandAsString(params.get(0));
                    result = str != null ? str.length() : 0;
                }
                break;
            default:
                // 其他函数暂不支持，返回第一个参数的值
                if (!params.isEmpty()) {
                    result = evaluateOperand(params.get(0));
                } else {
                    result = null;
                }
        }
    }
    
    /**
     * 默认visit方法，处理未覆盖的表达式类型
     * 如果ExpressionVisitorAdapter有默认实现，这个方法会被调用
     */
    public void visit(Expression expression) {
        // 如果表达式类型没有被特定的visit方法处理，记录警告并返回null
        logger.warning("Unhandled expression type: " + expression.getClass().getName() + ", expression: " + expression);
        result = null;
    }
    
    // ========== 辅助方法 ==========
    
    /**
     * 评估操作数
     */
    private Object evaluateOperand(Expression expression) {
        if (expression == null) {
            return null;
        }
        try {
            // 创建新的ExpressionEvaluator实例，避免result被覆盖
            ExpressionEvaluator evaluator = new ExpressionEvaluator();
            return evaluator.evaluate(expression, row, columnIndexMap, tables);
        } catch (SQLException e) {
            return null;
        }
    }
    
    /**
     * 评估操作数为字符串
     */
    private String evaluateOperandAsString(Expression expression) {
        Object value = evaluateOperand(expression);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 评估操作数为布尔值
     */
    private boolean evaluateOperandAsBoolean(Expression expression) {
        if (expression == null) {
            return false;
        }
        try {
            // 创建新的ExpressionEvaluator实例，避免result被覆盖
            ExpressionEvaluator evaluator = new ExpressionEvaluator();
            return evaluator.evaluateAsBoolean(expression, row, columnIndexMap, tables);
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * 执行算术运算
     */
    private Object performArithmetic(Object left, Object right, String operator) {
        if (left == null || right == null) {
            return null;
        }
        
        try {
            BigDecimal leftNum = toBigDecimal(left);
            BigDecimal rightNum = toBigDecimal(right);
            
            switch (operator) {
                case "+":
                    return leftNum.add(rightNum);
                case "-":
                    return leftNum.subtract(rightNum);
                case "*":
                    return leftNum.multiply(rightNum);
                case "/":
                    if (rightNum.compareTo(BigDecimal.ZERO) == 0) {
                        return null; // 除零
                    }
                    return leftNum.divide(rightNum, 10, BigDecimal.ROUND_HALF_UP);
                case "%":
                    return leftNum.remainder(rightNum);
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 转换为BigDecimal
     */
    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 比较两个值
     */
    private int compareValues(Object left, Object right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        if (right == null) {
            return 1;
        }
        
        // 先尝试字符串比较（如果都是字符串）
        if (left instanceof String && right instanceof String) {
            return ((String) left).compareTo((String) right);
        }
        
        // 尝试数值比较
        try {
            BigDecimal leftNum = toBigDecimal(left);
            BigDecimal rightNum = toBigDecimal(right);
            // 检查是否真的都是数字（通过比较原始字符串和BigDecimal字符串）
            String leftStr = left.toString().trim();
            String rightStr = right.toString().trim();
            String leftNumStr = leftNum.toString();
            String rightNumStr = rightNum.toString();
            
            // 如果原始字符串和BigDecimal字符串不匹配，说明不是纯数字，使用字符串比较
            if (!leftStr.equals(leftNumStr) && !leftStr.replaceFirst("^0+", "").equals(leftNumStr.replaceFirst("^0+", ""))) {
                return leftStr.compareTo(rightStr);
            }
            if (!rightStr.equals(rightNumStr) && !rightStr.replaceFirst("^0+", "").equals(rightNumStr.replaceFirst("^0+", ""))) {
                return leftStr.compareTo(rightStr);
            }
            
            return leftNum.compareTo(rightNum);
        } catch (NumberFormatException e) {
            // 不是数字，进行字符串比较
            return left.toString().compareTo(right.toString());
        }
    }
}

