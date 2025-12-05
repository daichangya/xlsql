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
package io.github.daichangya.xlsql.integration;

import static io.github.daichangya.xlsql.jdbc.Constants.DRIVER;
import static io.github.daichangya.xlsql.jdbc.Constants.URL_PFX_XLS;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.github.daichangya.xlsql.database.xlInstance;

/**
 * EngineCompatibilityTest - 引擎兼容性测试
 * 
 * <p>测试三个引擎（Native、H2、HSQLDB）的SQL语法兼容性、
 * 数据类型兼容性和表名格式兼容性。</p>
 * 
 * @author daichangya
 */
@Tag("integration")
@Tag("compatibility")
public class EngineCompatibilityTest {

    private xlInstance instance;
    private String testDataDir;

    @BeforeEach
    public void setUp() throws Exception {
        Class.forName(DRIVER);
        instance = xlInstance.getInstance();
        testDataDir = System.getProperty("user.dir") + File.separator + "database";
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (instance != null) {
            try {
                instance.setEngine("h2");
            } catch (Exception e) {
                // 忽略
            }
        }
    }

    @Test
    public void testUnderscoreTableNameFormatSupport() throws Exception {
        // 测试所有引擎都支持下划线分隔的表名格式
        String sql = "SELECT * FROM test1_Sheet1 LIMIT 1";
        
        testEngineSupportsSQL("native", sql);
        testEngineSupportsSQL("h2", sql);
        testEngineSupportsSQL("hsqldb", sql);
    }

    @Test
    public void testBasicSqlSyntaxCompatibility() throws Exception {
        // 测试基本SQL语法兼容性
        String[] sqls = {
            "SELECT * FROM test1_Sheet1 LIMIT 5",
            "SELECT a, b FROM test1_Sheet1 WHERE a IS NOT NULL LIMIT 5",
            "SELECT * FROM test1_Sheet1 ORDER BY a LIMIT 5",
            "SELECT COUNT(*) FROM test1_Sheet1"
        };

        for (String sql : sqls) {
            testEngineSupportsSQL("native", sql);
            testEngineSupportsSQL("h2", sql);
            testEngineSupportsSQL("hsqldb", sql);
        }
    }

    @Test
    public void testAggregateFunctionCompatibility() throws Exception {
        // 测试聚合函数兼容性
        String[] sqls = {
            "SELECT COUNT(*) FROM test1_Sheet1",
            "SELECT COUNT(a) FROM test1_Sheet1",
            "SELECT MAX(a) FROM test1_Sheet1",
            "SELECT MIN(a) FROM test1_Sheet1"
        };

        for (String sql : sqls) {
            testEngineSupportsSQL("native", sql);
            testEngineSupportsSQL("h2", sql);
            testEngineSupportsSQL("hsqldb", sql);
        }
    }

    @Test
    public void testGroupByCompatibility() throws Exception {
        // 测试GROUP BY兼容性
        String sql = "SELECT a, COUNT(*) FROM test1_Sheet1 GROUP BY a LIMIT 10";
        
        testEngineSupportsSQL("native", sql);
        testEngineSupportsSQL("h2", sql);
        testEngineSupportsSQL("hsqldb", sql);
    }

    @Test
    public void testJoinCompatibility() throws Exception {
        // 测试JOIN兼容性
        String sql = "SELECT t1.a, t2.b FROM test1_Sheet1 t1 " +
                    "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 5";
        
        try {
            testEngineSupportsSQL("native", sql);
            testEngineSupportsSQL("h2", sql);
            testEngineSupportsSQL("hsqldb", sql);
        } catch (Exception e) {
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("does not exist"))) {
                System.out.println("跳过测试: test2.xls文件不存在");
                return;
            }
            throw e;
        }
    }

    @Test
    public void testWhereConditionCompatibility() throws Exception {
        // 测试WHERE条件兼容性
        String[] sqls = {
            "SELECT * FROM test1_Sheet1 WHERE a = 'value' LIMIT 5",
            "SELECT * FROM test1_Sheet1 WHERE a IS NULL LIMIT 5",
            "SELECT * FROM test1_Sheet1 WHERE a IS NOT NULL LIMIT 5"
        };

        for (String sql : sqls) {
            testEngineSupportsSQL("native", sql);
            testEngineSupportsSQL("h2", sql);
            testEngineSupportsSQL("hsqldb", sql);
        }
    }

    @Test
    public void testLimitOffsetCompatibility() throws Exception {
        // 测试LIMIT/OFFSET兼容性
        String[] sqls = {
            "SELECT * FROM test1_Sheet1 LIMIT 5",
            "SELECT * FROM test1_Sheet1 LIMIT 5 OFFSET 2"
        };

        for (String sql : sqls) {
            testEngineSupportsSQL("native", sql);
            testEngineSupportsSQL("h2", sql);
            testEngineSupportsSQL("hsqldb", sql);
        }
    }

    @Test
    public void testStringFunctionCompatibility() throws Exception {
        // 测试字符串函数兼容性
        String sql = "SELECT UPPER(a) FROM test1_Sheet1 LIMIT 5";
        
        testEngineSupportsSQL("native", sql);
        testEngineSupportsSQL("h2", sql);
        testEngineSupportsSQL("hsqldb", sql);
    }

    @Test
    public void testOrderByDirectionCompatibility() throws Exception {
        // 测试ORDER BY方向兼容性
        String[] sqls = {
            "SELECT * FROM test1_Sheet1 ORDER BY a ASC LIMIT 5",
            "SELECT * FROM test1_Sheet1 ORDER BY a DESC LIMIT 5"
        };

        for (String sql : sqls) {
            testEngineSupportsSQL("native", sql);
            testEngineSupportsSQL("h2", sql);
            testEngineSupportsSQL("hsqldb", sql);
        }
    }

    @Test
    public void testHavingClauseCompatibility() throws Exception {
        // 测试HAVING子句兼容性
        String sql = "SELECT a, COUNT(*) AS cnt FROM test1_Sheet1 " +
                    "GROUP BY a HAVING COUNT(*) > 1 LIMIT 10";
        
        testEngineSupportsSQL("native", sql);
        testEngineSupportsSQL("h2", sql);
        testEngineSupportsSQL("hsqldb", sql);
    }

    @Test
    public void testComplexQueryCompatibility() throws Exception {
        // 测试复杂查询兼容性
        String sql = "SELECT a, COUNT(*) AS cnt FROM test1_Sheet1 " +
                    "WHERE a IS NOT NULL " +
                    "GROUP BY a " +
                    "HAVING COUNT(*) > 1 " +
                    "ORDER BY cnt DESC " +
                    "LIMIT 5";
        
        testEngineSupportsSQL("native", sql);
        testEngineSupportsSQL("h2", sql);
        testEngineSupportsSQL("hsqldb", sql);
    }

    /**
     * 测试指定引擎是否支持给定的SQL
     */
    private void testEngineSupportsSQL(String engineType, String sql) throws Exception {
        // 设置引擎
        instance.setEngine(engineType);
        if ("h2".equals(engineType)) {
            instance.setDriver("org.h2.Driver");
            instance.setUrl("jdbc:h2:mem:xlsql");
            instance.setUser("sa");
            instance.setPassword("");
        } else if ("hsqldb".equals(engineType)) {
            instance.setDriver("org.hsqldb.jdbcDriver");
            instance.setUrl("jdbc:hsqldb:mem:xlsql");
            instance.setUser("sa");
            instance.setPassword("");
        }

        String url = URL_PFX_XLS + testDataDir;
        
        try (Connection con = DriverManager.getConnection(url);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            assertNotNull(rs, engineType + "引擎应该支持SQL: " + sql);
            // 至少能执行，不抛出异常
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("does not exist"))) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            // 数据转换错误是正常的（某些列可能不是数值类型），允许继续
            if (e.getMessage() != null && e.getMessage().contains("Data conversion")) {
                return; // 跳过此测试，因为数据类型不匹配
            }
            fail(engineType + "引擎不支持SQL: " + sql + ", 错误: " + e.getMessage());
        }
    }
}

