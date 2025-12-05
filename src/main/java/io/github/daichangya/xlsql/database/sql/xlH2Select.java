/*
 * xlH2Select.java
 *
 * Created on 2025-01-XX
 *
 * H2数据库的SQL查询实现，与HSQLDB高度兼容
 */

package io.github.daichangya.xlsql.database.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * xlH2Select - H2数据库SQL查询实现
 * 
 * <p>该类实现了ASqlSelect抽象类，专门用于H2数据库的查询操作。
 * H2使用下划线分隔工作簿和工作表名称（如：workbook_sheet），无需双引号。
 * 它生成H2特定的SQL语句来查询Excel数据。</p>
 * 
 * @author  daichangya
 */
public class xlH2Select extends ASqlSelect {
    
    /**
     * 创建xlH2Select实例
     * 
     * @param con H2 JDBC连接对象
     */
    public xlH2Select(Connection con) {
        super(con);
    }
    
    /**
     * 查询数据（H2实现）
     * 
     * <p>生成H2特定的SQL语句：
     * - 如果工作簿名为"sa"（系统默认），则查询格式为：SELECT * FROM sheet
     * - 否则查询格式为：SELECT * FROM workbook_sheet
     * 
     * H2使用下划线分隔工作簿和工作表名称，无需双引号，类似MySQL实现。</p>
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
        // H2使用下划线分隔，无需引号
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

