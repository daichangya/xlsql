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

/**
 * 
 * @version $Revision: 1.3 $
 * @author daichangya
 */
public class xlDatabaseException extends xlException {
    /**
     * Creates a new instance of type xlDatabaseException.
     */
    public xlDatabaseException() {
        super();
    }

    /**
     * Creates a new instance of type xlDatabaseException.
     * 
     * @param msg
     */
    public xlDatabaseException(String msg) {
        super(msg);
    }
}