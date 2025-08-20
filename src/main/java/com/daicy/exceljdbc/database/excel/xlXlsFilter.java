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
package com.daicy.exceljdbc.database.excel;

import java.io.File;
import java.io.FilenameFilter;
/**
 * For type XLS: accept documents where extension is .xls (
 * https://excel.jsdiff.com/servlets/ProjectIssues issue 33 )
*/
public class xlXlsFilter implements FilenameFilter {
    protected static final String XLS = ".xls";

    public boolean accept(File dir, String name) {
        File file = new File(dir, name);
        // 接受目录或者.xls文件
        if (file.isDirectory()) {
            return true;
        }
        String ext = "";
        if (name.length() > 3) {
            ext = name.substring(name.length() - 4, name.length());
        }
        return (ext.equalsIgnoreCase(XLS));
    }
}
