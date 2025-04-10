package ports;

import entities.Event;
import entities.Fixture;
import entities.FootballLeague;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface FixtureProvider {
    ArrayList<Fixture> getFixturesByDate(LocalDate date, FootballLeague league) throws IOException;
    ArrayList<Event> getEventsByFixture(Fixture fixture) throws IOException;
}
