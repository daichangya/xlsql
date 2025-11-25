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

import com.jsdiff.xlsql.database.FileType;

import java.io.File;
import java.io.FilenameFilter;

/**
 * xlXlsFilter - Excel文件过滤器
 * 
 * <p>该类实现了FilenameFilter接口，用于过滤出Excel文件（.xls和.xlsx格式）。
 * 目录会被接受，以便递归搜索。</p>
 * 
 * <p>参考：https://excel.jsdiff.com/servlets/ProjectIssues issue 33</p>
 * 
 * @author daichangya
 */
public class xlXlsFilter implements FilenameFilter {

    /**
     * 判断文件或目录是否被接受
     * 
     * <p>接受所有目录（以便递归搜索）和Excel文件（.xls和.xlsx格式）。</p>
     * 
     * @param dir 文件所在目录
     * @param name 文件名
     * @return 如果是目录或Excel文件则返回true，否则返回false
     */
    public boolean accept(File dir, String name) {
        File file = new File(dir, name);
        // 接受所有目录，以便递归搜索
        if (file.isDirectory()) {
            return true;
        }
        // 检查文件扩展名是否为Excel格式
        FileType fileType = null;
        if (name.length() > 3) {
           // 提取文件扩展名并获取文件类型
           fileType = FileType.getByValue(file.getName().substring(file.getName().lastIndexOf(".") + 1));
        }
        // 接受.xls和.xlsx格式的文件
        return FileType.XLS.equals(fileType) || FileType.XLSX.equals(fileType);
    }
}
