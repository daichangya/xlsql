/*
 * x l S Q L  
 * (c) daichangya, xlsql.jsdiff.com
 * See XLSQL-license.txt for license details
 *
 */
package com.jsdiff.xlsql.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * xlResultSet - xlSQL结果集实现
 * 
 * <p>该类实现了JDBC ResultSet接口，作为后端数据库结果集的包装器。
 * 所有方法都委托给后端数据库的结果集对象。</p>
 * 
 * @author daichangya
 */
public class xlResultSet implements ResultSet {

    xlStatement xlStm;
    ResultSet dbRs;
    
    //~ Constructors �����������������������������������������������������������
    /**
     * 创建结果集实例
     * 
     * @param stm 关联的xlStatement对象
     * @param rs 后端数据库的结果集对象
     */
    protected xlResultSet(xlStatement stm, ResultSet rs) {
        xlStm = stm;
        dbRs = rs;
    }

    //~ Methods ����������������������������������������������������������������

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#absolute
    */
    public boolean absolute(int row) throws SQLException {
        return dbRs.absolute(row);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#afterLast
    */
    public void afterLast() throws SQLException {
        dbRs.afterLast();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#beforeFirst
    */
    public void beforeFirst() throws SQLException {
        dbRs.beforeFirst();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#cancelRowUpdates
    */
    public void cancelRowUpdates() throws SQLException {
        dbRs.cancelRowUpdates();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#clearWarnings
    */
    public void clearWarnings() throws SQLException {
        dbRs.clearWarnings();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#close
    */
    public void close() throws SQLException {
        dbRs.close();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#deleteRow
    */
    public void deleteRow() throws SQLException {
        dbRs.deleteRow();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#findColumn
    */
    public int findColumn(String columnName) throws SQLException {
        return dbRs.findColumn(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#first
    */
    public boolean first() throws SQLException {
        return dbRs.first();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getArray
    */
    public Array getArray(int i) throws SQLException {
        return dbRs.getArray(i);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getArray
    */
    public Array getArray(String colName) throws SQLException {
        return dbRs.getArray(colName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getAsciiStream
    */
    public InputStream getAsciiStream(int columnIndex)
                                       throws SQLException {
        return dbRs.getAsciiStream(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getAsciiStream
    */
    public InputStream getAsciiStream(String columnName)
                                       throws SQLException {
        return dbRs.getAsciiStream(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getBigDecimal
    */
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return dbRs.getBigDecimal(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getBigDecimal
    */
    public BigDecimal getBigDecimal(int columnIndex, int scale)
                           throws SQLException {
        return dbRs.getBigDecimal(columnIndex, scale);
    }
    
    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getBigDecimal
    */
    public BigDecimal getBigDecimal(String columnName)
                             throws SQLException {
        return dbRs.getBigDecimal(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getBigDecimal
    */
    public BigDecimal getBigDecimal(String columnName, int scale)
                             throws SQLException {
        return dbRs.getBigDecimal(columnName, scale);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getBinaryStream
    */
    public InputStream getBinaryStream(int columnIndex)
                                        throws SQLException {
        return dbRs.getBinaryStream(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getBinaryStream
    */
    public InputStream getBinaryStream(String columnName)
                                        throws SQLException {
        return dbRs.getBinaryStream(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getBlob
    */
    public Blob getBlob(int i) throws SQLException {
        return dbRs.getBlob(i);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getBlob
    */
    public Blob getBlob(String colName) throws SQLException {
        return dbRs.getBlob(colName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getBoolean
    */
    public boolean getBoolean(int columnIndex) throws SQLException {
        return dbRs.getBoolean(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getBoolean
    */
    public boolean getBoolean(String columnName) throws SQLException {
        return dbRs.getBoolean(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getByte
    */
    public byte getByte(int columnIndex) throws SQLException {
        return dbRs.getByte(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getByte
    */
    public byte getByte(String columnName) throws SQLException {
        return dbRs.getByte(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getBytes
    */
    public byte[] getBytes(int columnIndex) throws SQLException {
        return dbRs.getBytes(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getBytes
    */
    public byte[] getBytes(String columnName) throws SQLException {
        return dbRs.getBytes(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getCharacterStream
    */
    public Reader getCharacterStream(int columnIndex)
                                      throws SQLException {
        return dbRs.getCharacterStream(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getCharacterStream
    */
    public Reader getCharacterStream(String columnName)
                                      throws SQLException {
        return dbRs.getCharacterStream(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getClob
    */
    public Clob getClob(int i) throws SQLException {
        return dbRs.getClob(i);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getClob
    */
    public Clob getClob(String colName) throws SQLException {
        return dbRs.getClob(colName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getConcurrency
    */
    public int getConcurrency() throws SQLException {
        return dbRs.getConcurrency();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getCursorName
    */
    public String getCursorName() throws SQLException {
        return dbRs.getCursorName();
    }
    
    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getDate
    */
    public java.sql.Date getDate(int columnIndex) throws SQLException {
        return dbRs.getDate(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getDate
    */
    public java.sql.Date getDate(int columnIndex, Calendar cal)
                          throws SQLException {
        return dbRs.getDate(columnIndex, cal);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getDate
    */
    public java.sql.Date getDate(String columnName) throws SQLException {
        return dbRs.getDate(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getDate
    */
    public java.sql.Date getDate(String columnName, Calendar cal)
                          throws SQLException {
        return dbRs.getDate(columnName, cal);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getDouble
    */
    public double getDouble(int columnIndex) throws SQLException {
        return dbRs.getDouble(columnIndex);
    }
    
    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getDouble
    */
    public double getDouble(String columnName) throws SQLException {
        return dbRs.getDouble(columnName);
    }
    
    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getFetchDirection
    */
    public int getFetchDirection() throws SQLException {
        return dbRs.getFetchDirection();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getFetchSize
    */
    public int getFetchSize() throws SQLException {
        return dbRs.getFetchSize();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getFloat
    */
    public float getFloat(int columnIndex) throws SQLException {
        return dbRs.getFloat(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getFloat
    */
    public float getFloat(String columnName) throws SQLException {
        return dbRs.getFloat(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getInt
    */
    public int getInt(int columnIndex) throws SQLException {
        return dbRs.getInt(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getInt
    */
    public int getInt(String columnName) throws SQLException {
        return dbRs.getInt(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getLong
    */
    public long getLong(int columnIndex) throws SQLException {
        return dbRs.getLong(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getLong
    */
    public long getLong(String columnName) throws SQLException {
        return dbRs.getLong(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getMetaData
    */
    public ResultSetMetaData getMetaData() throws SQLException {
        ResultSetMetaData dbRsMeta = dbRs.getMetaData();
        ResultSetMetaData rsMeta = new xlResultSetMetaData(this, dbRsMeta);
        return rsMeta;
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getObject
    */
    public Object getObject(int columnIndex) throws SQLException {
        return dbRs.getObject(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getObject
    */
    public Object getObject(int i, Map map) throws SQLException {
        return dbRs.getObject(i, map);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getObject
    */
    public Object getObject(String columnName) throws SQLException {
        return dbRs.getObject(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getObject
    */
    public Object getObject(String colName, Map map)
                     throws SQLException {
        return dbRs.getObject(colName, map);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getRef
    */
    public Ref getRef(int i) throws SQLException {
        return dbRs.getRef(i);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getRef
    */
    public Ref getRef(String colName) throws SQLException {
        return dbRs.getRef(colName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getRow
    */
    public int getRow() throws SQLException {
        return dbRs.getRow();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getShort
    */
    public short getShort(int columnIndex) throws SQLException {
        return dbRs.getShort(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getShort
    */
    public short getShort(String columnName) throws SQLException {
        return dbRs.getShort(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getStatement
    */
    public Statement getStatement() throws SQLException {
        return xlStm;
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getString
    */
    public String getString(int columnIndex) throws SQLException {
        return dbRs.getString(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getString
    */
    public String getString(String columnName) throws SQLException {
        return dbRs.getString(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getTime
    */
    public Time getTime(int columnIndex) throws SQLException {
        return dbRs.getTime(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getTime
    */
    public Time getTime(int columnIndex, Calendar cal)
                 throws SQLException {
        return dbRs.getTime(columnIndex, cal);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getTime
    */
    public Time getTime(String columnName) throws SQLException {
        return dbRs.getTime(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getTime
    */
    public Time getTime(String columnName, Calendar cal)
                 throws SQLException {
        return dbRs.getTime(columnName, cal);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getTimestamp
    */
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return dbRs.getTimestamp(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getTimestamp
    */
    public Timestamp getTimestamp(int columnIndex, Calendar cal)
                           throws SQLException {
        return dbRs.getTimestamp(columnIndex, cal);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getTimestamp
    */
    public Timestamp getTimestamp(String columnName) throws SQLException {
        return dbRs.getTimestamp(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getTimestamp
    */
    public Timestamp getTimestamp(String columnName, Calendar cal)
                           throws SQLException {
        return dbRs.getTimestamp(columnName, cal);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getType
    */
    public int getType() throws SQLException {
        return dbRs.getType();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getUnicodeStream
    */
    public InputStream getUnicodeStream(int columnIndex)
                                 throws SQLException {
        return dbRs.getUnicodeStream(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getUnicodeStream
    */
    public InputStream getUnicodeStream(String columnName)
                                 throws SQLException {
        return dbRs.getUnicodeStream(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getURL
    */
    public URL getURL(int columnIndex) throws SQLException {
        return dbRs.getURL(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getURL
    */
    public URL getURL(String columnName) throws SQLException {
        return dbRs.getURL(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#getWarnings
    */
    public SQLWarning getWarnings() throws SQLException {
        return dbRs.getWarnings();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#insertRow
    */
    public void insertRow() throws SQLException {
        dbRs.insertRow();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#isAfterLast
    */
    public boolean isAfterLast() throws SQLException {
        return dbRs.isAfterLast();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#isBeforeFirst
    */
    public boolean isBeforeFirst() throws SQLException {
        return dbRs.isBeforeFirst();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#isFirst
    */
    public boolean isFirst() throws SQLException {
        return dbRs.isFirst();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#isLast
    */
    public boolean isLast() throws SQLException {
        return dbRs.isLast();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#last
    */
    public boolean last() throws SQLException {
        return dbRs.last();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#moveToCurrentRow
    */
    public void moveToCurrentRow() throws SQLException {
        dbRs.moveToCurrentRow();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#moveToInsertRow
    */
    public void moveToInsertRow() throws SQLException {
        dbRs.moveToInsertRow();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#next
    */
    public boolean next() throws SQLException {
        return dbRs.next();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#previous
    */
    public boolean previous() throws SQLException {
        return dbRs.previous();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#refreshRow
    */
    public void refreshRow() throws SQLException {
        dbRs.refreshRow();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#relative
    */
    public boolean relative(int rows) throws SQLException {
        return dbRs.relative(rows);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#rowDeleted
    */
    public boolean rowDeleted() throws SQLException {
        return dbRs.rowDeleted();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#rowInserted
    */
    public boolean rowInserted() throws SQLException {
        return dbRs.rowInserted();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#rowUpdated
    */
    public boolean rowUpdated() throws SQLException {
        return dbRs.rowUpdated();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#setFetchDirection
    */
    public void setFetchDirection(int direction) throws SQLException {
        dbRs.setFetchDirection(direction);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#setFetchSize
    */
    public void setFetchSize(int rows) throws SQLException {
        dbRs.setFetchSize(rows);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateArray
    */
    public void updateArray(int columnIndex, Array x) throws SQLException {
        dbRs.updateArray(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateArray
    */
    public void updateArray(String columnName, Array x)
                     throws SQLException {
        dbRs.updateArray(columnName, x);
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {

    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {

    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {

    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {

    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {

    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {

    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {

    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return "";
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return "";
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {

    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {

    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {

    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {

    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return null;
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return null;
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateAsciiStream
    */
    public void updateAsciiStream(int columnIndex, InputStream x, int length)
                           throws SQLException {
        dbRs.updateAsciiStream(columnIndex, x, length);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateAsciiStream
    */
    public void updateAsciiStream(String columnName, InputStream x, 
                                  int length) throws SQLException {
        dbRs.updateAsciiStream(columnName, x, length);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateBigDecimal
    */
    public void updateBigDecimal(int columnIndex, BigDecimal x)
                          throws SQLException {
        dbRs.updateBigDecimal(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateBigDecimal
    */
    public void updateBigDecimal(String columnName, BigDecimal x)
                          throws SQLException {
        dbRs.updateBigDecimal(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateBinaryStream
    */
    public void updateBinaryStream(int columnIndex, InputStream x, int length)
                            throws SQLException {
dbRs.updateBinaryStream(columnIndex ,x, length);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateBinaryStream
    */
    public void updateBinaryStream(String columnName, InputStream x, 
                                   int length) throws SQLException {
        dbRs.updateBinaryStream(columnName, x, length);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateBlob
    */
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        dbRs.updateBlob(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateBlob
    */
    public void updateBlob(String columnName, Blob x) throws SQLException {
        dbRs.updateBlob(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateBoolean
    */
    public void updateBoolean(int columnIndex, boolean x)
                       throws SQLException {
        dbRs.updateBoolean(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateBoolean
    */
    public void updateBoolean(String columnName, boolean x)
                       throws SQLException {
        dbRs.updateBoolean(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateByte
    */
    public void updateByte(int columnIndex, byte x) throws SQLException {
        dbRs.updateByte(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateByte
    */
    public void updateByte(String columnName, byte x) throws SQLException {
        dbRs.updateByte(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateBytes
    */
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        dbRs.updateBytes(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateBytes
    */
    public void updateBytes(String columnName, byte[] x)
                     throws SQLException {
        dbRs.updateBytes(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateCharacterStream
    */
    public void updateCharacterStream(int columnIndex, Reader x, int length)
                               throws SQLException {
        dbRs.updateCharacterStream(columnIndex, x, length);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateCharacterStream
    */
    public void updateCharacterStream(String columnNameIndex, Reader x, int length)
                               throws SQLException {
        dbRs.updateCharacterStream(columnNameIndex, x, length);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateClob
    */
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        dbRs.updateClob(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateClob
    */
    public void updateClob(String columnName, Clob x) throws SQLException {
        dbRs.updateClob(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateDate
    */
    public void updateDate(int columnIndex, java.sql.Date x)
                    throws SQLException {
        dbRs.updateDate(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateDate
    */
    public void updateDate(String columnName, java.sql.Date x)
                    throws SQLException {
        dbRs.updateDate(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateDouble
    */
    public void updateDouble(int columnIndex, double x) throws SQLException {
        dbRs.updateDouble(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateDouble
    */
    public void updateDouble(String columnName, double x)
                      throws SQLException {
        dbRs.updateDouble(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateFloat
    */
    public void updateFloat(int columnIndex, float x) throws SQLException {
        dbRs.updateFloat(columnIndex, x);
    }
    
    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateFloat
    */
    public void updateFloat(String columnName, float x) throws SQLException {
        dbRs.updateFloat(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateInt
    */
    public void updateInt(int columnIndex, int x) throws SQLException {
        dbRs.updateInt(columnIndex, x);
    }
    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateInt
    */
    public void updateInt(String columnName, int x) throws SQLException {
        dbRs.updateInt(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateLong
    */
    public void updateLong(int columnIndex, long x) throws SQLException {
        dbRs.updateLong(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateLong
    */
    public void updateLong(String columnName, long x) throws SQLException {
        dbRs.updateLong(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateNull
    */
    public void updateNull(int columnIndex) throws SQLException {
        dbRs.updateNull(columnIndex);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateNull
    */
    public void updateNull(String columnName) throws SQLException {
        dbRs.updateNull(columnName);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateObject
    */
    public void updateObject(int columnIndex, Object x)
                      throws SQLException {
        dbRs.updateObject(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateObject
    */
    public void updateObject(int columnIndex, Object x, int scale)
                      throws SQLException {
        dbRs.updateObject(columnIndex, x, scale);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateObject
    */
    public void updateObject(String columnName, Object x)
                      throws SQLException {
        dbRs.updateObject(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateObject
    */
    public void updateObject(String columnName, Object x, int scale)
                      throws SQLException {
        dbRs.updateObject(columnName, x, scale);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateRef
    */
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        dbRs.updateRef(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateRef
    */
    public void updateRef(String columnName, Ref x) throws SQLException {
        dbRs.updateRef(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateRow
    */
    public void updateRow() throws SQLException {
        dbRs.updateRow();
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateShort
    */
    public void updateShort(int columnIndex, short x) throws SQLException {
        dbRs.updateShort(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateShort
    */
    public void updateShort(String columnName, short x) throws SQLException {
        dbRs.updateShort(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateString
    */
    public void updateString(int columnIndex, String x) throws SQLException {
        dbRs.updateString(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateString
    */
    public void updateString(String columnName, String x)
                      throws SQLException {
        dbRs.updateString(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateTime
    */
    public void updateTime(String columnName, Time x)
                    throws SQLException {
        dbRs.updateTime(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateTime
    */
    public void updateTime(int columnIndex, Time x)
                    throws SQLException {
        dbRs.updateTime(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateTimestamp
    */
    public void updateTimestamp(int columnIndex, Timestamp x)
                         throws SQLException {
        dbRs.updateTimestamp(columnIndex, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#updateTimestamp
    */
    public void updateTimestamp(String columnName, Timestamp x)
                         throws SQLException {
        dbRs.updateTimestamp(columnName, x);
    }

    /**
    * Implements method in interface java.sql.ResultSet
    * @see ResultSet#wasNull
    */
    public boolean wasNull() throws SQLException {
        return dbRs.wasNull();
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