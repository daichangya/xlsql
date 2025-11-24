/*
 * xlQueryHsqldbForExcel.java
 *
 * Created on 9 juli 2025, 13:20
 */

package com.jsdiff.xlsql.database.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * xlMySQLSelect - MySQL SQL查询实现
 * 
 * <p>该类实现了ASqlSelect抽象类，专门用于MySQL数据库的查询操作。
 * 它生成MySQL特定的SQL语句来查询Excel数据。</p>
 * 
 * @author  daichangya
 */
public class xlMySQLSelect extends ASqlSelect {
    
    /**
     * 创建xlMySQLSelect实例
     * 
     * @param con MySQL JDBC连接对象
     */
    public xlMySQLSelect(Connection con) {
        super(con);
    }
    
    /**
     * 查询数据（MySQL实现）
     * 
     * <p>生成MySQL特定的SQL语句，格式为：SELECT * FROM workbook.sheet
     * MySQL使用点号分隔数据库名和表名，不需要引号。</p>
     * 
     * @param workbook 工作簿名称（对应MySQL数据库名）
     * @param sheet 工作表名称（对应MySQL表名）
     * @return 查询结果集
     * @throws SQLException 如果查询失败则抛出异常
     */
    public ResultSet QueryData(String workbook, String sheet) 
                                                        throws SQLException {
        Statement s = jdbc.createStatement();
        // MySQL使用"数据库.表"格式，不需要引号
        String sql = "SELECT * FROM " + workbook + "." + sheet;
        return s.executeQuery(sql);
    }
}