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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jsdiff.xlsql.database.ADatabase;
import com.jsdiff.xlsql.jdbc.DatabaseType;

/**
 * ExternalDbEngineAdapter - 外部数据库引擎适配器
 * 
 * <p>将HSQLDB/H2等外部数据库连接包装为ISqlExecutionEngine接口。
 * 这样可以将外部数据库引擎统一到相同的接口下，便于管理和切换。</p>
 * 
 * @author daichangya
 */
public class ExternalDbEngineAdapter implements ISqlExecutionEngine {
    
    /** 外部数据库连接 */
    private final Connection dbConnection;
    
    /** 引擎类型 */
    private final DatabaseType engineType;
    
    /**
     * 创建外部数据库引擎适配器
     * 
     * @param conn 外部数据库JDBC连接
     * @param type 数据库引擎类型
     */
    public ExternalDbEngineAdapter(Connection conn, DatabaseType type) {
        if (conn == null) {
            throw new NullPointerException("Database connection cannot be null");
        }
        if (type == null || type == DatabaseType.NATIVE) {
            throw new IllegalArgumentException("Invalid engine type for external adapter: " + type);
        }
        this.dbConnection = conn;
        this.engineType = type;
    }
    
    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        Statement stmt = dbConnection.createStatement();
        return stmt.executeQuery(sql);
    }
    
    @Override
    public int executeUpdate(String sql) throws SQLException {
        Statement stmt = dbConnection.createStatement();
        try {
            return stmt.executeUpdate(sql);
        } finally {
            stmt.close();
        }
    }
    
    @Override
    public void executeDDL(String sql) throws SQLException {
        Statement stmt = dbConnection.createStatement();
        try {
            stmt.execute(sql);
        } finally {
            stmt.close();
        }
    }
    
    @Override
    public DatabaseType getEngineType() {
        return engineType;
    }
    
    @Override
    public void initialize(ADatabase datastore) throws SQLException {
        // 外部数据库引擎的初始化在xlConnection.startup()中完成
        // 这里不需要额外操作
    }
    
    @Override
    public void shutdown() throws SQLException {
        // 外部数据库连接的关闭由各自的xlConnection实现负责
        // 这里不需要额外操作
    }
    
    /**
     * 获取底层数据库连接（用于特殊操作）
     * 
     * @return JDBC连接对象
     */
    public Connection getConnection() {
        return dbConnection;
    }
}

