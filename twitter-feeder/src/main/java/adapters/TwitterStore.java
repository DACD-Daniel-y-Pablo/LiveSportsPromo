package adapters;

import entities.TweetResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TwitterStore {
    private final String dbUrl;
    private final Connection sharedConn;

    // Constructor normal con URL
    public TwitterStore(String dbUrl) {
        this.dbUrl = dbUrl;
        this.sharedConn = null;
    }

    public TwitterStore() {
        this("jdbc:sqlite:twitter_data.db");
    }

    public TwitterStore(Connection sharedConn) {
        this.dbUrl = null;
        this.sharedConn = sharedConn;
    }

    private Connection getConnection() throws SQLException {
        return sharedConn != null ? sharedConn : DriverManager.getConnection(dbUrl);
    }

    public void initializeDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS tweets (
              id       INTEGER PRIMARY KEY AUTOINCREMENT,
              text     TEXT,
              timestamp TEXT,
              likes    INTEGER,
              retweets INTEGER,
              comments INTEGER,
              score    INTEGER
            );
            """;
        try (PreparedStatement s = getConnection().prepareStatement(sql)) {
            s.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error inicializando la DB", e);
        }
    }

    public void insertTweetResults(List<TweetResult> tweets) {
        String insert = """
            INSERT INTO tweets 
              (text, timestamp, likes, retweets, comments, score)
            VALUES 
              (?, datetime('now'), ?, ?, ?, ?);
            """;
        try (PreparedStatement s = getConnection().prepareStatement(insert)) {
            for (TweetResult tr : tweets) {
                s.setString(1, tr.getText());
                s.setInt(2, tr.getLikes());
                s.setInt(3, tr.getRetweets());
                s.setInt(4, tr.getComments());
                s.setInt(5, tr.getScore());
                s.addBatch();
            }
            s.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando tweets", e);
        }
    }
}
