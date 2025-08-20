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
package com.jsdiff.excel.database.export;

import java.util.List;
import java.util.ListIterator;

public class xlConsoleHandler implements IExportHandler {
    public void write (List export) throws xlExportException {
        ListIterator l = export.listIterator();

        while (l.hasNext()) {
            String output;
            output = (String) l.next();
            System.out.println(output);
        }
    }
    
    public void close() throws xlExportException {
        ;
    }
    
}