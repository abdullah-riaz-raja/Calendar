package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String url = "jdbc:mysql://localhost:3306/Calendar";
    private static final String uname = "root";
    private static final String pass = "secretDB";

    public static Connection startConnection() throws SQLException {
        return DriverManager.getConnection(url, uname, pass);
    }

}
