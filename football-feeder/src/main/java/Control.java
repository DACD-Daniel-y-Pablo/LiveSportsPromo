import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Control {
    public Control() {}

    public void run(ApiFixtureProvider api, SqliteStore sqLiteStore) throws IOException, InterruptedException{
        while (true) {
            ArrayList<Fixture> fixtures = api.getFixturesByDate(LocalDate.now(), FootballLeague.LALIGA);
            for (Fixture fixture : fixtures) {
                System.out.println("Nombre del encuentro: " + fixture.getFixtureName());
                System.out.println("ID del encuentro: " + fixture.getId().getId());
                System.out.println("Fecha del encuentro: " + fixture.getDate().getDate().toString());
                System.out.println("Hora del encuentro: " + fixture.getHour().getTime().toString());
                System.out.println("------------------------------------------------------------------");
            }
            System.out.println("Waiting 24h for the next update");
            sleep(24 * 60 * 60 * 1000);
        }

    }
}
