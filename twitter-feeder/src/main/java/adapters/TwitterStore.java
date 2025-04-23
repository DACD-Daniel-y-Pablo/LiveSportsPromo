package adapters;

import entities.Tweet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TwitterStore {

    private static final String DB_URL = "jdbc:sqlite:twitter_data.db";

    public void initializeDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS tweets (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tweet TEXT NOT NULL,
                fecha_hora TEXT NOT NULL,
                retweets INTEGER NOT NULL,
                me_gustas INTEGER NOT NULL,
                respuestas INTEGER NOT NULL,
                puntuacion INTEGER NULL
            );
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.execute();
            System.out.println("✅ Base de datos inicializada.");
        } catch (SQLException e) {
            System.err.println("❌ Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    public void insertTweets(List<Tweet> tweets) {
        for (Tweet tweet : tweets) {
            insertTweet(tweet);
        }
    }

    public void insertTweet(Tweet tweet) {
        String insertSQL = """
            INSERT INTO tweets (tweet, fecha_hora, retweets, me_gustas, respuestas, puntuacion)
            VALUES (?, ?, ?, ?, ?, NULL);
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

            stmt.setString(1, tweet.getText());
            stmt.setString(2, tweet.getCreatedAt());
            stmt.setInt(3, tweet.getRetweetCount());
            stmt.setInt(4, tweet.getLikeCount());
            stmt.setInt(5, tweet.getReplyCount());

            stmt.executeUpdate();
            System.out.println("✅ Tweet insertado correctamente.");

        } catch (SQLException e) {
            System.err.println("❌ Error al insertar tweet: " + e.getMessage());
        }
    }
}
