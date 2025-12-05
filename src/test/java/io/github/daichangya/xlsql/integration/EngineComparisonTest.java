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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.github.daichangya.xlsql.database.xlInstance;

/**
 * EngineComparisonTest - 引擎功能对比测试
 * 
 * <p>测试三个引擎（Native、H2、HSQLDB）在相同SQL下的结果一致性和性能对比。
 * 这个测试确保三个引擎能够提供一致的查询结果。</p>
 * 
 * @author daichangya
 */
@Tag("integration")
@Tag("comparison")
public class EngineComparisonTest {

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
    public void testBasicSelectConsistency() throws Exception {
        String sql = "SELECT * FROM test1_Sheet1 LIMIT 5";
        
        try {
            List<String[]> nativeResults = executeQueryWithEngine("native", sql);
            List<String[]> h2Results = executeQueryWithEngine("h2", sql);
            List<String[]> hsqldbResults = executeQueryWithEngine("hsqldb", sql);

            // 验证三个引擎返回的行数一致
            assertEquals(nativeResults.size(), h2Results.size(),
                "Native和H2应该返回相同数量的行");
            assertEquals(nativeResults.size(), hsqldbResults.size(),
                "Native和HSQLDB应该返回相同数量的行");

            // 验证列数一致
            if (!nativeResults.isEmpty()) {
                assertEquals(nativeResults.get(0).length, h2Results.get(0).length,
                    "Native和H2应该返回相同数量的列");
                assertEquals(nativeResults.get(0).length, hsqldbResults.get(0).length,
                    "Native和HSQLDB应该返回相同数量的列");
            }
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testCountConsistency() throws Exception {
        String sql = "SELECT COUNT(*) AS total FROM test1_Sheet1";
        
        try {
            List<String[]> nativeResults = executeQueryWithEngine("native", sql);
            List<String[]> h2Results = executeQueryWithEngine("h2", sql);
            List<String[]> hsqldbResults = executeQueryWithEngine("hsqldb", sql);

            // 验证COUNT结果一致
            assertFalse(nativeResults.isEmpty(), "Native应该返回结果");
            assertFalse(h2Results.isEmpty(), "H2应该返回结果");
            assertFalse(hsqldbResults.isEmpty(), "HSQLDB应该返回结果");

            String nativeCount = nativeResults.get(0)[0];
            String h2Count = h2Results.get(0)[0];
            String hsqldbCount = hsqldbResults.get(0)[0];

            assertEquals(nativeCount, h2Count, 
                "Native和H2的COUNT结果应该一致");
            assertEquals(nativeCount, hsqldbCount, 
                "Native和HSQLDB的COUNT结果应该一致");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testWhereConditionConsistency() throws Exception {
        String sql = "SELECT * FROM test1_Sheet1 WHERE a = 'value1' LIMIT 10";
        
        try {
            List<String[]> nativeResults = executeQueryWithEngine("native", sql);
            List<String[]> h2Results = executeQueryWithEngine("h2", sql);
            List<String[]> hsqldbResults = executeQueryWithEngine("hsqldb", sql);

            // 验证WHERE条件过滤后的行数一致
            assertEquals(nativeResults.size(), h2Results.size(),
                "Native和H2在WHERE条件下应该返回相同数量的行");
            assertEquals(nativeResults.size(), hsqldbResults.size(),
                "Native和HSQLDB在WHERE条件下应该返回相同数量的行");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testOrderByConsistency() throws Exception {
        String sql = "SELECT a FROM test1_Sheet1 ORDER BY a LIMIT 5";
        
        try {
            List<String[]> nativeResults = executeQueryWithEngine("native", sql);
            List<String[]> h2Results = executeQueryWithEngine("h2", sql);
            List<String[]> hsqldbResults = executeQueryWithEngine("hsqldb", sql);

            // 验证排序结果一致
            assertEquals(nativeResults.size(), h2Results.size(),
                "Native和H2的排序结果行数应该一致");
            assertEquals(nativeResults.size(), hsqldbResults.size(),
                "Native和HSQLDB的排序结果行数应该一致");

            // 验证排序顺序一致
            for (int i = 0; i < nativeResults.size(); i++) {
                assertEquals(nativeResults.get(i)[0], h2Results.get(i)[0],
                    "Native和H2的排序顺序应该一致");
                assertEquals(nativeResults.get(i)[0], hsqldbResults.get(i)[0],
                    "Native和HSQLDB的排序顺序应该一致");
            }
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testPerformanceComparison() throws Exception {
        String sql = "SELECT * FROM test1_Sheet1 LIMIT 100";
        
        try {
            long nativeTime = measureQueryTime("native", sql);
            long h2Time = measureQueryTime("h2", sql);
            long hsqldbTime = measureQueryTime("hsqldb", sql);

            System.out.println("查询性能对比（毫秒）:");
            System.out.println("  Native: " + nativeTime);
            System.out.println("  H2: " + h2Time);
            System.out.println("  HSQLDB: " + hsqldbTime);

            // 不做严格的性能断言，只记录对比数据
            assertTrue(nativeTime > 0, "Native引擎应该能执行查询");
            assertTrue(h2Time > 0, "H2引擎应该能执行查询");
            assertTrue(hsqldbTime > 0, "HSQLDB引擎应该能执行查询");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    /**
     * 使用指定引擎执行SQL查询并返回结果
     */
    private List<String[]> executeQueryWithEngine(String engineType, String sql) 
            throws Exception {
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

        List<String[]> results = new ArrayList<>();
        String url = URL_PFX_XLS + testDataDir;
        
        try (Connection con = DriverManager.getConnection(url);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getString(i + 1);
                }
                results.add(row);
            }
        }
        
        return results;
    }

    /**
     * 测量查询执行时间
     */
    private long measureQueryTime(String engineType, String sql) throws Exception {
        long startTime = System.currentTimeMillis();
        executeQueryWithEngine(engineType, sql);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}

