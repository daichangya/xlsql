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

/*
 * IExcelStore.java
 *
 * Created on 10 september 2025, 15:49
 */
package com.jsdiff.xlsql.database.excel;

import com.jsdiff.xlsql.database.ASubFolder;

import java.util.Map;

/**
 * For classes which store data read by an IExcelReader
 * 
 * @author daichangya
 */
public interface IExcelStore {
    /**
     * Return map where data can be stored
     *
     * @return Map
     */
    public Map<String, ASubFolder> getStore();

    default void addStore(String name, ASubFolder subfolder){
        getStore().put(name, subfolder);
    }
}