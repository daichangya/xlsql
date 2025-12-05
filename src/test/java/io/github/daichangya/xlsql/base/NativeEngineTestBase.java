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
package io.github.daichangya.xlsql.base;

import static io.github.daichangya.xlsql.jdbc.Constants.DRIVER;
import static io.github.daichangya.xlsql.jdbc.Constants.URL_PFX_XLS;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import io.github.daichangya.xlsql.database.xlInstance;
import io.github.daichangya.xlsql.util.TestDataFileGenerator;

/**
 * NativeEngineTestBase - Native引擎测试基类
 * 
 * <p>提供所有Native引擎测试的公共setup/teardown逻辑。
 * 所有Native引擎相关的测试类应该继承此类。</p>
 * 
 * @author daichangya
 */
public abstract class NativeEngineTestBase {

    /** JDBC连接对象 */
    protected Connection con;
    
    /** xlInstance实例 */
    protected xlInstance instance;
    
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
    
    /**
     * 测试前的公共初始化逻辑
     * 
     * @throws Exception 如果初始化失败
     */
    @BeforeEach
    public void setUpBase() throws Exception {
        // 确保xlDriver已注册
        Class.forName(DRIVER);
        
        // 获取xlInstance实例并设置为native引擎
        instance = xlInstance.getInstance();
        instance.setEngine("native");
        
        // 使用xlDriver连接，将自动使用xlConnectionNative
        // 连接URL指向database目录
        String databaseDir = System.getProperty("user.dir") + File.separator + "database";
        String url = URL_PFX_XLS + databaseDir;
        con = DriverManager.getConnection(url);
    }
    
    /**
     * 测试后的公共清理逻辑
     * 
     * @throws Exception 如果清理失败
     */
    @AfterEach
    public void tearDownBase() throws Exception {
        if (con != null && !con.isClosed()) {
            con.close();
        }
        
        // 重置引擎为h2，避免影响其他测试
        if (instance != null) {
            try {
                instance.setEngine("h2");
            } catch (io.github.daichangya.xlsql.database.xlException e) {
                // 忽略重置引擎时的异常
            }
        }
    }
    
    /**
     * 获取测试数据目录路径
     * 
     * @return 测试数据目录路径（database目录）
     */
    protected String getTestDataDir() {
        return System.getProperty("user.dir") + File.separator + "database";
    }
}

