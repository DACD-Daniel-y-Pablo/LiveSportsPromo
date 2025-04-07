import java.time.LocalDate;
import java.time.LocalTime;

public class Fixture {
    private FixtureTeams teams;
    private FixtureId id;
    private FixtureDate date;
    private FixtureHour hour;
    private FixtureStatus status;
    private FixtureLeague league;

    public Fixture(
            Team homeTeam,
            Team awayTeam,
            int fixtureId,
            LocalDate fixtureDate,
            LocalTime fixtureHour,
            StatusDescription statusDescription,
            FootballLeague footballLeague
    ) {
        this.teams = new FixtureTeams(homeTeam, awayTeam);
        this.id = new FixtureId(fixtureId);
        this.date = new FixtureDate(fixtureDate);
        this.hour = new FixtureHour(fixtureHour);
        this.status = new FixtureStatus(statusDescription);
        this.league = new FixtureLeague(footballLeague);
    }

    public String getFixtureName() {
        return this.teams.getHomeTeam().getName() + " vs " + this.teams.getAwayTeam().getName();
    }

    public FixtureTeams getTeams() {
        return teams;
    }

    public FixtureId getId() {
        return id;
    }

    public FixtureDate getDate() {
        return date;
    }

    public FixtureHour getHour() {
        return hour;
    }

    public FixtureStatus getStatus() {
        return status;
    }

    public FixtureLeague getLeague() {
        return league;
    }
}