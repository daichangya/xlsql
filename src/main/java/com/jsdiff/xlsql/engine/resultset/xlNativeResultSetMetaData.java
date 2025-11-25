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
package com.jsdiff.xlsql.engine.resultset;

import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;

/**
 * xlNativeResultSetMetaData - 自研引擎的结果集元数据实现
 * 
 * <p>提供结果集的列信息，包括列名、类型等。</p>
 * 
 * @author daichangya
 */
public class xlNativeResultSetMetaData implements ResultSetMetaData {
    
    /** 列名数组 */
    private final String[] columnNames;
    
    /** 列类型数组 */
    private final String[] columnTypes;
    
    /**
     * 创建xlNativeResultSetMetaData实例
     * 
     * @param columnNames 列名数组
     * @param columnTypes 列类型数组
     */
    public xlNativeResultSetMetaData(String[] columnNames, String[] columnTypes) {
        this.columnNames = columnNames != null ? columnNames : new String[0];
        this.columnTypes = columnTypes != null ? columnTypes : new String[0];
    }
    
    @Override
    public int getColumnCount() throws SQLException {
        return columnNames.length;
    }
    
    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }
    
    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return false;
    }
    
    @Override
    public boolean isSearchable(int column) throws SQLException {
        return true;
    }
    
    @Override
    public boolean isCurrency(int column) throws SQLException {
        return false;
    }
    
    @Override
    public int isNullable(int column) throws SQLException {
        return ResultSetMetaData.columnNullable;
    }
    
    @Override
    public boolean isSigned(int column) throws SQLException {
        String type = getColumnTypeName(column);
        return type.equalsIgnoreCase("INTEGER") || 
               type.equalsIgnoreCase("BIGINT") || 
               type.equalsIgnoreCase("DOUBLE");
    }
    
    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return 255; // 默认显示大小
    }
    
    @Override
    public String getColumnLabel(int column) throws SQLException {
        return getColumnName(column);
    }
    
    @Override
    public String getColumnName(int column) throws SQLException {
        checkColumn(column);
        return columnNames[column - 1];
    }
    
    @Override
    public String getSchemaName(int column) throws SQLException {
        return "";
    }
    
    @Override
    public int getPrecision(int column) throws SQLException {
        return 0;
    }
    
    @Override
    public int getScale(int column) throws SQLException {
        return 0;
    }
    
    @Override
    public String getTableName(int column) throws SQLException {
        return "";
    }
    
    @Override
    public String getCatalogName(int column) throws SQLException {
        return "";
    }
    
    @Override
    public int getColumnType(int column) throws SQLException {
        String typeName = getColumnTypeName(column);
        // 简化映射
        if (typeName.equalsIgnoreCase("VARCHAR") || typeName.equalsIgnoreCase("TEXT")) {
            return java.sql.Types.VARCHAR;
        } else if (typeName.equalsIgnoreCase("INTEGER")) {
            return java.sql.Types.INTEGER;
        } else if (typeName.equalsIgnoreCase("BIGINT")) {
            return java.sql.Types.BIGINT;
        } else if (typeName.equalsIgnoreCase("DOUBLE")) {
            return java.sql.Types.DOUBLE;
        } else if (typeName.equalsIgnoreCase("DATE")) {
            return java.sql.Types.DATE;
        } else if (typeName.equalsIgnoreCase("TIME")) {
            return java.sql.Types.TIME;
        } else if (typeName.equalsIgnoreCase("BIT")) {
            return java.sql.Types.BIT;
        } else {
            return java.sql.Types.VARCHAR; // 默认
        }
    }
    
    @Override
    public String getColumnTypeName(int column) throws SQLException {
        checkColumn(column);
        if (column - 1 < columnTypes.length) {
            return columnTypes[column - 1];
        }
        return "VARCHAR"; // 默认类型
    }
    
    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return true;
    }
    
    @Override
    public boolean isWritable(int column) throws SQLException {
        return false;
    }
    
    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }
    
    @Override
    public String getColumnClassName(int column) throws SQLException {
        int type = getColumnType(column);
        switch (type) {
            case java.sql.Types.INTEGER:
                return Integer.class.getName();
            case java.sql.Types.BIGINT:
                return Long.class.getName();
            case java.sql.Types.DOUBLE:
                return Double.class.getName();
            case java.sql.Types.DATE:
                return Date.class.getName();
            case java.sql.Types.TIME:
                return Time.class.getName();
            case java.sql.Types.BIT:
                return Boolean.class.getName();
            default:
                return String.class.getName();
        }
    }
    
    private void checkColumn(int column) throws SQLException {
        if (column < 1 || column > columnNames.length) {
            throw new SQLException("Column index out of range: " + column);
        }
    }
    
    // 未实现的方法返回默认值
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
    
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}

