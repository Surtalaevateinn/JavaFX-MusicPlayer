package app.musicplayer.db;

import app.musicplayer.model.Song;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/music?useSSL=false";
    private static final String USER = "root"; // 替换为你的 MySQL 用户名
    private static final String PASSWORD = "root"; // 替换为你的 MySQL 密码

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method to insert a list of songs into the database
    public void insertSongs(List<Song> songs) {
        String insertQuery = "INSERT INTO Songs (title, artist, album, length, trackNumber, discNumber, playCount, playDate, location) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            for (Song song : songs) {
                preparedStatement.setString(1, song.getTitle());
                preparedStatement.setString(2, song.getArtist());
                preparedStatement.setString(3, song.getAlbum());
                preparedStatement.setInt(4, song.getLength() != null ? Integer.parseInt(song.getLength()) : 0);
                preparedStatement.setInt(5, song.getTrackNumber() != 0 ? song.getTrackNumber() : 0);
                preparedStatement.setInt(6, song.getDiscNumber() != 0 ? song.getDiscNumber() : 0);
                preparedStatement.setInt(7, song.getPlayCount() != 0 ? song.getPlayCount() : 0);
                // Convert LocalDateTime to Timestamp
                LocalDateTime playDate = song.getPlayDate();
                preparedStatement.setTimestamp(8, playDate != null ?
                        Timestamp.valueOf(playDate) : null);
                preparedStatement.setString(9, song.getLocation());

                preparedStatement.addBatch(); // Add to batch
            }

            preparedStatement.executeBatch(); // Execute all queries in batch
            System.out.println("Songs inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error inserting songs: " + e.getMessage());
        }
    }
}