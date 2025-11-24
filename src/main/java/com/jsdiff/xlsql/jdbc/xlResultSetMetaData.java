/*
 * x l S Q L  
 * (c) daichangya, excel.jsdiff.com
 * See xlSQL-license.txt for license details
 *
 */
package com.jsdiff.xlsql.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * xlResultSetMetaData - xlSQL结果集元数据实现
 * 
 * <p>该类实现了JDBC ResultSetMetaData接口，作为后端数据库结果集元数据的包装器。
 * 所有方法都委托给后端数据库的结果集元数据对象，用于获取结果集中列的元数据信息。</p>
 * 
 * @author daichangya
 */
public class xlResultSetMetaData implements ResultSetMetaData {

    /** 后端数据库的结果集元数据对象 */
    private ResultSetMetaData dbRsMeta;
    /** 关联的xlResultSet对象 */
    private xlResultSet xlRs;
    
    /**
     * 创建结果集元数据实例
     * 
     * @param rs 关联的xlResultSet对象
     * @param rsmeta 后端数据库的ResultSetMetaData对象
     */
    public xlResultSetMetaData(xlResultSet rs, ResultSetMetaData rsmeta) {
        xlRs = rs;
        dbRsMeta = rsmeta;
    }

    /**
     * 获取指定列的目录名称
     * 
     * @param column 列索引（从1开始）
     * @return 列的目录名称
     * @throws SQLException 如果获取失败则抛出异常
     * @see ResultSetMetaData#getCatalogName
     */
    public String getCatalogName(int column) throws SQLException {
        return dbRsMeta.getCatalogName(column);
    }

    /**
     * 获取指定列的Java类名
     * 
     * @param column 列索引（从1开始）
     * @return 列的Java类名
     * @throws SQLException 如果获取失败则抛出异常
     * @see ResultSetMetaData#getColumnClassName
     */
    public String getColumnClassName(int column) throws SQLException {
        return dbRsMeta.getColumnClassName(column);
    }

    /**
     * 获取结果集中的列数
     * 
     * @return 列数
     * @throws SQLException 如果获取失败则抛出异常
     * @see ResultSetMetaData#getColumnCount
     */
    public int getColumnCount() throws SQLException {
        return dbRsMeta.getColumnCount();
    }

    /**
     * 获取指定列的显示大小
     * 
     * @param column 列索引（从1开始）
     * @return 列的显示大小
     * @throws SQLException 如果获取失败则抛出异常
     * @see ResultSetMetaData#getColumnDisplaySize
     */
    public int getColumnDisplaySize(int column) throws SQLException {
        return dbRsMeta.getColumnDisplaySize(column);
    }

    /**
     * 获取指定列的标签（显示名称）
     * 
     * @param column 列索引（从1开始）
     * @return 列的标签
     * @throws SQLException 如果获取失败则抛出异常
     * @see ResultSetMetaData#getColumnLabel
     */
    public String getColumnLabel(int column) throws SQLException {
        return dbRsMeta.getColumnLabel(column);
    }

    /**
     * 获取指定列的名称
     * 
     * @param column 列索引（从1开始）
     * @return 列的名称
     * @throws SQLException 如果获取失败则抛出异常
     * @see ResultSetMetaData#getColumnName
     */
    public String getColumnName(int column) throws SQLException {
        return dbRsMeta.getColumnName(column);
    }

    /**
     * 获取指定列的SQL类型
     * 
     * @param column 列索引（从1开始）
     * @return 列的SQL类型（java.sql.Types中的常量）
     * @throws SQLException 如果获取失败则抛出异常
     * @see ResultSetMetaData#getColumnType
     */
    public int getColumnType(int column) throws SQLException {
        return dbRsMeta.getColumnType(column);
    }

    /**
     * 获取指定列的类型名称
     * 
     * @param column 列索引（从1开始）
     * @return 列的类型名称
     * @throws SQLException 如果获取失败则抛出异常
     * @see ResultSetMetaData#getColumnTypeName
     */
    public String getColumnTypeName(int column) throws SQLException {
        return dbRsMeta.getColumnTypeName(column);
    }

    /**
     * 获取指定列的精度
     * 
     * @param column 列索引（从1开始）
     * @return 列的精度
     * @throws SQLException 如果获取失败则抛出异常
     * @see ResultSetMetaData#getPrecision
     */
    public int getPrecision(int column) throws SQLException {
        return dbRsMeta.getPrecision(column);
    }

    /**
     * 获取指定列的小数位数
     * 
     * @param column 列索引（从1开始）
     * @return 列的小数位数
     * @throws SQLException 如果获取失败则抛出异常
     * @see ResultSetMetaData#getScale
     */
    public int getScale(int column) throws SQLException {
        return dbRsMeta.getScale(column);
    }

    /**
     * 获取指定列的模式名称
     * 
     * @param column 列索引（从1开始）
     * @return 列的模式名称
     * @throws SQLException 如果获取失败则抛出异常
     * @see ResultSetMetaData#getSchemaName
     */
    public String getSchemaName(int column) throws SQLException {
        return dbRsMeta.getSchemaName(column);
    }

    /**
     * 获取指定列的表名称
     * 
     * @param column 列索引（从1开始）
     * @return 列的表名称
     * @throws SQLException 如果获取失败则抛出异常
     * @see ResultSetMetaData#getTableName
     */
    public String getTableName(int column) throws SQLException {
        return dbRsMeta.getTableName(column);
    }

    /**
     * 检查指定列是否自动递增
     * 
     * @param column 列索引（从1开始）
     * @return 如果列自动递增则返回true
     * @throws SQLException 如果检查失败则抛出异常
     * @see ResultSetMetaData#isAutoIncrement
     */
    public boolean isAutoIncrement(int column) throws SQLException {
        return dbRsMeta.isAutoIncrement(column);
    }

    /**
     * 检查指定列是否区分大小写
     * 
     * @param column 列索引（从1开始）
     * @return 如果列区分大小写则返回true
     * @throws SQLException 如果检查失败则抛出异常
     * @see ResultSetMetaData#isCaseSensitive
     */
    public boolean isCaseSensitive(int column) throws SQLException {
        return dbRsMeta.isCaseSensitive(column);
    }

    /**
     * 检查指定列是否为货币类型
     * 
     * @param column 列索引（从1开始）
     * @return 如果列为货币类型则返回true
     * @throws SQLException 如果检查失败则抛出异常
     * @see ResultSetMetaData#isCurrency
     */
    public boolean isCurrency(int column) throws SQLException {
        return dbRsMeta.isCurrency(column);
    }

    /**
     * 检查指定列是否绝对可写
     * 
     * @param column 列索引（从1开始）
     * @return 如果列绝对可写则返回true
     * @throws SQLException 如果检查失败则抛出异常
     * @see ResultSetMetaData#isDefinitelyWritable
     */
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return dbRsMeta.isDefinitelyWritable(column);
    }

    /**
     * 检查指定列是否允许NULL值
     * 
     * @param column 列索引（从1开始）
     * @return 列的可空性（ResultSetMetaData.columnNoNulls、columnNullable或columnNullableUnknown）
     * @throws SQLException 如果检查失败则抛出异常
     * @see ResultSetMetaData#isNullable
     */
    public int isNullable(int column) throws SQLException {
        return dbRsMeta.isNullable(column);
    }

    /**
     * 检查指定列是否只读
     * 
     * @param column 列索引（从1开始）
     * @return 如果列只读则返回true
     * @throws SQLException 如果检查失败则抛出异常
     * @see ResultSetMetaData#isReadOnly
     */
    public boolean isReadOnly(int column) throws SQLException {
        return dbRsMeta.isReadOnly(column);
    }

    /**
     * 检查指定列是否可用于WHERE子句
     * 
     * @param column 列索引（从1开始）
     * @return 如果列可用于WHERE子句则返回true
     * @throws SQLException 如果检查失败则抛出异常
     * @see ResultSetMetaData#isSearchable
     */
    public boolean isSearchable(int column) throws SQLException {
        return dbRsMeta.isSearchable(column);
    }

    /**
     * 检查指定列是否为有符号数
     * 
     * @param column 列索引（从1开始）
     * @return 如果列为有符号数则返回true
     * @throws SQLException 如果检查失败则抛出异常
     * @see ResultSetMetaData#isSigned
     */
    public boolean isSigned(int column) throws SQLException {
        return dbRsMeta.isSigned(column);
    }

    /**
     * 检查指定列是否可写
     * 
     * @param column 列索引（从1开始）
     * @return 如果列可写则返回true
     * @throws SQLException 如果检查失败则抛出异常
     * @see ResultSetMetaData#isWritable
     */
    public boolean isWritable(int column) throws SQLException {
        return dbRsMeta.isWritable(column);
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
