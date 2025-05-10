package adapters;

import entities.TweetResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TwitterStore {
    private static final String DB_URL = "jdbc:sqlite:twitter_data.db";

    public void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS tweets " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT, timestamp TEXT, " +
                "likes INTEGER, retweets INTEGER, comments INTEGER, score INTEGER)";
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement s = c.prepareStatement(sql)) {
            s.execute();
        } catch (SQLException e) {
            System.err.println("Error init DB: " + e.getMessage());
        }
    }

    public void insertTweetResults(List<TweetResult> tweets) {
        String insert = "INSERT INTO tweets (text,timestamp,likes,retweets,comments) " +
                "VALUES (?, datetime('now'), ?, ?, ?)";
        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement s = c.prepareStatement(insert)) {
            for (TweetResult tr : tweets) {
                s.setString(1, tr.getText());
                s.setInt(2, tr.getLikes());
                s.setInt(3, tr.getRetweets());
                s.setInt(4, tr.getComments());
                s.addBatch();
            }
            s.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error inserting tweets: " + e.getMessage());
        }
    }
}