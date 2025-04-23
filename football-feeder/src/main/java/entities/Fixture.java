package entities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Fixture {
    private int id;
    private String fixture;
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime dateTime;
    private StatusDescription status;
    private FootballLeague league;

    public Fixture(
            int id,
            String homeTeam,
            String awayTeam,
            LocalDateTime dateTime,
            String timezone,
            StatusDescription statusDescription,
            FootballLeague footballLeague
    ) {

        this.id = id;
        this.fixture = homeTeam + " vs " + awayTeam;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        setDateTime(ZoneId.systemDefault(), dateTime.atZone(ZoneId.of(timezone)));
        this.status = statusDescription;
        this.league = footballLeague;
    }

    public String getFixture() {
        return fixture;
    }

    public int getId() {
        return id;
    }

    private void setDateTime(ZoneId zoneId, ZonedDateTime zonedDateTime) {
        this.dateTime = zonedDateTime.withZoneSameInstant(zoneId).toLocalDateTime();
    }

    public String getHomeTeam() {
        return homeTeam;
    }


    public String getAwayTeam() {
        return awayTeam;
    }


    public LocalDateTime getDateTime() {
        return dateTime;
    }


    public StatusDescription getStatus() {
        return status;
    }

    public FootballLeague getLeague() {
        return league;
    }

}