package infrastructure.adapters;

import domain.Discount;
import domain.Event;
import domain.Tweet;
import infrastructure.ports.Repository;

import java.time.LocalDate;
import java.util.*;
import java.sql.*;

public class MysqlRepository implements Repository {
    private final Connection connection;

    public MysqlRepository(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
            initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database", e);
        }
    }

    @Override
    public void initializeDatabase() throws SQLException {
        Statement stmt = connection.createStatement();

        // Tabla discount
        stmt.executeUpdate("""
        CREATE TABLE IF NOT EXISTS discount (
            id INT AUTO_INCREMENT PRIMARY KEY,
            player_name VARCHAR(255),
            expire_date DATE,
            percentage INT,
            team_name VARCHAR(255)
        )
    """);

        // Tabla event
        stmt.executeUpdate("""
        CREATE TABLE IF NOT EXISTS event (
            id CHAR(36) PRIMARY KEY,
            match_name VARCHAR(255) NOT NULL,
            time_elapsed INT NOT NULL,
            team_name VARCHAR(255) NOT NULL,
            player_name VARCHAR(255) NOT NULL,
            type_event VARCHAR(255) NOT NULL,
            detail_event VARCHAR(255) NOT NULL,
            time_stamp DATETIME NOT NULL
        )
    """);

        // Tabla tweet
        stmt.executeUpdate("""
        CREATE TABLE IF NOT EXISTS tweet (
            id INT AUTO_INCREMENT PRIMARY KEY,
            event_id CHAR(36) NOT NULL,
            text TEXT NOT NULL,
            likes INT NOT NULL,
            comments INT NOT NULL,
            retweets INT NOT NULL,
            score INT NOT NULL,
            FOREIGN KEY (event_id) REFERENCES event(id)
        )
    """);
        stmt.close();
    }


    @Override
    public ArrayList<Discount> getAllDiscounts() throws SQLException {
        ArrayList<Discount> list = new ArrayList<>();
        String sql = "SELECT * FROM discount";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Discount d = new Discount(
                        rs.getString("player_name"),
                        rs.getInt("percentage"),
                        rs.getString("team_name"),
                        rs.getDate("expire_date").toLocalDate()
                );
                list.add(d);
            }
        }
        return list;
    }

    @Override
    public ArrayList<Event> getAllEvents() throws SQLException {
        ArrayList<Event> list = new ArrayList<>();
        String sql = "SELECT * FROM event";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Event e = new Event(
                        rs.getString("id"),
                        rs.getString("match_name"),
                        rs.getInt("time_elapsed"),
                        rs.getString("team_name"),
                        rs.getString("player_name"),
                        rs.getString("type_event"),
                        rs.getString("detail_event"),
                        rs.getTimestamp("time_stamp").toLocalDateTime()
                );
                list.add(e);
            }
        }
        return list;
    }

    @Override
    public ArrayList<Tweet> getAllTweets() throws SQLException {
        ArrayList<Tweet> list = new ArrayList<>();
        String sql = "SELECT * FROM tweet";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Tweet t = new Tweet(
                        rs.getString("event_id"),
                        rs.getString("text"),
                        rs.getInt("likes"),
                        rs.getInt("comments"),
                        rs.getInt("retweets"),
                        rs.getInt("score")
                );
                list.add(t);
            }
        }
        return list;
    }

    @Override
    public void save(Discount discount) throws SQLException {
        String sql = "INSERT INTO discount (player_name, expire_date, percentage, team_name) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, discount.getPlayerName());
            stmt.setDate(2, java.sql.Date.valueOf(discount.getExpireDate()));
            stmt.setInt(3, discount.getPercentage());
            stmt.setString(4, discount.getTeamName());
            stmt.executeUpdate();
        }
    }

    @Override
    public void save(Event event) throws SQLException {
        String sql = "INSERT INTO event (id, match_name, time_elapsed, team_name, player_name, type_event, detail_event, time_stamp) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, event.getId().toString());
            stmt.setString(2, event.getFixture());
            stmt.setInt(3, event.getTimeElapsed());
            stmt.setString(4, event.getTeamName());
            stmt.setString(5, event.getPlayerName());
            stmt.setString(6, event.getTypeEvent());
            stmt.setString(7, event.getDetailEvent());
            stmt.setTimestamp(8, java.sql.Timestamp.valueOf(event.getTimestamp()));
            stmt.executeUpdate();
        }
    }

    @Override
    public void save(Tweet tweet) throws SQLException {
        String sql = "INSERT INTO tweet (event_id, text, likes, comments, retweets, score) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tweet.getEvent_id().toString());
            stmt.setString(2, tweet.getText());
            stmt.setInt(3, tweet.getLikes());
            stmt.setInt(4, tweet.getComments());
            stmt.setInt(5, tweet.getRetweets());
            stmt.setInt(6, tweet.getScore());
            stmt.executeUpdate();
        }
    }

    @Override
    public Discount getDiscountById(String id) throws SQLException {
        String sql = "SELECT * FROM discount WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Discount(
                            rs.getString("player_name"),
                            rs.getInt("percentage"),
                            rs.getString("team_name"),
                            rs.getDate("expire_date").toLocalDate()
                    );
                } else {
                    throw new SQLException("Discount with ID " + id + " not found.");
                }
            }
        }
    }

    @Override
    public Event getEventById(String id) throws SQLException {
        String sql = "SELECT * FROM event WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Event(
                            rs.getString("id"),
                            rs.getString("match_name"),
                            rs.getInt("time_elapsed"),
                            rs.getString("team_name"),
                            rs.getString("player_name"),
                            rs.getString("type_event"),
                            rs.getString("detail_event"),
                            rs.getTimestamp("time_stamp").toLocalDateTime()
                    );
                } else {
                    throw new SQLException("Event with ID " + id + " not found.");
                }
            }
        }
    }

    @Override
    public Tweet getTweetById(String id) throws SQLException {
        String sql = "SELECT * FROM tweet WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Tweet(
                            rs.getString("event_id"),
                            rs.getString("text"),
                            rs.getInt("likes"),
                            rs.getInt("comments"),
                            rs.getInt("retweets"),
                            rs.getInt("score")
                    );
                } else {
                    throw new SQLException("Tweet with ID " + id + " not found.");
                }
            }
        }
    }

    @Override
    public ArrayList<Tweet> getTweetByEventId(String id) throws SQLException {
        String sql = "SELECT * FROM tweet WHERE event_id = ?";
        ArrayList<Tweet> tweets = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Tweet tweet = new Tweet(
                            rs.getString("event_id"),
                            rs.getString("text"),
                            rs.getInt("likes"),
                            rs.getInt("comments"),
                            rs.getInt("retweets"),
                            rs.getInt("score")
                    );
                    tweets.add(tweet);
                }
            }
        }
        return tweets;
    }

    @Override
    public ArrayList<Event> getEventsByType(String type) throws SQLException {
        ArrayList<Event> events = new ArrayList<>();

        String query = "SELECT * FROM event WHERE type_event = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event(
                        rs.getString("id"),
                        rs.getString("match_name"),
                        rs.getInt("time_elapsed"),
                        rs.getString("team_name"),
                        rs.getString("player_name"),
                        rs.getString("type_event"),
                        rs.getString("detail_event"),
                        rs.getTimestamp("time_stamp").toLocalDateTime()
                );
                events.add(event);
            }

        }
        return events;
    }

    @Override
    public boolean isDiscountApplied(String playerName) throws SQLException {
        String query = "SELECT COUNT(*) FROM discount WHERE player_name = ? AND expire_date > CURDATE()";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }

        }
        return false;
    }

    @Override
    public void deleteTweetsByEventId(String eventId) throws SQLException {
        String query = "DELETE FROM tweet WHERE event_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, eventId);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteEventById(String eventId) throws SQLException {
        String query = "DELETE FROM event WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, eventId);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteExpiredDiscounts() throws SQLException {
        String query = "DELETE FROM discount WHERE expire_date <= ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            statement.executeUpdate();
        }
    }
}

