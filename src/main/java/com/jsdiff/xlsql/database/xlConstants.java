/*jsdiff.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

package com.jsdiff.xlsql.database;

/**
 * xlConstants - 数据库常量定义
 * 
 * <p>该类定义了数据库操作中使用的常量，包括操作类型和类型转换方法。</p>
 * 
 * @version $Revision: 1.5 $
 * @author daichangya
 */
public class xlConstants {

    /** 操作类型：添加 */
    public static final int ADD = 0;

    /** 操作类型：更新 */
    public static final int UPDATE = 1;

    /** 操作类型：删除 */
    public static final int DELETE = 2;

    /** 错误消息：参数不存在 */
    public static final String NOARGS = "xlSQL: no such argument(s).";

    /**
     * 将SQL类型转换为xlSQL内部类型
     * 
     * <p>类型映射：</p>
     * <ul>
     *   <li>1 - 数值类型（TINYINT, SMALLINT, INTEGER, BIGINT, FLOAT, REAL, DOUBLE, NUMERIC, DECIMAL, BIT）</li>
     *   <li>2 - 字符串类型（CHAR, VARCHAR, LONGVARCHAR）</li>
     *   <li>3 - 日期时间类型（DATE, TIME, TIMESTAMP）</li>
     *   <li>4 - 布尔类型（BOOLEAN）</li>
     *   <li>0 - 其他类型</li>
     * </ul>
     * 
     * @param sqlType JDBC SQL类型常量
     * @return xlSQL内部类型（0-4）
     */
    public static int xlType(int sqlType) {
        int ret = 0;

        switch (sqlType) {
            // 数值类型
            case (-6):  // TINYINT
            case (-5):  // BIGINT
            case (-2):  // BINARY
            case 2:     // NUMERIC
            case 3:     // DECIMAL
            case 4:     // INTEGER
            case 5:     // SMALLINT
            case 6:     // FLOAT
            case 7:     // REAL
            case 8:     // DOUBLE
                ret = 1;
                break;
        
            // 字符串类型
            case 1:     // CHAR
            case 12:    // VARCHAR
            case 70:    // DATALINK
                ret = 2;
                break;
        
            // 日期时间类型
            case 91:    // DATE
            case 92:    // TIME
            case 93:    // TIMESTAMP
                ret = 3;
                break;
        
            // 布尔类型
            case -7:    // BIT
            case 16:    // BOOLEAN
                ret = 4;
                break;
            
            // 其他类型
            default:
                ret = 0;
        }

        return ret;
    }
}