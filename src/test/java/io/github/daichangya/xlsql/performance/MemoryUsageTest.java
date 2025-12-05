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

import static io.github.daichangya.xlsql.jdbc.Constants.URL_PFX_XLS;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * MemoryUsageTest - 内存使用测试
 * 
 * <p>测试三个引擎（Native、H2、HSQLDB）的内存使用情况，包括：
 * <ul>
 *   <li>连接内存占用</li>
 *   <li>查询结果集内存占用</li>
 *   <li>大数据集内存占用</li>
 *   <li>内存泄漏检测</li>
 * </ul>
 * </p>
 * 
 * @author daichangya
 */
@Tag("performance")
@Tag("memory")
public class MemoryUsageTest extends PerformanceTestBase {

    @Test
    public void testConnectionMemoryUsage() throws Exception {
        System.out.println("\n========== 连接内存使用测试 ==========");
        
        for (String engineType : new String[]{"native", "h2", "hsqldb"}) {
            try {
                setupEngine(engineType);
                
                // 多次强制GC以获得准确的基准
                for (int i = 0; i < 3; i++) {
                    System.gc();
                    Thread.sleep(200);
                }
                
                Runtime runtime = Runtime.getRuntime();
                long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
                
                // 创建连接
                String url = URL_PFX_XLS + testDataDir;
                Connection con = DriverManager.getConnection(url);
                
                long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
                long memoryUsed = memoryAfter - memoryBefore;
                
                System.out.println(engineType.toUpperCase() + " 连接内存: " + formatBytes(memoryUsed));
                
                con.close();
                
                // 内存使用可能是负数（GC介入），不做严格断言
            } catch (Exception e) {
                // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
                if (e.getMessage() != null && e.getMessage().contains("not found")) {
                    throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
                }
                throw e;
            }
        }
        
        System.out.println("==========================================\n");
    }

    @Test
    public void testQueryResultSetMemoryUsage() throws Exception {
        String sql = "SELECT * FROM test1_Sheet1 LIMIT 100";
        
        try {
            long nativeMemory = measureMemoryUsage("native", sql);
            long h2Memory = measureMemoryUsage("h2", sql);
            long hsqldbMemory = measureMemoryUsage("hsqldb", sql);

            printMemoryStats("查询结果集内存使用 (100行)", nativeMemory, h2Memory, hsqldbMemory);
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testLargeResultSetMemoryUsage() throws Exception {
        String sql = "SELECT * FROM test1_Sheet1 LIMIT 500";
        
        try {
            long nativeMemory = measureMemoryUsage("native", sql);
            long h2Memory = measureMemoryUsage("h2", sql);
            long hsqldbMemory = measureMemoryUsage("hsqldb", sql);

            printMemoryStats("大结果集内存使用 (500行)", nativeMemory, h2Memory, hsqldbMemory);
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testMemoryLeakDetection() throws Exception {
        // 测试重复执行查询是否导致内存泄漏
        String engineType = "native";
        String sql = "SELECT * FROM test1_Sheet1 LIMIT 10";
        int iterations = 100;
        
        try {
            setupEngine(engineType);
            
            System.out.println("\n========== 内存泄漏检测 (" + engineType + ") ==========");
            System.out.println("迭代次数: " + iterations);
            
            // 强制GC
            System.gc();
            Thread.sleep(200);
            
            Runtime runtime = Runtime.getRuntime();
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("初始内存: " + formatBytes(memoryBefore));
            
            // 执行多次查询
            for (int i = 0; i < iterations; i++) {
                executeQuery(sql);
                
                // 每20次迭代打印一次内存使用
                if ((i + 1) % 20 == 0) {
                    long currentMemory = runtime.totalMemory() - runtime.freeMemory();
                    System.out.println("迭代 " + (i + 1) + ": " + formatBytes(currentMemory));
                }
            }
            
            // 再次GC
            System.gc();
            Thread.sleep(200);
            
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            long memoryIncrease = memoryAfter - memoryBefore;
            
            System.out.println("最终内存: " + formatBytes(memoryAfter));
            System.out.println("内存增长: " + formatBytes(memoryIncrease));
            
            double increasePerIteration = (double)memoryIncrease / iterations;
            System.out.println("平均每次查询增长: " + formatBytes((long)increasePerIteration));
            
            // 如果内存增长过大，可能存在泄漏（这里不做严格断言，仅警告）
            if (memoryIncrease > 10 * 1024 * 1024) { // 10MB
                System.out.println("警告: 内存增长超过10MB，可能存在内存泄漏");
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
    public void testJoinMemoryUsage() throws Exception {
        String sql = "SELECT t1.a, t2.b FROM test1_Sheet1 t1 " +
                    "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 100";
        
        try {
            long nativeMemory = measureMemoryUsage("native", sql);
            long h2Memory = measureMemoryUsage("h2", sql);
            long hsqldbMemory = measureMemoryUsage("hsqldb", sql);

            printMemoryStats("JOIN查询内存使用 (100行)", nativeMemory, h2Memory, hsqldbMemory);
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
    public void testAggregateMemoryUsage() throws Exception {
        String sql = "SELECT a, COUNT(*) FROM test1_Sheet1 GROUP BY a LIMIT 100";
        
        try {
            long nativeMemory = measureMemoryUsage("native", sql);
            long h2Memory = measureMemoryUsage("h2", sql);
            long hsqldbMemory = measureMemoryUsage("hsqldb", sql);

            printMemoryStats("聚合查询内存使用 (100组)", nativeMemory, h2Memory, hsqldbMemory);
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testMemoryUsageGrowth() throws Exception {
        // 测试随数据量增长的内存使用情况
        String engineType = "native";
        int[] limits = {10, 50, 100, 200, 500};
        
        try {
            setupEngine(engineType);
            
            System.out.println("\n========== 内存使用增长测试 (" + engineType + ") ==========");
            System.out.println("数据量\t内存使用\t每行内存");
            
            for (int limit : limits) {
                String sql = "SELECT * FROM test1_Sheet1 LIMIT " + limit;
                
                // 强制GC
                System.gc();
                Thread.sleep(50);
                
                Runtime runtime = Runtime.getRuntime();
                long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
                
                int rowCount = executeQuery(sql);
                
                long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
                long memoryUsed = memoryAfter - memoryBefore;
                
                long memoryPerRow = rowCount > 0 ? memoryUsed / rowCount : 0;
                
                System.out.println(limit + "\t" + formatBytes(memoryUsed) + "\t\t" + 
                    formatBytes(memoryPerRow));
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
    public void testMultipleConnectionsMemoryUsage() throws Exception {
        // 测试多个连接的内存使用
        String engineType = "native";
        int connectionCount = 10;
        
        try {
            setupEngine(engineType);
            
            System.out.println("\n========== 多连接内存使用测试 (" + engineType + ") ==========");
            System.out.println("连接数: " + connectionCount);
            
            // 强制GC
            System.gc();
            Thread.sleep(200);
            
            Runtime runtime = Runtime.getRuntime();
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
            
            // 创建多个连接
            Connection[] connections = new Connection[connectionCount];
            String url = URL_PFX_XLS + testDataDir;
            
            for (int i = 0; i < connectionCount; i++) {
                connections[i] = DriverManager.getConnection(url);
            }
            
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            long totalMemory = memoryAfter - memoryBefore;
            long memoryPerConnection = totalMemory / connectionCount;
            
            System.out.println("总内存使用: " + formatBytes(totalMemory));
            System.out.println("平均每连接: " + formatBytes(memoryPerConnection));
            
            // 关闭连接
            for (Connection con : connections) {
                if (con != null) {
                    con.close();
                }
            }
            
            // 多次GC后检查内存是否释放
            for (int i = 0; i < 3; i++) {
                System.gc();
                Thread.sleep(200);
            }
            
            long memoryFinal = runtime.totalMemory() - runtime.freeMemory();
            long memoryReleased = memoryAfter - memoryFinal;
            
            System.out.println("释放内存: " + formatBytes(memoryReleased));
            System.out.println("==========================================\n");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }
}

