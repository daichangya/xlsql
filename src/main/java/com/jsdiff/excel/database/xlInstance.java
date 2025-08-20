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

import org.jconfig.*;
import org.jconfig.event.*;
import org.jconfig.handler.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
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
 */
public class xlInstance implements ConfigurationListener {
    private static Logger logger;
    private static xlInstance instance;
    private static final String XLSQL = "xlsql";
    private String name;
    private File file;
    private XMLFileHandler handler;
    private ConfigurationManager cm;
    private Configuration config;

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
        xlInstance ret;
        ret = getInstance(XLSQL);

        return ret;
    }

    /**
     */
    public static void disconnect() {
        instance = null;
    }

    /**
     * Creates an xlInstance
     *
     * @param cfg name of configuration [cfg]_config.xml on disk
     *
     * @return xlInstance
     *
     * @throws xlException [Tbd. When?]
     */
    public static xlInstance getInstance(String cfg) throws xlException {
        xlInstance ret = null;

        if (cfg == null) {
            cfg = XLSQL;
        }

        if (instance == null) {
            ret = new xlInstance(cfg);
        } else {
            ret = instance;
        }

        return ret;
    }

    private xlInstance(String cfg) throws xlException {
        logger = Logger.getLogger(this.getClass().getName());
        instance = this;
        name = cfg;

        try {
            // 首先尝试从系统属性获取配置文件路径
            String configPath = System.getProperty("xlsql.config.path");
            if (configPath != null && !configPath.isEmpty()) {
                file = new File(configPath);
            } else {
                // 尝试在当前目录查找配置文件
                file = new File("xlsql_config.xml");

                // 如果当前目录没有配置文件，尝试从 resources 复制默认配置
                if (!file.exists()) {
                    copyDefaultConfigFromResources();
                }
            }

            handler = new XMLFileHandler();
            handler.setFile(file);

            cm = ConfigurationManager.getInstance();

            if (file.exists()) {
                cm.load(handler, name);
                config = ConfigurationManager.getConfiguration(name);
                config.addConfigurationListener(this);

                engine = config.getProperty("engine", null, "general");
                config.setCategory(engine, true);
                logger.info("Configuration file: " + file.getAbsolutePath() + " loaded");
            } else {
                // 创建默认配置
                createDefaultConfiguration();
            }
        } catch (ConfigurationManagerException cme) {
            config = ConfigurationManager.getConfiguration(name);
        } catch (Exception e) {
            try {
                // 如果从 resources 复制失败，创建默认配置
                createDefaultConfiguration();
            }catch (Exception e1){

            }
        }

        setupLogging();
        logger.info("Instance created with engine " + getEngine());
    }

    /**
     * 从 resources 复制默认配置文件
     */
    private void copyDefaultConfigFromResources() throws IOException {
        // 从 classpath 获取默认配置文件
        InputStream defaultConfigStream = getClass().getClassLoader()
                .getResourceAsStream("xlsql_config.xml");

        if (defaultConfigStream != null) {
            // 复制到当前目录
            try (FileOutputStream fos = new FileOutputStream("xlsql_config.xml")) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = defaultConfigStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }
            defaultConfigStream.close();
            logger.info("Default configuration copied from resources");
        }
    }

    /**
     * 创建默认配置
     */
    private void createDefaultConfiguration() throws ConfigurationManagerException {
        config = ConfigurationManager.getConfiguration(name);
        config.addConfigurationListener(this);

        assert (config.isNew());

        config.setCategory("general", true);

        String engine = "hsqldb";
        setLog(getDefaultLogPath());
        setDatabase(System.getProperty("user.dir"));

        this.engine = engine;
        config.setProperty("engine", engine);
        addEngine(engine);
        config.setCategory(engine, true);

        setDriver("org.hsqldb.jdbcDriver");
        setUrl("jdbc:hsqldb:.");
        setSchema("");
        setUser("sa");
        setPassword("");
        config.setCategory(getEngine(), true);
        logger.info("Default configuration created.");
    }

    /**
     * 设置日志
     */
    private void setupLogging() throws xlException {
        try {
            if (getLog() == null) {
                setLog(getDefaultLogPath());
            }

            boolean append = true;
            FileHandler loghandler = new FileHandler(getLog(), append);
            loghandler.setFormatter(new SimpleFormatter());
            logger.addHandler(loghandler);
        } catch (IOException e) {
            throw new xlException("error while creating logfile");
        }
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
        return System.getProperty("user.dir") + File.separator + "xlsql.log";
    }
    /**
     * get log property
     *
     * @return log
     */
    public String getLog() {
        return config.getProperty("log", getDefaultLogPath(), "general");
    }

    /**
     * get log property
     *
     * @return log
     */
    public String getDatabase() {
        return config.getProperty("database", System.getProperty("user.dir"),
                                  "general");
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

            ;
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
//csny:       xlEngineDriver was eliminated because it was unnecessary
//            DriverManager.registerDriver(new xlEngineDriver(d));
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
        return config.getCategoryNames();
    }

    /**
     * DOCUMENT ME!
     *
     * @param engine DOCUMENT ME!
     */
    public void addEngine(String engine) {
        this.engine = engine;
        config.setCategory(engine, true);
        config.setProperty("engine", engine, "general");


        //
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
        if (engine.equalsIgnoreCase(engine)) {
            config.removeCategory(engine);
        }
    }

    /**
     * get engine property
     *
     * @return engine
     *
     * @throws IllegalStateException DOCUMENT ME!
     */
    public String getEngine() {
        String ret;
        ret = config.getProperty("engine", null, "general");

        if (ret == null) {
            throw new IllegalStateException(); //desc
        }

        return ret;
    }

    /**
     * get jdbc driver of sql engine
     *
     * @return driver
     */
    public String getDriver() {
        return config.getProperty("driver", engine);
    }

    /**
     * get url of sql engine
     *
     * @return url
     */
    public String getUrl() {
        return config.getProperty("url", engine);
    }

    /**
     * get schema, database, initial context of sql engine
     *
     * @return schema
     */
    public String getSchema() {
        return config.getProperty("schema", engine);
    }

    /**
     * get user of sql engine
     *
     * @return user
     */
    public String getUser() {
        return config.getProperty("user", engine);
    }

    /**
     * get password of sql engine user
     *
     * @return password
     */
    public String getPassword() {
        return config.getProperty("password", engine);
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
        config.setProperty("log", log, "general");
    }

    /**
     * set engine
     *
     * @param newengine any of the supported sql engines
     *
     * @throws xlException DOCUMENT ME!
     */
    public void setEngine(String newengine) throws xlException {
        String[] engines = getEngines();
        boolean found = false;

        for (int i = 0; i < engines.length; i++) {
            if (newengine.equalsIgnoreCase(engines[i])) {
                found = true;

                break;
            }
        }

        if (found) {
            this.engine = newengine;
            config.setProperty("engine", newengine, "general");
            config.setCategory(newengine, true);
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
        config.setProperty("database", database, "general");
    }

    /**
     * set driver
     *
     * @param driver java class of driver of sql engine
     */
    public void setDriver(String driver) {
        this.driver = driver;
        config.setCategory(engine, true);
        config.setProperty("driver", driver, engine);
    }

    /**
     * set url
     *
     * @param url required url for connecting to sql engine
     */
    public void setUrl(String url) {
        this.url = url;
        config.setCategory(engine, true);
        config.setProperty("url", url, engine);
    }

    /**
     * set schema
     *
     * @param schema initial schema after connect to sql engine
     */
    public void setSchema(String schema) {
        this.schema = schema;
        config.setCategory(engine, true);
        config.setProperty("schema", schema, engine);
    }

    /**
     * set user
     *
     * @param user user which connects to sql engine
     */
    public void setUser(String user) {
        this.user = user;
        config.setCategory(engine, true);
        config.setProperty("user", user, engine);
    }

    /**
     * set password
     *
     * @param password password of sql engine user
     */
    public void setPassword(String password) {
        this.password = password;
        config.setCategory(engine, true);
        config.setProperty("password", password, engine);
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void configurationChanged(org.jconfig.event.ConfigurationChangedEvent e) {
        saveConfig();
    }

    /**
     * DOCUMENT ME!
     *
     * @param categoryChangedEvent DOCUMENT ME!
     */
    public void categoryChanged(org.jconfig.event.CategoryChangedEvent categoryChangedEvent) {
        saveConfig();
    }

    /**
     * DOCUMENT ME!
     *
     * @param propertyChangedEvent DOCUMENT ME!
     */
    public void propertyChanged(org.jconfig.event.PropertyChangedEvent propertyChangedEvent) {
        saveConfig();
    }

    private void saveConfig() {
        try {
            if (config.isNew()) {
                cm.save(handler, config);
            } else {
                cm.save(name);
                cm.save(handler, config);
            }
        } catch (ConfigurationManagerException cfe) {
            logger.warning("xlSQL> -WRN: ConfigurationManagerException ..?");
        }

        config.resetCreated();
    }
}

