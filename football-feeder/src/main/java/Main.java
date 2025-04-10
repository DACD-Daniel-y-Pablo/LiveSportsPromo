import adapters.ApiFixtureProvider;
import adapters.SqliteEventStore;
import entities.FootballLeague;
import useCases.Control;

public class Main {
    public static void main(String[] args) {
        Control control = new Control(
                new ApiFixtureProvider(args[1], args[0]),
                new SqliteEventStore(args[2]));
        control.run(FootballLeague.LALIGA);
    }
}
