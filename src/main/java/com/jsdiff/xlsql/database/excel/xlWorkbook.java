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
package com.jsdiff.xlsql.database.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jsdiff.xlsql.database.ASubFolder;
import com.jsdiff.xlsql.database.FileType;
import com.jsdiff.xlsql.database.xlDatabaseException;
import com.jsdiff.xlsql.database.sql.ASqlSelect;


/**
 * xlWorkbook - Excel工作簿实现类
 * 
 * <p>该类表示Excel文件（工作簿），在xlSQL中对应数据库的模式（schema）。
 * 它管理工作簿中的所有工作表（对应数据库的表）。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>读取Excel文件中的所有工作表</li>
 *   <li>管理工作表的生命周期</li>
 *   <li>支持.xls和.xlsx格式</li>
 * </ul>
 * 
 * @version $Revision: 1.15 $
 * @author $author$
 *
 * Changed by Csongor Nyulas (csny): adapted to jexcel API version 2.6.6
 */
public class xlWorkbook extends ASubFolder {

    /**
     * 创建xlWorkbook对象（读取文件）
     * 
     * @param file Excel文件对象
     * @param name 工作簿名称（不含扩展名）
     * @throws xlDatabaseException 如果读取失败则抛出异常
     */
    public xlWorkbook(File file, String name) throws xlDatabaseException {
        super(file, name);
    }

    /**
     * 创建xlWorkbook对象（不读取文件）
     * 
     * @param dir 工作簿所在的目录
     * @param name 工作簿名称（不含扩展名）
     * @param dirty 脏数据标志
     */
    public xlWorkbook(File dir, String name, boolean dirty) {
        super(dir, name, dirty);
    }

    /**
     * 获取工作簿文件对象
     * 
     * @return Excel文件对象
     */
    protected File getWorkbookFile() {
        return getFile();
    }

    /**
     * 读取工作簿中的所有工作表
     * 
     * <p>根据文件类型（.xls或.xlsx）使用相应的POI类来读取工作簿，
     * 然后遍历所有工作表并创建xlSheet对象。</p>
     * 
     * @throws xlDatabaseException 如果读取失败则抛出异常
     */
    @Override
    protected void readFiles() throws xlDatabaseException {
        Workbook wb =  null;
        try {
            // 根据文件类型创建相应的Workbook对象
            if (FileType.XLSX.equals(getFileType())) {
                // Excel 2007+格式使用XSSFWorkbook
                wb = new XSSFWorkbook(getWorkbookFile());
            } else if (FileType.XLS.equals(getFileType())) {
                // Excel 97-2003格式使用HSSFWorkbook
                wb = new HSSFWorkbook(new FileInputStream(getWorkbookFile()));
            } else {
                logger.warning("xlSQL: Unsupported file format for: " + getWorkbookFile().getPath());
                return;
            }

            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                String name = wb.getSheetName(i);
                xlSheet obj = new xlSheet(getFile(), getSubFolderName(), name);
                addFile(name.toUpperCase(), obj);
                if (obj.isValid()) {
                    addValidFile(name.toUpperCase(), obj);
                }
            }
        } catch (IOException | InvalidFormatException e) {
            logger.warning("xlSQL: ERR on:" + getWorkbookFile().getPath() + " , NOT mounted.");
        }finally {
            IOUtils.closeQuietly(wb);
        }
    }


    /**
     * 关闭工作簿并保存更改
     * 
     * <p>根据脏数据标志执行相应的操作：</p>
     * <ul>
     *   <li>ADD或UPDATE：创建新的工作簿或更新现有工作簿，将所有有效工作表写入文件</li>
     *   <li>DELETE：删除工作簿文件</li>
     * </ul>
     * 
     * @param select SQL查询对象，用于获取要写入的数据
     * @throws xlDatabaseException 如果保存失败则抛出异常
     */
    @Override
    protected void close(ASqlSelect select) throws xlDatabaseException {
        sqlSelect = select;
        // 如果需要添加或更新
        if (bDirty[ADD] || bDirty[UPDATE]) {
            try (Workbook wb = new XSSFWorkbook()) {
                // 遍历所有有效工作表并写入数据
                Iterator i = validfiles.values().iterator();
                while (i.hasNext()) {
                    xlSheet ws = (xlSheet) i.next();
                    ws.close(wb, select);
                }
                // 将工作簿写入文件
                try (FileOutputStream out = new FileOutputStream(getWorkbookFile())) {
                    wb.write(out);
                }
                logger.info(getWorkbookFile().getPath() + " created/updated.");
            } catch (IOException e) {
                logger.severe(getWorkbookFile().getPath() + " NOT created/updated. " + e.getMessage());
            }
        } else if (bDirty[DELETE]) {
            // 如果需要删除，删除工作簿文件
            if (getWorkbookFile().delete()) {
                logger.info(getWorkbookFile().getPath() + " deleted.");
            } else {
                logger.warning(getWorkbookFile().getPath() + " NOT deleted.");
            }
        }
    }
}