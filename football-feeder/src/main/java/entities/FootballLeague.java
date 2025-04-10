package entities;

public enum FootballLeague {
    LALIGA(140, "La Liga"),
    UEFA_EUROPA_LEAGUE(2, "UEFA Europa League"),
    COPA_ARGENTINA(130, "Copa Argentina")
    ;


    private final int id;
    private final String name;

    FootballLeague(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
