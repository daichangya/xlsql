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
package com.jsdiff.xlsql.database.excel;

import java.io.File;
import java.util.Map;

import com.jsdiff.xlsql.database.AReader;
import com.jsdiff.xlsql.database.ASubFolder;
import com.jsdiff.xlsql.database.xlDatabaseException;
import com.jsdiff.xlsql.database.excel.io.ModernExcelReader;



/**
 * xlReader - Excel读取器实现类
 * 
 * <p>该类继承自AReader，专门用于读取Excel文件。
 * 它实现了IExcelReader和IExcelStore接口，使用ModernExcelReader来读取工作簿。</p>
 * 
 * @author daichangya
 */
public class xlReader extends AReader implements IExcelReader, IExcelStore {
    /**
     * 创建xlReader对象
     * 
     * @param dir Excel工作簿所在的根目录
     * @throws xlDatabaseException 如果对象无法实例化则抛出异常
     */
    public xlReader(File dir) throws xlDatabaseException {
        super(dir);
    }

    /**
     * 读取子文件夹（Excel文件）
     * 
     * @param dir 目录路径
     * @throws xlDatabaseException 如果读取失败则抛出异常
     */
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
        return super.getSubfolders();
    }

    /**
     * 实现IExcelReader接口：读取Excel工作簿
     * 
     * <p>使用ModernExcelReader来读取目录下的所有Excel文件。</p>
     * 
     * @param dir 存储Excel工作簿的目录
     * @throws xlDatabaseException 如果发生错误则抛出异常
     */
    @Override
    public void readWorkbooks(File dir) throws xlDatabaseException {
        // 使用现代Excel读取器读取工作簿
        IExcelReader reader = new ModernExcelReader(this);
        reader.readWorkbooks(dir);
    }
}