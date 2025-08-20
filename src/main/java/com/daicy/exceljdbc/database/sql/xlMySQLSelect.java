/*
 * xlQueryHsqldbForExcel.java
 *
 * Created on 9 juli 2025, 13:20
 */

package com.daicy.exceljdbc.database.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author  daichangya
 */
public class xlMySQLSelect extends ASqlSelect {
    
    
    /** Creates a new instance of xlMySQLSelect */
    public xlMySQLSelect(Connection con) {
        super(con);
    }
    
    public ResultSet QueryData(String workbook, String sheet) 
                                                        throws SQLException {
        Statement s = jdbc.createStatement();
        String sql = "SELECT * FROM " + workbook + "." + sheet;
        return s.executeQuery(sql);
    }
}