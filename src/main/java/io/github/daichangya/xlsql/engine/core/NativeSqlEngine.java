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
package io.github.daichangya.xlsql.engine.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import io.github.daichangya.xlsql.database.ADatabase;
import io.github.daichangya.xlsql.jdbc.DatabaseType;
import io.github.daichangya.xlsql.engine.executor.xlNativeSelect;

/**
 * NativeSqlEngine - 自研SQL执行引擎
 * 
 * <p>不依赖外部数据库，直接基于Excel数据执行SQL查询。
 * 第一阶段实现：支持基础SELECT查询，直接查询Excel数据。</p>
 * 
 * <p>后续扩展方向：</p>
 * <ul>
 *   <li>实现完整的SQL解析器</li>
 *   <li>支持JOIN、聚合函数、子查询等高级功能</li>
 *   <li>实现查询优化器</li>
 *   <li>支持索引和缓存</li>
 * </ul>
 * 
 * @author daichangya
 */
public class NativeSqlEngine implements ISqlExecutionEngine {
    
    /** 日志记录器 */
    private static final Logger logger = Logger.getLogger(NativeSqlEngine.class.getName());

    public static final String STATIC_TABLE_SCHEM = "SA";
    
    /** Excel数据存储对象 */
    private ADatabase datastore;
    
    /** 自研查询执行器 */
    private xlNativeSelect queryExecutor;
    
    /** 引擎是否已初始化 */
    private boolean initialized = false;
    
    @Override
    public void initialize(ADatabase datastore) throws SQLException {
        if (datastore == null) {
            throw new SQLException("Datastore cannot be null");
        }
        this.datastore = datastore;
        // 创建自研查询执行器（不需要外部数据库连接）
        this.queryExecutor = new xlNativeSelect(datastore);
        this.initialized = true;
        logger.info("Native SQL engine initialized");
    }
    
    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        if (!initialized) {
            throw new SQLException("Engine not initialized. Call initialize() first.");
        }
        
        // 第一阶段：简单实现，直接查询Excel数据
        // 后续将实现完整的SQL解析和执行
        logger.info("Executing native query: " + sql);
        
        // 暂时使用简化的查询方式
        // TODO: 实现完整的SQL解析器
        return queryExecutor.executeQuery(sql);
    }
    
    @Override
    public int executeUpdate(String sql) throws SQLException {
        if (!initialized) {
            throw new SQLException("Engine not initialized. Call initialize() first.");
        }
        
        // TODO: 实现UPDATE/INSERT/DELETE支持
        throw new SQLException("UPDATE/INSERT/DELETE not yet supported in native engine");
    }
    
    @Override
    public void executeDDL(String sql) throws SQLException {
        if (!initialized) {
            throw new SQLException("Engine not initialized. Call initialize() first.");
        }
        
        // TODO: 实现DDL支持
        throw new SQLException("DDL statements not yet supported in native engine");
    }
    
    @Override
    public DatabaseType getEngineType() {
        return DatabaseType.NATIVE;
    }
    
    @Override
    public void shutdown() throws SQLException {
        if (initialized) {
            this.datastore = null;
            this.queryExecutor = null;
            this.initialized = false;
            logger.info("Native SQL engine shut down");
        }
    }
    
    /**
     * 获取数据存储对象
     * 
     * @return Excel数据存储对象
     */
    public ADatabase getDatastore() {
        return datastore;
    }
}

