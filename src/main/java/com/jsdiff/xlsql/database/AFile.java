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
package com.jsdiff.xlsql.database;

import com.jsdiff.xlsql.database.sql.ASqlSelect;

import java.io.File;
import java.util.logging.Logger;


/**
 * Abstract File. Extend for particular use.
 * 
 * @author daichangya
 */
public abstract class AFile {
    protected static final Logger logger = Logger.getAnonymousLogger();
    private File file;
    private FileType fileType;
    private String fileName;
    private String sheetName;
    protected boolean validAsSqlTable;
    protected int columnCount;
    protected int rowCount;
    protected String[] columnNames;
    protected String[] columnTypes;
    protected boolean[] isChanged = new boolean[3];

    protected AFile(File file, String fileName, String sheetName) throws xlDatabaseException {
        this.file = file;
        this.fileName = fileName;
        this.sheetName = sheetName;
        this.fileType = FileType.getFileType(file);
        validAsSqlTable = readFile();
    }

    protected AFile(File file, String fileName, String sheetName, boolean bdirty) {
        this.file = file;
        this.fileName = fileName;
        this.sheetName = sheetName;
        this.fileType = FileType.getFileType(file);
        validAsSqlTable = true;
        rowCount = 1;
        isChanged[xlConstants.ADD] = bdirty;
    }

    protected abstract boolean readFile() throws xlDatabaseException;

    /**
     * DOCUMENT ME!
     *
     * @param subOut DOCUMENT ME!
     * @param select DOCUMENT ME!
     *
     * @throws xlDatabaseException DOCUMENT ME!
     */
    public abstract void close(Object subOut, ASqlSelect select)
                        throws xlDatabaseException;
    
    public abstract String[][] getValues() throws xlDatabaseException;
    
    /**
     * Setter for property rows.
     */
    void addRow() {
        rowCount++;
    }
    
    /**
     * Getter for property columns.
     *
     * @return Value of property columns.
     */
    String[] getColumnNames() {
        return this.columnNames;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    boolean getIsChanged(int i) {
        return isChanged[i];
    }
    
    /**
     * Getter for property rows.
     *
     * @return Value of property rows.
     */
    int getRows() {
        return rowCount;
    }
    
    /**
     * Getter for property sName.
     *
     * @return Value of property sName.
     */
    String getSName() {
        return sheetName;
    }
    
    /**
     * Getter for property types.
     *
     * @return Value of property types.
     */
    String[] getColumnTypes() {
        return this.columnTypes;
    }
    
    /**
     * Getter for property valid.
     *
     * @return Value of property valid.
     */
    public boolean isValid() {
        return validAsSqlTable;
    }
    
    /**
     * Getter for property bDirty.
     *
     * @param i DOCUMENT ME!
     * @param val DOCUMENT ME!
     */
    public void setIsChanged(int i, boolean val) {
        isChanged[i] = val;
    }

    public File getFile() {
        return file;
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSheetName() {
        return sheetName;
    }
}
