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
package com.jsdiff.xlsql.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility class for Excel operations using Java 8 streams
 * 
 * @author daichangya
 */
public class ExcelStreamUtils {
    
    /**
     * Finds all Excel files in a directory and its subdirectories
     * 
     * @param directory The directory to search
     * @return Stream of Excel files
     * @throws IOException If reading fails
     */
    public static Stream<Path> findExcelFiles(Path directory) throws IOException {
        return Files.walk(directory)
            .filter(Files::isRegularFile)
            .filter(path -> {
                String name = path.getFileName().toString().toLowerCase();
                return name.endsWith(".xls") || name.endsWith(".xlsx");
            });
    }
    
    /**
     * Opens a workbook from a file
     * 
     * @param file The Excel file
     * @return Workbook instance
     * @throws IOException If opening fails
     */
    public static Workbook openWorkbook(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            if (file.getName().toLowerCase().endsWith(".xlsx")) {
                return new XSSFWorkbook(fis);
            } else {
                return new HSSFWorkbook(fis);
            }
        }
    }
    
    /**
     * Gets a stream of sheets from a workbook
     * 
     * @param workbook The workbook
     * @return Stream of sheets
     */
    public static Stream<Sheet> getSheetStream(Workbook workbook) {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                workbook.sheetIterator(),
                Spliterator.ORDERED
            ),
            false
        );
    }
    
    /**
     * Gets a stream of rows from a sheet
     * 
     * @param sheet The sheet
     * @return Stream of rows
     */
    public static Stream<Row> getRowStream(Sheet sheet) {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                sheet.rowIterator(),
                Spliterator.ORDERED
            ),
            false
        );
    }
    
    /**
     * Gets a stream of cells from a row
     * 
     * @param row The row
     * @return Stream of cells
     */
    public static Stream<Cell> getCellStream(Row row) {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                row.cellIterator(),
                Spliterator.ORDERED
            ),
            false
        );
    }
    
    /**
     * Gets the header row as a list of strings
     * 
     * @param sheet The sheet
     * @return List of header values
     */
    public static List<String> getHeaderRow(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            return Collections.emptyList();
        }
        
        return getCellStream(headerRow)
            .map(ExcelStreamUtils::getCellValueAsString)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all data rows as a list of maps (column name -> value)
     * 
     * @param sheet The sheet
     * @return List of data rows as maps
     */
    public static List<Map<String, String>> getDataRowsAsMaps(Sheet sheet) {
        List<String> headers = getHeaderRow(sheet);
        if (headers.isEmpty()) {
            return Collections.emptyList();
        }
        
        return getRowStream(sheet)
            .skip(1) // Skip header row
            .map(row -> {
                Map<String, String> rowMap = new HashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i);
                    rowMap.put(headers.get(i), getCellValueAsString(cell));
                }
                return rowMap;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all data rows as a list of lists
     * 
     * @param sheet The sheet
     * @return List of data rows as lists
     */
    public static List<List<String>> getDataRowsAsLists(Sheet sheet) {
        int headerCount = getHeaderRow(sheet).size();
        if (headerCount == 0) {
            return Collections.emptyList();
        }
        
        return getRowStream(sheet)
            .skip(1) // Skip header row
            .map(row -> {
                return IntStream.range(0, headerCount)
                    .mapToObj(i -> getCellValueAsString(row.getCell(i)))
                    .collect(Collectors.toList());
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Filters rows based on a predicate
     * 
     * @param sheet The sheet
     * @param predicate The predicate to filter rows
     * @return List of filtered rows as maps
     */
    public static List<Map<String, String>> filterRows(Sheet sheet, Predicate<Map<String, String>> predicate) {
        return getDataRowsAsMaps(sheet).stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    /**
     * Maps rows using a function
     * 
     * @param sheet The sheet
     * @param mapper The function to map rows
     * @param <R> The result type
     * @return List of mapped rows
     */
    public static <R> List<R> mapRows(Sheet sheet, Function<Map<String, String>, R> mapper) {
        return getDataRowsAsMaps(sheet).stream()
            .map(mapper)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets a cell value as a string
     * 
     * @param cell The cell
     * @return String representation of the cell value
     */
    public static String getCellValueAsString(Cell cell) {
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