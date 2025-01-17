package app.musicplayer.db;

import app.musicplayer.model.Album;
import app.musicplayer.model.Artist;
import app.musicplayer.model.Song;
import app.musicplayer.util.Resources;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryDao {
    public  DatabaseManager databaseManager = new DatabaseManager();



    public void insertSongs(List<Song> songs) {
        String checkArtistQuery = "SELECT id FROM Artists WHERE name = ?";
        String insertArtistQuery = "INSERT INTO Artists (name,image_path) VALUES (?,?)";

        String checkAlbumQuery = "SELECT id FROM Albums WHERE title = ?";
        String insertAlbumQuery = "INSERT INTO Albums (title, artistId,artwork_path) VALUES (?, ?,?)";

        String checkSongQuery = "SELECT id FROM Songs WHERE title = ?";
        String insertSongQuery = "INSERT INTO Songs (id, title, length, trackNumber, discNumber, playCount, playDate, location, albumId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement checkArtistStmt = connection.prepareStatement(checkArtistQuery);
             PreparedStatement insertArtistStmt = connection.prepareStatement(insertArtistQuery, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement checkAlbumStmt = connection.prepareStatement(checkAlbumQuery);
             PreparedStatement insertAlbumStmt = connection.prepareStatement(insertAlbumQuery, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement checkSongStmt = connection.prepareStatement(checkSongQuery);
             PreparedStatement insertSongStmt = connection.prepareStatement(insertSongQuery)) {

            for (Song song : songs) {
                String artistName = song.getArtist();
                String albumTitle = song.getAlbum();
                String songTitle = song.getTitle();
                String location = song.getLocation();


                // Check if Artist exists
                int artistId = findOrInsertArtist(artistName, checkArtistStmt, insertArtistStmt);

                // Check if Album exists
                int albumId = findOrInsertAlbum(albumTitle, artistId, checkAlbumStmt, insertAlbumStmt,location);

                // Check if Song exists
                if (!isSongExists(songTitle, checkSongStmt)) {
                    // Insert Song
                    insertSongStmt.setInt(1, song.getId());
                    insertSongStmt.setString(2, songTitle);
                    insertSongStmt.setLong(3, song.getLengthInSeconds());
                    insertSongStmt.setInt(4, song.getTrackNumber());
                    insertSongStmt.setInt(5, song.getDiscNumber());
                    insertSongStmt.setInt(6, song.getPlayCount());
                    insertSongStmt.setTimestamp(7, song.getPlayDate() != null ? Timestamp.valueOf(song.getPlayDate()) : null);
                    insertSongStmt.setString(8, song.getLocation());
                    insertSongStmt.setInt(9, albumId);

                    insertSongStmt.addBatch();
                }
            }

            insertSongStmt.executeBatch();
            System.out.println("Songs inserted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error inserting songs: " + e.getMessage());
        }
    }

    private int findOrInsertArtist(String artistName, PreparedStatement checkStmt, PreparedStatement insertStmt) throws SQLException {
        checkStmt.setString(1, artistName);

        try (ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id"); // Artist exists
            }
        }

        // Insert new artist
        insertStmt.setString(1, artistName);
        insertStmt.setString(2, Resources.ARTIST_IMG+artistName);
        insertStmt.executeUpdate();
        try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }

        throw new SQLException("Failed to insert or retrieve Artist ID for: " + artistName);
    }

    private int findOrInsertAlbum(String albumTitle, int artistId, PreparedStatement checkStmt, PreparedStatement insertStmt,String location) throws SQLException {
        checkStmt.setString(1, albumTitle);
        try (ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id"); // Album exists
            }
        }

        // Insert new album
        insertStmt.setString(1, albumTitle);
        insertStmt.setInt(2, artistId);
        insertStmt.setString(3, location);
        insertStmt.executeUpdate();
        try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }

        throw new SQLException("Failed to insert or retrieve Album ID for: " + albumTitle);
    }

    private boolean isSongExists(String songTitle, PreparedStatement checkStmt) throws SQLException {
        checkStmt.setString(1, songTitle);
        try (ResultSet rs = checkStmt.executeQuery()) {
            return rs.next(); // Song exists
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
