package POS;
import java.sql.*;

public class DBConnection {
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "priya", "root");
        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
        return con;
    }
}

