/*zthinker.com

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
package com.jsdiff.xlsql.jdbc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jsdiff.xlsql.database.xlException;
import com.jsdiff.xlsql.database.xlInstance;

/**
 * xlDriver - Excel SQL操作的JDBC驱动主类
 * 
 * <p>该类实现了JDBC Driver接口，是xlSQL的核心驱动类。它负责：</p>
 * <ul>
 *   <li>注册JDBC驱动到DriverManager</li>
 *   <li>验证和接受JDBC URL</li>
 *   <li>建立到Excel文件的数据库连接</li>
 *   <li>管理后端数据库驱动（HSQLDB或MySQL）</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>
 * Class.forName("com.jsdiff.xlsql.jdbc.xlDriver");
 * String url = "jdbc:jsdiff:excel:/path/to/excel/files";
 * Connection conn = DriverManager.getConnection(url);
 * </pre>
 * 
 * @version 4.0
 * @author daichangya
 */
public class xlDriver implements Driver {
    /** JDBC URL前缀，用于识别xlSQL驱动 */
    private static final String PREFIX = Constants.URL_PFX_XLS;
    /** 主版本号 */
    private static final int MAJOR_VERSION = 4;
    /** 次版本号 */
    private static final int MINOR_VERSION = 0;
    /** JDBC兼容性标志，xlSQL不完全兼容JDBC标准 */
    private static final boolean JDBC_COMPLIANT = false;
    
    /** 日志记录器 */
    private static final Logger LOGGER = Logger.getLogger(xlDriver.class.getName());
    /** 已注册的后端驱动缓存，避免重复加载 */
    private static final ConcurrentHashMap<String, Driver> registeredDrivers = new ConcurrentHashMap<>();

    /**
     * 静态初始化块：在类加载时自动注册驱动到DriverManager
     */
    static {
        try {
            // 注册xlDriver到JDBC DriverManager
            DriverManager.registerDriver(new xlDriver());
            LOGGER.info("xlDriver registered successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to register xlDriver", e);
            throw new IllegalStateException("Failed to register xlDriver: " + e.getMessage(), e);
        }
    }

    /**
     * 检查URL是否可以被此驱动处理
     * 
     * <p>判断JDBC URL是否以"jdbc:jsdiff:excel:"开头，如果是则返回true</p>
     * 
     * @param url 待检查的JDBC URL
     * @return 如果URL以jdbc:jsdiff:excel:开头则返回true，否则返回false
     * @throws SQLException 如果URL为null则抛出异常
     */
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) {
            throw new SQLException("URL cannot be null");
        }
        // 检查URL是否以xlSQL的前缀开头
        return url.startsWith(PREFIX);
    }

    /**
     * 建立到数据库的连接
     * 
     * <p>该方法执行以下步骤：</p>
     * <ol>
     *   <li>验证URL是否被接受</li>
     *   <li>获取xlSQL配置实例</li>
     *   <li>加载并注册后端数据库驱动（HSQLDB或MySQL）</li>
     *   <li>建立后端数据库连接</li>
     *   <li>创建xlSQL连接包装器</li>
     * </ol>
     * 
     * @param url JDBC连接URL，格式：jdbc:jsdiff:excel:/path/to/excel/files
     * @param info 连接属性，可包含"config"属性指定配置文件
     * @return 到Excel数据库的JDBC连接对象
     * @throws SQLException 如果连接失败则抛出异常
     */
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        // 如果URL不被接受，返回null（JDBC规范要求）
        if (!acceptsURL(url)) {
            return null;
        }
        
        LOGGER.fine("Attempting to connect with URL: " + url);
        
        try {
            // 获取配置文件和实例
            String config = info.getProperty("config");
            xlInstance instance = xlInstance.getInstance(config);
            
            // 获取后端数据库连接信息
            String engineUrl = instance.getUrl();
            String engineSchema = instance.getSchema();
            
            // 注册后端数据库驱动（HSQLDB或MySQL）
            String driverClassName = instance.getDriver();
            loadAndRegisterDriver(driverClassName);
            
            // 创建后端数据库连接
            Connection backendConnection = DriverManager.getConnection(engineUrl,
                    instance.getUser(),
                    instance.getPassword());
            
            // 解析数据库路径并创建xlSQL连接
            String databasePath = resolveDatabasePath(url, instance);
            xlConnection connection = xlConnection.factory(databasePath, backendConnection, engineSchema);
            
            LOGGER.info("Connection established to " + 
                backendConnection.getMetaData().getDatabaseProductName());
            
            return connection;
            
        } catch (xlException xe) {
            // xlSQL特定异常
            LOGGER.log(Level.SEVERE, "xlSQL exception during connection", xe);
            throw new SQLException("xlSQL exception: " + xe.getMessage(), xe);
        } catch (SQLException sqe) {
            // SQL异常直接抛出
            LOGGER.log(Level.SEVERE, "SQL exception during connection", sqe);
            throw sqe;
        } catch (Exception e) {
            // 其他未预期的异常
            LOGGER.log(Level.SEVERE, "Unexpected exception during connection", e);
            throw new SQLException("Connection failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * 加载并注册JDBC驱动（如果尚未注册）
     * 
     * <p>该方法会检查驱动是否已加载，如果已加载则直接返回缓存的驱动实例。
     * 如果未加载，则通过反射加载驱动类并注册到DriverManager。</p>
     * 
     * @param className 驱动类的全限定名（如org.hsqldb.jdbcDriver）
     * @return 驱动实例
     * @throws SQLException 如果驱动加载失败则抛出异常
     */
    private Driver loadAndRegisterDriver(String className) throws SQLException {
        try {
            // 检查驱动是否已经加载过
            Driver existingDriver = registeredDrivers.get(className);
            if (existingDriver != null) {
                return existingDriver;
            }
            
            // 通过反射加载驱动类
            Class<?> driverClass = Class.forName(className);
            Driver driver = (Driver) driverClass.getDeclaredConstructor().newInstance();
            
            // 如果驱动尚未注册到DriverManager，则注册
            if (!isDriverRegistered(driver)) {
                DriverManager.registerDriver(driver);
                LOGGER.info("Registered backend driver: " + className);
            }
            
            // 将驱动缓存起来，避免重复加载
            registeredDrivers.put(className, driver);
            return driver;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load driver: " + className, e);
            throw new SQLException("Failed to load driver: " + className, e);
        }
    }
    
    /**
     * 从URL和实例配置中解析数据库路径
     * 
     * <p>如果URL只包含前缀（没有指定路径），则从配置实例获取数据库路径，
     * 如果配置中也没有，则使用当前工作目录。</p>
     * 
     * @param url JDBC连接URL
     * @param instance xlSQL配置实例
     * @return 解析后的数据库路径
     */
    private String resolveDatabasePath(String url, xlInstance instance) {
        // 如果URL只包含前缀，需要从配置中获取路径
        if (url.length() == PREFIX.length()) {
            String database = instance.getDatabase();
            if (database == null || database.isEmpty()) {
                // 如果配置中没有指定，使用当前工作目录
                return PREFIX + System.getProperty("user.dir");
            } else {
                // 使用配置中指定的数据库路径
                return PREFIX + database;
            }
        }
        // URL中已包含完整路径，直接返回
        return url;
    }

    /**
     * 检查驱动是否已经注册到DriverManager
     * 
     * @param driver 待检查的驱动实例
     * @return 如果驱动已注册则返回true，否则返回false
     */
    private boolean isDriverRegistered(Driver driver) {
        // 遍历所有已注册的驱动，检查是否有相同类型的驱动
        return Collections.list(DriverManager.getDrivers()).stream()
            .anyMatch(d -> d.getClass() == driver.getClass());
    }

    /**
     * 获取异常的堆栈跟踪信息字符串
     * 
     * @param throwable 要获取堆栈跟踪的异常对象
     * @return 堆栈跟踪信息字符串
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw, true)) {
            // 将堆栈跟踪信息写入StringWriter
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }

    /**
     * 获取驱动的主版本号
     * 
     * @return 主版本号（当前为4）
     */
    @Override
    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    /**
     * 获取驱动的次版本号
     * 
     * @return 次版本号（当前为0）
     */
    @Override
    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    /**
     * 获取驱动的属性信息
     * 
     * <p>返回驱动支持的连接属性列表，当前支持"config"属性用于指定配置文件。</p>
     * 
     * @param url JDBC连接URL
     * @param info 连接属性
     * @return 驱动属性信息数组
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        DriverPropertyInfo[] propertyInfo = new DriverPropertyInfo[1];
        
        // 配置config属性，用于指定配置文件名称
        DriverPropertyInfo configProperty = new DriverPropertyInfo("config", null);
        configProperty.description = "Configuration file name";
        configProperty.required = false;
        propertyInfo[0] = configProperty;
        
        return propertyInfo;
    }

    /**
     * 检查驱动是否完全符合JDBC规范
     * 
     * <p>xlSQL不完全符合JDBC规范，因此返回false。</p>
     * 
     * @return JDBC兼容性状态，当前返回false
     */
    @Override
    public boolean jdbcCompliant() {
        return JDBC_COMPLIANT;
    }

    /**
     * 获取父日志记录器（JDBC 4.1特性）
     * 
     * <p>xlSQL不支持此特性，会抛出SQLFeatureNotSupportedException异常。</p>
     * 
     * @return 父日志记录器
     * @throws SQLFeatureNotSupportedException 此特性不被支持
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger not supported");
    }
}
