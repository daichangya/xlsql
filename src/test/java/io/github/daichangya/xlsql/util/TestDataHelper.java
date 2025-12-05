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
package io.github.daichangya.xlsql.util;

import io.github.daichangya.xlsql.engine.plan.TableInfo;

/**
 * TestDataHelper - 测试数据辅助类
 * 
 * <p>提供Mock数据生成方法，用于单元测试。
 * 避免测试依赖外部Excel文件。</p>
 * 
 * @author daichangya
 */
public class TestDataHelper {
    
    /**
     * 创建Mock TableInfo
     * 
     * @param workbook 工作簿名称
     * @param sheet 工作表名称
     * @param columns 列名数组
     * @param data 数据矩阵（String[列][行]）
     * @return TableInfo实例
     */
    public static TableInfo createMockTable(String workbook, String sheet, 
                                           String[] columns, String[][] data) {
        TableInfo table = new TableInfo(workbook, sheet, null);
        
        // 确定列类型（默认为VARCHAR）
        String[] columnTypes = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            columnTypes[i] = "VARCHAR";
        }
        
        // 确定行数
        int rowCount = data != null && data.length > 0 ? data[0].length : 0;
        
        table.loadData(columns, columnTypes, data, rowCount);
        return table;
    }
    
    /**
     * 创建员工表测试数据
     * 
     * <p>包含以下列：id, name, age, salary, dept_id</p>
     * 
     * @return TableInfo实例
     */
    public static TableInfo createEmployeeTable() {
        String[] columns = {"id", "name", "age", "salary", "dept_id"};
        String[] columnTypes = {"INTEGER", "VARCHAR", "INTEGER", "DOUBLE", "INTEGER"};
        
        // 数据矩阵：String[列][行]
        String[][] data = {
            {"1", "2", "3", "4", "5"},           // id列
            {"Alice", "Bob", "Charlie", "David", "Eve"}, // name列
            {"25", "30", "35", "28", "32"},     // age列
            {"5000.0", "6000.0", "7000.0", "5500.0", "6500.0"}, // salary列
            {"1", "1", "2", "2", "3"}           // dept_id列
        };
        
        TableInfo table = new TableInfo("test1", "employees", null);
        table.loadData(columns, columnTypes, data, 5);
        return table;
    }
    
    /**
     * 创建部门表测试数据
     * 
     * <p>包含以下列：id, name, location</p>
     * 
     * @return TableInfo实例
     */
    public static TableInfo createDepartmentTable() {
        String[] columns = {"id", "name", "location"};
        String[] columnTypes = {"INTEGER", "VARCHAR", "VARCHAR"};
        
        // 数据矩阵：String[列][行]
        String[][] data = {
            {"1", "2", "3"},                      // id列
            {"IT", "HR", "Finance"},             // name列
            {"Beijing", "Shanghai", "Guangzhou"} // location列
        };
        
        TableInfo table = new TableInfo("test2", "departments", null);
        table.loadData(columns, columnTypes, data, 3);
        return table;
    }
    
    /**
     * 创建空表（只有列名，没有数据）
     * 
     * @return TableInfo实例
     */
    public static TableInfo createEmptyTable() {
        String[] columns = {"id", "name"};
        String[] columnTypes = {"INTEGER", "VARCHAR"};
        String[][] data = new String[2][0]; // 空数据
        
        TableInfo table = new TableInfo("test", "empty", null);
        table.loadData(columns, columnTypes, data, 0);
        return table;
    }
    
    /**
     * 创建包含NULL值的测试表
     * 
     * @return TableInfo实例
     */
    public static TableInfo createTableWithNulls() {
        String[] columns = {"id", "name", "value"};
        String[] columnTypes = {"INTEGER", "VARCHAR", "DOUBLE"};
        
        // 数据矩阵：包含NULL值
        String[][] data = {
            {"1", "2", "3"},
            {"Alice", null, "Bob"},  // name列包含NULL
            {"10.5", null, "20.0"}   // value列包含NULL
        };
        
        TableInfo table = new TableInfo("test", "with_nulls", null);
        table.loadData(columns, columnTypes, data, 3);
        return table;
    }
    
    /**
     * 创建包含特殊字符的测试表
     * 
     * @return TableInfo实例
     */
    public static TableInfo createTableWithSpecialChars() {
        String[] columns = {"id", "name", "description"};
        String[] columnTypes = {"INTEGER", "VARCHAR", "VARCHAR"};
        
        // 数据矩阵：包含特殊字符和中文
        String[][] data = {
            {"1", "2"},
            {"测试", "数据"},
            {"包含'单引号'", "包含\"双引号\""}
        };
        
        TableInfo table = new TableInfo("test", "special_chars", null);
        table.loadData(columns, columnTypes, data, 2);
        return table;
    }
    
    /**
     * 创建用于JOIN测试的两个表
     * 
     * @return TableInfo数组，[0]为左表，[1]为右表
     */
    public static TableInfo[] createJoinTestTables() {
        // 左表：订单表
        String[] orderColumns = {"order_id", "customer_id", "amount"};
        String[] orderTypes = {"INTEGER", "INTEGER", "DOUBLE"};
        String[][] orderData = {
            {"1", "2", "3"},
            {"101", "102", "101"},
            {"100.0", "200.0", "150.0"}
        };
        TableInfo orders = new TableInfo("test1", "orders", null);
        orders.loadData(orderColumns, orderTypes, orderData, 3);
        
        // 右表：客户表
        String[] customerColumns = {"customer_id", "name", "city"};
        String[] customerTypes = {"INTEGER", "VARCHAR", "VARCHAR"};
        String[][] customerData = {
            {"101", "102", "103"},
            {"Alice", "Bob", "Charlie"},
            {"Beijing", "Shanghai", "Guangzhou"}
        };
        TableInfo customers = new TableInfo("test2", "customers", null);
        customers.loadData(customerColumns, customerTypes, customerData, 3);
        
        return new TableInfo[]{orders, customers};
    }
    
    /**
     * 创建用于聚合测试的表
     * 
     * @return TableInfo实例
     */
    public static TableInfo createAggregateTestTable() {
        String[] columns = {"category", "value", "amount"};
        String[] columnTypes = {"VARCHAR", "INTEGER", "DOUBLE"};
        
        // 数据矩阵：包含重复的category值用于GROUP BY测试
        String[][] data = {
            {"A", "A", "B", "B", "C"},          // category列
            {"10", "20", "30", "40", "50"},     // value列
            {"100.0", "200.0", "300.0", "400.0", "500.0"} // amount列
        };
        
        TableInfo table = new TableInfo("test", "aggregate", null);
        table.loadData(columns, columnTypes, data, 5);
        return table;
    }
}

