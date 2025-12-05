/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://xlsql.jsdiff.com
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
package io.github.daichangya.xlsql.engine.model;

/**
 * JoinType - JOIN类型枚举
 * 
 * <p>定义SQL JOIN操作的类型，包括INNER、LEFT、RIGHT和FULL OUTER JOIN。</p>
 * 
 * @author daichangya
 */
public enum JoinType {
    /** 内连接：只返回两个表中匹配的行 */
    INNER,
    
    /** 左外连接：返回左表的所有行，以及右表中匹配的行 */
    LEFT,
    
    /** 右外连接：返回右表的所有行，以及左表中匹配的行 */
    RIGHT,
    
    /** 全外连接：返回两个表中的所有行 */
    FULL_OUTER
}

