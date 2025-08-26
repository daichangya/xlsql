import java.sql.*;

import static com.jsdiff.excel.jdbc.Constants.DRIVER;
import static com.jsdiff.excel.jdbc.Constants.URL_PFX_XLS;

public class TestExcelJDBC {
    public static void main(String[] args) {
        try {
            // 加载驱动
            Class.forName(DRIVER);
            System.out.println("Excel JDBC Driver loaded successfully");

            // 测试连接
            String url = URL_PFX_XLS + System.getProperty("user.dir");
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Connection established successfully");

            // 测试基本查询
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM \"test.Sheet1\" LIMIT 1");

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            System.out.println("Column count: " + columnCount);

            if (rs.next()) {
                System.out.println("Data query successful");
                for (int i = 1; i <= columnCount; i++) {
                    System.out.println("Column " + i + ": " + rs.getString(i));
                }
            }

            rs.close();
            stmt.close();
            conn.close();

            System.out.println("All tests passed!");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
//            // 清理测试文件
//            try {
//                new File("test.xlsx").delete();
//                new File("test.csv").delete();
//            } catch (Exception e) {
//                // Ignore cleanup errors
//            }
        }
    }

}
