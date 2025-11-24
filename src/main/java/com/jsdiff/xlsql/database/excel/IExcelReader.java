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
/*
 * IExcelReader.java
 *
 * Created on 10 september 2025, 15:49
 */
package com.jsdiff.xlsql.database.excel;

import com.jsdiff.xlsql.database.*;

import java.io.File;


/**
 * IExcelReader - Excel读取器接口
 * 
 * <p>该接口定义了读取Excel工作簿的标准方法。
 * 实现类负责从指定目录读取所有Excel文件并创建相应的工作簿对象。</p>
 * 
 * @author daichangya
 */
public interface IExcelReader {
    /**
     * 读取目录中的所有工作簿
     * 
     * <p>扫描指定目录下的所有Excel文件，为每个文件创建工作簿对象。</p>
     * 
     * @param dir 包含Excel文件的目录对象
     * @throws xlDatabaseException 如果读取失败则抛出异常
     */
    public void readWorkbooks(File dir) throws xlDatabaseException;
}