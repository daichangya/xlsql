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
/*
 * xlDatabaseFactory.java
 *
 * Created on 9 augustus 2025, 18:18
 */
package com.jsdiff.excel.database;

import com.jsdiff.excel.database.excel.*;

import java.io.File;


/**
 * DOCUMENT ME!
 * 
 * @author daichangya
 */
public class xlDatabaseFactory {
    public static ADatabase create(File dir, String type) throws xlDatabaseException {
        ADatabase ret = null;

        if (type.equals("xls")) {
            ret = new xlDatabase(dir);
        } else {
            throw new IllegalArgumentException();
        }

        return ret;
    }
    
    public static AExporter createExporter(File dir) throws xlDatabaseException { 
        return new xlExporter(dir);
    }

    public static ADatabase createDatabase(File dir, xlInstance instance) throws xlDatabaseException {
        ADatabase ret = null;
        ret = new xlDatabase(dir);
        return ret;
    }
}
