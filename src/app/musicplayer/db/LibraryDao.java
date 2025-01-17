package app.musicplayer.db;

import app.musicplayer.model.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class LibraryDao {
    public  DatabaseManager databaseManager = new DatabaseManager();

    public void insertSongs(List<Song> songs) {
        String insertQuery = "INSERT INTO Songs (id, title, artist, album, length, trackNumber, discNumber, playCount, playDate, location) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            for (Song song : songs) {
                preparedStatement.setInt(1, song.getId());
                preparedStatement.setString(2, song.getTitle());
                preparedStatement.setString(3, song.getArtist());
                preparedStatement.setString(4, song.getAlbum());
                preparedStatement.setLong(5, song.getLengthInSeconds()); // 使用秒数
                preparedStatement.setInt(6, song.getTrackNumber());
                preparedStatement.setInt(7, song.getDiscNumber());
                preparedStatement.setInt(8, song.getPlayCount());
                preparedStatement.setTimestamp(9, song.getPlayDate() != null ?
                        Timestamp.valueOf(song.getPlayDate()) : null);
                preparedStatement.setString(10, song.getLocation());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            System.out.println("Songs inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error inserting songs: " + e.getMessage());
        }
    }

    private Connection getConnection() {
        try {
            return databaseManager.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
