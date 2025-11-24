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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.jsdiff.xlsql.database.sql.ASqlSelect;


/**
 * ASubFolder - 子文件夹抽象基类
 * 
 * <p>该类表示数据源中的一个子文件夹（对应Excel文件），管理该文件夹下的所有文件（对应Excel工作表）。
 * 子类需要实现具体的文件读取逻辑。</p>
 * 
 * @version $Revision: 1.5 $
 * @author $author$
 */
public abstract class ASubFolder {
    /** 日志记录器 */
    protected static final Logger logger = Logger.getAnonymousLogger();
    /** 操作类型：添加 */
    protected static final int ADD = 0;
    /** 操作类型：更新 */
    protected static final int UPDATE = 1;
    /** 操作类型：删除 */
    protected static final int DELETE = 2;
    /** 文件对象 */
    private File file;
    /** 文件类型（XLS或XLSX） */
    private FileType fileType;
    /** SQL查询对象 */
    protected ASqlSelect sqlSelect;
    /** 文件名称 */
    private String fileName;
    /** 所有文件的映射表 */
    private Map<String, AFile> files = new HashMap<String, AFile>();
    /** 有效文件的映射表（可作为SQL表使用的文件） */
    protected Map<String, AFile> validfiles = new HashMap<String, AFile>();
    /** 脏数据标志数组，索引对应操作类型（ADD/UPDATE/DELETE） */
    protected boolean[] bDirty = new boolean[3];
//    protected String relativePath; // 存储相对路径


    /**
     * 创建子文件夹对象（读取文件）
     * 
     * <p>创建子文件夹并读取其中的所有文件（工作表）。</p>
     * 
     * @param file 文件对象（对应Excel文件）
     * @param name 文件名称（不含扩展名）
     * @throws xlDatabaseException 如果读取文件失败则抛出异常
     */
    public ASubFolder(File file, String name) throws xlDatabaseException {
        this.file = file;
        this.fileName = name;
        this.fileType = FileType.getFileType(file);
        readFiles();
    }

    /**
     * 创建子文件夹对象（不读取文件）
     * 
     * <p>创建子文件夹但不读取文件，用于新建的工作簿。</p>
     * 
     * @param file 文件对象（对应Excel文件）
     * @param name 文件名称（不含扩展名）
     * @param dirty 是否标记为需要添加的标志
     */
    public ASubFolder(File file, String name, boolean dirty) {
        this.file = file;
        this.fileName = name;
        this.fileType = FileType.getFileType(file);
        bDirty[ADD] = dirty;
    }

    /**
     * 设置脏数据标志
     * 
     * <p>标记子文件夹在指定操作类型下是否需要保存。</p>
     * 
     * @param i 操作类型（ADD、UPDATE或DELETE）
     * @param val 脏数据标志值
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
     * 获取所有文件的映射
     * 
     * @return 文件映射表，键为文件名称（大写），值为AFile对象
     */
    public Map<String, AFile> getFiles() {
        return files;
    }

    /**
     * 添加文件到映射表
     * 
     * @param name 文件名称（通常为大写）
     * @param file 文件对象
     */
    public void addFile(String name, AFile file) {
        files.put(name, file);
    }

    /**
     * 获取有效文件的映射
     * 
     * <p>有效文件是指可以作为SQL表使用的文件。</p>
     * 
     * @return 有效文件映射表
     */
    public Map<String, AFile> getValidFiles() {
        return validfiles;
    }

    /**
     * 添加有效文件到映射表
     * 
     * @param name 文件名称（通常为大写）
     * @param file 文件对象
     */
    public void addValidFile(String name, AFile file) {
        validfiles.put(name, file);
    }

    /**
     * 获取子文件夹名称
     * 
     * @return 子文件夹名称（对应Excel文件名，不含扩展名）
     */
    public String getSubFolderName() {
        return fileName;
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
     * 读取文件（抽象方法）
     * 
     * <p>子类需要实现此方法来读取特定类型的文件（如Excel工作表）。</p>
     * 
     * @throws xlDatabaseException 如果读取失败则抛出异常
     */
    protected abstract void readFiles() throws xlDatabaseException;

    /**
     * 关闭子文件夹并清理资源（抽象方法）
     * 
     * <p>子类需要实现此方法来执行特定的清理操作。</p>
     * 
     * @param select SQL查询对象
     * @throws xlDatabaseException 如果关闭失败则抛出异常
     */
    protected abstract void close(ASqlSelect select) throws xlDatabaseException;
}