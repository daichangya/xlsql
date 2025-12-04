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
 * ModernExcelReader - 现代Excel读取器实现
 * 
 * <p>该类使用Java 8+特性实现Excel文件读取，包括：</p>
 * <ul>
 *   <li>使用Stream API递归查找所有Excel文件</li>
 *   <li>并行处理多个Excel文件以提高性能</li>
 *   <li>使用ConcurrentHashMap确保线程安全</li>
 *   <li>更好的资源管理和错误处理</li>
 * </ul>
 * 
 * @author daichangya
 */
public class ModernExcelReader implements IExcelReader {
    /** 日志记录器 */
    private static final Logger LOGGER = Logger.getLogger(ModernExcelReader.class.getName());
    /** Excel存储对象，用于存储读取的工作簿 */
    private final IExcelStore store;
    /** 已处理的工作簿映射表（线程安全） */
    private final Map<String, ASubFolder> processedWorkbooks = new ConcurrentHashMap<>();

    /**
     * 创建Excel读取器
     *
     * @param store 用于存储工作簿的存储对象
     * @throws xlDatabaseException 如果初始化失败则抛出异常
     */    
    public ModernExcelReader(IExcelStore store) throws xlDatabaseException {
        this.store = store;
    }

    /**
     * 从指定目录读取所有Excel工作簿
     * 
     * <p>使用Java 8的Files.walk递归查找目录下的所有Excel文件，
     * 然后并行处理这些文件。</p>
     *
     * @param dir 包含Excel文件的目录
     * @throws xlDatabaseException 如果读取失败则抛出异常
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
            // 使用Java 8的Files.walk递归查找所有Excel文件
            try (Stream<Path> paths = Files.walk(dir.toPath())) {
                // 过滤出所有Excel文件
                List<File> excelFiles = paths
                    .filter(Files::isRegularFile)      // 只处理普通文件，排除目录
                    .map(Path::toFile)                  // 将Path转换为File对象
                    .filter(ExcelUtils::isExcelFile)   // 过滤出Excel文件（.xls和.xlsx）
                    .collect(Collectors.toList());
                
                // 并行处理所有Excel文件以提高性能
                excelFiles.parallelStream().forEach(this::processExcelFile);
                
                // 将所有处理过的工作簿添加到存储中
                processedWorkbooks.forEach(store::addStore);
                
                LOGGER.info("Processed " + excelFiles.size() + " Excel files");
            }
        } catch (IOException e) {
            throw new xlDatabaseException("Failed to read workbooks: " + e.getMessage() );
        }
    }
    
    /**
     * 处理单个Excel文件
     * 
     * <p>创建xlXlsInfo对象获取文件信息，然后创建xlWorkbook对象。
     * 如果工作簿名称重复，会记录警告但不抛出异常，确保处理单个文件失败不影响其他文件。</p>
     * 
     * @param file 要处理的Excel文件
     */
    private void processExcelFile(File file) {
        try {
            // 创建Excel文件信息对象，提取文件名和路径信息
            xlXlsInfo info = xlXlsInfo.createXlXlsInfo(file);
            String upperCaseName = info.getName().toUpperCase();
            
            // 检查是否已处理过同名工作簿（使用大写名称作为键）
            if (!processedWorkbooks.containsKey(upperCaseName)) {
                // 创建工作簿对象并添加到已处理列表
                ASubFolder workbook = new xlWorkbook(file, info.getName());
                processedWorkbooks.put(upperCaseName, workbook);
                LOGGER.info("Added workbook: " + file.getAbsolutePath());
            } else {
                // 发现重复的工作簿名称，记录警告但不抛出异常
                LOGGER.warning("Duplicate workbook name: " + upperCaseName + " - " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            // 处理单个文件失败不影响其他文件的处理
            LOGGER.log(Level.WARNING, "Failed to process Excel file: " + file, e);
        }
    }
}