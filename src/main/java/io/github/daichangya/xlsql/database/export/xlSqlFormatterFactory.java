/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://xlsql.jsdiff.com
   daichangya@163.com

 This program is free software; you can redistribute it and/or modify it under 
 the terms of the GNU General Public License as published by the Free Software 
 Foundation; either version 2 of the License, or (at your option) any later 
 version.

 This program is distributed in the hope that it will be useful, 
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License 
 along with this program; if not, write to the Free Software Foundation, 
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
/*
 * xlDatabaseFactory.java
 *
 * Created on 9 augustus 2025, 18:18
 */
package io.github.daichangya.xlsql.database.export;


import io.github.daichangya.xlsql.jdbc.DatabaseType;

/**
 * DOCUMENT ME!
 * 
 * @author daichangya
 */
public class xlSqlFormatterFactory {
    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static ASqlFormatter create(String type) {
        ASqlFormatter ret = null;

        DatabaseType dbType = DatabaseType.fromEngineName(type);
        switch (dbType) {
            case HSQLDB:
                ret = new xlHsqldbFormatter();
                break;
            case H2:
                ret = new xlH2Formatter();
                break;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + type);
        }

        return ret;
    }
}