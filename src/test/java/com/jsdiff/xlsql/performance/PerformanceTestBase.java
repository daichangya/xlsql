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
package com.jsdiff.xlsql.performance;

import static com.jsdiff.xlsql.jdbc.Constants.DRIVER;
import static com.jsdiff.xlsql.jdbc.Constants.URL_PFX_XLS;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.jsdiff.xlsql.database.xlInstance;
import com.jsdiff.xlsql.util.TestDataFileGenerator;

/**
 * PerformanceTestBase - 压力测试基类
 * 
 * <p>提供性能测试的公共功能，包括：
 * <ul>
 *   <li>测试环境初始化</li>
 *   <li>性能测量工具方法</li>
 *   <li>内存使用监控</li>
 *   <li>结果统计</li>
 * </ul>
 * </p>
 * 
 * @author daichangya
 */
public abstract class PerformanceTestBase {

    protected xlInstance instance;
    protected String testDataDir;

    /**
     * 在所有测试前生成测试数据文件
     * 
     * @throws Exception 如果生成失败
     */
    @BeforeAll
    public static void setUpTestData() throws Exception {
        String baseDir = System.getProperty("user.dir");
        String databaseDir = baseDir + File.separator + "database";
        
        // 确保database目录存在
        File dbDir = new File(databaseDir);
        if (!dbDir.exists()) {
            if (!dbDir.mkdirs()) {
                throw new RuntimeException("无法创建database目录: " + databaseDir);
            }
        }
        
        // 检查测试数据文件是否存在
        File test1File = new File(databaseDir, "test1.xls");
        File test2File = new File(databaseDir, "test2.xls");
        File test3File = new File(databaseDir, "test3.xls");
        
        // 如果文件不存在，自动生成
        if (!test1File.exists() || !test2File.exists() || !test3File.exists()) {
            System.out.println("测试数据文件不存在，正在生成到database目录...");
            try {
                TestDataFileGenerator.generateAllTestFiles(databaseDir);
                System.out.println("测试数据文件生成完成");
            } catch (Exception e) {
                throw new RuntimeException("无法生成测试数据文件: " + e.getMessage(), e);
            }
        }
        
        // 验证文件确实存在
        if (!test1File.exists()) {
            throw new RuntimeException("测试数据文件生成失败: database/test1.xls 不存在。请检查文件权限和磁盘空间。");
        }
        if (!test2File.exists()) {
            throw new RuntimeException("测试数据文件生成失败: database/test2.xls 不存在。请检查文件权限和磁盘空间。");
        }
        if (!test3File.exists()) {
            throw new RuntimeException("测试数据文件生成失败: database/test3.xls 不存在。请检查文件权限和磁盘空间。");
        }
    }

    @BeforeEach
    public void setUpBase() throws Exception {
        Class.forName(DRIVER);
        instance = xlInstance.getInstance();
        testDataDir = System.getProperty("user.dir") + File.separator + "database";
    }

    @AfterEach
    public void tearDownBase() throws Exception {
        if (instance != null) {
            try {
                instance.setEngine("h2");
            } catch (Exception e) {
                // 忽略
            }
        }
    }

    /**
     * 测量查询执行时间（带预热）
     * 
     * @param engineType 引擎类型
     * @param sql SQL语句
     * @return 执行时间（毫秒）
     */
    protected long measureQueryTime(String engineType, String sql) throws Exception {
        setupEngine(engineType);
        
        // 预热：执行2次查询以预热JVM和引擎
        for (int i = 0; i < 2; i++) {
            try {
                executeQuery(sql);
            } catch (Exception e) {
                // 预热失败不影响测试
            }
        }
        
        // 实际测量：执行3次取平均值
        long totalTime = 0;
        int iterations = 3;
        
        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();
            executeQuery(sql);
            long endTime = System.currentTimeMillis();
            totalTime += (endTime - startTime);
        }
        
        return totalTime / iterations;
    }

    /**
     * 重复执行查询并返回平均时间
     * 
     * @param engineType 引擎类型
     * @param sql SQL语句
     * @param iterations 迭代次数
     * @return 平均执行时间（毫秒）
     */
    protected long measureAverageQueryTime(String engineType, String sql, int iterations) 
            throws Exception {
        setupEngine(engineType);
        
        long totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();
            executeQuery(sql);
            long endTime = System.currentTimeMillis();
            totalTime += (endTime - startTime);
        }
        
        return totalTime / iterations;
    }

    /**
     * 测量查询的内存使用
     * 
     * @param engineType 引擎类型
     * @param sql SQL语句
     * @return 内存使用量（字节）
     */
    protected long measureMemoryUsage(String engineType, String sql) throws Exception {
        setupEngine(engineType);
        
        // 多次强制垃圾回收以获得准确的基准
        for (int i = 0; i < 3; i++) {
            System.gc();
            Thread.sleep(200);
        }
        
        Runtime runtime = Runtime.getRuntime();
        
        // 多次测量取平均值，减少GC影响
        long totalMemoryUsed = 0;
        int measurements = 3;
        
        for (int i = 0; i < measurements; i++) {
            // 再次GC确保基准一致
            System.gc();
            Thread.sleep(100);
            
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
            executeQuery(sql);
            
            // 等待GC完成
            System.gc();
            Thread.sleep(100);
            
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = memoryAfter - memoryBefore;
            
            // 只计算正数（负数表示GC在测量后执行）
            if (memoryUsed > 0) {
                totalMemoryUsed += memoryUsed;
            }
        }
        
        // 返回平均值，如果没有有效测量则返回0
        return totalMemoryUsed > 0 ? totalMemoryUsed / measurements : 0;
    }

    /**
     * 执行SQL查询
     * 
     * @param sql SQL语句
     * @return 结果行数
     */
    protected int executeQuery(String sql) throws Exception {
        String url = URL_PFX_XLS + testDataDir;
        int rowCount = 0;
        
        try (Connection con = DriverManager.getConnection(url);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rowCount++;
            }
        }
        
        return rowCount;
    }

    /**
     * 设置引擎
     * 
     * @param engineType 引擎类型
     */
    protected void setupEngine(String engineType) throws Exception {
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
    }

    /**
     * 打印性能统计信息
     * 
     * @param title 标题
     * @param nativeTime Native引擎时间
     * @param h2Time H2引擎时间
     * @param hsqldbTime HSQLDB引擎时间
     */
    protected void printPerformanceStats(String title, long nativeTime, long h2Time, long hsqldbTime) {
        System.out.println("\n========== " + title + " ==========");
        System.out.println("Native:  " + nativeTime + " ms");
        System.out.println("H2:      " + h2Time + " ms");
        System.out.println("HSQLDB:  " + hsqldbTime + " ms");
        
        if (nativeTime > 0) {
            System.out.println("H2/Native比率:      " + String.format("%.2f", (double)h2Time/nativeTime));
            System.out.println("HSQLDB/Native比率:  " + String.format("%.2f", (double)hsqldbTime/nativeTime));
        }
        System.out.println("==========================================\n");
    }

    /**
     * 打印内存使用统计信息
     * 
     * @param title 标题
     * @param nativeMemory Native引擎内存
     * @param h2Memory H2引擎内存
     * @param hsqldbMemory HSQLDB引擎内存
     */
    protected void printMemoryStats(String title, long nativeMemory, long h2Memory, long hsqldbMemory) {
        System.out.println("\n========== " + title + " ==========");
        System.out.println("Native:  " + formatBytes(nativeMemory));
        System.out.println("H2:      " + formatBytes(h2Memory));
        System.out.println("HSQLDB:  " + formatBytes(hsqldbMemory));
        System.out.println("==========================================\n");
    }

    /**
     * 格式化字节数
     * 
     * @param bytes 字节数
     * @return 格式化后的字符串
     */
    protected String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }
}

