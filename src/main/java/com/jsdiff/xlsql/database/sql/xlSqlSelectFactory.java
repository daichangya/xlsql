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

import java.sql.Connection;



/**
 * xlSqlSelectFactory - SQL查询对象工厂类
 * 
 * <p>该类提供工厂方法用于创建不同类型的SQL查询对象。
 * 根据数据库类型（hsqldb或mysql）创建相应的查询实现。</p>
 * 
 * @author daichangya
 */
public class xlSqlSelectFactory {
    /**
     * 创建SQL查询对象
     * 
     * <p>根据数据库类型创建相应的查询实现：</p>
     * <ul>
     *   <li>"hsqldb" - 创建xlHsqldbSelect实例</li>
     *   <li>"mysql" - 创建xlMySQLSelect实例</li>
     * </ul>
     * 
     * @param type 数据库类型（"hsqldb"或"mysql"）
     * @param con JDBC连接对象
     * @return SQL查询对象（ASqlSelect实现）
     * @throws IllegalArgumentException 如果类型不支持则抛出异常
     */
    public static ASqlSelect create(String type, Connection con) {
        ASqlSelect ret = null;

        if (type.equals("hsqldb")) {
            // 创建HSQLDB查询对象
            ret = new xlHsqldbSelect(con);
        } else if (type.equals("mysql")) {
            // 创建MySQL查询对象
            ret = new xlMySQLSelect(con);
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + type);
        }

        return ret;
    }
}