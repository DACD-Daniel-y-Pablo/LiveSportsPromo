package entities;

import java.util.UUID;

public class Event {
    private UUID id;
    private final String fixture;
    private final int timeElapsed;
    private final String teamName;
    private final String playerName;
    private final String typeEvent;
    private final String detailEvent;

    public Event(String fixture, int timeElapsed, String teamName, String playerName, String typeEvent, String detailEvent) {
        if (fixture == null || teamName == null || playerName == null || typeEvent == null || detailEvent == null) {
            throw new NullPointerException("No field can be null");
        }
        this.id = UUID.randomUUID();
        this.fixture = fixture;
        this.timeElapsed = timeElapsed;
        this.teamName = teamName;
        this.playerName = playerName;
        this.typeEvent = typeEvent;
        this.detailEvent = detailEvent;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public String getFixture() {
        return fixture;
    }

    public String getTeamName() {
        return teamName;
    }

    public UUID getId() { return id; }

    public String getPlayerName() {
        return playerName;
    }

    public String getTypeEvent() {
        return typeEvent;
    }

    public String getDetailEvent() {
        return detailEvent;
    }

    public String generateEventKey() {
        return String.format("%s|%d|%s|%s",
                this.getFixture(),
                this.getTimeElapsed(),
                this.getPlayerName(),
                this.getTypeEvent());
    }
}
