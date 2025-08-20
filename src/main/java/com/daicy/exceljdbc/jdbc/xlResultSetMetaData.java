/*
 * x l S Q L  
 * (c) daichangya, excel.jsdiff.com
 * See xlSQL-license.txt for license details
 *
 */
package com.daicy.exceljdbc.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;


public class xlResultSetMetaData implements ResultSetMetaData {

    private ResultSetMetaData dbRsMeta;
    private xlResultSet xlRs;
    //~ Constructors �����������������������������������������������������������

    /**
    * Constructs a new StatementImpl object.
    *
    */
    public xlResultSetMetaData(xlResultSet rs, ResultSetMetaData rsmeta) {
        xlRs = rs;
        dbRsMeta = rsmeta;
    }

    //~ Methods ����������������������������������������������������������������

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#getCatalogName
    */
    public String getCatalogName(int column) throws SQLException {
        return dbRsMeta.getCatalogName(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#getColumnClassName
    */
    public String getColumnClassName(int column) throws SQLException {
        return dbRsMeta.getColumnClassName(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#getColumnCount
    */
    public int getColumnCount() throws SQLException {
        return dbRsMeta.getColumnCount();
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#getColumnDisplaySize
    */
    public int getColumnDisplaySize(int column) throws SQLException {
        return dbRsMeta.getColumnDisplaySize(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#getColumnLabel
    */
    public String getColumnLabel(int column) throws SQLException {
        return dbRsMeta.getColumnLabel(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#getColumnName
    */
    public String getColumnName(int column) throws SQLException {
        return dbRsMeta.getColumnName(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#getColumnType
    */
    public int getColumnType(int column) throws SQLException {
        return dbRsMeta.getColumnType(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#getColumnTypeName
    */
    public String getColumnTypeName(int column) throws SQLException {
        return dbRsMeta.getColumnTypeName(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#getPrecision
    */
    public int getPrecision(int column) throws SQLException {
        return dbRsMeta.getPrecision(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#getScale
    */
    public int getScale(int column) throws SQLException {
        return dbRsMeta.getScale(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#getSchemaName
    */
    public String getSchemaName(int column) throws SQLException {
        return dbRsMeta.getSchemaName(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#getTableName
    */
    public String getTableName(int column) throws SQLException {
        return dbRsMeta.getTableName(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#isAutoIncrement
    */
    public boolean isAutoIncrement(int column) throws SQLException {
        return dbRsMeta.isAutoIncrement(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#isCaseSensitive
    */
    public boolean isCaseSensitive(int column) throws SQLException {
        return dbRsMeta.isCaseSensitive(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#isCurrency
    */
    public boolean isCurrency(int column) throws SQLException {
        return dbRsMeta.isCurrency(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#isDefinitelyWritable
    */
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return dbRsMeta.isDefinitelyWritable(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#isNullable
    */
    public int isNullable(int column) throws SQLException {
        return dbRsMeta.isNullable(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#isReadOnly
    */
    public boolean isReadOnly(int column) throws SQLException {
        return dbRsMeta.isReadOnly(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#isSearchable
    */
    public boolean isSearchable(int column) throws SQLException {
        return dbRsMeta.isSearchable(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#isSigned
    */
    public boolean isSigned(int column) throws SQLException {
        return dbRsMeta.isSigned(column);
    }

    /**
    * Implements method in interface java.sql.ResultSetMetaData
    * @see ResultSetMetaData#isWritable
    */
    public boolean isWritable(int column) throws SQLException {
        return dbRsMeta.isWritable(column);
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