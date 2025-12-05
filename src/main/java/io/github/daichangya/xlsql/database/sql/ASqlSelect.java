/*
 * xlSqlWriter.java
 *
 * Created on 5 juli 2025, 10:32
 */

package io.github.daichangya.xlsql.database.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ASqlSelect - SQL查询抽象基类
 * 
 * <p>该类是所有SQL查询实现的抽象基类，用于从后端数据库查询Excel数据。
 * 子类需要实现QueryData方法来执行具体的查询。</p>
 * 
 * @author  daichangya
 */
public abstract class ASqlSelect {
    
    /** JDBC连接对象 */
    protected Connection jdbc;
    
    /**
     * 创建ASqlSelect实例
     * 
     * @param con JDBC连接对象
     */
    public ASqlSelect(Connection con) {
        jdbc = con;
    }
    
    /**
     * 查询数据
     * 
     * <p>从后端数据库中查询指定工作簿和工作表的数据。</p>
     * 
     * @param workbook 工作簿名称（Excel文件名，不含扩展名）
     * @param sheet 工作表名称
     * @return 查询结果集
     * @throws SQLException 如果查询失败则抛出异常
     */
    public abstract ResultSet QueryData(String workbook, String sheet)
                                                            throws SQLException;
}