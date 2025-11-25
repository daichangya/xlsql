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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * AFolder - 文件夹抽象基类
 * 
 * <p>该类是所有文件夹操作的抽象基类，用于管理数据源目录结构。
 * 它维护子文件夹的映射，子类需要实现readSubFolders方法来读取子文件夹。</p>
 * 
 * @version $Revision: 1.4 $
 * @author $author$
 */
public abstract class AFolder {
    /** 目录文件对象 */
    protected File directory;
    /** 日志记录器 */
    protected static final Logger logger = Logger.getAnonymousLogger();
    /** 错误消息：参数不存在 */
    protected static final String NOARGS = "xlSQL: no such argument(s).";
    /** 子文件夹映射表，键为文件夹名称（大写），值为ASubFolder对象 */
    private Map<String,ASubFolder> subfolders = new HashMap<String,ASubFolder>();

    /**
     * 创建文件夹对象
     * 
     * <p>初始化文件夹并读取子文件夹。子类需要实现readSubFolders方法来读取具体的子文件夹。</p>
     * 
     * @param dir 数据源存储的根目录
     * @throws xlDatabaseException 如果数据库错误发生则抛出异常
     */
    public AFolder(File dir) throws xlDatabaseException {
        directory = dir;
        readSubFolders(dir);
    }

    /**
     * 读取子文件夹（抽象方法）
     * 
     * <p>子类需要实现此方法来读取特定类型的子文件夹（如Excel工作簿）。</p>
     * 
     * @param dir 要读取的目录
     * @throws xlDatabaseException 如果读取失败则抛出异常
     */
    protected abstract void readSubFolders(File dir) throws xlDatabaseException;

    /**
     * 获取所有子文件夹的映射
     * 
     * @return 子文件夹映射表，键为文件夹名称（大写），值为ASubFolder对象
     */
    public Map<String,ASubFolder> getSubfolders() {
        return subfolders;
    }

    /**
     * 添加子文件夹到映射表
     * 
     * @param name 子文件夹名称（通常为大写）
     * @param subfolder 子文件夹对象
     */
    public void addSubfolders(String name, ASubFolder subfolder) {
        subfolders.put(name, subfolder);
    }
}