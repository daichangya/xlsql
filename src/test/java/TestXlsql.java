import java.sql.*;
import java.util.Enumeration;

import static com.jsdiff.xlsql.jdbc.Constants.DRIVER;
import static com.jsdiff.xlsql.jdbc.Constants.URL_PFX_XLS;


public class TestXlsql {

    public static void main(String[] args) {
        try {
            System.out.println("Current working directory: " + System.getProperty("user.dir"));


            System.out.println("Loading driver: " + DRIVER);

            Class<?> driverClass = Class.forName(DRIVER);
            Driver d = (Driver) driverClass.newInstance();

            // 显式注册驱动程序
            DriverManager.registerDriver(d);
            System.out.println("Driver was successfully loaded and registered.");

            // 列出所有已注册的驱动
            System.out.println("Registered drivers:");
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver registeredDriver = drivers.nextElement();
                System.out.println("  - " + registeredDriver.getClass().getName());
            }

            String protocol = URL_PFX_XLS;
            String database = System.getProperty("user.dir");
            String url = protocol + ":" + database;
            System.out.println("URL: " + url);

            // 测试驱动是否接受这个URL
            System.out.println("Driver accepts URL: " + d.acceptsURL(url));

            // 尝试获取连接
            Connection con = DriverManager.getConnection(url);
            System.out.println("Connection established successfully!");

            Statement stm = con.createStatement();

            String sql = "DROP TABLE \"demo.xlsqly8\" IF EXISTS;"
                    + "CREATE TABLE \"demo.xlsqly8\" (v varchar(255));";
            System.out.println("Executing: " + sql);
            stm.execute(sql);

            // because it is release Y8 we'll do 8000
            System.out.println("Inserting 8000 records...");
            for (int i = 0; i < 8000; i++) {
                sql = "INSERT INTO \"demo.xlsqly8\" VALUES ('xlSQL Y8 - daicy');";
                stm.execute(sql);
                if (i % 1000 == 0) {
                    System.out.println("Inserted " + i + " records");
                }
            }
            System.out.println("All records inserted successfully!");

            // 关闭资源
            stm.close();
            con.close();
            System.out.println("Resources closed successfully!");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
