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

import com.jsdiff.xlsql.database.sql.ASqlSelect;

import java.io.File;
import java.util.*;


/**
 * Abstract database
 *
 * @version $Revision: 1.11 $
 * @author $author$
 */
public abstract class ADatabase extends AExporter {
    private static final int OP_ADD = 0;
    private static final int OP_UPDATE = 1;
    private static final int OP_DELETE = 2;
    private ASqlSelect sqlSelect;

    /**
     * Constructor
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
     * Close
     *
     * @param query Select
     *
     * @throws xlDatabaseException If problems occurred
     */
    public void close(ASqlSelect query) throws xlDatabaseException {
        sqlSelect = query;

        for (ASubFolder wb : getSubfolders().values()) {
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

    public boolean schemaOtherCaseExists(String workbook) {
        String workbookU = safeToUpperCase(workbook);
        ASubFolder wb = getSubfolder(workbookU);
        return wb != null && !workbook.equals(wb.getSubFolderName());
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
        ASubFolder wb = getSubfolder(subfolder);
        if (wb == null) {
            return false;
        }

        Map<String, AFile> files = wb.getFiles();
        AFile doc = files.get(docname);
        return doc != null && !doc.getIsChanged(OP_DELETE);
    }

    /**
     * Mark document for update.
     *
     * @param subfolder schema type of identifier for document
     *
     * @throws IllegalArgumentException If a problem has occurred
     */
    public void touchSchema(String subfolder) {
        String subfolderU = safeToUpperCase(subfolder);
        ASubFolder wb = getSubfolder(subfolderU);
        if (wb == null) {
            throw new IllegalArgumentException(NOARGS);
        }
        wb.bDirty[xlConstants.UPDATE] = true;
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
     * DOCUMENT ME!
     *
     * @param workbook DOCUMENT ME!
     */
    public void addSchema(String workbook) {
        String workbookU = safeToUpperCase(workbook);

        ASubFolder wb = getSubfolder(workbookU);
        if (wb != null) {
            wb.setDirty(OP_UPDATE, true);
        } else {
            ASubFolder obj = subFolderFactory(directory, workbook);
            addSubfolders(workbookU, obj);
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

    private String safeToUpperCase(String str) {
        return str == null ? null : str.toUpperCase();
    }

    private ASubFolder getSubfolder(String subfolderU) {
        return (ASubFolder) getSubfolders().get(subfolderU);
    }
}
