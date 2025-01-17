package app.musicplayer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/music?useSSL=false";
    private static final String USER = "root"; // 替换为你的 MySQL 用户名
    private static final String PASSWORD = "root"; // 替换为你的 MySQL 密码

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}