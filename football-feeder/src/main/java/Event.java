public class Event {
    private final int id;
    private final int timeElapsed;
    private final String teamName;
    private final String playerName;
    private final String typeEvent;
    private final String detailEvent;

    public Event(int id, int timeElapsed, String teamName, String playerName, String typeEvent, String detailEvent) {
        this.id = id;
        this.timeElapsed = timeElapsed;
        this.teamName = teamName;
        this.playerName = playerName;
        this.typeEvent = typeEvent;
        this.detailEvent = detailEvent;
    }
}
