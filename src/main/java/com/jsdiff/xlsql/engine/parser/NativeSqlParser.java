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
package com.jsdiff.xlsql.engine.parser;

import java.sql.SQLException;

import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * NativeSqlParser - 自研SQL引擎的解析器（基于JSqlParser）
 * 
 * <p>使用JSqlParser解析SQL SELECT语句，支持完整的MySQL语法。
 * 解析后返回JSqlParser的AST对象（PlainSelect），供执行器直接使用。</p>
 * 
 * @author daichangya
 */
public class NativeSqlParser {
    
    /** MySQL SQL解析器 */
    private final MySQLSqlParser mysqlParser;
    
    /**
     * 创建NativeSqlParser实例
     */
    public NativeSqlParser() {
        this.mysqlParser = new MySQLSqlParser();
    }
    
    /**
     * 解析SQL语句，生成查询计划（PlainSelect AST）
     * 
     * @param sql SQL查询语句
     * @return PlainSelect对象（JSqlParser的AST）
     * @throws SQLException 如果解析失败则抛出异常
     */
    public PlainSelect parse(String sql) throws SQLException {
        return mysqlParser.parse(sql);
    }
}
