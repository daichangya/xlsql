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
 * IExcelStore.java
 *
 * Created on 10 september 2025, 15:49
 */
package com.jsdiff.xlsql.database.excel;

import com.jsdiff.xlsql.database.ASubFolder;

import java.util.Map;

/**
 * IExcelStore - Excel存储接口
 * 
 * <p>该接口定义了存储Excel工作簿的标准方法。
 * 实现类负责维护工作簿的映射表，供IExcelReader使用。</p>
 * 
 * @author daichangya
 */
public interface IExcelStore {
    /**
     * 获取存储工作簿的映射表
     * 
     * <p>返回工作簿名称到ASubFolder对象的映射，键为大写的工作簿名称。</p>
     *
     * @return 工作簿映射表
     */
    public Map<String, ASubFolder> getStore();

    /**
     * 添加工作簿到存储
     * 
     * <p>默认实现：将工作簿添加到映射表中。</p>
     * 
     * @param name 工作簿名称（通常为大写）
     * @param subfolder 工作簿对象（ASubFolder实现）
     */
    default void addStore(String name, ASubFolder subfolder){
        getStore().put(name, subfolder);
    }
}