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

import java.io.File;
import java.util.Optional;

import com.jsdiff.xlsql.database.excel.xlDatabase;
import com.jsdiff.xlsql.database.excel.xlExporter;

/**
 * xlDatabaseFactory - 数据库对象工厂类
 * 
 * <p>该类提供工厂方法用于创建不同类型的数据库实例。
 * 当前支持Excel数据库（xls和xlsx格式）。</p>
 * 
 * @author daichangya
 */
public class xlDatabaseFactory {
    /**
     * 根据指定类型创建数据库实例
     * 
     * <p>当前支持的类型：</p>
     * <ul>
     *   <li>"xls" - Excel 97-2003格式</li>
     *   <li>"xlsx" - Excel 2007+格式</li>
     * </ul>
     * 
     * @param dir 包含数据库文件的目录
     * @param type 要创建的数据库类型（如"xls"或"xlsx"）
     * @return 数据库实例
     * @throws xlDatabaseException 如果数据库创建失败则抛出异常
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
                    // 创建Excel数据库实例
                    return new xlDatabase(dir);
                } catch (xlDatabaseException e) {
                    throw new RuntimeException(e);
                }
            })
            .orElseThrow(() -> new IllegalArgumentException("Unsupported database type: " + type));
    }
    
    /**
     * 为指定目录创建导出器实例
     * 
     * @param dir 要导出的目录
     * @return 导出器实例
     * @throws xlDatabaseException 如果导出器创建失败则抛出异常
     */
    public static AExporter createExporter(File dir) throws xlDatabaseException { 
        if (dir == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }
        // 创建Excel导出器
        return new xlExporter(dir);
    }

    /**
     * 使用指定的实例配置创建数据库实例
     * 
     * @param dir 包含数据库文件的目录
     * @param instance 配置实例
     * @return 数据库实例
     * @throws xlDatabaseException 如果数据库创建失败则抛出异常
     */
    public static ADatabase createDatabase(File dir, xlInstance instance) throws xlDatabaseException {
        if (dir == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }
        // 创建Excel数据库实例
        return new xlDatabase(dir);
    }
}
