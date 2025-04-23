package adapters;

import entities.Event;
import entities.Fixture;
import entities.FootballLeague;
import entities.StatusDescription;
import ports.FixtureProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

public class MockFixtureProvider implements FixtureProvider {
    private final Random random = new Random();

    @Override
    public ArrayList<Fixture> getFixturesByDate(LocalDate date, FootballLeague league) throws IOException {
        ArrayList<Fixture> fixtures = new ArrayList<>();

        // Generar 1-4 partidos ficticios
        int numFixtures = 1;

        for (int i = 0; i < numFixtures; i++) {
            fixtures.add(new Fixture(
                    random.nextInt(10000), // ID aleatorio
                    "Equipo Local " + (i + 1),
                    "Equipo Visitante " + (i + 1),
                    LocalDateTime.of(LocalDate.now(), LocalTime.now().minusHours(1)),
                    "UTC",
                    StatusDescription.NOT_STARTED,
                    league
            ));
        }

        return fixtures;
    }

    @Override
    public ArrayList<Event> getEventsByFixture(Fixture fixture) throws IOException {
        ArrayList<Event> events = new ArrayList<>();
        String[] eventTypes = {"Goal", "Card", "subst", "Var"};
        String[] details = {
                "Normal Goal", "Penalty", "Missed Penalty",
                "Yellow Card", "Red Card", "Substitution In",
                "Substitution Out", "Goal cancelled by VAR", "Penalty confirmed by VAR"
        };

        // Generar 1-3 eventos aleatorios por partido
        int numEvents = 1 + random.nextInt(3);

        for (int i = 0; i < numEvents; i++) {
            int minute = random.nextInt(91); // Minutos entre 0-90
            boolean isHomeTeam = random.nextBoolean();
            String team = isHomeTeam ? fixture.getHomeTeam() : fixture.getAwayTeam();

            events.add(new Event(
                    fixture.getFixture(),
                    minute,
                    team,
                    "Jugador " + (random.nextInt(23) + 1), // Jugador 1-23
                    eventTypes[random.nextInt(eventTypes.length)],
                    details[random.nextInt(details.length)]
            ));
        }

        return events;
    }

    public int getCallsLimit() {
        return 100;
    }
}