import java.time.LocalDate;

public class FixtureDate {
    private LocalDate date;

    public FixtureDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    private boolean ensureIsValid(LocalDate date) {
        // TODO
        return false;
    }
}
