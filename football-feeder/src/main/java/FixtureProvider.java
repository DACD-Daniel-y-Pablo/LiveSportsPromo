import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface FixtureProvider {
    ArrayList<Fixture> getFixturesByDate(LocalDate date, FootballLeague league) throws IOException;
    Team getTeamByFixture(Fixture fixture) throws IOException;
}
