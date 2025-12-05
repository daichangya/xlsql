/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://xlsql.jsdiff.com
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
package io.github.daichangya.xlsql.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.daichangya.xlsql.jdbc.Constants;


/**
 * XlConnectionFactory - 创建xlSQL JDBC连接的工具类
 * 
 * <p>该类提供静态方法用于创建到xlSQL的JDBC连接。
 * 主要用于简化连接创建过程。</p>
 * 
 * @version $Revision: 1.6 $
 * @author $author$
 */
public class XlConnectionFactory {

    /**
     * 创建到xlSQL的JDBC连接
     * 
     * <p>加载xlDriver驱动并创建到指定数据库目录的连接。</p>
     * 
     * @param database 数据库目录路径
     * @return JDBC连接对象
     * @throws SQLException 如果连接创建失败则抛出异常
     * @throws xlException 如果xlSQL异常则抛出异常
     */
    public static Connection create(String database) throws SQLException, 
                                                            xlException {
        try {
            // 使用 getDeclaredConstructor().newInstance() 替代已废弃的 newInstance()
            // 加载并实例化xlDriver驱动
            Driver d = (Driver) Class.forName(Constants.DRIVER).getDeclaredConstructor().newInstance();
            String url = Constants.URL_PFX_XLS + database;
            return DriverManager.getConnection(url);
        } catch (ClassNotFoundException nfe) {
            throw new xlException("driver not found. Classpath set ?");
        } catch (InstantiationException ie) {
            throw new xlException("ERR: while instantiating. ???");
        } catch (IllegalAccessException iae) {
            throw new xlException("ERR: illegal access. Privileges?");
        } catch (NoSuchMethodException nsme) {
            throw new xlException("ERR: driver constructor not found: " + nsme.getMessage());
        } catch (java.lang.reflect.InvocationTargetException ite) {
            throw new xlException("ERR: error invoking driver constructor: " + ite.getMessage());
        } catch (Exception e) {
            throw new xlException(e.getMessage());
        }
    }
}