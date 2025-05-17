package adapters;

import entities.Event;
import ports.FixtureEventStore;

import java.sql.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SqliteEventStore implements FixtureEventStore {
    private Connection connection;
    private final String databasePath;

    public SqliteEventStore(String databasePath) {
        this.databasePath = databasePath;
        connect();
        initializeDatabase();
    }

    public Connection getConnection() {
        return connection;
    }

    private void connect() {
        try {
            String url = "jdbc:sqlite:" + databasePath + "?busy_timeout=30000";
            connection = DriverManager.getConnection(url);
            connection.createStatement().execute("PRAGMA foreign_keys=ON");
            System.out.println("✅ Connected to database");
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to database");
        }
    }

    private void initializeDatabase() {
        String createEventsTable =  "CREATE TABLE IF NOT EXISTS events (\n" +
                                    "    fixture_name TEXT NOT NULL,\n" +
                                    "    time_elapsed INTEGER NOT NULL,\n" +
                                    "    team TEXT NOT NULL,\n" +
                                    "    player_name TEXT NOT NULL,\n" +
                                    "    type_event INTEGER NOT NULL,\n" +
                                    "    detail TEXT NOT NULL,\n" +
                                    "PRIMARY KEY (fixture_name, player_name,time_elapsed, type_event));";
        try {
            Statement stmt =  connection.createStatement();
            stmt.execute(createEventsTable);
            System.out.println("✅ Estructura de la base de datos verificada/creada");
        } catch (SQLException e) {
            System.err.println("❌ Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed");
            }
        } catch (SQLException e) {
            System.out.println("Failed to close connection");
        }
    }


    @Override
    public void store(ArrayList<Event> events) throws IOException {
        if (events == null || events.isEmpty()) return;

        // Validar conexión
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            throw new IOException("Error verificando conexión", e);
        }

        String sql = "INSERT OR IGNORE INTO events (fixture_name, time_elapsed, team, player_name, type_event, detail) VALUES (?, ?, ?, ?, ?, ?)";
        int maxRetries = 3;

        for (int retry = 0; retry < maxRetries; retry++) {
            try (Connection conn = this.connection;
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                conn.setAutoCommit(false);

                for (Event event : events) {
                    stmt.setString(1, event.getFixture());
                    stmt.setInt(2, event.getTimeElapsed());
                    stmt.setString(3, event.getTeamName());
                    stmt.setString(4, event.getPlayerName());
                    stmt.setString(5, event.getTypeEvent());
                    stmt.setString(6, event.getDetailEvent());
                    stmt.addBatch();
                }

                int[] results = stmt.executeBatch();
                conn.commit();

                System.out.printf("Insertados %d/%d eventos%n",
                        Arrays.stream(results).sum(), events.size());
                return; // Éxito

            } catch (SQLException e) {
                if (retry == maxRetries - 1) {
                    throw new IOException("Error después de " + maxRetries + " intentos", e);
                }
                try {
                    Thread.sleep(100 * (retry + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrumpido durante reintento", ie);
                }
            }
        }
    }
}
