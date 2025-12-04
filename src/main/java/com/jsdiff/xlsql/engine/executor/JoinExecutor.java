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
package com.jsdiff.xlsql.engine.executor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsdiff.xlsql.engine.model.JoinType;
import com.jsdiff.xlsql.engine.plan.JoinCondition;
import com.jsdiff.xlsql.engine.plan.JoinInfo;
import com.jsdiff.xlsql.engine.plan.TableInfo;

/**
 * JoinExecutor - JOIN执行器
 * 
 * <p>执行SQL JOIN操作，支持INNER、LEFT、RIGHT和FULL OUTER JOIN。
 * 使用嵌套循环连接算法（适合小数据集）和哈希连接算法（适合大数据集）。</p>
 * 
 * @author daichangya
 */
public class JoinExecutor {
    
    /**
     * 执行JOIN操作
     * 
     * @param leftRows 左表数据行列表
     * @param rightTable 右表信息
     * @param joinInfo JOIN信息
     * @param leftColumnIndexMap 左表列名到索引的映射
     * @return JOIN后的数据行列表
     * @throws SQLException 如果执行失败则抛出异常
     */
    public List<String[]> execute(List<String[]> leftRows, TableInfo rightTable,
                                 JoinInfo joinInfo, Map<String, Integer> leftColumnIndexMap) 
            throws SQLException {
        
        JoinType joinType = joinInfo.getType();
        JoinCondition condition = joinInfo.getCondition();
        
        // 解析连接条件
        String[] leftColParts = JoinCondition.parseColumnRef(condition.getLeftColumn());
        String[] rightColParts = JoinCondition.parseColumnRef(condition.getRightColumn());
        
        String leftColumnName = leftColParts[1].toUpperCase();
        String rightColumnName = rightColParts[1].toUpperCase();
        
        // 获取右表列索引
        int rightColumnIndex = rightTable.getColumnIndex(rightColumnName);
        if (rightColumnIndex == -1) {
            throw new SQLException("Column not found in right table: " + rightColumnName);
        }
        
        // 获取左表列索引
        Integer leftColumnIndexObj = leftColumnIndexMap.get(leftColumnName);
        if (leftColumnIndexObj == null) {
            throw new SQLException("Column not found in left table: " + leftColumnName);
        }
        int leftColumnIndex = leftColumnIndexObj;
        
        // 根据JOIN类型执行
        switch (joinType) {
            case INNER:
                return executeInnerJoin(leftRows, rightTable, leftColumnIndex, rightColumnIndex);
            case LEFT:
                return executeLeftJoin(leftRows, rightTable, leftColumnIndex, rightColumnIndex);
            case RIGHT:
                return executeRightJoin(leftRows, rightTable, leftColumnIndex, rightColumnIndex);
            case FULL_OUTER:
                return executeFullOuterJoin(leftRows, rightTable, leftColumnIndex, rightColumnIndex);
            default:
                throw new SQLException("Unsupported JOIN type: " + joinType);
        }
    }
    
    /**
     * 执行INNER JOIN（嵌套循环连接）
     * 
     * @param leftRows 左表数据行
     * @param rightTable 右表信息
     * @param leftColumnIndex 左表连接列索引
     * @param rightColumnIndex 右表连接列索引
     * @return JOIN后的数据行列表
     */
    private List<String[]> executeInnerJoin(List<String[]> leftRows, TableInfo rightTable,
                                           int leftColumnIndex, int rightColumnIndex) {
        List<String[]> result = new ArrayList<>();
        
        // 嵌套循环连接
        for (String[] leftRow : leftRows) {
            if (leftRow == null || leftColumnIndex >= leftRow.length) {
                continue;
            }
            
            String leftValue = leftRow[leftColumnIndex];
            if (leftValue == null) {
                continue; // NULL值不匹配
            }
            
            // 在右表中查找匹配的行
            for (int i = 0; i < rightTable.getRowCount(); i++) {
                String[] rightRow = rightTable.getRow(i);
                if (rightRow == null || rightColumnIndex >= rightRow.length) {
                    continue;
                }
                
                String rightValue = rightRow[rightColumnIndex];
                if (leftValue.equals(rightValue)) {
                    // 匹配，合并行
                    String[] joinedRow = combineRows(leftRow, rightRow);
                    result.add(joinedRow);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 执行LEFT JOIN
     * 
     * @param leftRows 左表数据行
     * @param rightTable 右表信息
     * @param leftColumnIndex 左表连接列索引
     * @param rightColumnIndex 右表连接列索引
     * @return JOIN后的数据行列表
     */
    private List<String[]> executeLeftJoin(List<String[]> leftRows, TableInfo rightTable,
                                           int leftColumnIndex, int rightColumnIndex) {
        List<String[]> result = new ArrayList<>();
        
        // 为右表构建哈希索引（优化查找）
        Map<String, List<String[]>> rightIndex = buildHashIndex(rightTable, rightColumnIndex);
        
        for (String[] leftRow : leftRows) {
            if (leftRow == null || leftColumnIndex >= leftRow.length) {
                continue;
            }
            
            String leftValue = leftRow[leftColumnIndex];
            boolean matched = false;
            
            if (leftValue != null) {
                List<String[]> matchingRightRows = rightIndex.get(leftValue);
                if (matchingRightRows != null && !matchingRightRows.isEmpty()) {
                    // 有匹配
                    for (String[] rightRow : matchingRightRows) {
                        String[] joinedRow = combineRows(leftRow, rightRow);
                        result.add(joinedRow);
                    }
                    matched = true;
                }
            }
            
            if (!matched) {
                // 没有匹配，使用NULL填充右表列
                String[] rightNullRow = createNullRow(rightTable.getColumnNames().length);
                String[] joinedRow = combineRows(leftRow, rightNullRow);
                result.add(joinedRow);
            }
        }
        
        return result;
    }
    
    /**
     * 执行RIGHT JOIN
     * 
     * @param leftRows 左表数据行
     * @param rightTable 右表信息
     * @param leftColumnIndex 左表连接列索引
     * @param rightColumnIndex 右表连接列索引
     * @return JOIN后的数据行列表
     */
    private List<String[]> executeRightJoin(List<String[]> leftRows, TableInfo rightTable,
                                           int leftColumnIndex, int rightColumnIndex) {
        List<String[]> result = new ArrayList<>();
        
        // 为左表构建哈希索引
        Map<String, List<String[]>> leftIndex = buildHashIndex(leftRows, leftColumnIndex);
        
        // 遍历右表
        for (int i = 0; i < rightTable.getRowCount(); i++) {
            String[] rightRow = rightTable.getRow(i);
            if (rightRow == null || rightColumnIndex >= rightRow.length) {
                continue;
            }
            
            String rightValue = rightRow[rightColumnIndex];
            boolean matched = false;
            
            if (rightValue != null) {
                List<String[]> matchingLeftRows = leftIndex.get(rightValue);
                if (matchingLeftRows != null && !matchingLeftRows.isEmpty()) {
                    // 有匹配
                    for (String[] leftRow : matchingLeftRows) {
                        String[] joinedRow = combineRows(leftRow, rightRow);
                        result.add(joinedRow);
                    }
                    matched = true;
                }
            }
            
            if (!matched) {
                // 没有匹配，使用NULL填充左表列
                String[] leftNullRow = createNullRow(leftRows.isEmpty() ? 0 : leftRows.get(0).length);
                String[] joinedRow = combineRows(leftNullRow, rightRow);
                result.add(joinedRow);
            }
        }
        
        return result;
    }
    
    /**
     * 执行FULL OUTER JOIN
     * 
     * @param leftRows 左表数据行
     * @param rightTable 右表信息
     * @param leftColumnIndex 左表连接列索引
     * @param rightColumnIndex 右表连接列索引
     * @return JOIN后的数据行列表
     */
    private List<String[]> executeFullOuterJoin(List<String[]> leftRows, TableInfo rightTable,
                                                int leftColumnIndex, int rightColumnIndex) {
        List<String[]> result = new ArrayList<>();
        
        // 先执行LEFT JOIN
        List<String[]> leftJoinResult = executeLeftJoin(leftRows, rightTable, 
                                                        leftColumnIndex, rightColumnIndex);
        result.addAll(leftJoinResult);
        
        // 为左表构建哈希索引（用于查找哪些右表行已经匹配）
        Map<String, List<String[]>> leftIndex = buildHashIndex(leftRows, leftColumnIndex);
        
        // 查找右表中未匹配的行
        for (int i = 0; i < rightTable.getRowCount(); i++) {
            String[] rightRow = rightTable.getRow(i);
            if (rightRow == null || rightColumnIndex >= rightRow.length) {
                continue;
            }
            
            String rightValue = rightRow[rightColumnIndex];
            boolean matched = false;
            
            if (rightValue != null) {
                List<String[]> matchingLeftRows = leftIndex.get(rightValue);
                if (matchingLeftRows != null && !matchingLeftRows.isEmpty()) {
                    matched = true; // 已经在LEFT JOIN中处理了
                }
            }
            
            if (!matched) {
                // 右表独有的行，使用NULL填充左表列
                String[] leftNullRow = createNullRow(leftRows.isEmpty() ? 0 : leftRows.get(0).length);
                String[] joinedRow = combineRows(leftNullRow, rightRow);
                result.add(joinedRow);
            }
        }
        
        return result;
    }
    
    /**
     * 构建哈希索引（用于优化JOIN查找）
     * 
     * @param rows 数据行列表
     * @param columnIndex 列索引
     * @return 值到行列表的映射
     */
    private Map<String, List<String[]>> buildHashIndex(List<String[]> rows, int columnIndex) {
        Map<String, List<String[]>> index = new HashMap<>();
        
        for (String[] row : rows) {
            if (row == null || columnIndex >= row.length) {
                continue;
            }
            
            String value = row[columnIndex];
            if (value == null) {
                continue; // NULL值不索引
            }
            
            index.computeIfAbsent(value, k -> new ArrayList<>()).add(row);
        }
        
        return index;
    }
    
    /**
     * 为表构建哈希索引
     * 
     * @param table 表信息
     * @param columnIndex 列索引
     * @return 值到行列表的映射
     */
    private Map<String, List<String[]>> buildHashIndex(TableInfo table, int columnIndex) {
        Map<String, List<String[]>> index = new HashMap<>();
        
        for (int i = 0; i < table.getRowCount(); i++) {
            String[] row = table.getRow(i);
            if (row == null || columnIndex >= row.length) {
                continue;
            }
            
            String value = row[columnIndex];
            if (value == null) {
                continue; // NULL值不索引
            }
            
            index.computeIfAbsent(value, k -> new ArrayList<>()).add(row);
        }
        
        return index;
    }
    
    /**
     * 合并两行数据
     * 
     * @param leftRow 左表行
     * @param rightRow 右表行
     * @return 合并后的行
     */
    private String[] combineRows(String[] leftRow, String[] rightRow) {
        int leftLen = leftRow != null ? leftRow.length : 0;
        int rightLen = rightRow != null ? rightRow.length : 0;
        
        String[] combined = new String[leftLen + rightLen];
        
        if (leftRow != null) {
            System.arraycopy(leftRow, 0, combined, 0, leftLen);
        }
        if (rightRow != null) {
            System.arraycopy(rightRow, 0, combined, leftLen, rightLen);
        }
        
        return combined;
    }
    
    /**
     * 创建NULL行（用于OUTER JOIN）
     * 
     * @param columnCount 列数
     * @return NULL行
     */
    private String[] createNullRow(int columnCount) {
        return new String[columnCount];
    }
}

