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
package com.jsdiff.excel.jdbc;

//import com.jsdiff.excel.database.xlEngineDriver;
import com.jsdiff.excel.database.xlException;
import com.jsdiff.excel.database.xlInstance;

import java.io.*;
import java.sql.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * xlDriver the main 'JDBC Driver' class, implements java.sql.Driver
 * 
 * @version $Revision: 1.10 $
 * @author $author$
 *
 * Changed by Csongor Nyulas (csny): corrected xlDriver:
 *        - no unnecessary usage of the xlEngineDriver
 *        - no repeated registration of the xlDriver or its back-end driver(s)
 *        - connect() method checks for valid URLs 
 *        //- classpath correction so that DriverManager can find the configuration file
 */
public class xlDriver implements Driver {
    private static final String PREFIX = Constants.URL_PFX_XLS;
    private static final int MAJOR_VERSION = 0;
    private static final int MINOR_VERSION = 0;
    private static final boolean JDBC_COMPLIANT = false;
    Logger logger;

    static {
        try {
            DriverManager.registerDriver(new xlDriver());
        	//csny 
            // attemp to make jconfig find the xlsql_config.xml file,
            // which is usually created in the local directory (".").
            // Calling this method seems to be unnecessary, after the 
            // corrections made in database.xlInstance class.
            //correctClassPath();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Checks if a url can be servived by this driver
     * 
     * @param url jdbc url
     * 
     * @return true if url starts with jdbc:jsdiff:excel:
     * 
     * @throws SQLException when url is null
     */
    public boolean acceptsURL(String url) throws SQLException {
        if (url != null) {
            return (url.startsWith(PREFIX));
        } else {
            throw new SQLException("Invalid url");
        }
    }

    /**
     * Establish connection
     * 
     * @param url JDBC url
     * @param info not used by xlSQL
     * 
     * @return JDBC Connection to database
     * 
     * @throws SQLException If the url does not contain a valid database path
     */
    public Connection connect(String url, Properties info)
                       throws SQLException {
    	if (! acceptsURL(url)) {
    		return null;
    	}
    	
        xlConnection ret = null;
        try {
            //
            // Connect to xlInstance
            String config = info.getProperty("config");
            xlInstance instance = xlInstance.getInstance(config);
            logger = instance.getLogger();
            
            //
            // Connect to SQL engine

            DriverManager.deregisterDriver(this);
//csny loading the same drivers over and over again is not necessary. xlEngineDriver can be also eliminated.
//            String classname = "com.mysql.jdbc.Driver";
//            Driver d = (Driver) Class.forName(classname).newInstance();
//            if (! isAWrappingxlEngineDriverAlreadyRegisteredFor(d))
//            	DriverManager.registerDriver(new xlEngineDriver(d));
//
//            classname = "org.hsqldb.jdbcDriver";
//            d = (Driver) Class.forName(classname).newInstance();
//            if (! isAWrappingxlEngineDriverAlreadyRegisteredFor(d))
//            	DriverManager.registerDriver(new xlEngineDriver(d));
            
            String classname = instance.getDriver();
            Driver d = (Driver) Class.forName(classname).newInstance();
//            if (! isAWrappingxlEngineDriverAlreadyRegisteredFor(d))
//            	DriverManager.registerDriver(new xlEngineDriver(d));
            if (! isBackendDriverAlreadyRegistered(d))
            	DriverManager.registerDriver(d);
			
            String eng_url = instance.getUrl();
            String eng_sch = instance.getSchema();
            String eng_usr = instance.getUser();
            String eng_pwd = instance.getPassword();

            Connection c = DriverManager.getConnection(eng_url, eng_usr, 
                                                       eng_pwd);
            DriverManager.registerDriver(this);
            //
            // Create a connection to xlSQL
            String database = instance.getDatabase();
            if (url.length() == PREFIX.length()) {
                if ((database == null) || (database.length() == 0)) {
                    url = PREFIX + System.getProperty("user.dir");
                } else {
                    url = PREFIX + database;
                }
            }
            ret = xlConnection.factory(url, c, eng_sch);
            String cName = c.getMetaData().getDatabaseProductName();
            logger.info("Connection to " + cName + " established.");
            
        } catch (xlException xe) {
            xe.printStackTrace();
        } catch (SQLException sqe) {
            throw sqe; 
        } catch (Exception e) {
            logger.info("Exception: " + getStackTrace(e));
            throw new SQLException(e.getCause());
        }
        //csny
        finally {
        	registerThisIfnecessary();
        }

        return ret;
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    /**
     * Supplies Major Version
     * 
     * @return Major Version
     */
    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    /**
     * Supplies Minor Version
     * 
     * @return Minor Version
     */
    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    /**
     * ( Required for generic tool bakers )
     * 
     * @param url JDBC url
     * @param info name of xlsql configuration
     * 
     * @return ( ? )
     */
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        DriverPropertyInfo[] pinfo = new DriverPropertyInfo[1];
        DriverPropertyInfo p;

        p = new DriverPropertyInfo("config", null);
        p.description = "configuration";
        p.required = false;
        pinfo[0] = p;

        return pinfo;
    }

    /**
     * Supplies JDBC compliancy level ( true or false )
     * 
     * @return xlSQL reports always false
     */
    public boolean jdbcCompliant() {
        return JDBC_COMPLIANT;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }


//    /**
//     * This function was intended to check if there is an xlEngineDriver wrapping
//     * driver <code>d</code> registered with the DriverManager. Function
//     * not used anymore since, we tentatively eliminated xlEngineDriver class.
//     * 
//     * @param d a Driver
//     * @deprecated Use isBackendDriverAlreadyRegistered(Driver d) instead
//     * @return <code>true</code> if a registered wrapping xlEngineDriver for d 
//     * was found, <code>false</code> otherwise
//     * 
//     * @author csny
//     */
//    public boolean isAWrappingxlEngineDriverAlreadyRegisteredFor(Driver d) {
//    	Enumeration<Driver> drivers = DriverManager.getDrivers();
//    	boolean res = false;
//    	while (drivers.hasMoreElements()) {
//    		Driver driver = drivers.nextElement();
//    		if ( driver.getClass() == xlEngineDriver.class ) {
//    			xlEngineDriver xlEngDriver = (xlEngineDriver) driver;
//    			if (xlEngDriver.getWrappedDriver().getClass() == d.getClass()) {
//    				res = true;
//    				break;
//    			}
//    		}
//    	}
//    	return res;
//    }
    
    /**
     * This function checks if driver <code>d</code> was registered with the 
     * DriverManager.
     * 
     * @param d a Driver
     * @return <code>true</code> if <code>d</code> was already registered with 
     * the DriverManager, <code>false</code> otherwise.
     * 
     * @author csny
     */
    public boolean isBackendDriverAlreadyRegistered(Driver d) {
    	Enumeration<Driver> drivers = DriverManager.getDrivers();
    	boolean res = false;
    	while (drivers.hasMoreElements()) {
    		Driver driver = drivers.nextElement();
			if (driver.getClass() == d.getClass()) {
				res = true;
				break;
			}
    	}
    	return res;
    }
    
    /**
     * Registers this xlDriver with the DriverManager only if it was not 
     * already registered before, in order to avoid multiple registration.
     *  
     * @throws SQLException if something goes wrong with the detection of
     * the already registered drivers or the registration of this
     * 
     * @author csny
     */
    public void registerThisIfnecessary() throws SQLException {
    	Enumeration<Driver> drivers = DriverManager.getDrivers();
    	boolean res = false;
    	while (drivers.hasMoreElements()) {
    		Driver driver = drivers.nextElement();
    		if ( driver == this ) {
				res = true;
				break;
    		}
    	}
    	if (res == false) {
    		DriverManager.registerDriver(this);
    	}
    }
  
//    /**
//     * Adds the current directory (".") to the CLASSPATH variable
//     * so that the DriverManager can find the configuration file
//     * ("xlsql_config.xml"). It may not work on Mac OS - for reasons
//     * uninvestigated. 
//     * 
//     * @author csny
//     */
//    private static void correctClassPath() {
//    	String classpath = System.getProperty("java.class.path");
//    	String pathsep = System.getProperty("path.separator");
//    	String[] paths = classpath.split(pathsep);
//    	boolean locDirFound = false;
//    	for (int i = 0; i < paths.length; i++) {
//			String p = paths[i];
//			if ( p.equals(".")) {
//				locDirFound = true;
//			}
//		}
//    	if (! locDirFound) {
//    		System.setProperty("java.class.path", "." + pathsep + classpath);
//    	}
//    }
}

