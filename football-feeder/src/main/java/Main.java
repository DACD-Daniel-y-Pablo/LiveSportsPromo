import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Dotenv dotenv = Dotenv.configure()
                .directory("football-feeder/src/main/resources/.env")
                .load();
        Control control = new Control();
        ApiFixtureProvider api = new ApiFixtureProvider(dotenv.get("BASE_URL"), dotenv.get("API_KEY"));
        SqliteStore db = new SqliteStore("football-feeder/src/main/resources/database.db");
        try {
            control.run(api, db);
        } catch (InterruptedException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
