package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UtilsDao {
    private static final String URL = "jdbc:mysql://localhost:3306/prevision";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection createConnection() throws Exception {     
        try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}