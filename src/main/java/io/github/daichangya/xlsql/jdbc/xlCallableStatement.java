/*
 * x l S Q L  
 * (c) daichangya, xlsql.jsdiff.com
 * See XLSQL-license.txt for license details
 *
 */

package io.github.daichangya.xlsql.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * xlCallableStatement - xlSQL可调用语句实现
 * 
 * <p>该类实现了JDBC CallableStatement接口，作为后端数据库可调用语句的包装器。
 * 所有方法都委托给后端数据库的CallableStatement对象。</p>
 * 
 * @author daichangya
 */
public class xlCallableStatement extends xlPreparedStatement 
                                            implements CallableStatement {

    /** 关联的xlConnection对象 */
    private xlConnection xlCon;
    /** 后端数据库的CallableStatement对象 */
    private CallableStatement dbCstm;

    /**
     * 创建可调用语句实例
     * 
     * <p>可调用语句用于调用存储过程，继承自PreparedStatement并实现CallableStatement接口。</p>
     * 
     * @param con 关联的xlConnection对象
     * @param clst 后端数据库的CallableStatement对象
     * @param sql 可调用的SQL语句（通常是存储过程调用）
     * @throws SQLException 如果创建失败则抛出异常
     */
    public xlCallableStatement(xlConnection con, CallableStatement clst, 
                                            String sql) throws SQLException {
        super (con, clst, sql);
        dbCstm = clst;
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getArray
    */
    public Array getArray(int i) throws SQLException {
        return dbCstm.getArray(i);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getArray
    */
    public Array getArray(String parameterName) throws SQLException {
        return dbCstm.getArray(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getBigDecimal
    */
    public java.math.BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return dbCstm.getBigDecimal(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getBigDecimal
    */
    public java.math.BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return dbCstm.getBigDecimal(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getBigDecimal
    */
    public java.math.BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        return dbCstm.getBigDecimal(parameterIndex,scale);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getBlob
    */
    public Blob getBlob(int i) throws SQLException {
        return dbCstm.getBlob(i);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getBlob
    */
    public Blob getBlob(String parameterName) throws SQLException {
        return dbCstm.getBlob(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getBoolean
    */
    public boolean getBoolean(int parameterIndex) throws SQLException {
        return dbCstm.getBoolean(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getBoolean
    */
    public boolean getBoolean(String parameterName) throws SQLException {
        return dbCstm.getBoolean(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getByte
    */
    public byte getByte(int parameterIndex) throws SQLException {
        return dbCstm.getByte(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getByte
    */
    public byte getByte(String parameterName) throws SQLException {
        return dbCstm.getByte(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getBytes
    */
    public byte[] getBytes(int parameterIndex) throws SQLException {
        return dbCstm.getBytes(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getBytes
    */
    public byte[] getBytes(String parameterName) throws SQLException {
        return dbCstm.getBytes(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getClob
    */
    public Clob getClob(int i) throws SQLException {
        return dbCstm.getClob(i);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getClob
    */
    public Clob getClob(String parameterName) throws SQLException {
        return dbCstm.getClob(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getDate
    */
    public Date getDate(int parameterIndex) throws SQLException {
        return dbCstm.getDate(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getDate
    */
    public Date getDate(String parameterName) throws SQLException {
        return dbCstm.getDate(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getDate
    */
    public Date getDate(int parameterIndex, java.util.Calendar cal) throws SQLException {
        return dbCstm.getDate(parameterIndex, cal);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getDate
    */
    public Date getDate(String parameterName, java.util.Calendar cal) throws SQLException {
        return dbCstm.getDate(parameterName, cal);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getDouble
    */
    public double getDouble(int parameterIndex) throws SQLException {
        return dbCstm.getDouble(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getDouble
    */
    public double getDouble(String parameterName) throws SQLException {
        return dbCstm.getDouble(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getFloat
    */
    public float getFloat(int parameterIndex) throws SQLException {
        return dbCstm.getFloat(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getFloat
    */
    public float getFloat(String parameterName) throws SQLException {
        return dbCstm.getFloat(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getInt
    */
    public int getInt(String parameterName) throws SQLException {
        return dbCstm.getInt(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getInt
    */
    public int getInt(int parameterIndex) throws SQLException {
        return dbCstm.getInt(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getLong
    */
    public long getLong(int parameterIndex) throws SQLException {
        return dbCstm.getLong(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getLong
    */
    public long getLong(String parameterName) throws SQLException {
        return dbCstm.getLong(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getObject
    */
    public Object getObject(int parameterIndex) throws SQLException {
        return dbCstm.getObject(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getObject
    */
    public Object getObject(String parameterName) throws SQLException {
        return dbCstm.getObject(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getObject
    */
    public Object getObject(int i, java.util.Map map) throws SQLException {
        return dbCstm.getObject(i, map);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getObject
    */
    public Object getObject(String parameterName, java.util.Map map) throws SQLException {
        return dbCstm.getObject(parameterName, map);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getRef
    */
    public Ref getRef(int i) throws SQLException {
        return dbCstm.getRef(i);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getRef
    */
    public Ref getRef(String parameterName) throws SQLException {
        return dbCstm.getRef(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getShort
    */
    public short getShort(String parameterName) throws SQLException {
        return dbCstm.getShort(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getShort
    */
    public short getShort(int parameterIndex) throws SQLException {
        return dbCstm.getShort(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getString
    */
    public String getString(String parameterName) throws SQLException {
        return dbCstm.getString(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getString
    */
    public String getString(int parameterIndex) throws SQLException {
        return dbCstm.getString(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getTime
    */
    public Time getTime(String parameterName) throws SQLException {
        return dbCstm.getTime(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getTime
    */
    public Time getTime(int parameterIndex) throws SQLException {
        return dbCstm.getTime(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getTime
    */
    public Time getTime(String parameterName, java.util.Calendar cal) throws SQLException {
        return dbCstm.getTime(parameterName, cal);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getTime
    */
    public Time getTime(int parameterIndex, java.util.Calendar cal) throws SQLException {
        return dbCstm.getTime(parameterIndex, cal);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getTimestamp
    */
    public Timestamp getTimestamp(String parameterName) throws SQLException {
        return dbCstm.getTimestamp(parameterName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getTimestamp
    */
    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        return dbCstm.getTimestamp(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getTimestamp
    */
    public Timestamp getTimestamp(String parameterName, java.util.Calendar cal) throws SQLException {
        return dbCstm.getTimestamp(parameterName, cal);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getTimestamp
    */
    public Timestamp getTimestamp(int parameterIndex, java.util.Calendar cal) throws SQLException {
        return dbCstm.getTimestamp(parameterIndex, cal);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getURL
    */
    public java.net.URL getURL(int parameterIndex) throws SQLException {
        return dbCstm.getURL(parameterIndex);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#getURL
    */
    public java.net.URL getURL(String parameterName) throws SQLException {
        return dbCstm.getURL(parameterName);
    }

    @Override
    public RowId getRowId(int parameterIndex) throws SQLException {
        return null;
    }

    @Override
    public RowId getRowId(String parameterName) throws SQLException {
        return null;
    }

    @Override
    public void setRowId(String parameterName, RowId x) throws SQLException {

    }

    @Override
    public void setNString(String parameterName, String value) throws SQLException {

    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {

    }

    @Override
    public void setNClob(String parameterName, NClob value) throws SQLException {

    }

    @Override
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {

    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {

    }

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        return null;
    }

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        return null;
    }

    @Override
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {

    }

    @Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        return null;
    }

    @Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        return null;
    }

    @Override
    public String getNString(int parameterIndex) throws SQLException {
        return "";
    }

    @Override
    public String getNString(String parameterName) throws SQLException {
        return "";
    }

    @Override
    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(String parameterName) throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream(String parameterName) throws SQLException {
        return null;
    }

    @Override
    public void setBlob(String parameterName, Blob x) throws SQLException {

    }

    @Override
    public void setClob(String parameterName, Clob x) throws SQLException {

    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {

    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {

    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {

    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {

    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {

    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {

    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {

    }

    @Override
    public void setClob(String parameterName, Reader reader) throws SQLException {

    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {

    }

    @Override
    public void setNClob(String parameterName, Reader reader) throws SQLException {

    }

    @Override
    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        return null;
    }

    @Override
    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        return null;
    }

    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#registerOutParameter
    */
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        dbCstm.registerOutParameter(parameterIndex, sqlType);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#registerOutParameter
    */
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        dbCstm.registerOutParameter(parameterName, sqlType);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#registerOutParameter
    */
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        dbCstm.registerOutParameter(parameterName, sqlType, typeName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#registerOutParameter
    */
    public void registerOutParameter(int paramIndex, int sqlType, int scale) throws SQLException {
        dbCstm.registerOutParameter(paramIndex, sqlType, scale);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#registerOutParameter
    */
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        dbCstm.registerOutParameter(parameterName, sqlType, scale);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#registerOutParameter
    */
    public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
        dbCstm.registerOutParameter(paramIndex, sqlType, typeName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setAsciiStream
    */
    public void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        dbCstm.setAsciiStream(parameterName, x, length);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setBigDecimal
    */
    public void setBigDecimal(String parameterName, java.math.BigDecimal x) throws SQLException {
        dbCstm.setBigDecimal(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setBinaryStream
    */
    public void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        dbCstm.setBinaryStream(parameterName, x, length);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setBoolean
    */
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        dbCstm.setBoolean(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setByte
    */
    public void setByte(String parameterName, byte x) throws SQLException {
        dbCstm.setByte(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setBytes
    */
    public void setBytes(String parameterName, byte[] x) throws SQLException {
        dbCstm.setBytes(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setCharacterStream
    */
    public void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws SQLException {
        dbCstm.setCharacterStream(parameterName, reader, length);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setDate
    */
    public void setDate(String parameterName, Date x) throws SQLException {
        dbCstm.setDate(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setDate
    */
    public void setDate(String parameterName, Date x, java.util.Calendar cal) throws SQLException {
        dbCstm.setDate(parameterName, x, cal);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setDouble
    */
    public void setDouble(String parameterName, double x) throws SQLException {
        throw new SQLException("not supported");
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setFloat
    */
    public void setFloat(String parameterName, float x) throws SQLException {
        dbCstm.setFloat(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setInt
    */
    public void setInt(String parameterName, int x) throws SQLException {
        dbCstm.setInt(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setLong
    */
    public void setLong(String parameterName, long x) throws SQLException {
        dbCstm.setLong(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setNull
    */
    public void setNull(String parameterName, int sqlType) throws SQLException {
        dbCstm.setNull(parameterName, sqlType);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setNull
    */
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        dbCstm.setNull(parameterName, sqlType, typeName);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setObject
    */
    public void setObject(String parameterName, Object x) throws SQLException {
        dbCstm.setObject(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setObject
    */
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        dbCstm.setObject(parameterName, x, targetSqlType);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setObject
    */
   public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        dbCstm.setObject(parameterName, x, targetSqlType);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setShort
    */
    public void setShort(String parameterName, short x) throws SQLException {
        dbCstm.setShort(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setString
    */
    public void setString(String parameterName, String x) throws SQLException {
        dbCstm.setString(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setTime
    */
    public void setTime(String parameterName, Time x) throws SQLException {
        dbCstm.setTime(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setTime
    */
    public void setTime(String parameterName, Time x, java.util.Calendar cal) throws SQLException {
        dbCstm.setTime(parameterName, x, cal);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setTimestamp
    */
    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        dbCstm.setTimestamp(parameterName, x);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setTimestamp
    */
    public void setTimestamp(String parameterName, Timestamp x, java.util.Calendar cal) throws SQLException {
        dbCstm.setTimestamp(parameterName, x, cal);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#setURL
    */
    public void setURL(String parameterName, java.net.URL val) throws SQLException {
        dbCstm.setURL(parameterName, val);
    }
    
    /**
    * Implements method in interface java.sql.CallableStatement
    * @see CallableStatement#wasNull
    */
    public boolean wasNull() throws SQLException {
        return dbCstm.wasNull();
    }
}