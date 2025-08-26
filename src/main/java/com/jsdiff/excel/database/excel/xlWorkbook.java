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
package com.jsdiff.excel.database.excel;

import com.jsdiff.excel.database.*;
import com.jsdiff.excel.database.sql.ASqlSelect;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;


/**
 * Represents a schema in the Excel context: the workbook
 * 
 * @version $Revision: 1.15 $
 * @author $author$
 *
 * Changed by Csongor Nyulas (csny): adapted to jexcel API version 2.6.6
 */
public class xlWorkbook extends ASubFolder {

    /**
     * Creates a new xlWorkbook object.
     * 
     * @param file where workbooks are stored
     * @param name name of workbook excluding extension
     * @throws xlDatabaseException DOCUMENT ME!
     */
    public xlWorkbook(File file, String name) throws xlDatabaseException {
        super(file, name);
    }

    /**
     * Creates a new xlWorkbook object.
     * 
     * @param dir directory where workbooks are stored
     * @param name name of workbook excluding extension
     * @param dirty DOCUMENT ME!
     */
    public xlWorkbook(File dir, String name, boolean dirty) {
        super(dir, name, dirty);
    }

    // 在 xlWorkbook.java 中添加方法
    protected File getWorkbookFile() {
        return getFile();
    }


    protected void readFiles() throws xlDatabaseException {
        Workbook wb =  null;
        try {
            if (FileType.XLSX.equals(getFileType())) {
                wb = new XSSFWorkbook(getWorkbookFile());
            } else if (FileType.XLS.equals(getFileType())) {
                // 需要引入HSSFWorkbook
                wb = new HSSFWorkbook(new FileInputStream(getWorkbookFile()));
            } else {
                logger.warning("xlSQL: Unsupported file format for: " + getWorkbookFile().getPath());
                return;
            }

            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                String name = wb.getSheetName(i);
                xlSheet obj = new xlSheet(getFile(), getSubFolderName(), name);
                files.put(name.toUpperCase(), obj);
                if (obj.isValid()) {
                    validfiles.put(name.toUpperCase(), obj);
                }
            }
        } catch (IOException | InvalidFormatException e) {
            logger.warning("xlSQL: ERR on:" + getWorkbookFile().getPath() + " , NOT mounted.");
        }finally {
            IOUtils.closeQuietly(wb);
        }
    }


    @Override
    protected void close(ASqlSelect select) throws xlDatabaseException {
        sqlSelect = select;
        if (bDirty[ADD] || bDirty[UPDATE]) {
            try (Workbook wb = new XSSFWorkbook()) {
                Iterator i = validfiles.values().iterator();
                while (i.hasNext()) {
                    xlSheet ws = (xlSheet) i.next();
                    ws.close(wb, select);
                }
                try (FileOutputStream out = new FileOutputStream(getWorkbookFile())) {
                    wb.write(out);
                }
                logger.info(getWorkbookFile().getPath() + " created/updated.");
            } catch (IOException e) {
                logger.severe(getWorkbookFile().getPath() + " NOT created/updated. " + e.getMessage());
            }
        } else if (bDirty[DELETE]) {
            if (getWorkbookFile().delete()) {
                logger.info(getWorkbookFile().getPath() + " deleted.");
            } else {
                logger.warning(getWorkbookFile().getPath() + " NOT deleted.");
            }
        }
    }
}