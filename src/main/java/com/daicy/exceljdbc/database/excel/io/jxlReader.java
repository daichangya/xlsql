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
package com.daicy.exceljdbc.database.excel.io;

import com.daicy.exceljdbc.database.*;
import com.daicy.exceljdbc.database.excel.IExcelReader;
import com.daicy.exceljdbc.database.excel.IExcelStore;
import com.daicy.exceljdbc.database.excel.xlWorkbook;
import com.daicy.exceljdbc.database.excel.xlXlsFilter;
import jxl.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


/**
 * Interface between xlSQL and the JXL library for reading Excel files
 * 
 * @author daichangya
 */
public class jxlReader implements IExcelReader {
    protected static final Logger logger = Logger.getAnonymousLogger();
    private final IExcelStore store;


    /**
     *
     * @param store
     * @throws xlDatabaseException
     */    
    public jxlReader(IExcelStore store) throws xlDatabaseException {
        this.store = store;
    }

    /**
     *
     * @param dir directory
     * @throws xlDatabaseException when an error occurs
     */
    public void readWorkbooks(File dir) throws xlDatabaseException {
        readWorkbooksRecursive(dir, dir);
    }

    private void readWorkbooksRecursive(File rootDir, File currentDir) throws xlDatabaseException {
        File[] files = currentDir.listFiles(new xlXlsFilter());

        if (files == null) {
            throw new xlDatabaseException("jxlReader cannot read from " + currentDir);
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (file.isDirectory()) {
                // 递归处理子目录
                readWorkbooksRecursive(rootDir, file);
            } else if (file.getName().toLowerCase().endsWith(".xls")) {
                // 处理.xls文件
                try {
                    Workbook workbook = Workbook.getWorkbook(file);
                    // 生成相对于根目录的路径作为工作簿名称
                    String relativePath = getRelativePath(rootDir, file);
                    String name = relativePath.substring(0, relativePath.lastIndexOf('.'));
                    // 将路径分隔符替换为下划线或其他合法字符
                    name = name.replace(File.separatorChar, '_');

                    ASubFolder obj = new xlWorkbook(rootDir, name);
                    store.getStore().put(name.toUpperCase(), obj);
                } catch (jxl.read.biff.BiffException jxle) {
                    logger.info(file + "-java excel api reports: '" +
                            jxle.getMessage() + "' ..?");
                    continue;
                } catch (IOException ioe) {
                    logger.warning(file + "-ioe exception: '" +
                            ioe.getMessage() + "' continuing..");
                    continue;
                }
            }
        }
    }

    // 获取相对路径的辅助方法
    private String getRelativePath(File rootDir, File file) {
        String rootPath = rootDir.getAbsolutePath();
        String filePath = file.getAbsolutePath();

        if (filePath.startsWith(rootPath)) {
            String relativePath = filePath.substring(rootPath.length());
            if (relativePath.startsWith(File.separator)) {
                relativePath = relativePath.substring(1);
            }
            return relativePath;
        }
        return file.getName();
    }
}