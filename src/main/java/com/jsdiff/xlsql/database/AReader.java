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
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;


/**
 * Abstract Reader.
 * 
 * @version $Revision: 1.4 $
 * @author $author$
 */
public abstract class AReader extends AFolder {
    /**
     * Creates a new Reader object.
     * 
     * @param dir ( root ) directory where datasource is stored
     * 
     * @throws xlDatabaseException when a database error occurs
     */
    public AReader(File dir) throws xlDatabaseException {
        super(dir);
    }

    /**
     * Column names
     * 
     * @param subfolder schema type of identifier for document
     * @param docname document name
     * 
     * @return String array with columnnames (assume never null or empty)
     * 
     * @throws IllegalArgumentException DOCUMENT ME!
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
     * Column types
     * 
     * @param subfolder schema type of identifier for document
     * @param docname DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws IllegalArgumentException DOCUMENT ME!
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
     * Row count
     * 
     * @param subfolder schema type of identifier for document
     * @param docname DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws IllegalArgumentException DOCUMENT ME!
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
     * Schemas
     * 
     * @return DOCUMENT ME!
     */
    public String[] getSchemas() {
        String[] ret = getSubfolders().values().stream()
            .map(wb -> ((ASubFolder) wb).getSubFolderName())
            .toArray(String[]::new);

        return ret;
    }

    /**
     * Tables
     * 
     * @param subfolder schema type of identifier for document
     * 
     * @return DOCUMENT ME!
     * 
     * @throws IllegalArgumentException DOCUMENT ME!
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

    private String toUpperCase(String str) {
        return str != null ? str.toUpperCase() : null;
    }

    private ASubFolder getSubfolder(String subfolderU) {
        return (ASubFolder) getSubfolders().get(subfolderU);
    }

    /**
     * Values ( string matrix )
     * 
     * @param subfolder schema type of identifier for document
     * @param docname DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws xlDatabaseException DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
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