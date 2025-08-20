/*
 * x l S Q L  
 * (c) daichangya, excel.jsdiff.com
 * See xlSQL-license.txt for license details
 *
 */
package com.daicy.exceljdbc.jdbc;

import java.sql.ParameterMetaData;
import java.sql.SQLException;


public class xlParameterMetaData implements ParameterMetaData {
    private xlPreparedStatement xlPstm;
    private ParameterMetaData dbPsMeta;
    
    //~ Constructors �����������������������������������������������������������

    /** Creates a new instance of ParameterMetaData */
    protected xlParameterMetaData(xlPreparedStatement pstm, 
                                            ParameterMetaData psmeta) {
        xlPstm = pstm;
        dbPsMeta = psmeta;
    }

    //~ Methods ����������������������������������������������������������������

    /**
    * Implements method in interface java.sql.ParameterMetaData
    * @see ParameterMetaData#getParameterClassName
    */
    public String getParameterClassName(int param) throws SQLException {
        return dbPsMeta.getParameterClassName(param);
    }

    /**
    * Implements method in interface java.sql.ParameterMetaData
    * @see ParameterMetaData#getParameterCount
    */
    public int getParameterCount() throws SQLException {
        return dbPsMeta.getParameterCount();
    }

    /**
    * Implements method in interface java.sql.ParameterMetaData
    * @see ParameterMetaData#getParameterMode
    */
    public int getParameterMode(int param) throws SQLException {
        return dbPsMeta.getParameterCount();
    }

    /**
    * Implements method in interface java.sql.ParameterMetaData
    * @see ParameterMetaData#getParameterType
    */
    public int getParameterType(int param) throws SQLException {
        return dbPsMeta.getParameterType(param);
    }

    /**
    * Implements method in interface java.sql.ParameterMetaData
    * @see ParameterMetaData#getParameterTypeName
    */
    public String getParameterTypeName(int param) throws SQLException {
        return dbPsMeta.getParameterTypeName(param);
    }

    /**
    * Implements method in interface java.sql.ParameterMetaData
    * @see ParameterMetaData#getPrecision
    */
    public int getPrecision(int param) throws SQLException {
        return dbPsMeta.getPrecision(param);
    }

    /**
    * Implements method in interface java.sql.ParameterMetaData
    * @see ParameterMetaData#getScale
    */
    public int getScale(int param) throws SQLException {
        return dbPsMeta.getScale(param);
    }

    /**
    * Implements method in interface java.sql.ParameterMetaData
    * @see ParameterMetaData#isNullable
    */
    public int isNullable(int param) throws SQLException {
        return dbPsMeta.isNullable(param);
    }

    /**
    * Implements method in interface java.sql.ParameterMetaData
    * @see ParameterMetaData#isSigned
    */
    public boolean isSigned(int param) throws SQLException {
        return dbPsMeta.isSigned(param);
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