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

import com.jsdiff.xlsql.database.*;
import com.jsdiff.xlsql.database.excel.io.ModernExcelReader;

import java.io.File;
import java.util.Map;



/**
 * Extends AFolder for Excel
 * 
 * @author daichangya
 */
public class xlDirectory extends AFolder implements IExcelReader, IExcelStore {
    /**
     * Creates a new xlFolderXls object.
     * 
     * @param dir relative root dir of workbooks
     * 
     * @throws xlDatabaseException when this object cannot be instantiated
     */
    public xlDirectory(File dir) throws xlDatabaseException {
        super(dir);
    }

    protected void readSubFolders(File dir) throws xlDatabaseException {
        readWorkbooks(dir);
    }

    /**
     * Implements IExcelStore
     * 
     * @return Map containing xlWorkbook objects
     */
    public Map getStore() {
        return super.getSubfolders();
    }

    /**
     * Implements IExcelReader
     * 
     * @param dir directory where workbooks are stored
     * 
     * @throws xlDatabaseException if an error occurs
     */
    public void readWorkbooks(File dir) throws xlDatabaseException {
        IExcelReader reader = new ModernExcelReader(this);
        reader.readWorkbooks(dir);
    }
}