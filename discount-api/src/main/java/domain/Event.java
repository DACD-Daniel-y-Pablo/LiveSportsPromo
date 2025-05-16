package domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Event {
    private UUID id;
    private final String fixture;
    private final int timeElapsed;
    private final String teamName;
    private final String playerName;
    private final String typeEvent;
    private final String detailEvent;
    private final LocalDateTime timestamp;

    public Event(String uuid, String fixture, int timeElapsed, String teamName, String playerName, String typeEvent, String detailEvent, LocalDateTime timestamp) {
        if (fixture == null || teamName == null || playerName == null || typeEvent == null || detailEvent == null) {
            throw new NullPointerException("No field can be null");
        }
        this.id = UUID.fromString(uuid);
        this.fixture = fixture;
        this.timeElapsed = timeElapsed;
        this.teamName = teamName;
        this.playerName = playerName;
        this.typeEvent = typeEvent;
        this.detailEvent = detailEvent;
        this.timestamp = timestamp;
    }

    public String getFixture() {
        return fixture;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public UUID getId() { return id; }

    public String getTeamName() {
        return teamName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTypeEvent() {
        return typeEvent;
    }

    public String getDetailEvent() {
        return detailEvent;
    }
}
