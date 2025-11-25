/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://excel.jsdiff.com
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
package com.jsdiff.xlsql.database;

import com.jsdiff.xlsql.database.excel.*;

import java.io.File;
import java.util.Optional;

/**
 * Factory class for creating database objects
 * 
 * @author daichangya
 */
public class xlDatabaseFactory {
    /**
     * Creates a database instance based on the specified type
     * 
     * @param dir Directory containing the database files
     * @param type Type of database to create (e.g., "xls")
     * @return A database instance
     * @throws xlDatabaseException If database creation fails
     */
    public static ADatabase create(File dir, String type) throws xlDatabaseException {
        if (dir == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        
        // 使用Java 8的Optional来处理可能为null的情况
        return Optional.of(type.toLowerCase())
            .filter(t -> t.equals("xls") || t.equals("xlsx"))
            .map(t -> {
                try {
                    return new xlDatabase(dir);
                } catch (xlDatabaseException e) {
                    throw new RuntimeException(e);
                }
            })
            .orElseThrow(() -> new IllegalArgumentException("Unsupported database type: " + type));
    }
    
    /**
     * Creates an exporter for the specified directory
     * 
     * @param dir Directory to export from
     * @return An exporter instance
     * @throws xlDatabaseException If exporter creation fails
     */
    public static AExporter createExporter(File dir) throws xlDatabaseException { 
        if (dir == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }
        return new xlExporter(dir);
    }

    /**
     * Creates a database instance with the specified instance configuration
     * 
     * @param dir Directory containing the database files
     * @param instance Configuration instance
     * @return A database instance
     * @throws xlDatabaseException If database creation fails
     */
    public static ADatabase createDatabase(File dir, xlInstance instance) throws xlDatabaseException {
        if (dir == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }
        return new xlDatabase(dir);
    }
}
