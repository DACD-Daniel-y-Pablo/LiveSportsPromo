package domain;

import java.time.LocalDate;

public class Discount {
    private final String playerName;
    private final String teamName;
    private final int percentage;
    private final LocalDate expireDate;


    public Discount(String playerName, int percentage, String teamName, LocalDate expireDate) {
        this.playerName = playerName;
        this.percentage = percentage;
        this.expireDate = expireDate;
        this.teamName = teamName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getPercentage() {
        return percentage;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

}

