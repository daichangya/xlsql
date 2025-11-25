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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.jsdiff.xlsql.engine.model.AggregateFunction;
import com.jsdiff.xlsql.engine.plan.OrderByItem;
import com.jsdiff.xlsql.engine.plan.QueryPlan;
import com.jsdiff.xlsql.engine.plan.TableInfo;
import com.jsdiff.xlsql.engine.resultset.xlNativeResultSet;

/**
 * ResultSetBuilder - 结果集构建器
 * 
 * <p>根据查询计划构建最终的结果集，包括列选择、排序和LIMIT处理。</p>
 * 
 * @author daichangya
 */
public class ResultSetBuilder {
    
    /**
     * 构建结果集
     * 
     * @param rows 数据行列表
     * @param plan 查询计划
     * @param columnIndexMap 列名到索引的映射
     * @param tables 表信息列表
     * @return ResultSet对象
     * @throws SQLException 如果构建失败则抛出异常
     */
    public ResultSet build(List<String[]> rows, QueryPlan plan,
                          Map<String, Integer> columnIndexMap,
                          List<TableInfo> tables) throws SQLException {
        
        // 应用列选择
        List<String[]> selectedRows = selectColumns(rows, plan, columnIndexMap);
        
        // 应用排序
        if (!plan.getOrderBy().isEmpty()) {
            selectedRows = applyOrderBy(selectedRows, plan, columnIndexMap);
        }
        
        // 应用LIMIT
        if (plan.getLimit() != null) {
            selectedRows = applyLimit(selectedRows, plan.getLimit(), plan.getOffset());
        }
        
        // 构建列名和类型数组
        String[] columnNames = buildColumnNames(plan, tables);
        String[] columnTypes = buildColumnTypes(plan, columnNames.length);
        
        // 转换为列优先的数据矩阵
        String[][] data = convertToColumnMatrix(selectedRows, columnNames.length);
        
        // 创建结果集
        return new xlNativeResultSet(columnNames, columnTypes, data, selectedRows.size());
    }
    
    /**
     * 应用列选择
     * 
     * @param rows 数据行列表
     * @param plan 查询计划
     * @param columnIndexMap 列名到索引的映射
     * @return 选择后的数据行列表
     */
    private List<String[]> selectColumns(List<String[]> rows, QueryPlan plan,
                                         Map<String, Integer> columnIndexMap) {
        if (plan.getSelectColumns().isEmpty() && plan.getAggregateFunctions().isEmpty()) {
            // SELECT *，返回所有列
            return rows;
        }
        
        List<String[]> result = new ArrayList<>();
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        
        for (String[] row : rows) {
            List<String> selectedValues = new ArrayList<>();
            
            // 添加普通列
            for (String col : plan.getSelectColumns()) {
                String columnName = col.toUpperCase();
                // 去掉表别名
                if (columnName.contains(".")) {
                    columnName = columnName.substring(columnName.indexOf(".") + 1);
                }
                
                Integer index = columnIndexMap.get(columnName);
                if (index != null && index >= 0 && index < row.length) {
                    // 找到列，直接使用
                    selectedValues.add(row[index]);
                } else {
                    // 列名查找失败，可能是表达式（如 UPPER(a)）
                    // 尝试解析并评估表达式
                    String value = evaluateExpression(col, row, columnIndexMap, plan);
                    selectedValues.add(value);
                }
            }
            
            // 聚合函数值已经在AggregationExecutor中处理了
            // 这里只需要确保行长度正确
            if (row.length >= selectedValues.size()) {
                // 如果行已经包含了聚合函数值，直接使用
                for (int i = selectedValues.size(); i < row.length; i++) {
                    selectedValues.add(row[i]);
                }
            }
            
            result.add(selectedValues.toArray(new String[0]));
        }
        
        return result;
    }
    
    /**
     * 评估表达式（如 UPPER(a)、LOWER(name) 等）
     * 
     * @param exprStr 表达式字符串
     * @param row 数据行
     * @param columnIndexMap 列名到索引的映射
     * @param plan 查询计划
     * @return 评估结果字符串，如果评估失败返回null
     */
    private String evaluateExpression(String exprStr, String[] row,
                                      Map<String, Integer> columnIndexMap,
                                      QueryPlan plan) {
        try {
            // 尝试解析表达式
            net.sf.jsqlparser.expression.Expression expr = 
                net.sf.jsqlparser.parser.CCJSqlParserUtil.parseExpression(exprStr);
            
            // 使用ExpressionEvaluator评估
            ExpressionEvaluator evaluator = new ExpressionEvaluator();
            Object result = evaluator.evaluate(expr, row, columnIndexMap, 
                convertTablesForEvaluator(plan));
            
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            // 解析或评估失败，返回null
            return null;
        }
    }
    
    /**
     * 将QueryPlan中的表信息转换为ExpressionEvaluator需要的格式
     */
    private List<TableInfo> convertTablesForEvaluator(QueryPlan plan) {
        List<TableInfo> result = new ArrayList<>();
        
        // 添加主表
        if (plan.getMainTable() != null) {
            result.add(plan.getMainTable());
        }
        
        // 添加JOIN表
        for (com.jsdiff.xlsql.engine.plan.JoinInfo join : plan.getJoins()) {
            result.add(join.getTable());
        }
        
        return result;
    }
    
    /**
     * 应用ORDER BY排序
     * 
     * @param rows 数据行列表
     * @param plan 查询计划
     * @param columnIndexMap 列名到索引的映射
     * @return 排序后的数据行列表
     */
    private List<String[]> applyOrderBy(List<String[]> rows, QueryPlan plan,
                                       Map<String, Integer> columnIndexMap) {
        if (rows.isEmpty()) {
            return rows;
        }
        
        List<String[]> sortedRows = new ArrayList<>(rows);
        
        Collections.sort(sortedRows, new Comparator<String[]>() {
            @Override
            public int compare(String[] row1, String[] row2) {
                for (OrderByItem item : plan.getOrderBy()) {
                    String column = item.getColumn().toUpperCase();
                    // 去掉表别名
                    if (column.contains(".")) {
                        column = column.substring(column.indexOf(".") + 1);
                    }
                    
                    Integer index = columnIndexMap.get(column);
                    if (index == null || index < 0) {
                        // 可能是聚合函数别名，尝试在行中查找
                        // 简化处理：假设聚合函数值在行的末尾
                        continue;
                    }
                    
                    if (index >= row1.length || index >= row2.length) {
                        continue;
                    }
                    
                    String val1 = row1[index];
                    String val2 = row2[index];
                    
                    int cmp = compareValues(val1, val2);
                    if (cmp != 0) {
                        return item.isDescending() ? -cmp : cmp;
                    }
                }
                return 0;
            }
        });
        
        return sortedRows;
    }
    
    /**
     * 比较两个值
     * 
     * @param val1 值1
     * @param val2 值2
     * @return 比较结果（-1, 0, 1）
     */
    private int compareValues(String val1, String val2) {
        if (val1 == null && val2 == null) {
            return 0;
        }
        if (val1 == null) {
            return -1;
        }
        if (val2 == null) {
            return 1;
        }
        
        // 尝试数值比较
        try {
            java.math.BigDecimal num1 = new java.math.BigDecimal(val1);
            java.math.BigDecimal num2 = new java.math.BigDecimal(val2);
            return num1.compareTo(num2);
        } catch (NumberFormatException e) {
            // 不是数字，进行字符串比较
            return val1.compareTo(val2);
        }
    }
    
    /**
     * 应用LIMIT限制
     * 
     * @param rows 数据行列表
     * @param limit LIMIT值
     * @param offset OFFSET值
     * @return 限制后的数据行列表
     */
    private List<String[]> applyLimit(List<String[]> rows, Integer limit, Integer offset) {
        int start = (offset != null && offset > 0) ? offset : 0;
        int end = (limit != null && limit > 0) ? start + limit : rows.size();
        
        if (start >= rows.size()) {
            return new ArrayList<>();
        }
        
        if (end > rows.size()) {
            end = rows.size();
        }
        
        return rows.subList(start, end);
    }
    
    /**
     * 构建列名数组
     * 
     * @param plan 查询计划
     * @param tables 表信息列表
     * @return 列名数组
     */
    private String[] buildColumnNames(QueryPlan plan, List<TableInfo> tables) {
        List<String> columnNames = new ArrayList<>();
        
        // 添加普通列
        if (plan.getSelectColumns().isEmpty() && plan.getAggregateFunctions().isEmpty()) {
            // SELECT *，包含所有表的列
            for (TableInfo table : tables) {
                String[] cols = table.getColumnNames();
                if (cols != null) {
                    for (String col : cols) {
                        columnNames.add(col);
                    }
                }
            }
        } else {
            // 添加选择的列
            columnNames.addAll(plan.getSelectColumns());
        }
        
        // 添加聚合函数列
        for (AggregateFunction func : plan.getAggregateFunctions()) {
            String name = func.getAlias() != null ? func.getAlias() : func.getDisplayName();
            columnNames.add(name);
        }
        
        return columnNames.toArray(new String[0]);
    }
    
    /**
     * 构建列类型数组
     * 
     * @param plan 查询计划
     * @param columnCount 列数
     * @return 列类型数组
     */
    private String[] buildColumnTypes(QueryPlan plan, int columnCount) {
        String[] types = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            types[i] = "VARCHAR"; // 默认类型
        }
        return types;
    }
    
    /**
     * 将行优先的数据转换为列优先的矩阵
     * 
     * @param rows 数据行列表
     * @param columnCount 列数
     * @return 列优先的数据矩阵
     */
    private String[][] convertToColumnMatrix(List<String[]> rows, int columnCount) {
        if (rows.isEmpty()) {
            return new String[columnCount][0];
        }
        
        String[][] matrix = new String[columnCount][rows.size()];
        
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            for (int j = 0; j < columnCount && j < row.length; j++) {
                matrix[j][i] = row[j];
            }
        }
        
        return matrix;
    }
}

