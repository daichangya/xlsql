/*
 * x l S Q L  
 * (c) daichangya, xlsql.jsdiff.com
 * See XLSQL-license.txt for license details
 *
 */
package com.jsdiff.xlsql.jdbc;

import java.sql.ParameterMetaData;
import java.sql.SQLException;

/**
 * xlParameterMetaData - xlSQL参数元数据实现
 * 
 * <p>该类实现了JDBC ParameterMetaData接口，作为后端数据库参数元数据的包装器。
 * 所有方法都委托给后端数据库的参数元数据对象，用于获取预编译语句中参数的元数据信息。</p>
 * 
 * @author daichangya
 */
public class xlParameterMetaData implements ParameterMetaData {
    /** 关联的xlPreparedStatement对象 */
    private xlPreparedStatement xlPstm;
    /** 后端数据库的参数元数据对象 */
    private ParameterMetaData dbPsMeta;
    
    /**
     * 创建参数元数据实例
     * 
     * @param pstm 关联的xlPreparedStatement对象
     * @param psmeta 后端数据库的ParameterMetaData对象
     */
    protected xlParameterMetaData(xlPreparedStatement pstm, 
                                            ParameterMetaData psmeta) {
        xlPstm = pstm;
        dbPsMeta = psmeta;
    }

    /**
     * 获取指定参数的Java类名
     * 
     * @param param 参数索引（从1开始）
     * @return 参数的Java类名
     * @throws SQLException 如果获取失败则抛出异常
     * @see ParameterMetaData#getParameterClassName
     */
    public String getParameterClassName(int param) throws SQLException {
        return dbPsMeta.getParameterClassName(param);
    }

    /**
     * 获取预编译语句中的参数数量
     * 
     * @return 参数数量
     * @throws SQLException 如果获取失败则抛出异常
     * @see ParameterMetaData#getParameterCount
     */
    public int getParameterCount() throws SQLException {
        return dbPsMeta.getParameterCount();
    }

    /**
     * 获取指定参数的模式（IN、OUT或INOUT）
     * 
     * @param param 参数索引（从1开始）
     * @return 参数模式（ParameterMetaData.parameterModeIn、parameterModeOut等）
     * @throws SQLException 如果获取失败则抛出异常
     * @see ParameterMetaData#getParameterMode
     */
    public int getParameterMode(int param) throws SQLException {
        return dbPsMeta.getParameterMode(param);
    }

    /**
     * 获取指定参数的SQL类型
     * 
     * @param param 参数索引（从1开始）
     * @return 参数的SQL类型（java.sql.Types中的常量）
     * @throws SQLException 如果获取失败则抛出异常
     * @see ParameterMetaData#getParameterType
     */
    public int getParameterType(int param) throws SQLException {
        return dbPsMeta.getParameterType(param);
    }

    /**
     * 获取指定参数的类型名称
     * 
     * @param param 参数索引（从1开始）
     * @return 参数的类型名称
     * @throws SQLException 如果获取失败则抛出异常
     * @see ParameterMetaData#getParameterTypeName
     */
    public String getParameterTypeName(int param) throws SQLException {
        return dbPsMeta.getParameterTypeName(param);
    }

    /**
     * 获取指定参数的精度
     * 
     * @param param 参数索引（从1开始）
     * @return 参数的精度
     * @throws SQLException 如果获取失败则抛出异常
     * @see ParameterMetaData#getPrecision
     */
    public int getPrecision(int param) throws SQLException {
        return dbPsMeta.getPrecision(param);
    }

    /**
     * 获取指定参数的小数位数
     * 
     * @param param 参数索引（从1开始）
     * @return 参数的小数位数
     * @throws SQLException 如果获取失败则抛出异常
     * @see ParameterMetaData#getScale
     */
    public int getScale(int param) throws SQLException {
        return dbPsMeta.getScale(param);
    }

    /**
     * 检查指定参数是否允许NULL值
     * 
     * @param param 参数索引（从1开始）
     * @return 参数的可空性（ParameterMetaData.parameterNoNulls、parameterNullable或parameterNullableUnknown）
     * @throws SQLException 如果检查失败则抛出异常
     * @see ParameterMetaData#isNullable
     */
    public int isNullable(int param) throws SQLException {
        return dbPsMeta.isNullable(param);
    }

    /**
     * 检查指定参数是否为有符号数
     * 
     * @param param 参数索引（从1开始）
     * @return 如果参数为有符号数则返回true
     * @throws SQLException 如果检查失败则抛出异常
     * @see ParameterMetaData#isSigned
     */
    public boolean isSigned(int param) throws SQLException {
        return dbPsMeta.isSigned(param);
    }

    /**
     * 解包对象为指定接口类型（JDBC 4.0特性）
     * 
     * @param iface 接口类型
     * @return 解包后的对象，如果不支持则返回null
     * @throws SQLException 如果解包失败则抛出异常
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    /**
     * 检查对象是否为指定接口的包装器（JDBC 4.0特性）
     * 
     * @param iface 接口类型
     * @return 如果对象是包装器则返回true，否则返回false
     * @throws SQLException 如果检查失败则抛出异常
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
