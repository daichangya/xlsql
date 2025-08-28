/*
 * x l S Q L  
 * (c) daichangya, excel.jsdiff.com
 * See xlSQL-license.txt for license details
 *
 */
package com.jsdiff.xlsql.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;


public class xlSavepoint implements Savepoint {

    xlConnection xlCon;
    Savepoint dbSave;
    
    //~ Constructors �����������������������������������������������������������

    /** Creates a new instance of SavepointImpl */
    public xlSavepoint(xlConnection con, Savepoint save) {
        xlCon = con;
        dbSave = save;
    }

    //~ Methods ����������������������������������������������������������������

    /**
    * Implements method in interface java.sql.Connection
    * @see Savepoint#getSavepointId
    */
    public int getSavepointId() throws SQLException {
        return dbSave.getSavepointId();
    }

    /**
    * Implements method in interface java.sql.Connection
    * @see Savepoint#getSavepointName
    */
    public String getSavepointName() throws SQLException {
        return dbSave.getSavepointName();
    }
}