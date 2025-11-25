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
package com.jsdiff.xlsql.engine.model;

/**
 * AggregateType - 聚合函数类型枚举
 * 
 * <p>定义支持的SQL聚合函数类型。</p>
 * 
 * @author daichangya
 */
public enum AggregateType {
    /** COUNT - 计数函数 */
    COUNT,
    
    /** SUM - 求和函数 */
    SUM,
    
    /** AVG - 平均值函数 */
    AVG,
    
    /** MAX - 最大值函数 */
    MAX,
    
    /** MIN - 最小值函数 */
    MIN
}

