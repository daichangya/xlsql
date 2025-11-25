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
import java.util.logging.Logger;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * MySQLSqlParser - 基于JSqlParser的MySQL SQL解析器
 * 
 * <p>使用JSqlParser开源库解析SQL语句，支持完整的MySQL语法。
 * 解析后返回JSqlParser的AST对象（PlainSelect），供执行器直接使用。</p>
 * 
 * <p>支持的语法：</p>
 * <ul>
 *   <li>SELECT语句（包括DISTINCT）</li>
 *   <li>FROM子句（单表、多表、子查询）</li>
 *   <li>JOIN（INNER、LEFT、RIGHT、FULL OUTER、CROSS）</li>
 *   <li>WHERE条件（所有表达式类型）</li>
 *   <li>GROUP BY（列、表达式）</li>
 *   <li>HAVING条件</li>
 *   <li>ORDER BY（列、表达式、位置）</li>
 *   <li>LIMIT/OFFSET（MySQL两种语法都支持）</li>
 *   <li>聚合函数和MySQL函数</li>
 *   <li>反引号标识符</li>
 * </ul>
 * 
 * @author daichangya
 */
public class MySQLSqlParser {
    
    /** 日志记录器 */
    private static final Logger logger = Logger.getLogger(MySQLSqlParser.class.getName());
    
    /**
     * 解析SQL语句
     * 
     * @param sql SQL查询语句
     * @return PlainSelect对象（JSqlParser的AST）
     * @throws SQLException 如果解析失败则抛出异常
     */
    public PlainSelect parse(String sql) throws SQLException {
        if (sql == null || sql.trim().isEmpty()) {
            throw new SQLException("SQL statement cannot be null or empty");
        }
        
        logger.info("Parsing SQL with JSqlParser: " + sql);
        
        try {
            // 使用JSqlParser解析SQL
            Statement statement = CCJSqlParserUtil.parse(sql);
            
            // 检查是否是SELECT语句
            if (!(statement instanceof Select)) {
                throw new SQLException("Only SELECT statements are supported. Got: " + 
                                     statement.getClass().getSimpleName());
            }
            
            Select select = (Select) statement;
            
            // 获取PlainSelect（普通SELECT，不是UNION等）
            if (!(select.getSelectBody() instanceof PlainSelect)) {
                throw new SQLException("Complex SELECT statements (UNION, etc.) are not yet supported");
            }
            
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            
            logger.info("SQL parsed successfully");
            return plainSelect;
            
        } catch (JSQLParserException e) {
            String errorMsg = "SQL parsing failed: " + e.getMessage();
            logger.severe(errorMsg);
            throw new SQLException(errorMsg, e);
        }
    }
    
    /**
     * 验证SQL语句是否可以解析
     * 
     * @param sql SQL查询语句
     * @return 如果可以解析返回true
     */
    public boolean canParse(String sql) {
        try {
            parse(sql);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}

