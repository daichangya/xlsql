/*jsdiff.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

package com.jsdiff.xlsql.jdbc;

/**
 * xlSQL全局常量定义
 * 
 * <p>该接口定义了xlSQL JDBC驱动使用的所有常量，包括：</p>
 * <ul>
 *   <li>驱动类名和版本信息</li>
 *   <li>JDBC URL前缀</li>
 *   <li>应用程序标识符</li>
 * </ul>
 *
 * @version $Revision: 1.1 $
 * @author daichangya
 *
 * Changed by Csongor Nyulas (csny): Release constants changed
 */
public interface Constants {
    //~ Static variables/initializers ������������������������������������������
    public static final String APP = "xlSQL> ";

    public static final String DRIVER = "com.jsdiff.xlsql.jdbc.xlDriver";

    public static final String DRIVER_NAME = "jsdiff/xlSQL Excel JDBC Driver";
    public static final String DRIVER_RELEASE = "beta:Y8";
    public static final String DRIVER_CLASS = "com.jsdiff.xlsql.jdbc.jdbcDriverXls";
    public static final int MAJOR_VERSION = 0;
    public static final int MINOR_VERSION = 0;
    public static final String URL_PFX_XLS = "jdbc:jsdiff:excel:";
    public static final String URL_PFX_CSV = "jdbc:jsdiff:csv:";
    public static final boolean JDBC_COMPLIANT = false;
    public static final int JDBC_MAJOR_VERSION = 3;
    public static final int JDBC_MINOR_VERSION = 0;
    
    public static final int MAJOR_XLSQL_VERSION = 0;
    public static final int MINOR_XLSQL_VERSION = 0;
    public static final String XLSQL = "xlSQL (with HSQL database engine)";
    public static final String XLSQL_RELEASE = "beta:X1";
    
    public static final String URL = "url";

}