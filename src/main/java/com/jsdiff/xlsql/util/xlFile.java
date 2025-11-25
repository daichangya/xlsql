/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://excel.jsdiff.com
   daichangya@163.com

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by the Free 
 Software Foundation; either version 2 of the License, or (at your option) 
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public 
 License along with this program; if not, write to the Free Software 
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package com.jsdiff.xlsql.util;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Adds recursive listFiles method to java.io.File
 * 
 * @author daichangya
 */
public class xlFile extends File {
    /**
     * Settle a string in path and extension to create a File.
     * 
     * @param file File in string with or without path and extension
     * @param ext String array with possible extensions. All extensions will be
     *        tried to create an existing file.
     * 
     * @return xlFile object
     */
    public static xlFile settle(String file, String[] ext) {
        xlFile f = new xlFile(file);

        if (!f.exists()) {
            file = System.getProperty("user.dir") + File.separator + file;
            f = new xlFile(file);

            for (int i = 0; i < ext.length; i++) {
                if (!f.exists()) {
                    if (!file.endsWith(ext[i])) {
                        f = new xlFile(file + "." + ext[i]);
                    }
                } else {
                    break;
                }
            }
        }

        return f;
    }

    /**
     * Creates an xlFile object from String
     * 
     * @param file Name of file
     */
    public xlFile(String file) {
        super(file);
    }

    /**
     * Returns an array of abstract pathnames denoting the files in the
     * directory denoted by this abstract pathname. If this abstract pathname
     * does not denote a directory, then this method returns null. Otherwise
     * an array of File objects is returned, one for each file or directory in
     * the directory, recursively if the recursive parameter is true.
     * Pathnames denoting the directory itself and the directory's parent
     * directory are not included in the result.
     * 
     * @param selection 0 for files and directories, positive values for files
     *        only and and negative value for subdirectories
     * @param recursive if this value is true the returned array will also
     *        contain the File objects as found in all subdirectories
     *        recursively
     * 
     * @return An array of abstract pathnames denoting the files and
     *         directories in the directory denoted by this abstract pathname.
     *         The array will be empty if the directory contains no files or
     *         directories as in selection. Returns null if this abstract
     *         pathname does not denote a directory, or if an I/O error
     *         occurs.
     */
    public File[] listFiles(int selection, boolean recursive) {
        File[] ret;

        if (this.isDirectory()) {
            List v = new ArrayList();
            v = filelist(v, this, selection, recursive);

            Object[] o = v.toArray();
            ret = new File[o.length];

            for (int i = 0; i < o.length; i++) {
                ret[i] = (File) v.get(i);
            }
        } else {
            ret = null;
        }

        return ret;
    }

    private List filelist(List v, File f, int selection, boolean recursive) {
        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                if (selection <= 0) {
                    v.add(files[i]);
                }

                if (recursive) {
                    filelist(v, files[i], selection, recursive);
                }
            } else {
                if (selection >= 0) {
                    v.add(files[i]);
                }
            }
        }

        return v;
    }
}

