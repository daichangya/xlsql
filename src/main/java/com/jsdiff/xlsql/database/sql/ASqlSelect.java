/*
 * xlSqlWriter.java
 *
 * Created on 5 juli 2025, 10:32
 */

package com.jsdiff.xlsql.database.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author  daichangya
 */

public abstract class ASqlSelect {
    
    protected Connection jdbc;
    
    /** Creates a new instance of xlSqlSelect */
    public ASqlSelect(Connection con) {
        jdbc = con;
    }
    
    public abstract ResultSet QueryData(String workbook, String sheet)
                                                            throws SQLException;
}