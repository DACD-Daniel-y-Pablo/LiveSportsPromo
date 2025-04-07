public enum StatusDescription {
    TIME_TO_BE_DEFINED("TBD"),
    NOT_STARTED("NS"),
    FIRST_HALF("1H"),
    HALFTIME("HT"),
    SECOND_HALF("2H"),
    EXTRA_TIME("ET"),
    BREAK_TIME("BT"),
    PENALTY_IN_PROGRESS("P"),
    MATCH_SUSPENDED("SUSP"),
    MATCH_INTERRUPTED("INT"),
    MATCH_FINISHED("FT"),
    FINISHED_WITHOUT_PENALTIES("AET"),
    FINISHED_WITH_PENALTIES("PEN"),
    MATCH_POSTPONED("PST"),
    MATCH_CANCELLED("CANC"),
    MATCH_ABANDONED("ABD"),
    TECHNICAL_LOSS("AWD"),
    WALKOVER("WO"),
    IN_PROGRESS("LIVE");

    private final String shortStatus;

    StatusDescription(String shortStatus) {
        this.shortStatus = shortStatus;
    }

    public String getShortStatus() {
        return shortStatus;
    }

    // Metodo para encontrar el enum por el valor "short"
    public static StatusDescription fromApiValue(String apiVAlue) {
        if (apiVAlue == null) {
            return null;
        }
        for (StatusDescription status : values()) {
            if (status.getShortStatus() == apiVAlue) {
                return status;
            }
        }
        return null;
    }
}
