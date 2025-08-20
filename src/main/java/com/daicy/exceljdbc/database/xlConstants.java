/*zthinker.com

    Copyright (C) 2025 jsdiff Information Sciences, all 
    rights reserved.
    
    This program is licensed under the terms of the GNU 
    General Public License.You should have received a copy 
    of the GNU General Public License along with this 
    program;
*/

package com.daicy.exceljdbc.database;

/**
 *
 * @version $Revision: 1.5 $
 * @author daichangya
 */
public class xlConstants {

    public static final int ADD = 0;

    public static final int UPDATE = 1;

    public static final int DELETE = 2;

    public static final String NOARGS = "xlSQL: no such argument(s).";


    public static int xlType(int sqlType) {
        int ret = 0;

        switch (sqlType) {
            case (-6):
            case (-5):
            case (-2):
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                ret = 1;
                break;
        
            case 1:
            case 12:
            case 70:
                ret = 2;
                break;
        
            case 91:
            case 92:
            case 93:
                ret = 3;
                break;
        
            case -7:
            case 16:
                ret = 4;
                break;
            
            default:
                ret = 0;
        }

        return ret;
    }
}