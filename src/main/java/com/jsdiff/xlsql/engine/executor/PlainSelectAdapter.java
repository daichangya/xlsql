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
package com.jsdiff.xlsql.engine.executor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jsdiff.xlsql.engine.model.AggregateFunction;
import com.jsdiff.xlsql.engine.model.AggregateType;
import com.jsdiff.xlsql.engine.model.JoinType;
import com.jsdiff.xlsql.engine.plan.JoinCondition;
import com.jsdiff.xlsql.engine.plan.JoinInfo;
import com.jsdiff.xlsql.engine.plan.OrderByItem;
import com.jsdiff.xlsql.engine.plan.QueryPlan;
import com.jsdiff.xlsql.engine.plan.TableInfo;
import com.jsdiff.xlsql.engine.plan.WhereCondition;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;

/**
 * PlainSelectAdapter - PlainSelect到QueryPlan的适配器
 * 
 * <p>将JSqlParser的PlainSelect AST转换为QueryPlan，以便现有执行器可以继续使用。
 * 这是一个临时适配器，后续可以逐步重构执行器直接使用PlainSelect。</p>
 * 
 * @author daichangya
 */
public class PlainSelectAdapter {
    
    /**
     * 将PlainSelect转换为QueryPlan
     * 
     * @param plainSelect JSqlParser的PlainSelect对象
     * @return QueryPlan对象
     * @throws SQLException 如果转换失败则抛出异常
     */
    public QueryPlan toQueryPlan(PlainSelect plainSelect) throws SQLException {
        QueryPlan plan = new QueryPlan();
        
        // 转换SELECT子句
        convertSelect(plainSelect, plan);
        
        // 转换FROM子句
        convertFrom(plainSelect, plan);
        
        // 转换JOIN子句
        convertJoins(plainSelect, plan);
        
        // 转换WHERE子句
        convertWhere(plainSelect, plan);
        
        // 转换GROUP BY子句
        convertGroupBy(plainSelect, plan);
        
        // 转换HAVING子句
        convertHaving(plainSelect, plan);
        
        // 转换ORDER BY子句
        convertOrderBy(plainSelect, plan);
        
        // 转换LIMIT子句
        convertLimit(plainSelect, plan);
        
        return plan;
    }
    
    /**
     * 转换SELECT子句
     */
    private void convertSelect(PlainSelect plainSelect, QueryPlan plan) throws SQLException {
        final List<String> columns = new ArrayList<>();
        final List<AggregateFunction> aggregates = new ArrayList<>();
        
        if (plainSelect.getSelectItems() != null) {
            for (SelectItem item : plainSelect.getSelectItems()) {
                // 使用访问者模式处理SelectItem
                item.accept(new SelectItemVisitorAdapter() {
                    public void visit(AllColumns allColumns) {
                        // SELECT * - 暂时标记为空列表，后续处理
                    }
                    
                    public void visit(AllTableColumns allTableColumns) {
                        // SELECT table.* - 暂时标记为空列表
                    }
                    
                    @Override
                    public void visit(SelectItem selectItem) {
                        // 处理表达式项 - 通过toString获取表达式字符串
                        String itemStr = selectItem.toString();
                        
                        // 尝试解析表达式（简化处理）
                        // 检查是否是聚合函数
                        String upper = itemStr.toUpperCase();
                        if (upper.startsWith("COUNT(") || upper.startsWith("SUM(") || 
                            upper.startsWith("AVG(") || upper.startsWith("MAX(") || 
                            upper.startsWith("MIN(")) {
                            // 聚合函数
                            AggregateType type = getAggregateTypeFromString(upper);
                            String column = extractColumnFromString(itemStr);
                            boolean distinct = upper.contains("DISTINCT");
                            String alias = null; // 暂时不支持别名
                            
                            aggregates.add(new AggregateFunction(type, column, distinct, alias));
                        } else {
                            // 普通列或表达式
                            columns.add(itemStr);
                        }
                    }
                });
            }
        }
        
        plan.setSelectColumns(columns);
        plan.setAggregateFunctions(aggregates);
    }
    
    /**
     * 从字符串中获取聚合函数类型
     */
    private AggregateType getAggregateTypeFromString(String str) {
        if (str.startsWith("COUNT(")) {
            return AggregateType.COUNT;
        } else if (str.startsWith("SUM(")) {
            return AggregateType.SUM;
        } else if (str.startsWith("AVG(")) {
            return AggregateType.AVG;
        } else if (str.startsWith("MAX(")) {
            return AggregateType.MAX;
        } else if (str.startsWith("MIN(")) {
            return AggregateType.MIN;
        }
        return AggregateType.COUNT;
    }
    
    /**
     * 从字符串中提取列名
     */
    private String extractColumnFromString(String str) {
        int start = str.indexOf('(');
        int end = str.lastIndexOf(')');
        if (start > 0 && end > start) {
            String param = str.substring(start + 1, end).trim();
            if (param.equals("*")) {
                return "*";
            }
            return param;
        }
        return "*";
    }
    
    /**
     * 转换FROM子句
     */
    private void convertFrom(PlainSelect plainSelect, QueryPlan plan) throws SQLException {
        if (plainSelect.getFromItem() == null) {
            throw new SQLException("Missing FROM clause");
        }
        
        if (plainSelect.getFromItem() instanceof Table) {
            Table table = (Table) plainSelect.getFromItem();
            String[] tableParts = parseTableName(table);
            String workbook = tableParts[0];
            String sheet = tableParts[1];
            String alias = table.getAlias() != null ? table.getAlias().getName() : null;
            
            TableInfo tableInfo = new TableInfo(workbook, sheet, alias);
            plan.setMainTable(tableInfo);
        } else {
            throw new SQLException("Subqueries in FROM clause are not yet supported");
        }
    }
    
    /**
     * 转换JOIN子句
     */
    private void convertJoins(PlainSelect plainSelect, QueryPlan plan) throws SQLException {
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                if (join.getRightItem() instanceof Table) {
                    Table rightTable = (Table) join.getRightItem();
                    String[] tableParts = parseTableName(rightTable);
                    String workbook = tableParts[0];
                    String sheet = tableParts[1];
                    String alias = rightTable.getAlias() != null ? rightTable.getAlias().getName() : null;
                    
                    TableInfo joinTable = new TableInfo(workbook, sheet, alias);
                    
                    // 转换JOIN类型
                    JoinType joinType = convertJoinType(join);
                    
                    // 转换JOIN条件
                    JoinCondition condition = convertJoinCondition(join);
                    
                    JoinInfo joinInfo = new JoinInfo(joinType, joinTable, condition);
                    plan.addJoin(joinInfo);
                } else {
                    throw new SQLException("Subqueries in JOIN are not yet supported");
                }
            }
        }
    }
    
    /**
     * 转换WHERE子句
     */
    private void convertWhere(PlainSelect plainSelect, QueryPlan plan) {
        Expression whereExpr = plainSelect.getWhere();
        if (whereExpr != null) {
            // 暂时使用简化的WhereCondition
            // 后续可以改进以支持复杂表达式
            WhereCondition condition = convertExpressionToWhereCondition(whereExpr);
            plan.setWhereClause(condition);
        }
    }
    
    /**
     * 转换GROUP BY子句
     */
    private void convertGroupBy(PlainSelect plainSelect, QueryPlan plan) {
        if (plainSelect.getGroupBy() != null && plainSelect.getGroupBy().getGroupByExpressions() != null) {
            for (Object obj : plainSelect.getGroupBy().getGroupByExpressions()) {
                // JSqlParser 4.7的getGroupByExpressions()返回List<Object>，需要转换为Expression
                if (obj instanceof Expression) {
                    Expression expr = (Expression) obj;
                    plan.addGroupByColumn(expr.toString());
                } else {
                    // 如果不是Expression，使用toString
                    plan.addGroupByColumn(obj.toString());
                }
            }
        }
    }
    
    /**
     * 转换HAVING子句
     */
    private void convertHaving(PlainSelect plainSelect, QueryPlan plan) {
        Expression havingExpr = plainSelect.getHaving();
        if (havingExpr != null) {
            WhereCondition condition = convertExpressionToWhereCondition(havingExpr);
            plan.setHavingClause(condition);
        }
    }
    
    /**
     * 转换ORDER BY子句
     */
    private void convertOrderBy(PlainSelect plainSelect, QueryPlan plan) {
        if (plainSelect.getOrderByElements() != null) {
            for (OrderByElement elem : plainSelect.getOrderByElements()) {
                String column = elem.getExpression().toString();
                OrderByItem.SortDirection direction = elem.isAsc() ? 
                    OrderByItem.SortDirection.ASC : OrderByItem.SortDirection.DESC;
                plan.addOrderBy(new OrderByItem(column, direction));
            }
        }
    }
    
    /**
     * 转换LIMIT子句
     */
    private void convertLimit(PlainSelect plainSelect, QueryPlan plan) {
        Limit limit = plainSelect.getLimit();
        if (limit != null) {
            if (limit.getRowCount() != null) {
                // JSqlParser 4.7的Limit API可能不同，尝试多种方式获取值
                try {
                    // 尝试直接获取Long值
                    if (limit.getRowCount() instanceof net.sf.jsqlparser.expression.LongValue) {
                        net.sf.jsqlparser.expression.LongValue longValue = 
                            (net.sf.jsqlparser.expression.LongValue) limit.getRowCount();
                        plan.setLimit((int) longValue.getValue());
                    } else {
                        // 尝试通过toString解析
                        String rowCountStr = limit.getRowCount().toString();
                        plan.setLimit(Integer.parseInt(rowCountStr));
                    }
                } catch (Exception e) {
                    // 如果失败，尝试使用反射
                    try {
                        java.lang.reflect.Method getValue = limit.getRowCount().getClass().getMethod("getValue");
                        Object value = getValue.invoke(limit.getRowCount());
                        if (value instanceof Number) {
                            plan.setLimit(((Number) value).intValue());
                        }
                    } catch (Exception ex) {
                        // 忽略错误
                    }
                }
            }
            if (limit.getOffset() != null) {
                // 类似处理offset
                try {
                    if (limit.getOffset() instanceof net.sf.jsqlparser.expression.LongValue) {
                        net.sf.jsqlparser.expression.LongValue longValue = 
                            (net.sf.jsqlparser.expression.LongValue) limit.getOffset();
                        plan.setOffset((int) longValue.getValue());
                    } else {
                        String offsetStr = limit.getOffset().toString();
                        plan.setOffset(Integer.parseInt(offsetStr));
                    }
                } catch (Exception e) {
                    try {
                        java.lang.reflect.Method getValue = limit.getOffset().getClass().getMethod("getValue");
                        Object value = getValue.invoke(limit.getOffset());
                        if (value instanceof Number) {
                            plan.setOffset(((Number) value).intValue());
                        }
                    } catch (Exception ex) {
                        // 忽略错误
                    }
                }
            }
        }
    }
    
    // ========== 辅助方法 ==========
    
    /**
     * 解析表名
     * 
     * <p>从JSqlParser的Table对象中提取工作簿和工作表名称。
     * JSqlParser解析MySQL语法时，表名可能包含反引号（`），需要移除。</p>
     * 
     * @param table JSqlParser的Table对象
     * @return 数组，[0]=工作簿名，[1]=工作表名
     */
    private String[] parseTableName(Table table) {
        String workbook = table.getSchemaName();
        String sheet = table.getName();
        
        // 移除反引号（MySQL语法中的标识符引号）
        if (workbook != null) {
            workbook = workbook.replace("`", "").trim();
        }
        if (sheet != null) {
            sheet = sheet.replace("`", "").trim();
        }
        
        // 如果工作簿为空，使用默认值"SA"
        if (workbook == null || workbook.isEmpty()) {
            workbook = "SA";
        }
        
        // 如果工作表为空，使用空字符串
        if (sheet == null) {
            sheet = "";
        }
        
        return new String[]{workbook, sheet};
    }
    
    /**
     * 转换JOIN类型
     */
    private JoinType convertJoinType(Join join) {
        if (join.isInner()) {
            return JoinType.INNER;
        } else if (join.isLeft()) {
            return JoinType.LEFT;
        } else if (join.isRight()) {
            return JoinType.RIGHT;
        } else if (join.isFull()) {
            return JoinType.FULL_OUTER;
        } else {
            return JoinType.INNER; // 默认
        }
    }
    
    /**
     * 转换JOIN条件
     */
    private JoinCondition convertJoinCondition(Join join) throws SQLException {
        Expression onExpr = join.getOnExpression();
        if (onExpr == null) {
            throw new SQLException("JOIN must have ON condition");
        }
        
        // 简化处理：假设是等值连接（col1 = col2）
        // 后续可以改进以支持复杂条件
        String exprStr = onExpr.toString();
        String[] parts = exprStr.split("\\s*=\\s*");
        if (parts.length == 2) {
            return new JoinCondition(parts[0].trim(), parts[1].trim(), "=");
        } else {
            throw new SQLException("Unsupported JOIN condition: " + exprStr);
        }
    }
    
    /**
     * 转换表达式为WhereCondition（简化版）
     * 
     * 注意：这是一个临时实现，后续ConditionEvaluator应该直接使用Expression对象
     */
    private WhereCondition convertExpressionToWhereCondition(Expression expr) {
        // 简化处理：将表达式转换为字符串表示
        // 后续ConditionEvaluator需要重构以直接支持Expression对象
        String exprStr = expr.toString();
        
        // 尝试解析简单条件（col = value）
        if (exprStr.contains("=")) {
            String[] parts = exprStr.split("\\s*=\\s*", 2);
            if (parts.length == 2) {
                return new WhereCondition(parts[0].trim(), "=", parts[1].trim());
            }
        }
        
        // 对于复杂表达式，暂时返回一个占位符
        // 后续需要改进ConditionEvaluator以支持Expression
        return new WhereCondition(exprStr, "=", "1"); // 临时方案
    }
    
    /**
     * 判断是否是聚合函数
     */
    private boolean isAggregateFunction(String funcName) {
        return funcName.equals("COUNT") || funcName.equals("SUM") || 
               funcName.equals("AVG") || funcName.equals("MAX") || 
               funcName.equals("MIN");
    }
    
    /**
     * 获取聚合函数类型
     */
    private AggregateType getAggregateType(String funcName) {
        switch (funcName) {
            case "COUNT":
                return AggregateType.COUNT;
            case "SUM":
                return AggregateType.SUM;
            case "AVG":
                return AggregateType.AVG;
            case "MAX":
                return AggregateType.MAX;
            case "MIN":
                return AggregateType.MIN;
            default:
                return AggregateType.COUNT;
        }
    }
    
    /**
     * 从函数中提取列名
     */
    private String extractColumnFromFunction(Function func) {
        if (func.getParameters() == null || func.getParameters().getExpressions().isEmpty()) {
            return "*";
        }
        
        ExpressionList params = func.getParameters();
        if (params.getExpressions().size() == 1) {
            Object param = params.getExpressions().get(0);
            return param.toString();
        }
        
        return "*";
    }
}
