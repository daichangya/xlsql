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
package com.daicy.exceljdbc.database.excel;

import com.daicy.exceljdbc.database.*;
import com.daicy.exceljdbc.database.sql.ASqlSelect;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
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
    protected static final String XLS = ".xls";

    /**
     * Creates a new xlWorkbook object.
     * 
     * @param dir directory where workbooks are stored
     * @param name name of workbook excluding extension
     * @throws xlDatabaseException DOCUMENT ME!
     */
    public xlWorkbook(File dir, String name) throws xlDatabaseException {
        super(dir, name);
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
        return new File(directory.getPath() + File.separator + relativePath + XLS);
    }


    protected void readFiles() throws xlDatabaseException {
        Workbook wb = null;

        try {
            wb = Workbook.getWorkbook(getWorkbookFile());

            String[] names = wb.getSheetNames();

            for (int i = 0; i < names.length; i++) {
                xlSheet obj = new xlSheet(directory, subFolderName, names[i]);
                files.put(names[i].toUpperCase(), obj);

                if (obj.isValid()) {
                    validfiles.put(names[i].toUpperCase(), obj);
                }
            }
        } catch (IOException ioe) {
            logger.warning("xlSQL: -xls> io ERR on:" + 
                           getWorkbookFile().getPath() + " , NOT mounted.");
        } catch (jxl.read.biff.BiffException jxlr) {
            logger.warning("xlSQL: -xls> jxl ERR on:" + 
                           getWorkbookFile().getPath() + " , NOT mounted.");
        }
    }

    protected void close(ASqlSelect select) throws xlDatabaseException {
        sqlSelect = select;
        WritableWorkbook wbOut;
       if (bDirty[ADD]) {
            //A schema has been added so we'll create a new workbook
            try {
                wbOut = Workbook.createWorkbook(getWorkbookFile());

                // execute xlSheet.close() for all sheet objects
                Iterator i = validfiles.values().iterator();

                while (i.hasNext()) {
                    xlSheet ws;
                    ws = (xlSheet) i.next();
                    ws.close(wbOut, select);
                }

                //
                wbOut.write();
                wbOut.close();
                bDirty[ADD] = false;
                logger.info(getWorkbookFile().getPath() + " created. ");
            } catch (IOException ioe) {
                logger.severe(getWorkbookFile().getPath() + " NOT created. " + 
                              "VERIFY datasource integrity. (IOException: " + ioe.getMessage() + ")");
            } catch (WriteException we) {
            	logger.severe(getWorkbookFile().getPath() + " NOT created. " + 
                		"VERIFY datasource integrity. (WriteException: " + we.getMessage() + ")");
			}
        } else if (bDirty[UPDATE]) {
            //modifications have been logged for this workbook
            //Create copy workbook with extension *.xls_ 
            Workbook wb = null;

            try {
                wb = Workbook.getWorkbook(getWorkbookFile());

                File tmp = new File(getWorkbookFile().getName() + "_");
                wbOut = Workbook.createWorkbook(tmp, wb);

                // execute xlSheet.close() for all sheet objects
                Iterator i = validfiles.values().iterator();

                while (i.hasNext()) {
                    xlSheet ws;
                    ws = (xlSheet) i.next();
                    ws.close(wbOut, select);
                }


                //Write a copy first, then remove original, finally rename
                wbOut.write();
                wbOut.close();
                getWorkbookFile().delete();
                tmp.renameTo(getWorkbookFile());
                logger.info(getWorkbookFile().getPath() + " re-created. ");
            } catch (IOException ioe) {
                throw new xlDatabaseException("xlSQL: -xls> ERR: io");
            } catch (WriteException we) {
            	new xlDatabaseException("xlSQL: -xls> ERR: writeException");
            } catch (jxl.read.biff.BiffException jxlr) {
                throw new xlDatabaseException("xlSQL: -xls> ERR: jxl");
            }
        } else if (bDirty[DELETE]) {
            //all sheets have been dropped for this workbook: delete file
            if (getWorkbookFile().delete()) {
                logger.info(getWorkbookFile().getPath() + " deleted. ");
            } else {
                logger.warning(getWorkbookFile().getPath() + " NOT deleted. " + 
                              "VERIFY datasource integrity.");
            }
        }
    }
}