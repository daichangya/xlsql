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
package com.jsdiff.excel.database;

import com.jsdiff.excel.database.sql.ASqlSelect;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Abstract SubFolder. Extend for particular use.
 * 
 * @version $Revision: 1.5 $
 * @author $author$
 */
public abstract class ASubFolder {
    protected static final Logger logger = Logger.getAnonymousLogger();
    protected static final int ADD = 0;
    protected static final int UPDATE = 1;
    protected static final int DELETE = 2;
    private File file;
    private FileType fileType;
    protected ASqlSelect sqlSelect;
    private String fileName;;
    protected Map files = new HashMap();
    protected Map validfiles = new HashMap();
    protected boolean[] bDirty = new boolean[3];
//    protected String relativePath; // 存储相对路径


    /**
     * Creates a new xlSubFolder object.
     * 
     * @param file DOCUMENT ME!
     * @param name DOCUMENT ME!
     */
    public ASubFolder(File file, String name) throws xlDatabaseException {
        this.file = file;
        this.fileName = name;
        this.fileType = FileType.getFileType(file);
        readFiles();
    }

    /**
     * Creates a new xlSubFolder object.
     * 
     * @param file DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param dirty DOCUMENT ME!
     */
    public ASubFolder(File file, String name, boolean dirty) {
        this.file = file;
        this.fileName = name;
        this.fileType = FileType.getFileType(file);
        bDirty[ADD] = dirty;
    }

    /**
     * Setter for property bDirty.
     * 
     * @param i DOCUMENT ME!
     * @param val DOCUMENT ME!
     */
    public void setDirty(int i, boolean val) {
        bDirty[i] = val;
        if (i == ADD) {
            bDirty[ADD] = true;            
        }
        else if (i == UPDATE) {
            bDirty[UPDATE] = true;            
        }
        else if (i == DELETE) {
            bDirty[DELETE] = true;            
        }



    }

    /**
     * Getter for property files.
     * 
     * @return Value of property files.
     */
    public Map getFiles() {
        return files;
    }

    /**
     * Getter for property validfiles.
     * 
     * @return Value of property validfiles.
     */
    public Map getValidFiles() {
        return validfiles;
    }

    /**
     * Getter for property subFolderName.
     * 
     * @return Value of property subFolderName.
     */
    public String getSubFolderName() {
        return fileName;
    }

    public File getFile() {
        return file;
    }

    public FileType getFileType() {
        return fileType;
    }

    protected abstract void readFiles() throws xlDatabaseException;

    protected abstract void close(ASqlSelect select) throws xlDatabaseException;
}