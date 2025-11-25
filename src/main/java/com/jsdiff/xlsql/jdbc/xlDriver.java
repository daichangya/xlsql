/*jsdiff.com

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

import com.jsdiff.xlsql.database.xlException;
import com.jsdiff.xlsql.database.xlInstance;

import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * xlDriver - The main JDBC Driver class for Excel SQL operations
 * 
 * @version 2.0
 * @author daichangya
 */
public class xlDriver implements Driver {
    private static final String PREFIX = Constants.URL_PFX_XLS;
    private static final int MAJOR_VERSION = 2;
    private static final int MINOR_VERSION = 0;
    private static final boolean JDBC_COMPLIANT = false;
    
    private static final Logger LOGGER = Logger.getLogger(xlDriver.class.getName());
    private static final ConcurrentHashMap<String, Driver> registeredDrivers = new ConcurrentHashMap<>();

    static {
        try {
            DriverManager.registerDriver(new xlDriver());
            LOGGER.info("xlDriver registered successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to register xlDriver", e);
            throw new IllegalStateException("Failed to register xlDriver: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if a URL can be handled by this driver
     * 
     * @param url JDBC URL to check
     * @return true if URL starts with jdbc:jsdiff:excel:
     * @throws SQLException if URL is null
     */
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) {
            throw new SQLException("URL cannot be null");
        }
        return url.startsWith(PREFIX);
    }

    /**
     * Establishes a connection to the database
     * 
     * @param url JDBC URL
     * @param info Connection properties
     * @return JDBC Connection to database
     * @throws SQLException If connection fails
     */
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }
        
        LOGGER.fine("Attempting to connect with URL: " + url);
        
        try {
            // Get configuration and instance
            String config = info.getProperty("config");
            xlInstance instance = xlInstance.getInstance(config);
            
            // 使用连接池获取后端数据库连接
            String engineUrl = instance.getUrl();
            String engineSchema = instance.getSchema();
            
            // 创建后端连接的属性
            Properties backendProps = new Properties();
            backendProps.setProperty("user", instance.getUser());
            backendProps.setProperty("password", instance.getPassword());
            
            // 注册后端驱动
            String driverClassName = instance.getDriver();
            loadAndRegisterDriver(driverClassName);
            
            // 从连接池获取连接
            Connection backendConnection = ConnectionPoolManager
                .getPool(engineUrl, backendProps)
                .getConnection();
            
            // 创建xlSQL连接
            String databasePath = resolveDatabasePath(url, instance);
            xlConnection connection = xlConnection.factory(databasePath, backendConnection, engineSchema);
            
            LOGGER.info("Connection established to " + 
                backendConnection.getMetaData().getDatabaseProductName());
            
            return connection;
            
        } catch (xlException xe) {
            LOGGER.log(Level.SEVERE, "xlSQL exception during connection", xe);
            throw new SQLException("xlSQL exception: " + xe.getMessage(), xe);
        } catch (SQLException sqe) {
            LOGGER.log(Level.SEVERE, "SQL exception during connection", sqe);
            throw sqe;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected exception during connection", e);
            throw new SQLException("Connection failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Loads and registers a JDBC driver if not already registered
     * 
     * @param className Driver class name
     * @return The driver instance
     * @throws SQLException If driver loading fails
     */
    private Driver loadAndRegisterDriver(String className) throws SQLException {
        try {
            // Check if already loaded
            Driver existingDriver = registeredDrivers.get(className);
            if (existingDriver != null) {
                return existingDriver;
            }
            
            // Load the driver class
            Class<?> driverClass = Class.forName(className);
            Driver driver = (Driver) driverClass.getDeclaredConstructor().newInstance();
            
            // Register with DriverManager if needed
            if (!isDriverRegistered(driver)) {
                DriverManager.registerDriver(driver);
                LOGGER.info("Registered backend driver: " + className);
            }
            
            // Cache the driver
            registeredDrivers.put(className, driver);
            return driver;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load driver: " + className, e);
            throw new SQLException("Failed to load driver: " + className, e);
        }
    }
    
    /**
     * Resolves the database path from URL and instance configuration
     * 
     * @param url The JDBC URL
     * @param instance The xlSQL instance
     * @return The resolved database path
     */
    private String resolveDatabasePath(String url, xlInstance instance) {
        if (url.length() == PREFIX.length()) {
            String database = instance.getDatabase();
            if (database == null || database.isEmpty()) {
                return PREFIX + System.getProperty("user.dir");
            } else {
                return PREFIX + database;
            }
        }
        return url;
    }

    /**
     * Checks if a driver is already registered with DriverManager
     * 
     * @param driver The driver to check
     * @return true if already registered
     */
    private boolean isDriverRegistered(Driver driver) {
        return Collections.list(DriverManager.getDrivers()).stream()
            .anyMatch(d -> d.getClass() == driver.getClass());
    }

    /**
     * Gets the stack trace as a string
     * 
     * @param throwable The throwable to get stack trace from
     * @return Stack trace as string
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw, true)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }

    /**
     * Gets the major version number
     * 
     * @return Major version
     */
    @Override
    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    /**
     * Gets the minor version number
     * 
     * @return Minor version
     */
    @Override
    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    /**
     * Gets property info for this driver
     * 
     * @param url JDBC URL
     * @param info Connection properties
     * @return Array of property info
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        DriverPropertyInfo[] propertyInfo = new DriverPropertyInfo[1];
        
        DriverPropertyInfo configProperty = new DriverPropertyInfo("config", null);
        configProperty.description = "Configuration file name";
        configProperty.required = false;
        propertyInfo[0] = configProperty;
        
        return propertyInfo;
    }

    /**
     * Checks if driver is JDBC compliant
     * 
     * @return JDBC compliance status
     */
    @Override
    public boolean jdbcCompliant() {
        return JDBC_COMPLIANT;
    }

    /**
     * Gets the parent logger (JDBC 4.1)
     * 
     * @return Parent logger
     * @throws SQLFeatureNotSupportedException if not supported
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger not supported");
    }
}
