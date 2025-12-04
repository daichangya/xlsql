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

import com.jsdiff.xlsql.database.sql.ASqlSelect;


/**
 * ADatabase - 数据库抽象基类
 * 
 * <p>该类是所有数据库实现的抽象基类，继承自AExporter。
 * 提供了数据库操作的基本功能，包括：</p>
 * <ul>
 *   <li>数据行的增删改操作</li>
 *   <li>SQL查询支持</li>
 *   <li>表和列的管理</li>
 * </ul>
 *
 * @version $Revision: 1.11 $
 * @author $author$
 */
public abstract class ADatabase extends AExporter {
    /** 操作类型：添加 */
    private static final int OP_ADD = 0;
    /** 操作类型：更新 */
    private static final int OP_UPDATE = 1;
    /** 操作类型：删除 */
    private static final int OP_DELETE = 2;
    /** SQL查询对象，用于执行SELECT查询 */
    private ASqlSelect sqlSelect;

    /**
     * 创建数据库实例
     *
     * @param dir 数据源存储的根目录
     * @throws xlDatabaseException 如果数据库错误发生则抛出异常
     */
    public ADatabase(File dir) throws xlDatabaseException {
        super(dir);
    }

    /**
     * 向数据库添加一行数据
     * 
     * <p>在指定的文档（表）中添加一个新行。文档必须存在且有效。</p>
     *
     * @param subfolder 文档的模式类型标识符（对应Excel文件名）
     * @param docname 文档名称（对应Excel工作表名）
     * @throws IllegalArgumentException 如果子文件夹或文档不存在则抛出异常
     */
    public void addRow(String subfolder, String docname) {
        String subfolderU = safeToUpperCase(subfolder);
        String docnameU = safeToUpperCase(docname);

        ASubFolder sF = getSubfolder(subfolderU);
        if (sF == null) {
            throw new IllegalArgumentException(NOARGS);
        }

        Map<String, AFile> files = sF.getFiles();
        AFile doc = files.get(docnameU);
        if (doc == null || !doc.isValid()) {
            throw new IllegalArgumentException(NOARGS);
        }

        doc.addRow();
    }

    /**
     * 关闭数据库并清理资源
     * 
     * <p>关闭所有子文件夹（工作簿）并清理SQL查询对象。</p>
     *
     * @param query SQL查询对象
     * @throws xlDatabaseException 如果关闭过程中出现问题则抛出异常
     */
    public void close(ASqlSelect query) throws xlDatabaseException {
        sqlSelect = query;

        // 关闭所有子文件夹
        for (ASubFolder wb : getSubfolders().values()) {
            wb.close(query);
        }
    }

    /**
     * 移除表（标记为删除）
     * 
     * <p>将指定的表标记为删除状态，实际删除操作在关闭时执行。</p>
     *
     * @param subfolder 子文件夹名称（对应Excel文件名）
     * @param docname 文档名称（对应Excel工作表名）
     */
    public void removeTable(String subfolder, String docname) {
        String subfolderU = safeToUpperCase(subfolder);
        String docnameU = safeToUpperCase(docname);

        if (!tableExists(subfolderU, docnameU)) {
            return;
        }

        ASubFolder wb = getSubfolder(subfolderU);
        if (wb == null) {
            return;
        }

        wb.bDirty[OP_ADD] = false;
        wb.bDirty[OP_UPDATE] = false;
        wb.bDirty[OP_DELETE] = true;

        Map<String, AFile> files = wb.getFiles();
        AFile doc = files.get(docnameU);
        if (doc != null && doc.isValid()) {
            doc.setIsChanged(OP_DELETE, true);

            for (AFile s : files.values()) {
                if (!s.getIsChanged(OP_DELETE)) {
                    wb.bDirty[OP_DELETE] = false;
                    wb.bDirty[OP_UPDATE] = true;
                    break;
                }
            }
        }
    }

    /**
     * 检查是否存在不同大小写的模式（工作簿）
     * 
     * <p>检查是否存在与指定名称相同但大小写不同的工作簿。</p>
     *
     * @param workbook 工作簿名称
     * @return 如果存在不同大小写的工作簿则返回true
     */
    public boolean schemaOtherCaseExists(String workbook) {
        String workbookU = safeToUpperCase(workbook);
        ASubFolder wb = getSubfolder(workbookU);
        return wb != null && !workbook.equals(wb.getSubFolderName());
    }

    /**
     * 检查模式（工作簿）是否存在
     * 
     * <p>当前实现始终返回false，因为xlSQL不直接支持模式概念。</p>
     * 
     * @param subfolder 文档的模式类型标识符
     * @return 如果模式存在则返回true（当前始终返回false）
     */
    public boolean schemaExists(String subfolder) {
        return false;
    }

    /**
     * 检查表（文档）是否存在
     * 
     * <p>检查指定的子文件夹和文档是否存在且未被标记为删除。</p>
     *
     * @param subfolder 文档的模式类型标识符（对应Excel文件名）
     * @param docname 文档标识符（对应Excel工作表名）
     * @return 如果文档存在且有效则返回true
     */
    public boolean tableExists(String subfolder, String docname) {
        ASubFolder wb = getSubfolder(subfolder);
        if (wb == null) {
            return false;
        }

        Map<String, AFile> files = wb.getFiles();
        AFile doc = files.get(docname);
        return doc != null && !doc.getIsChanged(OP_DELETE);
    }

    /**
     * 标记模式（工作簿）需要更新
     * 
     * <p>将指定的工作簿标记为需要更新状态。</p>
     *
     * @param subfolder 文档的模式类型标识符（对应Excel文件名）
     * @throws IllegalArgumentException 如果子文件夹不存在则抛出异常
     */
    public void touchSchema(String subfolder) {
        String subfolderU = safeToUpperCase(subfolder);
        ASubFolder wb = getSubfolder(subfolderU);
        if (wb == null) {
            throw new IllegalArgumentException(NOARGS);
        }
        // 标记工作簿为需要更新
        wb.bDirty[xlConstants.UPDATE] = true;
    }

    /**
     * 标记表（文档）需要更新
     * 
     * <p>将指定的文档标记为需要更新状态。</p>
     *
     * @param subfolder 文档的模式类型标识符（对应Excel文件名）
     * @param docname 文档名称（对应Excel工作表名）
     * @throws IllegalArgumentException 如果子文件夹或文档不存在则抛出异常
     */
    public void touchTable(String subfolder, String docname) {
        String subfolderU = safeToUpperCase(subfolder);
        String docnameU = safeToUpperCase(docname);

        ASubFolder wb = getSubfolder(subfolderU);
        if (wb == null) {
            throw new IllegalArgumentException(NOARGS);
        }

        Map<String, AFile> files = wb.getFiles();
        AFile doc = files.get(docnameU);
        if (doc == null || !doc.isValid()) {
            throw new IllegalArgumentException(NOARGS);
        }

        doc.setIsChanged(xlConstants.UPDATE, true);
    }

    /**
     * 添加模式（工作簿）
     * 
     * <p>如果工作簿已存在则标记为需要更新，否则创建新的工作簿。</p>
     *
     * @param workbook 工作簿名称（对应Excel文件名）
     */
    public void addSchema(String workbook) {
        String workbookU = safeToUpperCase(workbook);

        ASubFolder wb = getSubfolder(workbookU);
        if (wb != null) {
            // 工作簿已存在，标记为需要更新
            wb.setDirty(OP_UPDATE, true);
        } else {
            // 创建新的工作簿
            ASubFolder obj = subFolderFactory(directory, workbook);
            addSubfolders(workbookU, obj);
        }
    }

    /**
     * 添加表（工作表）
     * 
     * <p>在指定的工作簿中添加新的工作表。如果工作表已存在则恢复其删除状态。</p>
     *
     * @param workbook 工作簿名称（对应Excel文件名）
     * @param sheet 工作表名称
     * @throws IllegalArgumentException 如果工作簿不存在则抛出异常
     */
    public void addTable(String workbook, String sheet) {
        String workbookU = safeToUpperCase(workbook);
        String sheetU = safeToUpperCase(sheet);

        ASubFolder wb = getSubfolder(workbookU);
        if (wb == null) {
            throw new IllegalArgumentException(NOARGS);
        }

        Map<String, AFile> files = wb.getFiles();
        if (files.containsKey(sheetU)) {
            AFile sh = files.get(sheetU);
            sh.setIsChanged(OP_DELETE, false);
        } else {
            AFile obj = fileFactory(directory, wb.getSubFolderName(), sheet);
            obj.setIsChanged(OP_ADD, true);
            wb.addFile(sheetU, obj);
            wb.addValidFile(sheetU, obj);
        }
    }

    /**
     * 创建子文件夹（工作簿）实例的工厂方法
     * 
     * <p>子类需要实现此方法来创建特定类型的子文件夹对象。</p>
     *
     * @param dir 目录文件对象
     * @param subfolder 子文件夹名称
     * @return 子文件夹实例
     */
    public abstract ASubFolder subFolderFactory(File dir, String subfolder);

    /**
     * 创建文件（工作表）实例的工厂方法
     * 
     * <p>子类需要实现此方法来创建特定类型的文件对象。</p>
     *
     * @param dir 目录文件对象
     * @param subfolder 子文件夹名称（对应Excel文件名）
     * @param file 文件名称（对应Excel工作表名）
     * @return 文件实例
     */
    public abstract AFile fileFactory(File dir, String subfolder, String file);

    /**
     * 安全地将字符串转换为大写
     * 
     * <p>如果字符串为null则返回null，否则返回大写形式。</p>
     *
     * @param str 要转换的字符串
     * @return 大写字符串，如果输入为null则返回null
     */
    private String safeToUpperCase(String str) {
        return str == null ? null : str.toUpperCase();
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
}
