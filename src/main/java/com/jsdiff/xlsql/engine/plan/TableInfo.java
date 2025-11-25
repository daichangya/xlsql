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

import java.util.HashMap;
import java.util.Map;

/**
 * TableInfo - 表信息
 * 
 * <p>存储Excel表（工作簿和工作表）的元数据和数据。
 * 用于查询计划执行时访问表信息。</p>
 * 
 * @author daichangya
 */
public class TableInfo {
    
    /** 工作簿名称（Excel文件名，不含扩展名） */
    private final String workbook;
    
    /** 工作表名称 */
    private final String sheet;
    
    /** 表别名（SQL中的AS别名） */
    private final String alias;
    
    /** 列名数组 */
    private String[] columnNames;
    
    /** 列类型数组 */
    private String[] columnTypes;
    
    /** 数据值矩阵（String[列][行]） */
    private String[][] data;
    
    /** 行数（不包括标题行） */
    private int rowCount;
    
    /** 列名到索引的映射（用于快速查找） */
    private Map<String, Integer> columnIndexMap;
    
    /**
     * 创建TableInfo实例
     * 
     * @param workbook 工作簿名称
     * @param sheet 工作表名称
     * @param alias 表别名（可以为null）
     */
    public TableInfo(String workbook, String sheet, String alias) {
        this.workbook = workbook;
        this.sheet = sheet;
        this.alias = alias != null ? alias : "";
    }
    
    /**
     * 加载表数据
     * 
     * @param columnNames 列名数组
     * @param columnTypes 列类型数组
     * @param data 数据值矩阵
     * @param rowCount 行数
     */
    public void loadData(String[] columnNames, String[] columnTypes, 
                       String[][] data, int rowCount) {
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        this.data = data;
        this.rowCount = rowCount;
        
        // 构建列名到索引的映射
        this.columnIndexMap = new HashMap<>();
        for (int i = 0; i < columnNames.length; i++) {
            columnIndexMap.put(columnNames[i].toUpperCase(), i);
        }
    }
    
    /**
     * 根据列名获取列索引
     * 
     * @param columnName 列名
     * @return 列索引（从0开始），如果不存在返回-1
     */
    public int getColumnIndex(String columnName) {
        if (columnIndexMap == null) {
            return -1;
        }
        Integer index = columnIndexMap.get(columnName.toUpperCase());
        return index != null ? index : -1;
    }
    
    /**
     * 获取指定行的数据
     * 
     * @param rowIndex 行索引（从0开始）
     * @return 该行的数据数组，如果行索引无效返回null
     */
    public String[] getRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rowCount || data == null) {
            return null;
        }
        
        // 将列优先的数据矩阵转换为行优先
        String[] row = new String[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            if (i < data.length && rowIndex < data[i].length) {
                row[i] = data[i][rowIndex];
            } else {
                row[i] = null;
            }
        }
        return row;
    }
    
    // Getters
    
    public String getWorkbook() {
        return workbook;
    }
    
    public String getSheet() {
        return sheet;
    }
    
    public String getAlias() {
        return alias;
    }
    
    public String[] getColumnNames() {
        return columnNames;
    }
    
    public String[] getColumnTypes() {
        return columnTypes;
    }
    
    public String[][] getData() {
        return data;
    }
    
    public int getRowCount() {
        return rowCount;
    }
    
    public Map<String, Integer> getColumnIndexMap() {
        return columnIndexMap;
    }
    
    /**
     * 获取表的完整名称（用于显示）
     * 
     * @return 表名（workbook.sheet 或 sheet）
     */
    public String getFullName() {
        if (workbook == null || workbook.isEmpty() || workbook.equals("SA")) {
            return sheet;
        }
        return workbook + "." + sheet;
    }
}

