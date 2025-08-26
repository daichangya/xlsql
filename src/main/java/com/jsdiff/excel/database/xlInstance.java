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
package com.jsdiff.excel.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
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


/**
 * Represents an instance of xlSQL
 *
 * @author daichangya
 *
 * Changed by Csongor Nyulas (csny):
 *      - no unnecessary usage of the xlEngineDriver
 *      - initialization problems with existing configuration file solved
 * Modified to use Properties instead of XML configuration
 */
public class xlInstance {
    private static Logger logger = Logger.getLogger(xlInstance.class.getName());
    private static xlInstance instance;
    private static final String XLSQL_DEFAULT_PATH = System.getProperty("user.home") +File.separator+".xlsql";
    private static final String XLSQL_DEFAULT_CONFIG_PATH = "xlsql_config.properties";
    private static final String XLSQL_DEFAULT_LOG_PATH = "xlsql.log";

    private String configFileName;
    private File file;
    private Properties configProps;
    private boolean configModified = false;

    //
    private String log;
    private String engine;
    private String database;

    //
    private String driver;
    private String url;
    private String schema;
    private String user;
    private String password;

    /**
     * Creates an xlInstance with the name xlsql
     *
     * @return xlInstance
     *
     * @throws xlException [Tbd. When?]
     */
    public static xlInstance getInstance() throws xlException {
        return getInstance(XLSQL_DEFAULT_CONFIG_PATH);
    }

    /**
     */
    public static void disconnect() {
        instance = null;
    }

    /**
     * Creates an xlInstance
     *
     * @param cfg name of configuration [cfg]_config.properties on disk
     *
     * @return xlInstance
     *
     * @throws xlException [Tbd. When?]
     */
    public static xlInstance getInstance(String cfg) throws xlException {
        xlInstance ret = null;

        if (cfg == null) {
            cfg = XLSQL_DEFAULT_CONFIG_PATH;
        }

        if (instance == null) {
            ret = new xlInstance(cfg);
        } else {
            ret = instance;
        }

        return ret;
    }

    private xlInstance(String cfg) throws xlException {
        instance = this;
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
        this.engine = "hsqldb";
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
     * @return 获取默认配置路径
     */
    private String getDefaultConfigPath() {
        String configPath = System.getProperty("xlsql.config.path");
        if (configPath != null && !configPath.isEmpty()) {
            return configPath;
        }
        // 默认使用用户目录下的日志文件
        return XLSQL_DEFAULT_PATH + File.separator + XLSQL_DEFAULT_CONFIG_PATH;
    }


    /**
     * 获取默认日志路径
     * @return 默认日志文件路径
     */
    private String getDefaultLogPath() {
        String logPath = System.getProperty("xlsql.log.path");
        if (logPath != null && !logPath.isEmpty()) {
            return logPath;
        }
        // 默认使用用户目录下的日志文件
        return XLSQL_DEFAULT_PATH + File.separator + XLSQL_DEFAULT_LOG_PATH;
    }

    /**
     * get log property
     *
     * @return log
     */
    public String getLog() {
        return getProperty("general.log", getDefaultLogPath());
    }

    /**
     * get database property
     *
     * @return database
     */
    public String getDatabase() {
        return getProperty("general.database", System.getProperty("user.dir"));
    }

    /**
     * Excel database exporter
     *
     * @param dir Path to database
     *
     * @return Database exporter
     *
     * @throws xlException When an error occurs
     * @throws IllegalArgumentException When dir is null or invalid
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
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws xlException DOCUMENT ME!
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
     * get java.sql.Connection to engine
     *
     * @return Connection
     */
    public Connection connect() {
        Connection ret = null;

        try {
            String classname = this.getDriver();
            logger.info("=> loading driver: " + classname);

            Driver d = (Driver) Class.forName(classname).newInstance();
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
        } catch (SQLException sqe) {
            logger.warning("java.sql package reports: '" + sqe.getMessage()
                    + ":" + sqe.getSQLState() + "' ..?");
        }

        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getEngines() {
        // 简化实现，返回固定引擎列表
        return new String[]{"general", "hsqldb"};
    }

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     */
    public void addEngine(String engine) {
        this.engine = engine;
        setProperty("general.engine", engine);

        // 初始化引擎相关属性
        setDriver("");
        setUrl("");
        setSchema("");
        setUser("");
        setPassword("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
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
     * get engine property
     *
     * @return engine
     *
     * @throws IllegalStateException DOCUMENT ME!
     */
    public String getEngine() {
        String ret = getProperty("general.engine", null);

        if (ret == null) {
            throw new IllegalStateException("Engine not configured");
        }

        return ret;
    }

    /**
     * get jdbc driver of sql engine
     *
     * @return driver
     */
    public String getDriver() {
        return getProperty(getEngine() + ".driver", "");
    }

    /**
     * get url of sql engine
     *
     * @return url
     */
    public String getUrl() {
        return getProperty(getEngine() + ".url", "");
    }

    /**
     * get schema, database, initial context of sql engine
     *
     * @return schema
     */
    public String getSchema() {
        return getProperty(getEngine() + ".schema", "");
    }

    /**
     * get user of sql engine
     *
     * @return user
     */
    public String getUser() {
        return getProperty(getEngine() + ".user", "");
    }

    /**
     * get password of sql engine user
     *
     * @return password
     */
    public String getPassword() {
        return getProperty(getEngine() + ".password", "");
    }

    /**
     * get logger
     *
     * @return handle to instance logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * set log property
     *
     * @param log location of xlsql logfile
     */
    public void setLog(String log) {
        this.log = log;
        setProperty("general.log", log);
        if (configModified) {
            saveProperties();
        }
    }

    /**
     * set engine
     *
     * @param newengine any of the supported sql engines
     *
     * @throws xlException DOCUMENT ME!
     */
    public void setEngine(String newengine) throws xlException {
        // 简化验证
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
     * set database
     *
     * @param database path to database
     */
    public void setDatabase(String database) {
        this.database = database;
        setProperty("general.database", database);
        if (configModified) {
            saveProperties();
        }
    }

    /**
     * set driver
     *
     * @param driver java class of driver of sql engine
     */
    public void setDriver(String driver) {
        this.driver = driver;
        setProperty(getEngine() + ".driver", driver);
        if (configModified) {
            saveProperties();
        }
    }

    /**
     * set url
     *
     * @param url required url for connecting to sql engine
     */
    public void setUrl(String url) {
        this.url = url;
        setProperty(getEngine() + ".url", url);
        if (configModified) {
            saveProperties();
        }
    }

    /**
     * set schema
     *
     * @param schema initial schema after connect to sql engine
     */
    public void setSchema(String schema) {
        this.schema = schema;
        setProperty(getEngine() + ".schema", schema);
        if (configModified) {
            saveProperties();
        }
    }

    /**
     * set user
     *
     * @param user user which connects to sql engine
     */
    public void setUser(String user) {
        this.user = user;
        setProperty(getEngine() + ".user", user);
        if (configModified) {
            saveProperties();
        }
    }

    /**
     * set password
     *
     * @param password password of sql engine user
     */
    public void setPassword(String password) {
        this.password = password;
        setProperty(getEngine() + ".password", password);
        if (configModified) {
            saveProperties();
        }
    }
}
