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
package io.github.daichangya.xlsql.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Utility class for Excel file operations using Apache POI
 * 
 * @author daichangya
 */
public class ExcelUtils {
    private static final Logger LOGGER = Logger.getLogger(ExcelUtils.class.getName());
    
    /** Default VARCHAR column type with length */
    private static final String DEFAULT_VARCHAR_TYPE = "VARCHAR(255)";
    
    /** Maximum number of rows to analyze for type inference */
    private static final int MAX_ROWS_FOR_TYPE_ANALYSIS = 100;
    
    /**
     * Determines if a file is an Excel file
     * 
     * @param file The file to check
     * @return true if the file is an Excel file
     */
    public static boolean isExcelFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        
        String name = file.getName().toLowerCase();
        return name.endsWith(".xls") || name.endsWith(".xlsx");
    }
    
    /**
     * Determines if a file is an Excel 2007+ file (.xlsx)
     * 
     * @param file The file to check
     * @return true if the file is an Excel 2007+ file
     */
    public static boolean isXlsxFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        
        return file.getName().toLowerCase().endsWith(".xlsx");
    }
    
    /**
     * Gets the sheet names from an Excel file
     * 
     * @param file The Excel file
     * @return Array of sheet names
     * @throws IOException If reading fails
     */
    public static String[] getSheetNames(File file) throws IOException {
        try (Workbook workbook = openWorkbook(file)) {
            int sheetCount = workbook.getNumberOfSheets();
            String[] names = new String[sheetCount];
            
            for (int i = 0; i < sheetCount; i++) {
                names[i] = workbook.getSheetName(i);
            }
            
            return names;
        }
    }
    
    /**
     * Gets the column names from the first row of a sheet
     * 
     * @param file The Excel file
     * @param sheetName The sheet name
     * @return Array of column names
     * @throws IOException If reading fails
     */
    public static String[] getColumnNames(File file, String sheetName) throws IOException {
        try (Workbook workbook = openWorkbook(file)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                return new String[0];
            }
            
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return new String[0];
            }
            
            int cellCount = headerRow.getLastCellNum();
            String[] columnNames = new String[cellCount];
            
            for (int i = 0; i < cellCount; i++) {
                Cell cell = headerRow.getCell(i);
                columnNames[i] = getCellValueAsString(cell);
                
                // If column name is empty, use a default name
                if (columnNames[i] == null || columnNames[i].trim().isEmpty()) {
                    columnNames[i] = "Column" + (i + 1);
                }
            }
            
            return columnNames;
        }
    }
    
    /**
     * Gets the data from a sheet as a 2D array
     * 
     * @param file The Excel file
     * @param sheetName The sheet name
     * @param includeHeader Whether to include the header row
     * @return 2D array of data
     * @throws IOException If reading fails
     */
    public static String[][] getSheetData(File file, String sheetName, boolean includeHeader) throws IOException {
        try (Workbook workbook = openWorkbook(file)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                return new String[0][0];
            }
            
            int startRow = includeHeader ? 0 : 1;
            int rowCount = sheet.getPhysicalNumberOfRows() - startRow;
            if (rowCount <= 0) {
                return new String[0][0];
            }
            
            // Determine the maximum number of columns
            int maxColumns = 0;
            for (int i = startRow; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getLastCellNum() > maxColumns) {
                    maxColumns = row.getLastCellNum();
                }
            }
            
            String[][] data = new String[rowCount][maxColumns];
            
            for (int i = startRow; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < maxColumns; j++) {
                        Cell cell = row.getCell(j);
                        data[i - startRow][j] = getCellValueAsString(cell);
                    }
                }
            }
            
            return data;
        }
    }
    
    /**
     * Infers the SQL data types from the data in a sheet
     * 
     * @param file The Excel file
     * @param sheetName The sheet name
     * @return Array of SQL data types
     * @throws IOException If reading fails
     */
    public static String[] inferColumnTypes(File file, String sheetName) throws IOException {
        try (Workbook workbook = openWorkbook(file)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null || sheet.getPhysicalNumberOfRows() <= 1) {
                return new String[0];
            }
            
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return new String[0];
            }
            
            int cellCount = headerRow.getLastCellNum();
            String[] columnTypes = new String[cellCount];
            
            // Initialize all columns as VARCHAR
            for (int i = 0; i < cellCount; i++) {
                columnTypes[i] = DEFAULT_VARCHAR_TYPE;
            }
            
            // Analyze data rows to infer types
            int maxRowsToAnalyze = Math.min(MAX_ROWS_FOR_TYPE_ANALYSIS, sheet.getPhysicalNumberOfRows() - 1);
            for (int i = 1; i <= maxRowsToAnalyze; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                for (int j = 0; j < cellCount; j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) continue;
                    
                    // Update column type based on cell type
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                columnTypes[j] = "TIMESTAMP";
                            } else {
                                double value = cell.getNumericCellValue();
                                if (value == Math.floor(value) && !Double.isInfinite(value)) {
                                    // Integer value
                                    if (columnTypes[j].equals(DEFAULT_VARCHAR_TYPE)) {
                                        columnTypes[j] = "INTEGER";
                                    }
                                } else {
                                    // Decimal value
                                    columnTypes[j] = "DOUBLE";
                                }
                            }
                            break;
                        case BOOLEAN:
                            if (columnTypes[j].equals(DEFAULT_VARCHAR_TYPE)) {
                                columnTypes[j] = "BOOLEAN";
                            }
                            break;
                        case STRING:
                            // If we find a string in a numeric column, revert to VARCHAR
                            if (!columnTypes[j].equals(DEFAULT_VARCHAR_TYPE)) {
                                columnTypes[j] = DEFAULT_VARCHAR_TYPE;
                            }
                            break;
                        default:
                            // Keep as VARCHAR for other types
                            break;
                    }
                }
            }
            
            return columnTypes;
        }
    }
    
    /**
     * Opens an Excel workbook based on file extension
     * 
     * @param file The Excel file
     * @return Workbook instance
     * @throws IOException If opening fails
     */
    private static Workbook openWorkbook(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            if (file.getName().toLowerCase().endsWith(".xlsx")) {
                return new XSSFWorkbook(fis);
            } else {
                return new HSSFWorkbook(fis);
            }
        }
    }
    
    /**
     * Gets a cell value as a string
     * 
     * @param cell The cell
     * @return String representation of the cell value
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value) && !Double.isInfinite(value)) {
                        return String.valueOf((long)value);
                    } else {
                        return String.valueOf(value);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    try {
                        return cell.getStringCellValue();
                    } catch (Exception ex) {
                        return "#FORMULA_ERROR#";
                    }
                }
            default:
                return "";
        }
    }
}