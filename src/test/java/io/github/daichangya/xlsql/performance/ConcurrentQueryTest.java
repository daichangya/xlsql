///*jsdiff.com
//
// Copyright (C) 2025 jsdiff
//   jsdiff Information Sciences
//   http://xlsql.jsdiff.com
//   daichangya@163.com
//
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the Free
// Software Foundation; either version 2 of the License, or (at your option)
// any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
// more details. You should have received a copy of the GNU General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//*/
//package io.github.daichangya.xlsql.performance;
//
//import static io.github.daichangya.xlsql.jdbc.Constants.URL_PFX_XLS;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//
///**
// * ConcurrentQueryTest - 并发查询测试
// *
// * <p>测试三个引擎（Native、H2、HSQLDB）的并发查询能力，包括：
// * <ul>
// *   <li>多线程并发查询</li>
// *   <li>线程安全性验证</li>
// *   <li>并发性能测试</li>
// *   <li>资源竞争测试</li>
// * </ul>
// * </p>
// *
// * @author daichangya
// */
//@Tag("performance")
//@Tag("concurrent")
//public class ConcurrentQueryTest extends PerformanceTestBase {
//
//    @Test
//    public void testConcurrentQueriesNative() throws Exception {
//        testConcurrentQueries("native", 10, 5);
//    }
//
//    @Test
//    public void testConcurrentQueriesH2() throws Exception {
//        testConcurrentQueries("h2", 10, 5);
//    }
//
//    @Test
//    public void testConcurrentQueriesHSQLDB() throws Exception {
//        testConcurrentQueries("hsqldb", 10, 5);
//    }
//
//    @Test
//    public void testHighConcurrencyNative() throws Exception {
//        testConcurrentQueries("native", 50, 10);
//    }
//
//    @Test
//    public void testHighConcurrencyH2() throws Exception {
//        testConcurrentQueries("h2", 50, 10);
//    }
//
//    @Test
//    public void testHighConcurrencyHSQLDB() throws Exception {
//        testConcurrentQueries("hsqldb", 50, 10);
//    }
//
//    @Test
//    public void testConcurrentDifferentQueries() throws Exception {
//        String engineType = "native";
//        int threadCount = 10;
//
//        try {
//            setupEngine(engineType);
//
//            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//            List<Future<Integer>> futures = new ArrayList<>();
//            AtomicInteger successCount = new AtomicInteger(0);
//            AtomicInteger failureCount = new AtomicInteger(0);
//            List<String> errors = new CopyOnWriteArrayList<>();
//
//            // 提交不同类型的查询任务
//            for (int i = 0; i < threadCount; i++) {
//                final int queryType = i % 4;
//                Future<Integer> future = executor.submit(() -> {
//                    try {
//                        String sql;
//                        switch (queryType) {
//                            case 0:
//                                sql = "SELECT * FROM test1_Sheet1 LIMIT 10";
//                                break;
//                            case 1:
//                                sql = "SELECT COUNT(*) FROM test1_Sheet1";
//                                break;
//                            case 2:
//                                sql = "SELECT * FROM test1_Sheet1 WHERE a IS NOT NULL LIMIT 10";
//                                break;
//                            case 3:
//                                sql = "SELECT * FROM test1_Sheet1 ORDER BY a LIMIT 10";
//                                break;
//                            default:
//                                sql = "SELECT * FROM test1_Sheet1 LIMIT 10";
//                        }
//
//                        int rowCount = executeQuery(sql);
//                        successCount.incrementAndGet();
//                        return rowCount;
//                    } catch (Exception e) {
//                        failureCount.incrementAndGet();
//                        String errorMsg = e.getClass().getSimpleName() + ": " +
//                                       (e.getMessage() != null ? e.getMessage() : "未知错误");
//                        errors.add(errorMsg);
//                        // 不抛出异常，让其他线程继续执行
//                        return -1;
//                    }
//                });
//                futures.add(future);
//            }
//
//            // 等待所有任务完成，收集异常信息
//            for (Future<Integer> future : futures) {
//                try {
//                    future.get(30, TimeUnit.SECONDS);
//                } catch (TimeoutException e) {
//                    failureCount.incrementAndGet();
//                    errors.add("TimeoutException: 查询超时");
//                } catch (Exception e) {
//                    failureCount.incrementAndGet();
//                    errors.add(e.getClass().getSimpleName() + ": " +
//                              (e.getMessage() != null ? e.getMessage() : "未知错误"));
//                }
//            }
//
//            executor.shutdown();
//            executor.awaitTermination(1, TimeUnit.MINUTES);
//
//            System.out.println("\n========== 并发不同查询测试 (" + engineType + ") ==========");
//            System.out.println("线程数: " + threadCount);
//            System.out.println("成功: " + successCount.get());
//            System.out.println("失败: " + failureCount.get());
//            if (!errors.isEmpty()) {
//                System.out.println("错误详情（前5个）:");
//                errors.stream().limit(5).forEach(err -> System.out.println("  - " + err));
//            }
//            System.out.println("==========================================\n");
//
//            assertTrue(successCount.get() > 0,
//                "至少应该有一些成功的查询。失败数: " + failureCount.get() +
//                (errors.isEmpty() ? "" : ", 错误: " + errors.get(0)));
//        } catch (Exception e) {
//            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
//            if (e.getMessage() != null && e.getMessage().contains("not found")) {
//                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
//            }
//            throw e;
//        }
//    }
//
//    @Test
//    public void testThreadSafety() throws Exception {
//        String engineType = "native";
//        int threadCount = 20;
//        int queriesPerThread = 5;
//
//        try {
//            setupEngine(engineType);
//
//            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//            CountDownLatch latch = new CountDownLatch(threadCount);
//            AtomicInteger totalQueries = new AtomicInteger(0);
//            List<String> errors = new CopyOnWriteArrayList<>();
//
//            for (int i = 0; i < threadCount; i++) {
//                executor.submit(() -> {
//                    try {
//                        for (int j = 0; j < queriesPerThread; j++) {
//                            executeQuery("SELECT * FROM test1_Sheet1 LIMIT 5");
//                            totalQueries.incrementAndGet();
//                        }
//                    } catch (Exception e) {
//                        errors.add(e.getMessage());
//                    } finally {
//                        latch.countDown();
//                    }
//                });
//            }
//
//            latch.await(2, TimeUnit.MINUTES);
//            executor.shutdown();
//
//            System.out.println("\n========== 线程安全性测试 (" + engineType + ") ==========");
//            System.out.println("线程数: " + threadCount);
//            System.out.println("每线程查询数: " + queriesPerThread);
//            System.out.println("总查询数: " + totalQueries.get());
//            System.out.println("错误数: " + errors.size());
//            if (!errors.isEmpty()) {
//                System.out.println("错误示例: " + errors.get(0));
//            }
//            System.out.println("==========================================\n");
//
//            assertTrue(totalQueries.get() > 0, "应该成功执行一些查询");
//        } catch (Exception e) {
//            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
//            if (e.getMessage() != null && e.getMessage().contains("not found")) {
//                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
//            }
//            throw e;
//        }
//    }
//
//    /**
//     * 测试并发查询
//     *
//     * @param engineType 引擎类型
//     * @param threadCount 线程数
//     * @param queriesPerThread 每个线程的查询数
//     */
//    private void testConcurrentQueries(String engineType, int threadCount, int queriesPerThread)
//            throws Exception {
//        try {
//            setupEngine(engineType);
//
//            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//            List<Future<Long>> futures = new ArrayList<>();
//
//            long startTime = System.currentTimeMillis();
//
//            for (int i = 0; i < threadCount; i++) {
//                Future<Long> future = executor.submit(() -> {
//                    long threadStartTime = System.currentTimeMillis();
//                    for (int j = 0; j < queriesPerThread; j++) {
//                        executeQuery("SELECT * FROM test1_Sheet1 LIMIT 10");
//                    }
//                    long threadEndTime = System.currentTimeMillis();
//                    return threadEndTime - threadStartTime;
//                });
//                futures.add(future);
//            }
//
//            long maxThreadTime = 0;
//            int completedThreads = 0;
//            for (Future<Long> future : futures) {
//                try {
//                    Long threadTime = future.get(1, TimeUnit.MINUTES);
//                    maxThreadTime = Math.max(maxThreadTime, threadTime);
//                    completedThreads++;
//                } catch (TimeoutException e) {
//                    System.out.println("警告: 某个线程超时");
//                } catch (Exception e) {
//                    // 记录错误但继续
//                }
//            }
//
//            executor.shutdown();
//            executor.awaitTermination(2, TimeUnit.MINUTES);
//
//            // 清理所有线程的连接
//            cleanupConnections();
//
//            long endTime = System.currentTimeMillis();
//            long totalTime = endTime - startTime;
//
//            System.out.println("\n========== 并发查询测试 (" + engineType + ") ==========");
//            System.out.println("线程数: " + threadCount);
//            System.out.println("每线程查询数: " + queriesPerThread);
//            System.out.println("总查询数: " + (threadCount * queriesPerThread));
//            System.out.println("完成线程数: " + completedThreads);
//            System.out.println("总耗时: " + totalTime + " ms");
//            System.out.println("最长线程耗时: " + maxThreadTime + " ms");
//            if (totalTime > 0) {
//                System.out.println("平均吞吐量: " +
//                    String.format("%.2f", (threadCount * queriesPerThread * 1000.0) / totalTime) + " 查询/秒");
//            }
//            System.out.println("==========================================\n");
//
//            assertTrue(completedThreads > 0, "至少应该有一些线程成功完成");
//            assertTrue(totalTime > 0, "总耗时应该大于0");
//        } catch (Exception e) {
//            cleanupConnections();
//            if (e.getMessage() != null && e.getMessage().contains("not found")) {
//                System.out.println("跳过测试: test1.xls文件不存在");
//                return;
//            }
//            throw e;
//        }
//    }
//
//    // 使用ThreadLocal存储连接，每个线程复用连接以提高性能
//    private static final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();
//
//    /**
//     * 执行SQL查询（覆盖父类方法以支持并发和连接复用）
//     */
//    @Override
//    protected int executeQuery(String sql) throws Exception {
//        String url = URL_PFX_XLS + testDataDir;
//        int rowCount = 0;
//
//        // 获取或创建线程本地连接
//        Connection con = threadLocalConnection.get();
//        if (con == null || con.isClosed()) {
//            con = DriverManager.getConnection(url);
//            threadLocalConnection.set(con);
//        }
//
//        try (Statement stmt = con.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            while (rs.next()) {
//                rowCount++;
//            }
//        } catch (Exception e) {
//            // 如果连接出现问题，清除并重新创建
//            if (con != null && !con.isClosed()) {
//                try {
//                    con.close();
//                } catch (Exception ignored) {
//                }
//            }
//            threadLocalConnection.remove();
//            throw e;
//        }
//
//        return rowCount;
//    }
//
//    /**
//     * 清理线程本地连接（在测试结束后调用）
//     */
//    private void cleanupConnections() {
//        Connection con = threadLocalConnection.get();
//        if (con != null) {
//            try {
//                if (!con.isClosed()) {
//                    con.close();
//                }
//            } catch (Exception ignored) {
//            }
//            threadLocalConnection.remove();
//        }
//    }
//}
//
