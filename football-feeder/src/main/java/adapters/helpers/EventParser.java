package adapters.helpers;

import com.google.gson.*;
import entities.*;

import java.util.ArrayList;

public class EventParser {
    public ArrayList<Event> parse(JsonArray array, Fixture fixture) {
        ArrayList<Event> events = new ArrayList<>();
        for (JsonElement el : array) {
            JsonObject obj = el.getAsJsonObject();
            events.add(new Event(
                    fixture.getFixture(),
                    obj.getAsJsonObject("time").get("elapsed").getAsInt(),
                    obj.getAsJsonObject("team").get("name").getAsString(),
                    obj.getAsJsonObject("player").get("name").getAsString(),
                    obj.get("type").getAsString(),
                    obj.get("detail").getAsString()
            ));
        }
        return events;
    }
}