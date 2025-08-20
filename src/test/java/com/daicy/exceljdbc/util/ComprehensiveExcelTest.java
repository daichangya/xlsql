package com.daicy.exceljdbc.util;// ComprehensiveExcelTest.java

import java.io.File;
import java.sql.*;

public class ComprehensiveExcelTest {

    public static final String DATA_XLS = "test.xls";

    public static void main(String[] args) {
        try {
            // 1. 创建测试Excel文件
            createTestExcel();
            
            // 2. 测试连接和查询
            testExcelQuery();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void createTestExcel() throws Exception {
        // 这里可以调用之前创建的CreateTestExcel逻辑
        // 或者检查文件是否已存在
        File testFile = new File(DATA_XLS);
        if (!testFile.exists()) {
            System.out.println("请先创建文件"+DATA_XLS);
            return;
        }
        System.out.println("测试文件已存在: " + testFile.getAbsolutePath());
    }
    
    private static void testExcelQuery() throws Exception {
        // 加载驱动
        String driver = "com.daicy.exceljdbc.jdbc.xlDriver";
        Class.forName(driver);
        
        // 连接到当前目录
        String url = "jdbc:jsdiff:excel:" + System.getProperty("user.dir");
        Connection con = DriverManager.getConnection(url);
        
        System.out.println("=== Excel查询测试 ===");
        
        // 获取数据库元数据
        DatabaseMetaData metaData = con.getMetaData();
        System.out.println("驱动名称: " + metaData.getDriverName());
        System.out.println("驱动版本: " + metaData.getDriverVersion());
        
        // 列出所有表（Excel文件）
        System.out.println("\n=== 可用的Excel文件 ===");
        ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
        while (tables.next()) {
            System.out.println("表名: " + tables.getString("TABLE_NAME"));
        }
        tables.close();
        
        // 执行查询
        Statement stmt = con.createStatement();
        
        System.out.println("\n=== 查询文件 ==="+DATA_XLS);
        ResultSet rs = stmt.executeQuery("SELECT * FROM \"test.Sheet1\" LIMIT 1");
        
        // 显示列信息
        int columnCount = rs.getMetaData().getColumnCount();
        System.out.println("列数: " + columnCount);
        for (int i = 1; i <= columnCount; i++) {
            System.out.println("列 " + i + ": " + rs.getMetaData().getColumnName(i) 
                + " (" + rs.getMetaData().getColumnTypeName(i) + ")");
        }
        
        // 显示数据
        System.out.println("\n数据内容:");
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rs.getString(i) + "\t");
            }
            System.out.println();
        }
        
        rs.close();
        stmt.close();
        con.close();
        
        System.out.println("\n=== 测试完成 ===");
    }
}
