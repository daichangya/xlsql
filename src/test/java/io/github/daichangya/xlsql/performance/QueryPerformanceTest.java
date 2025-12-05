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
package io.github.daichangya.xlsql.performance;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * QueryPerformanceTest - 查询性能测试
 * 
 * <p>测试三个引擎（Native、H2、HSQLDB）的查询性能，包括：
 * <ul>
 *   <li>单表查询性能</li>
 *   <li>JOIN查询性能</li>
 *   <li>聚合查询性能</li>
 *   <li>复杂查询性能</li>
 *   <li>大数据集查询性能</li>
 * </ul>
 * </p>
 * 
 * @author daichangya
 */
@Tag("performance")
public class QueryPerformanceTest extends PerformanceTestBase {

    @Test
    public void testSimpleSelectPerformance() throws Exception {
        String sql = "SELECT * FROM test1_Sheet1 LIMIT 100";
        
        try {
            long nativeTime = measureQueryTime("native", sql);
            long h2Time = measureQueryTime("h2", sql);
            long hsqldbTime = measureQueryTime("hsqldb", sql);

            printPerformanceStats("简单SELECT查询性能", nativeTime, h2Time, hsqldbTime);

            // 验证所有引擎都能执行
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

    @Test
    public void testWhereConditionPerformance() throws Exception {
        String sql = "SELECT * FROM test1_Sheet1 WHERE a IS NOT NULL LIMIT 100";
        
        try {
            long nativeTime = measureQueryTime("native", sql);
            long h2Time = measureQueryTime("h2", sql);
            long hsqldbTime = measureQueryTime("hsqldb", sql);

            printPerformanceStats("WHERE条件查询性能", nativeTime, h2Time, hsqldbTime);

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

    @Test
    public void testOrderByPerformance() throws Exception {
        String sql = "SELECT * FROM test1_Sheet1 ORDER BY a LIMIT 100";
        
        try {
            long nativeTime = measureQueryTime("native", sql);
            long h2Time = measureQueryTime("h2", sql);
            long hsqldbTime = measureQueryTime("hsqldb", sql);

            printPerformanceStats("ORDER BY查询性能", nativeTime, h2Time, hsqldbTime);

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

    @Test
    public void testJoinPerformance() throws Exception {
        String sql = "SELECT t1.a, t2.b FROM test1_Sheet1 t1 " +
                    "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 100";
        
        try {
            long nativeTime = measureQueryTime("native", sql);
            long h2Time = measureQueryTime("h2", sql);
            long hsqldbTime = measureQueryTime("hsqldb", sql);

            printPerformanceStats("JOIN查询性能", nativeTime, h2Time, hsqldbTime);

            assertTrue(nativeTime > 0, "Native引擎应该能执行查询");
            assertTrue(h2Time > 0, "H2引擎应该能执行查询");
            assertTrue(hsqldbTime > 0, "HSQLDB引擎应该能执行查询");
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
    public void testAggregatePerformance() throws Exception {
        String sql = "SELECT COUNT(*), MAX(a), MIN(a) FROM test1_Sheet1";
        
        try {
            long nativeTime = measureQueryTime("native", sql);
            long h2Time = measureQueryTime("h2", sql);
            long hsqldbTime = measureQueryTime("hsqldb", sql);

            printPerformanceStats("聚合查询性能", nativeTime, h2Time, hsqldbTime);

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

    @Test
    public void testGroupByPerformance() throws Exception {
        String sql = "SELECT a, COUNT(*) FROM test1_Sheet1 GROUP BY a LIMIT 100";
        
        try {
            long nativeTime = measureQueryTime("native", sql);
            long h2Time = measureQueryTime("h2", sql);
            long hsqldbTime = measureQueryTime("hsqldb", sql);

            printPerformanceStats("GROUP BY查询性能", nativeTime, h2Time, hsqldbTime);

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

    @Test
    public void testComplexQueryPerformance() throws Exception {
        String sql = "SELECT a, COUNT(*) AS cnt FROM test1_Sheet1 " +
                    "WHERE a IS NOT NULL " +
                    "GROUP BY a " +
                    "HAVING COUNT(*) > 1 " +
                    "ORDER BY cnt DESC " +
                    "LIMIT 50";
        
        try {
            long nativeTime = measureQueryTime("native", sql);
            long h2Time = measureQueryTime("h2", sql);
            long hsqldbTime = measureQueryTime("hsqldb", sql);

            printPerformanceStats("复杂查询性能", nativeTime, h2Time, hsqldbTime);

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

    @Test
    public void testAverageQueryTime() throws Exception {
        String sql = "SELECT * FROM test1_Sheet1 LIMIT 50";
        int iterations = 5;
        
        try {
            long nativeAvg = measureAverageQueryTime("native", sql, iterations);
            long h2Avg = measureAverageQueryTime("h2", sql, iterations);
            long hsqldbAvg = measureAverageQueryTime("hsqldb", sql, iterations);

            printPerformanceStats("平均查询时间 (" + iterations + "次)", nativeAvg, h2Avg, hsqldbAvg);

            assertTrue(nativeAvg > 0, "Native引擎应该能执行查询");
            assertTrue(h2Avg > 0, "H2引擎应该能执行查询");
            assertTrue(hsqldbAvg > 0, "HSQLDB引擎应该能执行查询");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }
}

