package entities;

public enum FootballLeague {
    LA_LIGA(140, "La Liga"),
    COPA_ARGENTINA(130, "Copa Argentina"),
    UEFA_CHAMPIONS_LEAGUE(2, "UEFA Champions League"),
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
