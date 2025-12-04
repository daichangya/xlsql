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
 * xlHsqldbSelect - HSQLDB SQL查询实现
 * 
 * <p>该类实现了ASqlSelect抽象类，专门用于HSQLDB数据库的查询操作。
 * 它使用下划线分隔工作簿和工作表名称（如：workbook_sheet），生成HSQLDB特定的SQL语句来查询Excel数据。</p>
 * 
 * @author  daichangya
 */
public class xlHsqldbSelect extends ASqlSelect {
    
    /**
     * 创建xlHsqldbSelect实例
     * 
     * @param con HSQLDB JDBC连接对象
     */
    public xlHsqldbSelect(Connection con) {
        super(con);
    }
    
    /**
     * 查询数据（HSQLDB实现）
     * 
     * <p>生成HSQLDB特定的SQL语句：
     * - 如果工作簿名为"sa"（系统默认），则查询格式为：SELECT * FROM sheet
     * - 否则查询格式为：SELECT * FROM workbook_sheet</p>
     * 
     * @param workbook 工作簿名称（Excel文件名）
     * @param sheet 工作表名称
     * @return 查询结果集
     * @throws SQLException 如果查询失败则抛出异常
     */
    public ResultSet QueryData(String workbook, String sheet) 
                                                        throws SQLException {
        Statement s = jdbc.createStatement();
        String sql;
        // HSQLDB使用下划线分隔，无需引号
        if (workbook.equalsIgnoreCase("sa")) {
            // 系统默认模式，只使用工作表名
            sql = "SELECT * FROM " + sheet;
        } else {
            // 使用"工作簿_工作表"格式
            sql = "SELECT * FROM " + workbook + "_" + sheet;
        }
        return s.executeQuery(sql);
    }
}