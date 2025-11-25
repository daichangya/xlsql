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
import java.util.logging.Logger;

import com.jsdiff.xlsql.database.sql.ASqlSelect;


/**
 * AFile - 文件抽象基类
 * 
 * <p>该类表示数据源中的一个文件（对应Excel工作表），管理文件的结构和数据。
 * 子类需要实现readFile方法来读取文件内容。</p>
 * 
 * @author daichangya
 */
public abstract class AFile {
    /** 日志记录器 */
    protected static final Logger logger = Logger.getAnonymousLogger();
    /** 文件对象 */
    private File file;
    /** 文件类型（XLS或XLSX） */
    private FileType fileType;
    /** 文件名称（不含扩展名） */
    private String fileName;
    /** 工作表名称 */
    private String sheetName;
    /** 是否可作为SQL表使用的标志 */
    protected boolean validAsSqlTable;
    /** 列数 */
    protected int columnCount;
    /** 行数 */
    protected int rowCount;
    /** 列名数组 */
    protected String[] columnNames;
    /** 列类型数组 */
    private String[] columnTypes;
    /** 变更标志数组，索引对应操作类型（ADD/UPDATE/DELETE） */
    protected boolean[] isChanged = new boolean[3];

    /**
     * 创建文件对象（读取文件）
     * 
     * <p>创建文件对象并读取文件内容，判断是否可以作为SQL表使用。</p>
     * 
     * @param file 文件对象（对应Excel文件）
     * @param fileName 文件名称（不含扩展名）
     * @param sheetName 工作表名称
     * @throws xlDatabaseException 如果读取文件失败则抛出异常
     */
    protected AFile(File file, String fileName, String sheetName) throws xlDatabaseException {
        this.file = file;
        this.fileName = fileName;
        this.sheetName = sheetName;
        this.fileType = FileType.getFileType(file);
        // 读取文件并判断是否有效
        validAsSqlTable = readFile();
    }

    /**
     * 创建文件对象（不读取文件）
     * 
     * <p>创建文件对象但不读取文件，用于新建的工作表。</p>
     * 
     * @param file 文件对象（对应Excel文件）
     * @param fileName 文件名称（不含扩展名）
     * @param sheetName 工作表名称
     * @param bdirty 是否标记为需要添加的标志
     */
    protected AFile(File file, String fileName, String sheetName, boolean bdirty) {
        this.file = file;
        this.fileName = fileName;
        this.sheetName = sheetName;
        this.fileType = FileType.getFileType(file);
        validAsSqlTable = true;
        rowCount = 1; // 至少有一行（标题行）
        isChanged[xlConstants.ADD] = bdirty;
    }

    /**
     * 读取文件（抽象方法）
     * 
     * <p>子类需要实现此方法来读取特定类型的文件内容。</p>
     * 
     * @return 如果文件可以作为SQL表使用则返回true
     * @throws xlDatabaseException 如果读取失败则抛出异常
     */
    protected abstract boolean readFile() throws xlDatabaseException;

    /**
     * 关闭文件并清理资源（抽象方法）
     * 
     * <p>子类需要实现此方法来执行特定的清理操作，如关闭Workbook等。</p>
     *
     * @param subOut 子文件夹输出对象
     * @param select SQL查询对象
     * @throws xlDatabaseException 如果关闭失败则抛出异常
     */
    public abstract void close(Object subOut, ASqlSelect select)
                        throws xlDatabaseException;
    
    /**
     * 获取数据值矩阵（抽象方法）
     * 
     * <p>子类需要实现此方法来获取文件中的所有数据值。</p>
     * 
     * @return 数据值矩阵（String[列][行]）
     * @throws xlDatabaseException 如果获取数据失败则抛出异常
     */
    public abstract String[][] getValues() throws xlDatabaseException;
    
    /**
     * 添加一行数据
     * 
     * <p>增加行计数器，用于跟踪数据行数。</p>
     */
    void addRow() {
        rowCount++;
    }
    
    /**
     * 获取列名数组
     *
     * @return 列名数组
     */
    String[] getColumnNames() {
        return this.columnNames;
    }
    
    /**
     * 获取指定操作类型的变更标志
     *
     * @param i 操作类型（ADD、UPDATE或DELETE）
     * @return 如果文件在该操作类型下已变更则返回true
     */
    boolean getIsChanged(int i) {
        return isChanged[i];
    }
    
    /**
     * 获取行数
     * 
     * <p>返回文件的总行数（包括标题行）。</p>
     *
     * @return 行数
     */
    int getRows() {
        return rowCount;
    }
    
    /**
     * 获取工作表名称
     *
     * @return 工作表名称
     */
    String getSName() {
        return sheetName;
    }
    
    /**
     * 获取列类型数组
     *
     * @return 列类型数组（SQL类型名称）
     */
    String[] getColumnTypes() {
        return this.columnTypes;
    }

    /**
     * 设置列类型数组
     * 
     * @param columnTypes 列类型数组
     */
    public void setColumnTypes(String[] columnTypes) {
        this.columnTypes = columnTypes;
    }

    /**
     * 检查文件是否可以作为SQL表使用
     * 
     * <p>文件必须包含有效的列名和数据才能作为SQL表使用。</p>
     *
     * @return 如果文件可以作为SQL表使用则返回true
     */
    public boolean isValid() {
        return validAsSqlTable;
    }
    
    /**
     * 设置指定操作类型的变更标志
     *
     * @param i 操作类型（ADD、UPDATE或DELETE）
     * @param val 变更标志值
     */
    public void setIsChanged(int i, boolean val) {
        isChanged[i] = val;
    }

    /**
     * 获取文件对象
     * 
     * @return 文件对象（对应Excel文件）
     */
    public File getFile() {
        return file;
    }

    /**
     * 获取文件类型
     * 
     * @return 文件类型枚举值（XLS或XLSX）
     */
    public FileType getFileType() {
        return fileType;
    }

    /**
     * 获取文件名称
     * 
     * @return 文件名称（不含扩展名）
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 获取工作表名称
     * 
     * @return 工作表名称
     */
    public String getSheetName() {
        return sheetName;
    }
}
