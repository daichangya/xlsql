/*zthinker.com

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
package com.jsdiff.xlsql.database.excel.io;

import com.jsdiff.xlsql.database.*;
import com.jsdiff.xlsql.database.excel.*;
import com.jsdiff.xlsql.util.ExcelUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Modern implementation of Excel reader using Java 8+ features
 * 
 * @author daichangya
 */
public class ModernExcelReader implements IExcelReader {
    private static final Logger LOGGER = Logger.getLogger(ModernExcelReader.class.getName());
    private final IExcelStore store;
    private final Map<String, ASubFolder> processedWorkbooks = new ConcurrentHashMap<>();

    /**
     * Creates a new Excel reader
     *
     * @param store The store to add workbooks to
     * @throws xlDatabaseException If initialization fails
     */    
    public ModernExcelReader(IExcelStore store) throws xlDatabaseException {
        this.store = store;
    }

    /**
     * Reads all Excel workbooks from the specified directory
     *
     * @param dir Directory containing Excel files
     * @throws xlDatabaseException If reading fails
     */
    @Override
    public void readWorkbooks(File dir) throws xlDatabaseException {
        if (dir == null || !dir.exists()) {
            throw new xlDatabaseException("Directory does not exist: " + dir);
        }
        
        if (!dir.isDirectory()) {
            throw new xlDatabaseException("Not a directory: " + dir);
        }
        
        try {
            // Use Java 8 Files.walk to recursively find all Excel files
            try (Stream<Path> paths = Files.walk(dir.toPath())) {
                List<File> excelFiles = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(ExcelUtils::isExcelFile)
                    .collect(Collectors.toList());
                
                // Process all Excel files in parallel
                excelFiles.parallelStream().forEach(this::processExcelFile);
                
                // Add all processed workbooks to the store
                processedWorkbooks.forEach(store::addStore);
                
                LOGGER.info("Processed " + excelFiles.size() + " Excel files");
            }
        } catch (IOException e) {
            throw new xlDatabaseException("Failed to read workbooks: " + e.getMessage() );
        }
    }
    
    /**
     * Processes a single Excel file
     * 
     * @param file The Excel file to process
     */
    private void processExcelFile(File file) {
        try {
            xlXlsInfo info = xlXlsInfo.createXlXlsInfo(file);
            String upperCaseName = info.getName().toUpperCase();
            
            if (!processedWorkbooks.containsKey(upperCaseName)) {
                ASubFolder workbook = new xlWorkbook(file, info.getName());
                processedWorkbooks.put(upperCaseName, workbook);
                LOGGER.info("Added workbook: " + file.getAbsolutePath());
            } else {
                LOGGER.warning("Duplicate workbook name: " + upperCaseName + " - " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to process Excel file: " + file, e);
        }
    }
}