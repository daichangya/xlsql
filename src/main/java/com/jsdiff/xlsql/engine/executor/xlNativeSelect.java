/*
 * xlNativeSelect.java
 *
 * Created on 2025-01-XX
 *
 * 自研SQL引擎的查询实现，直接查询Excel数据，不依赖外部数据库
 */

package com.jsdiff.xlsql.engine.executor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.jsdiff.xlsql.database.ADatabase;
import com.jsdiff.xlsql.database.xlDatabaseException;
import com.jsdiff.xlsql.engine.parser.NativeSqlParser;
import com.jsdiff.xlsql.engine.plan.QueryPlan;
import com.jsdiff.xlsql.engine.plan.TableInfo;
import com.jsdiff.xlsql.engine.plan.JoinInfo;
import com.jsdiff.xlsql.engine.executor.JoinExecutor;
import com.jsdiff.xlsql.engine.executor.AggregationExecutor;
import com.jsdiff.xlsql.engine.executor.ConditionEvaluator;
import com.jsdiff.xlsql.engine.executor.ResultSetBuilder;
import com.jsdiff.xlsql.engine.executor.PlainSelectAdapter;
import com.jsdiff.xlsql.engine.resultset.xlNativeResultSet;

import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * xlNativeSelect - 自研SQL引擎的查询实现
 * 
 * <p>该类直接基于Excel数据执行查询，不依赖外部数据库引擎。
 * 第一阶段实现：支持简单的SELECT * FROM "workbook.sheet"查询。</p>
 * 
 * <p>后续扩展方向：</p>
 * <ul>
 *   <li>实现完整的SQL解析（WHERE、ORDER BY、LIMIT等）</li>
 *   <li>支持列选择（SELECT col1, col2 FROM ...）</li>
 *   <li>支持JOIN查询</li>
 *   <li>支持聚合函数</li>
 * </ul>
 * 
 * @author  daichangya
 */
public class xlNativeSelect {
    
    /** 日志记录器 */
    private static final Logger logger = Logger.getLogger(xlNativeSelect.class.getName());
    
    /** Excel数据存储对象 */
    private final ADatabase datastore;
    
    /**
     * 创建xlNativeSelect实例
     * 
     * @param datastore Excel数据存储对象
     */
    public xlNativeSelect(ADatabase datastore) {
        if (datastore == null) {
            throw new NullPointerException("Datastore cannot be null");
        }
        this.datastore = datastore;
    }
    
    /**
     * 执行SQL查询
     * 
     * <p>完整的SQL查询实现，支持：
     * <ul>
     *   <li>SELECT子句（列选择、聚合函数）</li>
     *   <li>FROM子句（单表或多表）</li>
     *   <li>JOIN子句（INNER、LEFT、RIGHT、FULL OUTER）</li>
     *   <li>WHERE子句（条件过滤）</li>
     *   <li>GROUP BY子句（分组）</li>
     *   <li>HAVING子句（分组过滤）</li>
     *   <li>ORDER BY子句（排序）</li>
     *   <li>LIMIT子句（限制结果数）</li>
     * </ul>
     * </p>
     * 
     * @param sql SQL查询语句
     * @return 查询结果集
     * @throws SQLException 如果查询失败则抛出异常
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        if (sql == null || sql.trim().isEmpty()) {
            throw new SQLException("SQL statement cannot be null or empty");
        }
        
        logger.info("Executing native query: " + sql);
        
        // 1. 解析SQL（使用JSqlParser）
        NativeSqlParser parser = new NativeSqlParser();
        PlainSelect plainSelect = parser.parse(sql);
        
        // 2. 转换为QueryPlan（适配器模式，后续可以逐步移除）
        PlainSelectAdapter adapter = new PlainSelectAdapter();
        QueryPlan plan = adapter.toQueryPlan(plainSelect);
        
        // 2. 加载表数据
        List<TableInfo> tables = loadTables(plan);
        
        // 3. 执行FROM和JOIN
        List<String[]> rows = executeFromAndJoins(plan, tables);
        
        // 4. 应用WHERE条件
        if (plan.getWhereClause() != null) {
            rows = applyWhereCondition(rows, plan, tables);
        }
        
        // 5. 执行聚合和分组
        if (plan.hasAggregation() || plan.hasGroupBy()) {
            Map<String, Integer> columnIndexMap = buildColumnIndexMap(tables);
            AggregationExecutor aggExecutor = new AggregationExecutor();
            rows = aggExecutor.execute(rows, plan, columnIndexMap);
        }
        
        // 6. 构建结果集（包括列选择、排序、LIMIT）
        Map<String, Integer> columnIndexMap = buildColumnIndexMap(tables);
        ResultSetBuilder builder = new ResultSetBuilder();
        return builder.build(rows, plan, columnIndexMap, tables);
    }
    
    /**
     * 加载表数据
     * 
     * @param plan 查询计划
     * @return 表信息列表
     * @throws SQLException 如果加载失败则抛出异常
     */
    private List<TableInfo> loadTables(QueryPlan plan) throws SQLException {
        List<TableInfo> tables = new ArrayList<>();
        
        // 加载主表
        TableInfo mainTable = plan.getMainTable();
        loadTableData(mainTable);
        tables.add(mainTable);
        
        // 加载JOIN表
        for (JoinInfo join : plan.getJoins()) {
            TableInfo joinTable = join.getTable();
            loadTableData(joinTable);
            tables.add(joinTable);
        }
        
        return tables;
    }
    
    /**
     * 加载表数据
     * 
     * @param table 表信息
     * @throws SQLException 如果加载失败则抛出异常
     */
    private void loadTableData(TableInfo table) throws SQLException {
        try {
            String[] columnNames = datastore.getColumnNames(table.getWorkbook(), table.getSheet());
            String[] columnTypes = datastore.getColumnTypes(table.getWorkbook(), table.getSheet());
            String[][] values = datastore.getValues(table.getWorkbook(), table.getSheet());
            int rowCount = datastore.getRows(table.getWorkbook(), table.getSheet());
            
            table.loadData(columnNames, columnTypes, values, rowCount);
        } catch (xlDatabaseException e) {
            throw new SQLException("Failed to load table data: " + table.getFullName() + 
                                 " - " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Table not found: " + table.getFullName(), e);
        }
    }
    
    /**
     * 执行FROM和JOIN操作
     * 
     * @param plan 查询计划
     * @param tables 表信息列表
     * @return JOIN后的数据行列表
     * @throws SQLException 如果执行失败则抛出异常
     */
    private List<String[]> executeFromAndJoins(QueryPlan plan, List<TableInfo> tables) 
            throws SQLException {
        TableInfo mainTable = plan.getMainTable();
        
        // 将主表数据转换为行列表
        List<String[]> rows = convertTableToRows(mainTable);
        Map<String, Integer> columnIndexMap = buildColumnIndexMapForTable(mainTable, 0);
        
        // 执行JOIN
        JoinExecutor joinExecutor = new JoinExecutor();
        int leftColumnCount = mainTable.getColumnNames().length;
        
        for (JoinInfo join : plan.getJoins()) {
            // 更新列索引映射（包含之前所有表的列）
            Map<String, Integer> updatedColumnIndexMap = new HashMap<>(columnIndexMap);
            TableInfo rightTable = join.getTable();
            Map<String, Integer> rightColumnIndexMap = buildColumnIndexMapForTable(rightTable, leftColumnCount);
            updatedColumnIndexMap.putAll(rightColumnIndexMap);
            
            // 执行JOIN
            rows = joinExecutor.execute(rows, rightTable, join, columnIndexMap);
            
            // 更新列索引映射和列数
            columnIndexMap = updatedColumnIndexMap;
            leftColumnCount += rightTable.getColumnNames().length;
        }
        
        return rows;
    }
    
    /**
     * 将表数据转换为行列表
     * 
     * @param table 表信息
     * @return 数据行列表
     */
    private List<String[]> convertTableToRows(TableInfo table) {
        List<String[]> rows = new ArrayList<>();
        
        for (int i = 0; i < table.getRowCount(); i++) {
            String[] row = table.getRow(i);
            if (row != null) {
                rows.add(row);
            }
        }
        
        return rows;
    }
    
    /**
     * 为表构建列索引映射
     * 
     * @param table 表信息
     * @param columnOffset 列偏移量（用于多表JOIN）
     * @return 列名到索引的映射
     */
    private Map<String, Integer> buildColumnIndexMapForTable(TableInfo table, int columnOffset) {
        Map<String, Integer> map = new HashMap<>();
        String[] columnNames = table.getColumnNames();
        
        if (columnNames != null) {
            for (int i = 0; i < columnNames.length; i++) {
                String colName = columnNames[i].toUpperCase();
                map.put(colName, i + columnOffset);
                
                // 如果有表别名，也添加别名.列名
                if (table.getAlias() != null && !table.getAlias().isEmpty()) {
                    map.put(table.getAlias().toUpperCase() + "." + colName, i + columnOffset);
                }
            }
        }
        
        return map;
    }
    
    /**
     * 构建所有表的列索引映射
     * 
     * @param tables 表信息列表
     * @return 列名到索引的映射
     */
    private Map<String, Integer> buildColumnIndexMap(List<TableInfo> tables) {
        Map<String, Integer> map = new HashMap<>();
        int columnOffset = 0;
        
        for (TableInfo table : tables) {
            Map<String, Integer> tableMap = buildColumnIndexMapForTable(table, columnOffset);
            map.putAll(tableMap);
            columnOffset += table.getColumnNames().length;
        }
        
        return map;
    }
    
    /**
     * 应用WHERE条件过滤
     * 
     * @param rows 数据行列表
     * @param plan 查询计划
     * @param tables 表信息列表
     * @return 过滤后的数据行列表
     * @throws SQLException 如果过滤失败则抛出异常
     */
    private List<String[]> applyWhereCondition(List<String[]> rows, QueryPlan plan,
                                               List<TableInfo> tables) throws SQLException {
        ConditionEvaluator evaluator = new ConditionEvaluator();
        Map<String, Integer> columnIndexMap = buildColumnIndexMap(tables);
        
        List<String[]> filteredRows = new ArrayList<>();
        
        for (String[] row : rows) {
            if (evaluator.evaluate(plan.getWhereClause(), row, columnIndexMap, tables)) {
                filteredRows.add(row);
            }
        }
        
        return filteredRows;
    }
    
    /**
     * 查询数据（兼容ASqlSelect接口）
     * 
     * <p>为了兼容现有的ASqlSelect接口，提供此方法。
     * 但自研引擎不需要JDBC连接，因此此方法不使用Connection参数。</p>
     * 
     * @param workbook 工作簿名称
     * @param sheet 工作表名称
     * @return 查询结果集
     * @throws SQLException 如果查询失败则抛出异常
     */
    public ResultSet QueryData(String workbook, String sheet) throws SQLException {
        try {
            String[] columnNames = datastore.getColumnNames(workbook, sheet);
            String[] columnTypes = datastore.getColumnTypes(workbook, sheet);
            String[][] values = datastore.getValues(workbook, sheet);
            int rowCount = datastore.getRows(workbook, sheet);
            
            return new xlNativeResultSet(columnNames, columnTypes, values, rowCount);
            
        } catch (xlDatabaseException e) {
            throw new SQLException("Failed to query Excel data: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Table not found: " + workbook + "." + sheet, e);
        }
    }
}

