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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;

/**
 * xlNativeResultSet - 自研引擎的结果集实现
 * 
 * <p>基于内存数据实现ResultSet接口，用于自研SQL引擎返回查询结果。
 * 数据直接从Excel读取，存储在内存中。</p>
 * 
 * @author daichangya
 */
public class xlNativeResultSet implements ResultSet {
    
    /** 列名数组 */
    private final String[] columnNames;
    
    /** 列类型数组 */
    private final String[] columnTypes;
    
    /** 数据值矩阵（String[列][行]） */
    private final String[][] values;
    
    /** 总行数 */
    private final int rowCount;
    
    /** 当前行索引（从0开始，-1表示在首行之前） */
    private int currentRow = -1;
    
    /** 结果集是否已关闭 */
    private boolean closed = false;
    
    /**
     * 创建xlNativeResultSet实例
     * 
     * @param columnNames 列名数组
     * @param columnTypes 列类型数组
     * @param values 数据值矩阵（String[列][行]）
     * @param rowCount 总行数
     */
    public xlNativeResultSet(String[] columnNames, String[] columnTypes, 
                             String[][] values, int rowCount) {
        this.columnNames = columnNames != null ? columnNames : new String[0];
        this.columnTypes = columnTypes != null ? columnTypes : new String[0];
        this.values = values != null ? values : new String[0][0];
        this.rowCount = rowCount;
    }
    
    @Override
    public boolean next() throws SQLException {
        checkClosed();
        if (currentRow < rowCount - 1) {
            currentRow++;
            return true;
        }
        return false;
    }
    
    @Override
    public void close() throws SQLException {
        closed = true;
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        checkClosed();
        if (currentRow < 0 || currentRow >= rowCount) {
            return true;
        }
        // 检查当前行的指定列是否为空
        // 这里简化实现，假设空字符串为null
        return false;
    }
    
    @Override
    public String getString(int columnIndex) throws SQLException {
        checkClosed();
        checkRow();
        if (columnIndex < 1 || columnIndex > columnNames.length) {
            throw new SQLException("Column index out of range: " + columnIndex);
        }
        int colIndex = columnIndex - 1; // 转换为0-based
        if (colIndex >= values.length || currentRow >= values[colIndex].length) {
            return null;
        }
        String value = values[colIndex][currentRow];
        return (value == null || value.isEmpty()) ? null : value;
    }
    
    @Override
    public String getString(String columnLabel) throws SQLException {
        int columnIndex = findColumn(columnLabel);
        return getString(columnIndex);
    }
    
    @Override
    public int findColumn(String columnLabel) throws SQLException {
        checkClosed();
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equalsIgnoreCase(columnLabel)) {
                return i + 1; // 返回1-based索引
            }
        }
        throw new SQLException("Column not found: " + columnLabel);
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        checkClosed();
        return new xlNativeResultSetMetaData(columnNames, columnTypes);
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }
    
    // 辅助方法
    
    private void checkClosed() throws SQLException {
        if (closed) {
            throw new SQLException("ResultSet is closed");
        }
    }
    
    private void checkRow() throws SQLException {
        if (currentRow < 0) {
            throw new SQLException("ResultSet is not positioned on a row. Call next() first.");
        }
        if (currentRow >= rowCount) {
            throw new SQLException("ResultSet is positioned after the last row");
        }
    }
    
    // 未实现的方法（返回默认值或抛出异常）
    
    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        String value = getString(columnIndex);
        return value != null && (value.equalsIgnoreCase("true") || value.equals("1") || value.equalsIgnoreCase("Y"));
    }
    
    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }
    
    @Override
    public int getInt(int columnIndex) throws SQLException {
        String value = getString(columnIndex);
        if (value == null || value.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }
    
    @Override
    public long getLong(int columnIndex) throws SQLException {
        String value = getString(columnIndex);
        if (value == null || value.isEmpty()) {
            return 0;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }
    
    @Override
    public double getDouble(int columnIndex) throws SQLException {
        String value = getString(columnIndex);
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }
    
    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        String value = getString(columnIndex);
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getBigDecimal(findColumn(columnLabel));
    }
    
    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        BigDecimal value = getBigDecimal(columnIndex);
        if (value != null) {
            return value.setScale(scale, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return new byte[0];
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getBigDecimal(findColumn(columnLabel), scale);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return new byte[0];
    }

    // 其他方法抛出未支持异常或返回默认值
    
    @Override
    public Statement getStatement() throws SQLException {
        return null;
    }
    
    @Override
    public String getCursorName() throws SQLException {
        checkClosed();
        return null; // 自研引擎不支持游标名称
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }
    
    @Override
    public void clearWarnings() throws SQLException {
    }
    
    // 以下方法暂不支持，抛出异常
    @Override
    public byte getByte(int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public byte getByte(String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public short getShort(int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public short getShort(String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public float getFloat(int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public float getFloat(String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Date getDate(int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Date getDate(String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Date getDate(int columnIndex, java.util.Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Date getDate(String columnLabel, java.util.Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Time getTime(int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Time getTime(String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Time getTime(int columnIndex, java.util.Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Time getTime(String columnLabel, java.util.Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Timestamp getTimestamp(int columnIndex, java.util.Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Timestamp getTimestamp(String columnLabel, java.util.Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }

    @Override
    public Object getObject(int columnIndex, java.util.Map<String, Class<?>> map) throws SQLException {
        // 简单实现 - 实际应用中可以根据 map 参数做类型映射
        return getObject(columnIndex);
    }

    @Override
    public Object getObject(String columnLabel, java.util.Map<String, Class<?>> map) throws SQLException {
        // 简单实现 - 实际应用中可以根据 map 参数做类型映射
        return getObject(columnLabel);
    }


    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return getString(columnIndex);
    }
    
    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return getString(columnLabel);
    }
    
    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        Object value = getObject(columnIndex);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        throw new SQLException("Cannot convert column " + columnIndex + " to " + type.getName());
    }
    
    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return getObject(findColumn(columnLabel), type);
    }

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        ResultSet.super.updateObject(columnIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        ResultSet.super.updateObject(columnLabel, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        ResultSet.super.updateObject(columnIndex, x, targetSqlType);
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
        ResultSet.super.updateObject(columnLabel, x, targetSqlType);
    }

    @Override
    public java.sql.Array getArray(int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public java.sql.Array getArray(String columnLabel) throws SQLException {
        return getArray(findColumn(columnLabel));
    }
    
    @Override
    public java.sql.Blob getBlob(int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public java.sql.Blob getBlob(String columnLabel) throws SQLException {
        return getBlob(findColumn(columnLabel));
    }
    
    @Override
    public java.sql.Clob getClob(int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public java.sql.Clob getClob(String columnLabel) throws SQLException {
        return getClob(findColumn(columnLabel));
    }
    
    @Override
    public java.sql.Ref getRef(int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public java.sql.Ref getRef(String columnLabel) throws SQLException {
        return getRef(findColumn(columnLabel));
    }
    
    // JDBC 4.0+ 新增的 get 方法
    @Override
    public java.sql.RowId getRowId(int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public java.sql.RowId getRowId(String columnLabel) throws SQLException {
        return getRowId(findColumn(columnLabel));
    }
    
    @Override
    public java.sql.NClob getNClob(int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public java.sql.NClob getNClob(String columnLabel) throws SQLException {
        return getNClob(findColumn(columnLabel));
    }
    
    @Override
    public java.sql.SQLXML getSQLXML(int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public java.sql.SQLXML getSQLXML(String columnLabel) throws SQLException {
        return getSQLXML(findColumn(columnLabel));
    }
    
    @Override
    public String getNString(int columnIndex) throws SQLException {
        return getString(columnIndex);
    }
    
    @Override
    public String getNString(String columnLabel) throws SQLException {
        return getNString(findColumn(columnLabel));
    }
    
    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return getNCharacterStream(findColumn(columnLabel));
    }
    
    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return null; // 不支持
    }
    
    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return getCharacterStream(findColumn(columnLabel));
    }
    
    @Override
    public java.net.URL getURL(int columnIndex) throws SQLException {
        String value = getString(columnIndex);
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return new java.net.URL(value);
        } catch (java.net.MalformedURLException e) {
            throw new SQLException("Invalid URL: " + value, e);
        }
    }
    
    @Override
    public java.net.URL getURL(String columnLabel) throws SQLException {
        return getURL(findColumn(columnLabel));
    }
    
    // 实现ResultSet接口的其他必需方法（简化实现）
    
    @Override
    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }
    
    @Override
    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }
    
    @Override
    public boolean isBeforeFirst() throws SQLException {
        checkClosed();
        return currentRow == -1;
    }
    
    @Override
    public boolean isAfterLast() throws SQLException {
        checkClosed();
        return currentRow >= rowCount;
    }
    
    @Override
    public boolean isFirst() throws SQLException {
        checkClosed();
        return currentRow == 0;
    }
    
    @Override
    public boolean isLast() throws SQLException {
        checkClosed();
        return currentRow == rowCount - 1;
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        checkClosed();
        currentRow = -1;
    }
    
    @Override
    public void afterLast() throws SQLException {
        checkClosed();
        currentRow = rowCount;
    }
    
    @Override
    public boolean first() throws SQLException {
        checkClosed();
        if (rowCount > 0) {
            currentRow = 0;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean last() throws SQLException {
        checkClosed();
        if (rowCount > 0) {
            currentRow = rowCount - 1;
            return true;
        }
        return false;
    }
    
    @Override
    public int getRow() throws SQLException {
        checkClosed();
        return currentRow + 1; // 返回1-based行号
    }
    
    @Override
    public boolean absolute(int row) throws SQLException {
        checkClosed();
        if (row > 0) {
            if (row <= rowCount) {
                currentRow = row - 1;
                return true;
            } else {
                currentRow = rowCount;
                return false;
            }
        } else if (row < 0) {
            int absRow = rowCount + row + 1;
            if (absRow >= 1) {
                currentRow = absRow - 1;
                return true;
            } else {
                currentRow = -1;
                return false;
            }
        } else {
            currentRow = -1;
            return false;
        }
    }
    
    @Override
    public boolean relative(int rows) throws SQLException {
        checkClosed();
        int newRow = currentRow + rows;
        if (newRow < -1) {
            currentRow = -1;
            return false;
        } else if (newRow >= rowCount) {
            currentRow = rowCount;
            return false;
        } else {
            currentRow = newRow;
            return true;
        }
    }
    
    @Override
    public boolean previous() throws SQLException {
        checkClosed();
        if (currentRow > 0) {
            currentRow--;
            return true;
        } else {
            currentRow = -1;
            return false;
        }
    }
    
    // 行状态检测方法
    @Override
    public boolean rowDeleted() throws SQLException {
        checkClosed();
        return false; // 自研引擎是只读的，不会有行被删除
    }

    @Override
    public boolean rowInserted() throws SQLException {
        checkClosed();
        return false; // 自研引擎是只读的，不会有行被插入
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        checkClosed();
        return false; // 自研引擎是只读的，不会有行被更新
    }
    
    // 其他未实现的方法返回默认值或抛出异常
    @Override
    public void setFetchDirection(int direction) throws SQLException {
        // 只支持向前
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }
    
    @Override
    public void setFetchSize(int rows) throws SQLException {
        // 不支持
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }
    
    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }
    
    // 以下方法不支持更新操作（自研引擎是只读的）
    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, int length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, int length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateRef(String columnLabel, java.sql.Ref x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBlob(int columnIndex, java.sql.Blob x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBlob(String columnLabel, java.sql.Blob x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateClob(int columnIndex, java.sql.Clob x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateClob(String columnLabel, java.sql.Clob x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateClob(int columnIndex, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateClob(String columnLabel, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateClob(int columnIndex, java.io.Reader reader) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateClob(String columnLabel, java.io.Reader reader) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateRowId(int columnIndex, java.sql.RowId x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateRowId(String columnLabel, java.sql.RowId x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNClob(int columnIndex, java.sql.NClob nClob) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNClob(String columnLabel, java.sql.NClob nClob) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNClob(int columnIndex, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNClob(String columnLabel, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNClob(int columnIndex, java.io.Reader reader) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNClob(String columnLabel, java.io.Reader reader) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateSQLXML(int columnIndex, java.sql.SQLXML xmlObject) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateSQLXML(String columnLabel, java.sql.SQLXML xmlObject) throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void updateRow() throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void insertRow() throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void deleteRow() throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void refreshRow() throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLException("Updates not supported");
    }
    
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
    
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}

