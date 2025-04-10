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

    private void connect() {
        try {
            String url = "jdbc:sqlite:" + databasePath;
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
                                    "    player TEXT NOT NULL,\n" +
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
        if (events == null || events.isEmpty()) {
            return;
        }
        String sql = "INSERT OR IGNORE INTO events " +
                "(fixture_name, time_elapsed, team, player, type_event, detail) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = this.connection;
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);  // Iniciar transacción

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

            // Opcional: Loggear resultados
            int inserted = java.util.Arrays.stream(results).sum();
            System.out.printf("Insertados %d/%d eventos (duplicados ignorados)%n",
                    inserted, events.size());
        } catch (SQLException e) {
            throw new IOException("Error al guardar eventos en SQLite", e);
        }
    }
}
