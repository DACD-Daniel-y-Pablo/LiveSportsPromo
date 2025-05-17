import adapters.ActiveMQEventSender;
import adapters.ApiFixtureProvider;
import adapters.MockFixtureProvider;
import adapters.SqliteEventStore;
import entities.FootballLeague;
import useCases.Control;

public class Main {
    public static void main(String[] args) {
        //Control control = new Control(new ApiFixtureProvider(args[1], args[0]), new ActiveMQEventSender(args[4], args[3]));
        // Control control = new Control(new MockFixtureProvider(), new SqliteEventStore(args[2]));
        Control control = new Control(new MockFixtureProvider(), new ActiveMQEventSender(args[4], args[3]));
        control.run(FootballLeague.LA_LIGA);
    }
}
