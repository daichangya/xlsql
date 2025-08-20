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
package com.daicy.exceljdbc.database;

import com.daicy.exceljdbc.database.sql.ASqlSelect;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;


/**
 * Abstract database
 * 
 * @version $Revision: 1.11 $
 * @author $author$
 */
public abstract class ADatabase extends AExporter {
    private static final int ADD = 0;
    private static final int UPDATE = 1;
    private static final int DELETE = 2;
    private ASqlSelect sqlSelect;

    /**
     * DOCUMENT ME!
     * 
     * @param dir ( root ) directory where datasource is stored
     * 
     * @throws xlDatabaseException when a database error occurs
     */
    public ADatabase(File dir) throws xlDatabaseException {
        super(dir);
    }

    /**
     * Adds a row to the database
     * 
     * @param subfolder schema type of identifier for document
     * @param docname DOCUMENT ME!
     * 
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public void addRow(String subfolder, String docname) {
        String subfolderU = subfolder.toUpperCase();
        String docnameU = docname.toUpperCase();

        if (subfolders.containsKey(subfolderU)) {
            ASubFolder sF = (ASubFolder) subfolders.get(subfolderU);

            if (sF.files.containsKey(docnameU)) {
                AFile doc = (AFile) sF.files.get(docnameU);

                if (doc.isValid()) {
                    doc.addRow();
                } else {
                    throw new IllegalArgumentException(NOARGS);
                }
            } else {
                throw new IllegalArgumentException(NOARGS);
            }
        } else {
            throw new IllegalArgumentException(NOARGS);
        }
    }

    /**
     * Close
     * 
     * @param query Select
     * 
     * @throws xlDatabaseException If problems occurred
     */
    public void close(ASqlSelect query) throws xlDatabaseException {
        sqlSelect = query;

        // execute xlSubFolfer.close() for all subfolder objects
        Iterator i = subfolders.values().iterator();

        while (i.hasNext()) {
            ASubFolder wb;
            wb = (ASubFolder) i.next();
            wb.close(query);
        }
    }

    /**
     * Remove table.
     * 
     * @param subfolder name
     * @param docname name
     */
    public void removeTable(String subfolder, String docname) {
        // If subfolder and/or document do not exist: ok (IF EXISTS option)
        String subfolderU = subfolder.toUpperCase();
        String docnameU = docname.toUpperCase();

        if (tableExists(subfolderU, docnameU)) {

            if (subfolders.containsKey(subfolderU)) {
                ASubFolder wb = (ASubFolder) subfolders.get(subfolderU);


                //jsdiff...
                //smell, simplify this...
                wb.bDirty[ADD] = false;
                wb.bDirty[UPDATE] = false;
                wb.bDirty[DELETE] = true;

                if (wb.files.containsKey(docnameU)) {
                    AFile doc = (AFile) wb.files.get(docnameU);

                    if (doc.isValid()) {
                        doc.setIsChanged(DELETE, true);

                        Collection cl = wb.files.values();
                        AFile s;
                        Iterator l = cl.iterator();

                        while (l.hasNext()) {
                            s = (AFile) l.next();

                            if (!s.getIsChanged(DELETE)) {
                                wb.bDirty[DELETE] = false;
                                wb.bDirty[UPDATE] = true;

                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean schemaOtherCaseExists(String workbook) {
        boolean ret = false;
        // key in Uppercase, don't touch case for name
        String workbookU = workbook.toUpperCase();

        if (subfolders.containsKey(workbookU)) {
            ASubFolder wb = (ASubFolder) subfolders.get(workbookU);
            if (!workbook.equals(wb.getSubFolderName())) {
                ret = true;
            }
        } 
        return ret;
    }
    
    /**
     * Returns always false.
     * @param subfolder schema type of identifier for document
     * 
     * @return True if schema exists
     */
    public boolean schemaExists(String subfolder) {
        return false;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param subfolder schema type of identifier for document
     * @param docname document identifier
     * 
     * @return true if document exists
     */
    public boolean tableExists(String subfolder, String docname) {
        boolean ret = false;

        if (subfolders.containsKey(subfolder)) {
            ASubFolder wb = (ASubFolder) subfolders.get(subfolder);

            if (wb.files.containsKey(docname)) {
                AFile doc = (AFile) wb.files.get(docname);

                if (!doc.getIsChanged(DELETE)) {
                    ret = true;
                }
            }
        }

        return ret;
    }

    /**
     * Mark document for update.
     * 
     * @param subfolder schema type of identifier for document
     * 
     * @throws IllegalArgumentException If a problem has occurred
     */
    public void touchSchema(String subfolder) {
        String subfolderU = subfolder.toUpperCase();

        if (subfolders.containsKey(subfolderU)) {
            ASubFolder wb = (ASubFolder) subfolders.get(subfolderU);
            wb.bDirty[xlConstants.UPDATE] = true;
        } else {
            throw new IllegalArgumentException(NOARGS);
        }
    }

    /**
     * Mark document for update.
     * 
     * @param subfolder schema type of identifier for document
     * @param docname DOCUMENT ME!
     * 
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public void touchTable(String subfolder, String docname) {
        String subfolderU = subfolder.toUpperCase();
        String docnameU = docname.toUpperCase();

        if (subfolders.containsKey(subfolderU)) {
            ASubFolder wb = (ASubFolder) subfolders.get(subfolderU);

            if (wb.files.containsKey(docnameU)) {
                AFile doc = (AFile) wb.files.get(docnameU);

                if (doc.isValid()) {
                    doc.setIsChanged(xlConstants.UPDATE, true);
                } else {
                    throw new IllegalArgumentException(NOARGS);
                }
            } else {
                throw new IllegalArgumentException(NOARGS);
            }
        } else {
            throw new IllegalArgumentException(NOARGS);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param workbook DOCUMENT ME!
     */
    public void addSchema(String workbook) {
        // key in Uppercase, don't touch case for name
        String workbookU = workbook.toUpperCase();

        if (subfolders.containsKey(workbookU)) {
            ASubFolder wb = (ASubFolder) subfolders.get(workbookU);
            wb.setDirty(UPDATE, true);
        } else {
            ASubFolder obj = subFolderFactory(directory, workbook);
            subfolders.put(workbookU, obj);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param workbook DOCUMENT ME!
     * @param sheet DOCUMENT ME!
     * 
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public void addTable(String workbook, String sheet) {
        // key in Uppercase, don't touch case for name
        String workbookU = workbook.toUpperCase();
        String sheetU = sheet.toUpperCase();

        if (subfolders.containsKey(workbookU)) {
            ASubFolder wb = (ASubFolder) subfolders.get(workbookU);

            if (wb.getFiles().containsKey(sheetU)) {
                AFile sh = (AFile) wb.getFiles().get(sheetU);
                sh.setIsChanged(DELETE, false); // Added 25.10
            } else {
                AFile obj = fileFactory(directory, wb.getSubFolderName(), sheet);
                obj.setIsChanged(ADD,true);
                wb.getFiles().put(sheetU, obj);
                wb.getValidFiles().put(sheetU, obj);
            }
        } else {
            throw new IllegalArgumentException(NOARGS);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param dir DOCUMENT ME!
     * @param subfolder DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public abstract ASubFolder subFolderFactory(File dir, String subfolder);

    /**
     * DOCUMENT ME!
     * 
     * @param dir DOCUMENT ME!
     * @param subfolder DOCUMENT ME!
     * @param file DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public abstract AFile fileFactory(File dir, String subfolder, String file);
}