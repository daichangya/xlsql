/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://xlsql.jsdiff.com
   daichangya@163.com

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by the Free 
 Software Foundation; either version 2 of the License, or (at your option) 
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public 
 License along with this program; if not, write to the Free Software 
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package com.jsdiff.xlsql.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * TestDataFileGenerator - 测试数据文件生成器
 * 
 * <p>用于在测试前生成所需的Excel测试数据文件。
 * 这些文件将被放置在项目根目录，供集成测试使用。</p>
 * 
 * @author daichangya
 */
public class TestDataFileGenerator {

    /**
     * 生成所有测试数据文件
     * 
     * @param baseDir 基础目录（通常是项目根目录）
     * @throws IOException 如果生成失败
     */
    public static void generateAllTestFiles(String baseDir) throws IOException {
        generateTest1Xls(baseDir);
        generateTest2Xls(baseDir);
        generateTest3Xls(baseDir);
        System.out.println("测试数据文件生成完成！");
    }

    /**
     * 生成test1.xls文件
     * 
     * <p>包含Sheet1工作表，有以下列：
     * <ul>
     *   <li>a: 字符串列（value1, value2, value3等）</li>
     *   <li>b: 字符串列（data1, data2, data3等）</li>
     *   <li>id: 整数列（1, 2, 3等）</li>
     *   <li>age: 整数列（25, 30, 35等）</li>
     *   <li>salary: 数值列（5000.0, 6000.0, 7000.0等）</li>
     * </ul>
     * </p>
     */
    public static void generateTest1Xls(String baseDir) throws IOException {
        File file = new File(baseDir, "test1.xls");
        
        try (HSSFWorkbook workbook = new HSSFWorkbook();
             FileOutputStream out = new FileOutputStream(file)) {
            
            Sheet sheet = workbook.createSheet("Sheet1");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"a", "b", "id", "age", "salary"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 创建数据行（1000行数据，用于大数据集测试）
            int rowCount = 1000;
            String[] valuePrefixes = {"value1", "value2", "value3", "value4", "value5"};
            
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1);
                
                // a列：循环使用value1-value5
                Cell cellA = row.createCell(0);
                cellA.setCellValue(valuePrefixes[rowIndex % valuePrefixes.length]);
                
                // b列：data + 行号
                Cell cellB = row.createCell(1);
                cellB.setCellValue("data" + (rowIndex + 1));
                
                // id列：行号（从1开始）
                Cell cellId = row.createCell(2);
                cellId.setCellValue(rowIndex + 1);
                
                // age列：25-35之间的循环值
                Cell cellAge = row.createCell(3);
                cellAge.setCellValue(25 + (rowIndex % 11));
                
                // salary列：5000.0-7000.0之间的循环值
                Cell cellSalary = row.createCell(4);
                cellSalary.setCellValue(5000.0 + (rowIndex % 200) * 10.0);
            }
            
            workbook.write(out);
            System.out.println("已生成: " + file.getAbsolutePath());
        }
    }

    /**
     * 生成test2.xls文件
     * 
     * <p>包含Sheet1工作表，用于JOIN测试，有以下列：
     * <ul>
     *   <li>id: 整数列（与test1.xls的id关联）</li>
     *   <li>b: 字符串列（用于JOIN测试）</li>
     *   <li>name: 字符串列（部门名称等）</li>
     * </ul>
     * </p>
     */
    public static void generateTest2Xls(String baseDir) throws IOException {
        File file = new File(baseDir, "test2.xls");
        
        try (HSSFWorkbook workbook = new HSSFWorkbook();
             FileOutputStream out = new FileOutputStream(file)) {
            
            Sheet sheet = workbook.createSheet("Sheet1");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"id", "b", "name"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 创建数据行（500行数据，部分id与test1.xls匹配，用于JOIN测试）
            int rowCount = 500;
            String[] departments = {"Department1", "Department2", "Department3", "Department4", "Department5"};
            
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1);
                
                // id列：1-500（与test1.xls的id部分匹配）
                Cell cellId = row.createCell(0);
                cellId.setCellValue(rowIndex + 1);
                
                // b列：join_data + 行号
                Cell cellB = row.createCell(1);
                cellB.setCellValue("join_data" + (rowIndex + 1));
                
                // name列：循环使用部门名称
                Cell cellName = row.createCell(2);
                cellName.setCellValue(departments[rowIndex % departments.length]);
            }
            
            workbook.write(out);
            System.out.println("已生成: " + file.getAbsolutePath());
        }
    }

    /**
     * 生成test3.xls文件
     * 
     * <p>包含Sheet1工作表，用于多表JOIN测试</p>
     */
    public static void generateTest3Xls(String baseDir) throws IOException {
        File file = new File(baseDir, "test3.xls");
        
        try (HSSFWorkbook workbook = new HSSFWorkbook();
             FileOutputStream out = new FileOutputStream(file)) {
            
            Sheet sheet = workbook.createSheet("Sheet1");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"id", "c", "description"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 创建数据行（200行数据，用于多表JOIN测试）
            int rowCount = 200;
            
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1);
                
                // id列：1-200
                Cell cellId = row.createCell(0);
                cellId.setCellValue(rowIndex + 1);
                
                // c列：extra_data + 行号
                Cell cellC = row.createCell(1);
                cellC.setCellValue("extra_data" + (rowIndex + 1));
                
                // description列：Description + 行号
                Cell cellDesc = row.createCell(2);
                cellDesc.setCellValue("Description" + (rowIndex + 1));
            }
            
            workbook.write(out);
            System.out.println("已生成: " + file.getAbsolutePath());
        }
    }

    /**
     * 清理测试数据文件
     * 
     * @param baseDir 基础目录（测试数据文件在baseDir/database目录下）
     */
    public static void cleanupTestFiles(String baseDir) {
        String databaseDir = baseDir + File.separator + "database";
        String[] files = {"test1.xls", "test2.xls", "test3.xls"};
        for (String fileName : files) {
            File file = new File(databaseDir, fileName);
            if (file.exists() && file.delete()) {
                System.out.println("已删除: " + file.getAbsolutePath());
            }
        }
    }

    /**
     * 主方法：用于手动生成测试数据文件
     * 
     * <p>默认生成到项目根目录下的database目录</p>
     */
    public static void main(String[] args) {
        String baseDir = System.getProperty("user.dir");
        String databaseDir = baseDir + File.separator + "database";
        
        // 确保database目录存在
        File dbDir = new File(databaseDir);
        if (!dbDir.exists()) {
            if (!dbDir.mkdirs()) {
                System.err.println("无法创建database目录: " + databaseDir);
                return;
            }
        }
        
        try {
            generateAllTestFiles(databaseDir);
        } catch (IOException e) {
            System.err.println("生成测试数据文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

