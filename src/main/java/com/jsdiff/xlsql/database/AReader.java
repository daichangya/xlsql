/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://xlsql.jsdiff.com
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
import java.util.Map;


/**
 * AReader - 读取器抽象基类
 * 
 * <p>该类继承自AFolder，提供了读取数据源的功能。
 * 子类需要实现具体的读取逻辑，用于从数据源中获取列名、列类型、数据值等信息。</p>
 * 
 * @version $Revision: 1.4 $
 * @author $author$
 */
public abstract class AReader extends AFolder {
    /**
     * 创建读取器对象
     * 
     * @param dir 数据源存储的根目录
     * @throws xlDatabaseException 如果数据库错误发生则抛出异常
     */
    public AReader(File dir) throws xlDatabaseException {
        super(dir);
    }

    /**
     * 获取列名数组
     * 
     * <p>从指定的文档中获取所有列的名称。</p>
     * 
     * @param subfolder 文档的模式类型标识符（对应Excel文件名）
     * @param docname 文档名称（对应Excel工作表名）
     * 
     * @return 列名数组（假设不为null或空）
     * 
     * @throws IllegalArgumentException 如果子文件夹或文档不存在则抛出异常
     */
    public String[] getColumnNames(String subfolder, String docname) {
        String[] ret = { "" };
        String subfolderU = toUpperCase(subfolder);
        String docnameU = toUpperCase(docname);

        ASubFolder wb = getSubfolder(subfolderU);
        if (wb != null) {

            Map<String, AFile> files = wb.getFiles();
            if (files.containsKey(docnameU)) {
                AFile doc = (AFile) files.get(docnameU);

                if (doc.isValid()) {
                    ret = doc.getColumnNames();
                } else {
                    throw new IllegalArgumentException(NOARGS);
                }
            } else {
                throw new IllegalArgumentException(NOARGS);
            }
        } else {
            throw new IllegalArgumentException(NOARGS);
        }

        return ret;
    }

    /**
     * 获取列类型数组
     * 
     * <p>从指定的文档中获取所有列的数据类型。</p>
     * 
     * @param subfolder 文档的模式类型标识符（对应Excel文件名）
     * @param docname 文档名称（对应Excel工作表名）
     * 
     * @return 列类型数组（SQL类型名称）
     * 
     * @throws IllegalArgumentException 如果子文件夹或文档不存在则抛出异常
     */
    public String[] getColumnTypes(String subfolder, String docname) {
        String[] ret = { "" };
        String subfolderU = toUpperCase(subfolder);
        String docnameU = toUpperCase(docname);

        ASubFolder wb = getSubfolder(subfolderU);
        if (wb != null) {

            Map<String, AFile> files = wb.getFiles();
            if (files.containsKey(docnameU)) {
                AFile doc = (AFile) files.get(docnameU);

                if (doc.isValid()) {
                    ret = doc.getColumnTypes();
                } else {
                    throw new IllegalArgumentException(NOARGS);
                }
            } else {
                throw new IllegalArgumentException(NOARGS);
            }
        } else {
            throw new IllegalArgumentException(NOARGS);
        }

        return ret;
    }

    /**
     * 获取行数
     * 
     * <p>获取指定文档的数据行数（不包括标题行）。</p>
     * 
     * @param subfolder 文档的模式类型标识符（对应Excel文件名）
     * @param docname 文档名称（对应Excel工作表名）
     * 
     * @return 数据行数（不包括标题行）
     * 
     * @throws IllegalArgumentException 如果子文件夹或文档不存在则抛出异常
     */
    public int getRows(String subfolder, String docname) {
        int ret = 0;
        String subfolderU = toUpperCase(subfolder);
        String docnameU = toUpperCase(docname);

        ASubFolder wb = getSubfolder(subfolderU);
        if (wb != null) {

            Map<String, AFile> files = wb.getFiles();
            if (files.containsKey(docnameU)) {
                AFile doc = (AFile) files.get(docnameU);

                if (doc.isValid()) {
                    // 减去标题行
                    ret = doc.getRows() - 1;
                } else {
                    throw new IllegalArgumentException(NOARGS);
                }
            } else {
                throw new IllegalArgumentException(NOARGS);
            }
        } else {
            throw new IllegalArgumentException(NOARGS);
        }

        return ret;
    }

    /**
     * 获取所有模式（工作簿）名称
     * 
     * <p>返回所有子文件夹的名称数组，使用Java 8 Stream API实现。</p>
     * 
     * @return 模式名称数组
     */
    public String[] getSchemas() {
        String[] ret = getSubfolders().values().stream()
            .map(wb -> ((ASubFolder) wb).getSubFolderName())
            .toArray(String[]::new);

        return ret;
    }

    /**
     * 获取指定模式（工作簿）下的所有表（工作表）名称
     * 
     * @param subfolder 文档的模式类型标识符（对应Excel文件名）
     * 
     * @return 表名称数组
     * 
     * @throws IllegalArgumentException 如果子文件夹不存在则抛出异常
     */
    public String[] getTables(String subfolder) {
        String[] ret = { "" };
        String subfolderU = subfolder.toUpperCase();

        if (getSubfolders().containsKey(subfolderU)) {
            ASubFolder wb = (ASubFolder) getSubfolders().get(subfolderU);
            String[] t = (String[]) wb.validfiles.keySet()
                                                 .toArray(new String[0]);
            String[] tables = new String[t.length];

            for (int i = 0; i < t.length; i++) {
                AFile doc = (AFile) wb.validfiles.get(t[i]);
                tables[i] = doc.getSName();
            }

            ret = tables;
        } else {
            throw new IllegalArgumentException(NOARGS);
        }

        return ret;
    }

    /**
     * 将字符串转换为大写（安全处理null）
     *
     * @param str 要转换的字符串
     * @return 大写字符串，如果输入为null则返回null
     */
    private String toUpperCase(String str) {
        return str != null ? str.toUpperCase() : null;
    }

    /**
     * 根据大写名称获取子文件夹
     *
     * @param subfolderU 子文件夹名称（大写）
     * @return 子文件夹对象，如果不存在则返回null
     */
    private ASubFolder getSubfolder(String subfolderU) {
        return (ASubFolder) getSubfolders().get(subfolderU);
    }

    /**
     * 获取数据值矩阵
     * 
     * <p>从指定的文档中获取所有数据值，返回二维字符串数组。
     * 第一维是列，第二维是行。</p>
     * 
     * @param subfolder 文档的模式类型标识符（对应Excel文件名）
     * @param docname 文档名称（对应Excel工作表名）
     * 
     * @return 数据值矩阵（String[列][行]）
     * 
     * @throws xlDatabaseException 如果读取数据时发生错误则抛出异常
     * @throws IllegalArgumentException 如果子文件夹或文档不存在则抛出异常
     */
    public String[][] getValues(String subfolder, String docname)
                         throws xlDatabaseException {
        String[][] ret = 
        {
            { "" }
        };
        String subfolderU = toUpperCase(subfolder);
        String docnameU = toUpperCase(docname);

        ASubFolder wb = getSubfolder(subfolderU);
        if (wb != null) {

            Map<String, AFile> files = wb.getFiles();
            if (files.containsKey(docnameU)) {
                AFile doc = (AFile) files.get(docnameU);

                if (doc.isValid()) {
                    ret = doc.getValues();
                } else {
                    throw new IllegalArgumentException(NOARGS);
                }
            } else {
                throw new IllegalArgumentException(NOARGS);
            }
        } else {
            throw new IllegalArgumentException(NOARGS);
        }

        return ret;
    }
}