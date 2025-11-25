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
 * xlDatabaseFactory.java
 *
 * Created on 9 augustus 2025, 18:18
 */
package com.jsdiff.xlsql.database.sql;



/**
 * xlSqlParserFactory - SQL解析器工厂类
 * 
 * <p>该类提供工厂方法用于创建不同类型的SQL解析器对象。
 * 根据数据库类型（hsqldb或mysql）创建相应的解析器实现。</p>
 * 
 * @author daichangya
 */
public class xlSqlParserFactory {
    /**
     * 创建SQL解析器对象
     * 
     * <p>根据数据库类型创建相应的解析器实现：</p>
     * <ul>
     *   <li>"hsqldb" - 创建xlHsqldb解析器实例</li>
     *   <li>"mysql" - 创建xlMySQL解析器实例（需要context参数）</li>
     * </ul>
     * 
     * @param type 数据库类型（"hsqldb"或"mysql"）
     * @param database 数据库对象
     * @param context MySQL数据库上下文（模式名称），HSQLDB不需要此参数
     * @return SQL解析器对象（ASqlParser实现）
     * @throws IllegalArgumentException 如果类型不支持则抛出异常
     */
    public static ASqlParser create(String type, com.jsdiff.xlsql.database.ADatabase database, String context) {
        ASqlParser ret = null;

        if (type.equals("hsqldb")) {
            // 创建HSQLDB解析器
            ret = new xlHsqldb(database);
        } else if (type.equals("mysql")) {
            // 创建MySQL解析器（需要context参数）
            ret = new xlMySQL(database, context);
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + type);
        }

        return ret;
    }
}