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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jsdiff.xlsql.engine.model.AggregateFunction;
import com.jsdiff.xlsql.engine.model.AggregateType;
import com.jsdiff.xlsql.engine.plan.QueryPlan;
import com.jsdiff.xlsql.engine.plan.TableInfo;
import com.jsdiff.xlsql.engine.executor.ConditionEvaluator;

/**
 * AggregationExecutor - 聚合执行器
 * 
 * <p>执行GROUP BY分组和聚合函数计算。
 * 支持COUNT、SUM、AVG、MAX、MIN等聚合函数。</p>
 * 
 * @author daichangya
 */
public class AggregationExecutor {
    
    /**
     * 执行聚合操作
     * 
     * @param rows 数据行列表
     * @param plan 查询计划
     * @param columnIndexMap 列名到索引的映射
     * @return 聚合后的数据行列表
     * @throws SQLException 如果执行失败则抛出异常
     */
    public List<String[]> execute(List<String[]> rows, QueryPlan plan,
                                  Map<String, Integer> columnIndexMap) throws SQLException {
        
        if (!plan.hasGroupBy() && !plan.hasAggregation()) {
            // 没有GROUP BY也没有聚合函数，直接返回
            return rows;
        }
        
        if (plan.hasGroupBy()) {
            // 有GROUP BY，执行分组聚合
            return executeGroupByAggregation(rows, plan, columnIndexMap);
        } else {
            // 没有GROUP BY但有聚合函数，执行全局聚合（返回一行）
            return executeGlobalAggregation(rows, plan, columnIndexMap);
        }
    }
    
    /**
     * 执行GROUP BY分组聚合
     * 
     * @param rows 数据行列表
     * @param plan 查询计划
     * @param columnIndexMap 列名到索引的映射
     * @return 聚合后的数据行列表
     * @throws SQLException 如果执行失败则抛出异常
     */
    private List<String[]> executeGroupByAggregation(List<String[]> rows, QueryPlan plan,
                                                      Map<String, Integer> columnIndexMap) 
            throws SQLException {
        
        // 执行分组
        Map<String, List<String[]>> groups = groupBy(rows, plan.getGroupByColumns(), columnIndexMap);
        
        // 对每个分组计算聚合函数
        List<String[]> result = new ArrayList<>();
        ConditionEvaluator evaluator = new ConditionEvaluator();
        
        for (Map.Entry<String, List<String[]>> entry : groups.entrySet()) {
            List<String[]> group = entry.getValue();
            
            // 计算聚合函数值
            Map<String, Object> aggregateValues = new HashMap<>();
            for (AggregateFunction func : plan.getAggregateFunctions()) {
                Object value = calculateAggregate(group, func, columnIndexMap);
                String key = func.getAlias() != null ? func.getAlias() : func.getDisplayName();
                aggregateValues.put(key, value);
                aggregateValues.put(func.getDisplayName(), value); // 也使用显示名称作为键
            }
            
            // 应用HAVING过滤
            if (plan.getHavingClause() != null) {
                if (!evaluator.evaluateHaving(plan.getHavingClause(), aggregateValues)) {
                    continue; // 不满足HAVING条件，跳过
                }
            }
            
            // 构建结果行
            String[] resultRow = buildResultRow(group, plan, aggregateValues, columnIndexMap);
            result.add(resultRow);
        }
        
        return result;
    }
    
    /**
     * 执行全局聚合（无GROUP BY）
     * 
     * @param rows 数据行列表
     * @param plan 查询计划
     * @param columnIndexMap 列名到索引的映射
     * @return 聚合后的数据行列表（只有一行）
     * @throws SQLException 如果执行失败则抛出异常
     */
    private List<String[]> executeGlobalAggregation(List<String[]> rows, QueryPlan plan,
                                                     Map<String, Integer> columnIndexMap) 
            throws SQLException {
        
        // 计算聚合函数值
        Map<String, Object> aggregateValues = new HashMap<>();
        for (AggregateFunction func : plan.getAggregateFunctions()) {
            Object value = calculateAggregate(rows, func, columnIndexMap);
            String key = func.getAlias() != null ? func.getAlias() : func.getDisplayName();
            aggregateValues.put(key, value);
            aggregateValues.put(func.getDisplayName(), value);
        }
        
        // 构建结果行
        String[] resultRow = buildResultRow(rows, plan, aggregateValues, columnIndexMap);
        
        List<String[]> result = new ArrayList<>();
        result.add(resultRow);
        return result;
    }
    
    /**
     * 执行分组操作
     * 
     * @param rows 数据行列表
     * @param groupByColumns 分组列列表
     * @param columnIndexMap 列名到索引的映射
     * @return 分组映射（分组键 -> 行列表）
     */
    private Map<String, List<String[]>> groupBy(List<String[]> rows, List<String> groupByColumns,
                                                Map<String, Integer> columnIndexMap) {
        Map<String, List<String[]>> groups = new HashMap<>();
        
        for (String[] row : rows) {
            // 生成分组键
            String groupKey = generateGroupKey(row, groupByColumns, columnIndexMap);
            groups.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(row);
        }
        
        return groups;
    }
    
    /**
     * 生成分组键
     * 
     * @param row 数据行
     * @param groupByColumns 分组列列表
     * @param columnIndexMap 列名到索引的映射
     * @return 分组键字符串
     */
    private String generateGroupKey(String[] row, List<String> groupByColumns,
                                   Map<String, Integer> columnIndexMap) {
        StringBuilder key = new StringBuilder();
        
        for (int i = 0; i < groupByColumns.size(); i++) {
            if (i > 0) {
                key.append("|"); // 使用|分隔多列
            }
            
            String columnName = groupByColumns.get(i).toUpperCase();
            // 去掉表别名
            if (columnName.contains(".")) {
                columnName = columnName.substring(columnName.indexOf(".") + 1);
            }
            
            Integer index = columnIndexMap.get(columnName);
            if (index != null && index >= 0 && index < row.length) {
                String value = row[index];
                key.append(value != null ? value : "NULL");
            } else {
                key.append("NULL");
            }
        }
        
        return key.toString();
    }
    
    /**
     * 计算聚合函数值
     * 
     * @param rows 数据行列表
     * @param func 聚合函数
     * @param columnIndexMap 列名到索引的映射
     * @return 聚合值
     * @throws SQLException 如果计算失败则抛出异常
     */
    private Object calculateAggregate(List<String[]> rows, AggregateFunction func,
                                     Map<String, Integer> columnIndexMap) throws SQLException {
        
        switch (func.getType()) {
            case COUNT:
                return calculateCount(rows, func, columnIndexMap);
            case SUM:
                return calculateSum(rows, func, columnIndexMap);
            case AVG:
                return calculateAvg(rows, func, columnIndexMap);
            case MAX:
                return calculateMaxMin(rows, func, columnIndexMap, AggregateType.MAX);
            case MIN:
                return calculateMaxMin(rows, func, columnIndexMap, AggregateType.MIN);
            default:
                throw new SQLException("Unsupported aggregate function: " + func.getType());
        }
    }
    
    /**
     * 计算COUNT聚合函数
     * 
     * @param rows 数据行列表
     * @param func 聚合函数
     * @param columnIndexMap 列名到索引的映射
     * @return COUNT值
     */
    private Long calculateCount(List<String[]> rows, AggregateFunction func,
                                Map<String, Integer> columnIndexMap) {
        String column = func.getColumn();
        
        if (column == null || "*".equals(column)) {
            // COUNT(*) - 统计所有行（包括NULL）
            return (long) rows.size();
        }
        
        // COUNT(column) - 统计非NULL值
        Integer index = columnIndexMap.get(column.toUpperCase());
        if (index == null) {
            return 0L;
        }
        
        if (func.isDistinct()) {
            // COUNT(DISTINCT column)
            Set<String> distinctValues = new HashSet<>();
            for (String[] row : rows) {
                if (row != null && index < row.length && row[index] != null) {
                    distinctValues.add(row[index]);
                }
            }
            return (long) distinctValues.size();
        } else {
            // COUNT(column) - 统计非NULL值
            long count = 0;
            for (String[] row : rows) {
                if (row != null && index < row.length && row[index] != null) {
                    count++;
                }
            }
            return count;
        }
    }
    
    /**
     * 计算SUM聚合函数
     * 
     * @param rows 数据行列表
     * @param func 聚合函数
     * @param columnIndexMap 列名到索引的映射
     * @return SUM值
     * @throws SQLException 如果计算失败则抛出异常
     */
    private BigDecimal calculateSum(List<String[]> rows, AggregateFunction func,
                                    Map<String, Integer> columnIndexMap) throws SQLException {
        String column = func.getColumn();
        if (column == null || "*".equals(column)) {
            throw new SQLException("SUM(*) is not allowed");
        }
        
        Integer index = columnIndexMap.get(column.toUpperCase());
        if (index == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sum = BigDecimal.ZERO;
        Set<String> distinctValues = null;
        
        if (func.isDistinct()) {
            distinctValues = new HashSet<>();
        }
        
        for (String[] row : rows) {
            if (row == null || index >= row.length) {
                continue;
            }
            
            String value = row[index];
            if (value == null) {
                continue; // 忽略NULL值
            }
            
            if (func.isDistinct()) {
                if (distinctValues.contains(value)) {
                    continue; // 已处理过，跳过
                }
                distinctValues.add(value);
            }
            
            try {
                BigDecimal num = new BigDecimal(value);
                sum = sum.add(num);
            } catch (NumberFormatException e) {
                // 不是数字，忽略
            }
        }
        
        return sum;
    }
    
    /**
     * 计算AVG聚合函数
     * 
     * @param rows 数据行列表
     * @param func 聚合函数
     * @param columnIndexMap 列名到索引的映射
     * @return AVG值
     * @throws SQLException 如果计算失败则抛出异常
     */
    private BigDecimal calculateAvg(List<String[]> rows, AggregateFunction func,
                                   Map<String, Integer> columnIndexMap) throws SQLException {
        String column = func.getColumn();
        if (column == null || "*".equals(column)) {
            throw new SQLException("AVG(*) is not allowed");
        }
        
        BigDecimal sum = calculateSum(rows, func, columnIndexMap);
        long count = calculateCount(rows, new AggregateFunction(AggregateType.COUNT, column, false, null), 
                                   columnIndexMap);
        
        if (count == 0) {
            return null; // 没有非NULL值，返回NULL
        }
        
        return sum.divide(new BigDecimal(count), 10, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 计算MAX/MIN聚合函数
     * 
     * @param rows 数据行列表
     * @param func 聚合函数
     * @param columnIndexMap 列名到索引的映射
     * @param type MAX或MIN
     * @return MAX/MIN值
     * @throws SQLException 如果计算失败则抛出异常
     */
    private String calculateMaxMin(List<String[]> rows, AggregateFunction func,
                                   Map<String, Integer> columnIndexMap, AggregateType type) 
            throws SQLException {
        String column = func.getColumn();
        if (column == null || "*".equals(column)) {
            throw new SQLException(type + "(*) is not allowed");
        }
        
        Integer index = columnIndexMap.get(column.toUpperCase());
        if (index == null) {
            return null;
        }
        
        String result = null;
        Set<String> distinctValues = null;
        
        if (func.isDistinct()) {
            distinctValues = new HashSet<>();
        }
        
        for (String[] row : rows) {
            if (row == null || index >= row.length) {
                continue;
            }
            
            String value = row[index];
            if (value == null) {
                continue; // 忽略NULL值
            }
            
            if (func.isDistinct()) {
                if (distinctValues.contains(value)) {
                    continue;
                }
                distinctValues.add(value);
            }
            
            if (result == null) {
                result = value;
            } else {
                // 尝试数值比较
                try {
                    BigDecimal num1 = new BigDecimal(result);
                    BigDecimal num2 = new BigDecimal(value);
                    int cmp = num2.compareTo(num1);
                    if ((type == AggregateType.MAX && cmp > 0) || 
                        (type == AggregateType.MIN && cmp < 0)) {
                        result = value;
                    }
                } catch (NumberFormatException e) {
                    // 不是数字，进行字符串比较
                    int cmp = value.compareTo(result);
                    if ((type == AggregateType.MAX && cmp > 0) || 
                        (type == AggregateType.MIN && cmp < 0)) {
                        result = value;
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * 构建结果行
     * 
     * @param group 分组数据（用于提取GROUP BY列值）
     * @param plan 查询计划
     * @param aggregateValues 聚合函数值映射
     * @param columnIndexMap 列名到索引的映射
     * @return 结果行
     */
    private String[] buildResultRow(List<String[]> group, QueryPlan plan,
                                    Map<String, Object> aggregateValues,
                                    Map<String, Integer> columnIndexMap) {
        List<String> resultValues = new ArrayList<>();
        
        // 添加普通列（如果有）
        if (!group.isEmpty()) {
            String[] firstRow = group.get(0);
            for (String col : plan.getSelectColumns()) {
                String columnName = col.toUpperCase();
                // 去掉表别名
                if (columnName.contains(".")) {
                    columnName = columnName.substring(columnName.indexOf(".") + 1);
                }
                
                Integer index = columnIndexMap.get(columnName);
                if (index != null && index >= 0 && index < firstRow.length) {
                    resultValues.add(firstRow[index]);
                } else {
                    resultValues.add(null);
                }
            }
        }
        
        // 添加聚合函数值
        for (AggregateFunction func : plan.getAggregateFunctions()) {
            String key = func.getAlias() != null ? func.getAlias() : func.getDisplayName();
            Object value = aggregateValues.get(key);
            if (value == null) {
                value = aggregateValues.get(func.getDisplayName());
            }
            resultValues.add(value != null ? value.toString() : null);
        }
        
        return resultValues.toArray(new String[0]);
    }
}

