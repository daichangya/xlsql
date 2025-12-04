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
package com.jsdiff.xlsql.engine.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.jsdiff.xlsql.database.ADatabase;
import com.jsdiff.xlsql.jdbc.DatabaseType;

/**
 * ISqlExecutionEngine - SQL执行引擎接口
 * 
 * <p>定义统一的SQL执行接口，支持外部数据库引擎（HSQLDB/H2）和自研引擎。
 * 通过此接口，xlSQL可以统一管理不同的SQL执行后端。</p>
 * 
 * @author daichangya
 */
public interface ISqlExecutionEngine {
    
    /**
     * 执行SELECT查询语句
     * 
     * @param sql SQL查询语句
     * @return 查询结果集
     * @throws SQLException 如果执行失败则抛出异常
     */
    ResultSet executeQuery(String sql) throws SQLException;
    
    /**
     * 执行UPDATE/INSERT/DELETE语句
     * 
     * @param sql SQL更新语句
     * @return 受影响的行数
     * @throws SQLException 如果执行失败则抛出异常
     */
    int executeUpdate(String sql) throws SQLException;
    
    /**
     * 执行DDL语句（CREATE/DROP/ALTER TABLE等）
     * 
     * @param sql DDL语句
     * @throws SQLException 如果执行失败则抛出异常
     */
    void executeDDL(String sql) throws SQLException;
    
    /**
     * 获取引擎类型
     * 
     * @return 数据库类型枚举值
     */
    DatabaseType getEngineType();
    
    /**
     * 初始化引擎（加载Excel数据）
     * 
     * <p>对于外部数据库引擎，此方法会将Excel数据加载到数据库中。
     * 对于自研引擎，此方法会初始化内部数据结构。</p>
     * 
     * @param datastore Excel数据存储对象
     * @throws SQLException 如果初始化失败则抛出异常
     */
    void initialize(ADatabase datastore) throws SQLException;
    
    /**
     * 关闭引擎并清理资源
     * 
     * @throws SQLException 如果关闭失败则抛出异常
     */
    void shutdown() throws SQLException;
}

