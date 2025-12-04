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
package com.jsdiff.xlsql.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.jsdiff.xlsql.jdbc.DatabaseType;


/**
 * xlInstance - xlSQL配置实例（单例模式）
 * 
 * <p>该类使用单例模式管理xlSQL的配置信息，包括：</p>
 * <ul>
 *   <li>数据库引擎配置（HSQLDB或H2）</li>
 *   <li>连接参数（URL、用户名、密码等）</li>
 *   <li>日志配置</li>
 *   <li>配置文件管理</li>
 * </ul>
 * 
 * <p>支持从配置文件、系统属性和环境变量加载配置。</p>
 *
 * @author daichangya
 *
 * Changed by Csongor Nyulas (csny):
 *      - no unnecessary usage of the xlEngineDriver
 *      - initialization problems with existing configuration file solved
 * Modified to use Properties instead of XML configuration
 */
public class xlInstance {
    /** 日志记录器 */
    private static Logger logger = Logger.getLogger(xlInstance.class.getName());
    /** 单例实例（使用volatile确保可见性） */
    private static volatile xlInstance instance;
    /** 同步锁对象，用于线程安全的单例实现 */
    private static final Object lock = new Object();
    /** xlSQL默认配置目录路径 */
    private static final String XLSQL_DEFAULT_PATH = System.getProperty("user.home") +File.separator+".xlsql";
    /** 默认配置文件名称 */
    private static final String XLSQL_DEFAULT_CONFIG_PATH = "xlsql_config.properties";
    /** 默认日志文件名称 */
    private static final String XLSQL_DEFAULT_LOG_PATH = "xlsql.log";
    /** 环境变量：配置文件路径 */
    private static final String XLSQL_CONFIG_PATH_ENV = "XLSQL_CONFIG_PATH";
    /** 环境变量：日志文件路径 */
    private static final String XLSQL_LOG_PATH_ENV = "XLSQL_LOG_PATH";

    /** 配置文件名称 */
    private String configFileName;
    /** 配置文件对象 */
    private File file;
    /** 配置属性对象 */
    private Properties configProps;
    /** 配置是否被修改的标志 */
    private boolean configModified = false;

    /** 日志文件路径 */
    private String log;
    /** 数据库引擎名称（hsqldb、h2或native） */
    private String engine;
    /** 数据库路径 */
    private String database;

    /** 数据库驱动类名 */
    private String driver;
    /** 数据库连接URL */
    private String url;
    /** 数据库模式（schema）名称 */
    private String schema;
    /** 数据库用户名 */
    private String user;
    /** 数据库密码 */
    private String password;

    /**
     * 获取xlInstance单例实例（使用默认配置文件名）
     * 
     * <p>使用默认配置文件"xlsql_config.properties"创建实例。</p>
     *
     * @return xlInstance单例实例
     * @throws xlException 如果实例创建失败则抛出异常
     */
    public static xlInstance getInstance() throws xlException {
        return getInstance(XLSQL_DEFAULT_CONFIG_PATH);
    }

    /**
     * 断开连接并清除单例实例
     * 
     * <p>该方法用于清理单例实例，释放资源。此方法是线程安全的。</p>
     */
    public static void disconnect() {
        synchronized (lock) {
            instance = null;
        }
    }

    /**
     * 获取xlInstance单例实例（使用指定配置文件名）
     * 
     * <p>使用双重检查锁定模式确保线程安全。如果实例已存在则直接返回，
     * 否则创建新实例。</p>
     *
     * @param cfg 配置文件名（如"xlsql_config.properties"）
     * @return xlInstance单例实例
     * @throws xlException 如果实例创建失败则抛出异常
     */
    public static xlInstance getInstance(String cfg) throws xlException {
        if (cfg == null) {
            cfg = XLSQL_DEFAULT_CONFIG_PATH;
        }

        // 双重检查锁定模式，确保线程安全
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new xlInstance(cfg);
                }
            }
        }

        return instance;
    }

    private xlInstance(String cfg) throws xlException {
        configFileName = cfg;
        configProps = new Properties();

        try {
            if(null == configFileName || configFileName.isEmpty()){
                // 尝试从系统属性获取配置文件路径
                configFileName = System.getProperty("xlsql.config.path", configFileName);
            }
            if(null == configFileName || configFileName.isEmpty()){
                // 创建默认配置
                createDefaultConfiguration();
            }else {
                file = new File(configFileName);
            }

            if (file.exists()) {
                loadProperties();
                logger.info("Configuration file: " + file.getAbsolutePath() + " loaded");
            }else {
                if(!XLSQL_DEFAULT_CONFIG_PATH.equals(configFileName)){
                    logger.warning("Configuration file: " + file.getAbsolutePath() + " not found");
                }
                createDefaultConfiguration();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning("Failed to create default configuration: " + e.getMessage());
        }

        setupLogging();
        logger.info("Instance created with engine " + getEngine());
    }

    /**
     * 加载 Properties 配置文件
     */
    private void loadProperties() throws IOException {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            configProps.load(reader);
        }
    }

    /**
     * 保存 Properties 配置文件
     */
    private void saveProperties() {
        try {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                configProps.store(fos, "xlSQL Configuration");
            }
            configModified = false;
        } catch (IOException e) {
            logger.warning("Failed to save configuration: " + e.getMessage());
        }
    }

    /**
     * 创建默认配置
     */
    private void createDefaultConfiguration() {
        file = getFile(getDefaultConfigPath());
        this.engine = DatabaseType.NATIVE.getName();
        this.database = System.getProperty("user.dir");
        this.log = getDefaultLogPath();
        setProperty("general.database", database);
        setProperty("general.engine", engine);
        setProperty("general.log", log);

        setProperty("hsqldb.driver", "org.hsqldb.jdbcDriver");
        setProperty("hsqldb.url", "jdbc:hsqldb:.");
        setProperty("hsqldb.schema", "");
        setProperty("hsqldb.user", "sa");
        setProperty("hsqldb.password", "");

        // H2数据库配置（推荐替代HSQLDB，SQL兼容性更好）
        setProperty("h2.driver", "org.h2.Driver");
        setProperty("h2.url", "jdbc:h2:mem:xlsql");
        setProperty("h2.schema", "");
        setProperty("h2.user", "sa");
        setProperty("h2.password", "");

        // 自研NATIVE引擎配置（不依赖外部数据库）
        setProperty("native.driver", ""); // 不需要驱动
        setProperty("native.url", ""); // 不需要URL
        setProperty("native.schema", "");
        setProperty("native.user", "");
        setProperty("native.password", "");


        logger.info("Default configuration created.");
        saveProperties();
    }

    /**
     * 设置配置属性
     */
    private void setProperty(String key, String value) {
        if (value != null) {
            configProps.setProperty(key, value);
        } else {
            configProps.remove(key);
        }
        configModified = true;
    }

    /**
     * 获取配置属性
     */
    private String getProperty(String key, String defaultValue) {
        return configProps.getProperty(key, defaultValue);
    }

    /**
     * 设置日志
     */
    private void setupLogging() throws xlException {
        try {
            String logPath = getLog();
            if (logPath == null || logPath.isEmpty()) {
                logPath = getDefaultLogPath();
                setLog(logPath);
            }
            File logFile = getFile(logPath);

            boolean append = true;
            FileHandler loghandler = new FileHandler(logPath, append);
            loghandler.setFormatter(new SimpleFormatter());
            logger.addHandler(loghandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new xlException("error while creating logfile");
        }
    }

    private static File getFile(String logPath) {
        // 确保日志文件的父目录存在
        File logFile = new File(logPath);
        File logDir = logFile.getParentFile();
        if (logDir != null && !logDir.exists()) {
            logDir.mkdirs(); // 创建所有必要的父目录
        }
        return logFile;
    }


    /**
     * 获取默认配置路径
     * 
     * <p>按以下优先级查找配置路径：</p>
     * <ol>
     *   <li>系统属性 xlsql.config.path</li>
     *   <li>环境变量 XLSQL_CONFIG_PATH</li>
     *   <li>默认路径 ~/.xlsql/xlsql_config.properties</li>
     * </ol>
     * 
     * @return 配置文件路径
     */
    private String getDefaultConfigPath() {
        // 首先检查系统属性
        String configPath = System.getProperty("xlsql.config.path");
        if (configPath != null && !configPath.isEmpty()) {
            return configPath;
        }
        // 然后检查环境变量
        configPath = System.getenv(XLSQL_CONFIG_PATH_ENV);
        if (configPath != null && !configPath.isEmpty()) {
            return configPath;
        }
        // 默认使用用户主目录
        return XLSQL_DEFAULT_PATH + File.separator + XLSQL_DEFAULT_CONFIG_PATH;
    }

    /**
     * 获取默认日志路径
     * 
     * <p>按以下优先级查找日志路径：</p>
     * <ol>
     *   <li>系统属性 xlsql.log.path</li>
     *   <li>环境变量 XLSQL_LOG_PATH</li>
     *   <li>默认路径 ~/.xlsql/xlsql.log</li>
     * </ol>
     * 
     * @return 日志文件路径
     */
    private String getDefaultLogPath() {
        // 首先检查系统属性
        String logPath = System.getProperty("xlsql.log.path");
        if (logPath != null && !logPath.isEmpty()) {
            return logPath;
        }
        // 然后检查环境变量
        logPath = System.getenv(XLSQL_LOG_PATH_ENV);
        if (logPath != null && !logPath.isEmpty()) {
            return logPath;
        }
        // 默认使用用户主目录
        return XLSQL_DEFAULT_PATH + File.separator + XLSQL_DEFAULT_LOG_PATH;
    }

    /**
     * 获取日志文件路径配置
     *
     * @return 日志文件路径
     */
    public String getLog() {
        return getProperty("general.log", getDefaultLogPath());
    }

    /**
     * 获取数据库路径配置
     *
     * @return 数据库路径，如果未配置则返回当前工作目录
     */
    public String getDatabase() {
        return getProperty("general.database", System.getProperty("user.dir"));
    }

    /**
     * 获取Excel数据库导出器
     * 
     * <p>根据指定的目录路径创建数据库导出器实例，用于导出Excel数据。</p>
     *
     * @param dir 数据库目录路径
     * @return 数据库导出器实例
     * @throws xlException 如果导出器创建失败则抛出异常
     * @throws IllegalArgumentException 如果目录路径为null或无效则抛出异常
     */
    public AExporter getExporter(String dir) throws xlException {
        AExporter ret;

        if (dir != null) {
            File f = new File(dir);

            try {
                ret = xlDatabaseFactory.createExporter(f);
            } catch (xlDatabaseException xde) {
                throw new xlException("xlSQL/db reports '" + xde.getMessage()
                        + "'");
            }
        } else {
            throw new IllegalArgumentException(); //desc
        }

        return ret;
    }

    /**
     * 获取xlSQL数据库实例
     * 
     * <p>根据当前配置的数据库路径创建数据库实例。</p>
     *
     * @return xlSQL数据库实例
     * @throws xlException 如果数据库创建失败则抛出异常
     */
    public ADatabase getXlDatabase() throws xlException {
        ADatabase ret = null;

        try {
            ret = xlDatabaseFactory.createDatabase(new File(getDatabase()),
                    this);
        } catch (xlDatabaseException xde) {
            throw new xlException(xde.getMessage());
        }

        return ret;
    }

    /**
     * 连接到后端数据库引擎
     * 
     * <p>加载并注册后端数据库驱动（HSQLDB或H2），然后建立连接。</p>
     *
     * @return 到后端数据库的JDBC连接对象，如果连接失败则返回null
     */
    public Connection connect() {
        Connection ret = null;

        try {
            String classname = this.getDriver();
            logger.info("=> loading driver: " + classname);

            // 使用 getDeclaredConstructor().newInstance() 替代已废弃的 newInstance()
            Driver d = (Driver) Class.forName(classname).getDeclaredConstructor().newInstance();
            logger.info("OK. " + classname + " loaded.");
            logger.info("=> registering driver: " + classname);
            DriverManager.registerDriver(d);
            logger.info("OK. ");

            String url = getUrl();
            String user = getUser();
            String password = getPassword();
            logger.info("=> connecting to: " + user + "/" + password + "@"
                    + url);
            ret = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException nfe) {
            logger.warning("Driver not found. Classpath set?");
        } catch (InstantiationException ie) {
            logger.warning("Error while instantiating driver class. ..?");
        } catch (IllegalAccessException iae) {
            logger.warning("Illegal access. Have sources been modified?");
        } catch (NoSuchMethodException nsme) {
            logger.warning("Driver constructor not found: " + nsme.getMessage());
        } catch (java.lang.reflect.InvocationTargetException ite) {
            logger.warning("Error invoking driver constructor: " + ite.getMessage());
        } catch (SQLException sqe) {
            logger.warning("java.sql package reports: '" + sqe.getMessage()
                    + ":" + sqe.getSQLState() + "' ..?");
        }

        return ret;
    }

    /**
     * 获取支持的数据库引擎列表
     * 
     * @return 支持的引擎名称数组（当前为"general"、"hsqldb"和"h2"）
     */
    public String[] getEngines() {
        // 返回所有支持的引擎列表
        return new String[]{"general", "hsqldb", "h2", "native"};
    }

    /**
     * 添加新的数据库引擎配置
     * 
     * <p>初始化指定引擎的配置属性（驱动、URL、模式、用户、密码）。</p>
     *
     * @param engine 引擎名称（如"hsqldb"、"h2"或"native"）
     */
    public void addEngine(String engine) {
        this.engine = engine;
        setProperty("general.engine", engine);

        // 初始化引擎相关属性为空值
        setDriver("");
        setUrl("");
        setSchema("");
        setUser("");
        setPassword("");
    }

    /**
     * 移除数据库引擎配置
     * 
     * <p>从配置中删除指定引擎的所有相关属性。</p>
     *
     * @param engine 要移除的引擎名称
     */
    public void removeEngine(String engine) {
        // 移除引擎相关属性
        configProps.remove(engine + ".driver");
        configProps.remove(engine + ".url");
        configProps.remove(engine + ".schema");
        configProps.remove(engine + ".user");
        configProps.remove(engine + ".password");
        configModified = true;
    }

    /**
     * 获取当前配置的数据库引擎
     *
     * @return 引擎名称（如"hsqldb"、"h2"或"native"）
     * @throws IllegalStateException 如果引擎未配置则抛出异常
     */
    public String getEngine() {
        String ret = getProperty("general.engine", null);

        if (ret == null) {
            throw new IllegalStateException("Engine not configured");
        }

        return ret;
    }

    /**
     * 获取后端数据库的JDBC驱动类名
     *
     * @return 驱动类的全限定名（如"org.hsqldb.jdbcDriver"）
     */
    public String getDriver() {
        return getProperty(getEngine() + ".driver", "");
    }

    /**
     * 获取后端数据库的连接URL
     *
     * @return 数据库连接URL
     */
    public String getUrl() {
        return getProperty(getEngine() + ".url", "");
    }

    /**
     * 获取数据库模式（schema）名称
     * 
     * <p>模式名称用于指定数据库中的初始上下文或命名空间。</p>
     *
     * @return 模式名称
     */
    public String getSchema() {
        return getProperty(getEngine() + ".schema", "");
    }

    /**
     * 获取数据库用户名
     *
     * @return 用户名
     */
    public String getUser() {
        return getProperty(getEngine() + ".user", "");
    }

    /**
     * 获取数据库密码
     * 
     * <p><b>安全警告：</b>密码以明文形式存储在配置文件中。
     * 请确保配置文件具有适当的文件权限。生产环境建议使用密码加密或环境变量。</p>
     *
     * @return 密码（明文）
     */
    public String getPassword() {
        return getProperty(getEngine() + ".password", "");
    }

    /**
     * 获取日志记录器
     *
     * @return 实例的日志记录器对象
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * 设置日志文件路径
     *
     * @param log xlSQL日志文件的位置
     */
    public void setLog(String log) {
        this.log = log;
        setProperty("general.log", log);
        if (configModified) {
            saveProperties();
        }
    }

    /**
     * 设置数据库引擎
     * 
     * <p>切换到指定的数据库引擎，引擎必须是支持的引擎之一。</p>
     *
     * @param newengine 要设置的SQL引擎名称（如"hsqldb"、"h2"或"native"）
     * @throws xlException 如果引擎名称无效则抛出异常
     */
    public void setEngine(String newengine) throws xlException {
        // 验证引擎名称不为空
        if (newengine != null && !newengine.isEmpty()) {
            this.engine = newengine;
            setProperty("general.engine", newengine);
            if (configModified) {
                saveProperties();
            }
        } else {
            throw new xlException("Engine " + newengine + "..?! Verify.");
        }
    }

    /**
     * 设置数据库路径
     * 
     * <p>设置Excel数据库文件的根目录路径。</p>
     *
     * @param database 数据库目录路径
     */
    public void setDatabase(String database) {
        this.database = database;
        setProperty("general.database", database);
        if (configModified) {
            saveProperties();
        }
    }

    /**
     * 设置后端数据库的JDBC驱动类名
     *
     * @param driver SQL引擎的JDBC驱动类的全限定名
     */
    public void setDriver(String driver) {
        this.driver = driver;
        setProperty(getEngine() + ".driver", driver);
        if (configModified) {
            saveProperties();
        }
    }

    /**
     * 设置后端数据库的连接URL
     *
     * @param url 连接到SQL引擎所需的URL
     */
    public void setUrl(String url) {
        this.url = url;
        setProperty(getEngine() + ".url", url);
        if (configModified) {
            saveProperties();
        }
    }

    /**
     * 设置数据库模式（schema）名称
     * 
     * <p>设置连接到SQL引擎后的初始模式。</p>
     *
     * @param schema 连接到SQL引擎后的初始模式名称
     */
    public void setSchema(String schema) {
        this.schema = schema;
        setProperty(getEngine() + ".schema", schema);
        if (configModified) {
            saveProperties();
        }
    }

    /**
     * 设置数据库用户名
     *
     * @param user 连接到SQL引擎的用户名
     */
    public void setUser(String user) {
        this.user = user;
        setProperty(getEngine() + ".user", user);
        if (configModified) {
            saveProperties();
        }
    }

    /**
     * 设置数据库密码
     * 
     * <p><b>安全警告：</b>密码以明文形式存储在配置文件中。
     * 生产环境建议使用密码加密或环境变量来存储敏感凭证。</p>
     *
     * @param password SQL引擎用户的密码（以明文形式存储）
     */
    public void setPassword(String password) {
        this.password = password;
        setProperty(getEngine() + ".password", password);
        if (configModified) {
            saveProperties();
            logger.warning("安全警告：在配置文件中直接存储密码是不推荐的。建议使用更安全的方法，如环境变量或凭证管理系统。");
        }
    }
}
