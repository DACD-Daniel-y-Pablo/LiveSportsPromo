import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.io.IOException;

public class SqliteStore implements FixtureStore {
    private Connection connection;
    private final String databasePath;

    public SqliteStore(String databasePath) {
        this.databasePath = databasePath;
        connect();
        initializeDatabase();
    }

    private void connect() {
        try {
            String url = "jdbc:sqlite:" + databasePath;
            System.out.println(url);
            connection = DriverManager.getConnection(url);
            connection.createStatement().execute("PRAGMA foreign_keys=ON");
            System.out.println("✅ Connected to database");
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to database");
        }
    }

    private void initializeDatabase() {
        String createEventsTable = "CREATE TABLE IF NOT EXISTS events (\n" +
                                   "    fixture_name TEXT NOT NULL,\n" +
                                   "    home_team TEXT NOT NULL,\n" +
                                   "    away_team TEXT NOT NULL,\n" +
                                   "    player_name TEXT NOT NULL,\n" +
                                   "    team_player TEXT NOT NULL,\n" +
                                   "    type_event INTEGER NOT NULL\n" +
                                   ")\n";

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
    public void store(Fixture fixture) throws IOException {
        // TODO
    }
}
