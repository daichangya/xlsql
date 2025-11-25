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
package com.jsdiff.xlsql.database.excel;

import com.jsdiff.xlsql.database.*;
import com.jsdiff.xlsql.database.excel.io.ModernExcelReader;
import com.jsdiff.xlsql.database.excel.io.ModernExcelReader;
import com.jsdiff.xlsql.database.xlDatabaseException;

import java.io.File;
import java.util.Map;




/**
 * xlDatabase - Excel数据库实现类
 * 
 * <p>该类实现了ADatabase抽象类，专门用于处理Excel文件。
 * 它将Excel文件目录视为数据库，Excel文件视为模式（schema），工作表视为表（table）。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>扫描指定目录下的Excel文件</li>
 *   <li>读取Excel文件的工作表结构</li>
 *   <li>提供工厂方法创建xlWorkbook和xlSheet对象</li>
 * </ul>
 * 
 * @author daichangya
 */
public class xlDatabase extends ADatabase implements IExcelReader, IExcelStore {

    /**
     * 创建xlDatabase对象
     * 
     * <p>初始化Excel数据库，扫描指定目录下的所有Excel文件。</p>
     * 
     * @param dir Excel文件所在的根目录
     * @throws xlDatabaseException 如果对象无法实例化则抛出异常
     */
    public xlDatabase(File dir) throws xlDatabaseException {
        super(dir);
    }

    /**
     * 读取子文件夹（Excel文件）
     * 
     * @param dir 目录路径
     * @throws xlDatabaseException 如果读取失败则抛出异常
     */
    @Override
    protected void readSubFolders(File dir) throws xlDatabaseException {
        readWorkbooks(dir);
    }

    /**
     * 实现IExcelStore接口：获取存储的Excel工作簿映射
     * 
     * @return 包含xlWorkbook对象的映射表
     */
    @Override
    public Map<String, ASubFolder> getStore() {
        return getSubfolders();
    }

    /**
     * 实现IExcelReader接口：读取Excel工作簿
     * 
     * <p>使用ModernExcelReader来读取目录下的所有Excel文件，
     * 支持并行处理和更好的资源管理。</p>
     * 
     * @param dir 存储Excel工作簿的目录
     * @throws xlDatabaseException 如果发生错误则抛出异常
     */
    @Override
    public void readWorkbooks(File dir) throws xlDatabaseException {
        // 使用现代Excel读取器，支持并行处理和更好的资源管理
        IExcelReader reader = new ModernExcelReader(this);
        reader.readWorkbooks(dir);
    }
    
    /**
     * 创建子文件夹（工作簿）对象的工厂方法
     * 
     * @param dir 目录路径
     * @param subfolder 子文件夹名称（Excel文件名，不含扩展名）
     * @return xlWorkbook对象
     */
    @Override
    public ASubFolder subFolderFactory(File dir, String subfolder) {
        return new xlWorkbook(dir, subfolder, true);
    }
    
    /**
     * 创建文件（工作表）对象的工厂方法
     * 
     * @param dir 目录路径
     * @param subfolder 子文件夹名称（Excel文件名）
     * @param file 文件名称（工作表名）
     * @return xlSheet对象
     */
    @Override
    public AFile fileFactory(File dir, String subfolder, String file) {
        return new xlSheet(dir, subfolder, file, true);
    }
}