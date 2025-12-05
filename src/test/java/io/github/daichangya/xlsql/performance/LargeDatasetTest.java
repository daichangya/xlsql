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
 * LargeDatasetTest - 大数据集测试
 * 
 * <p>测试三个引擎（Native、H2、HSQLDB）处理大数据集的能力，包括：
 * <ul>
 *   <li>大量行数据查询</li>
 *   <li>内存使用监控</li>
 *   <li>查询性能衰减测试</li>
 *   <li>分页查询效率</li>
 * </ul>
 * </p>
 * 
 * @author daichangya
 */
@Tag("performance")
@Tag("large-dataset")
public class LargeDatasetTest extends PerformanceTestBase {

    @Test
    public void testLargeResultSet() throws Exception {
        // 测试读取大量数据（如果文件包含大量行）
        String sql = "SELECT * FROM test1_Sheet1 LIMIT 1000";
        
        try {
            long nativeTime = measureQueryTime("native", sql);
            long h2Time = measureQueryTime("h2", sql);
            long hsqldbTime = measureQueryTime("hsqldb", sql);

            printPerformanceStats("大结果集查询性能 (1000行)", nativeTime, h2Time, hsqldbTime);

            assertTrue(nativeTime > 0, "Native引擎应该能执行大数据集查询");
            assertTrue(h2Time > 0, "H2引擎应该能执行大数据集查询");
            assertTrue(hsqldbTime > 0, "HSQLDB引擎应该能执行大数据集查询");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testPaginationEfficiency() throws Exception {
        // 测试分页查询效率
        int pageSize = 100;
        int pages = 10;
        
        try {
            setupEngine("native");
            
            System.out.println("\n========== 分页查询效率测试 (Native) ==========");
            System.out.println("页大小: " + pageSize);
            System.out.println("页数: " + pages);
            
            long totalTime = 0;
            for (int page = 0; page < pages; page++) {
                int offset = page * pageSize;
                String sql = "SELECT * FROM test1_Sheet1 LIMIT " + pageSize + " OFFSET " + offset;
                
                long startTime = System.currentTimeMillis();
                int rowCount = executeQuery(sql);
                long endTime = System.currentTimeMillis();
                long pageTime = endTime - startTime;
                totalTime += pageTime;
                
                System.out.println("页 " + (page + 1) + ": " + rowCount + " 行, " + pageTime + " ms");
            }
            
            System.out.println("总耗时: " + totalTime + " ms");
            System.out.println("平均每页: " + (totalTime / pages) + " ms");
            System.out.println("==========================================\n");

            assertTrue(totalTime > 0, "分页查询应该能成功执行");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testMemoryUsageWithLargeResultSet() throws Exception {
        String sql = "SELECT * FROM test1_Sheet1 LIMIT 500";
        
        try {
            long nativeMemory = measureMemoryUsage("native", sql);
            long h2Memory = measureMemoryUsage("h2", sql);
            long hsqldbMemory = measureMemoryUsage("hsqldb", sql);

            printMemoryStats("大结果集内存使用 (500行)", nativeMemory, h2Memory, hsqldbMemory);

            // 内存使用可能是负数（如果GC介入），所以不做严格断言
            System.out.println("注意: 内存使用测量可能受GC影响，仅供参考");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testPerformanceDegradation() throws Exception {
        // 测试查询性能是否随数据量增加而衰减
        int[] limits = {10, 50, 100, 200, 500};
        
        try {
            setupEngine("native");
            
            System.out.println("\n========== 性能衰减测试 (Native) ==========");
            System.out.println("数据量\t耗时(ms)\t每行耗时(ms)");
            
            for (int limit : limits) {
                String sql = "SELECT * FROM test1_Sheet1 LIMIT " + limit;
                
                long startTime = System.currentTimeMillis();
                int rowCount = executeQuery(sql);
                long endTime = System.currentTimeMillis();
                long queryTime = endTime - startTime;
                
                double timePerRow = rowCount > 0 ? (double)queryTime / rowCount : 0;
                System.out.println(limit + "\t" + queryTime + "\t\t" + 
                    String.format("%.4f", timePerRow));
            }
            
            System.out.println("==========================================\n");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testAggregationOnLargeDataset() throws Exception {
        // 测试在大数据集上的聚合性能
        String sql = "SELECT COUNT(*), MAX(a), MIN(a) FROM test1_Sheet1";
        
        try {
            long nativeTime = measureQueryTime("native", sql);
            long h2Time = measureQueryTime("h2", sql);
            long hsqldbTime = measureQueryTime("hsqldb", sql);

            printPerformanceStats("大数据集聚合查询性能", nativeTime, h2Time, hsqldbTime);

            assertTrue(nativeTime > 0, "Native引擎应该能执行聚合查询");
            assertTrue(h2Time > 0, "H2引擎应该能执行聚合查询");
            assertTrue(hsqldbTime > 0, "HSQLDB引擎应该能执行聚合查询");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testGroupByOnLargeDataset() throws Exception {
        // 测试在大数据集上的GROUP BY性能
        String sql = "SELECT a, COUNT(*) FROM test1_Sheet1 GROUP BY a LIMIT 1000";
        
        try {
            long nativeTime = measureQueryTime("native", sql);
            long h2Time = measureQueryTime("h2", sql);
            long hsqldbTime = measureQueryTime("hsqldb", sql);

            printPerformanceStats("大数据集GROUP BY性能", nativeTime, h2Time, hsqldbTime);

            assertTrue(nativeTime > 0, "Native引擎应该能执行GROUP BY查询");
            assertTrue(h2Time > 0, "H2引擎应该能执行GROUP BY查询");
            assertTrue(hsqldbTime > 0, "HSQLDB引擎应该能执行GROUP BY查询");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }
}

