package adapters.helpers;

import com.google.gson.*;
import entities.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FixtureParser {
    public ArrayList<Fixture> parse(JsonArray array, FootballLeague league) {
        ArrayList<Fixture> fixtures = new ArrayList<>();
        for (JsonElement el : array) {
            JsonObject obj = el.getAsJsonObject();
            if (obj.getAsJsonObject("league").get("id").getAsInt() != league.getId()) continue;

            JsonObject fx = obj.getAsJsonObject("fixture");
            JsonObject teams = obj.getAsJsonObject("teams");
            fixtures.add(new Fixture(
                    fx.get("id").getAsInt(),
                    teams.getAsJsonObject("home").get("name").getAsString(),
                    teams.getAsJsonObject("away").get("name").getAsString(),
                    LocalDateTime.parse(fx.get("date").getAsString(), DateTimeFormatter.ISO_DATE_TIME),
                    fx.get("timezone").getAsString(),
                    StatusDescription.fromApiValue(fx.getAsJsonObject("status").get("short").getAsString()),
                    league
            ));
        }
        return fixtures;
    }
}
