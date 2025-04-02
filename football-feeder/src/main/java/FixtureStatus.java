public class FixtureStatus {
    private StatusDescription description;
    private int elapsedMinutes;
    private int extraMinutes;

    public FixtureStatus(StatusDescription description, int elapsedMinutes) {
        this.description = description;
        this.elapsedMinutes = elapsedMinutes;
    }
}
