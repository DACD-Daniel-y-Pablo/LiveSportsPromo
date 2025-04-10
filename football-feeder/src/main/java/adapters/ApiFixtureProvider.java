package adapters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.Event;
import entities.Fixture;
import entities.FootballLeague;
import entities.StatusDescription;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ports.FixtureProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ApiFixtureProvider implements FixtureProvider {
    private final String urlBase;
    private final String apiKey;
    private final OkHttpClient client;

    public ApiFixtureProvider(String urlBase, String apiKey) {
        this.urlBase = urlBase;
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
    }

    public int getCallsLimit() {
        return 100;
    }

    @Override
    public ArrayList<Fixture> getFixturesByDate(LocalDate date, FootballLeague league) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(this.urlBase + "/fixtures?date=" + date.toString());
        builder.addHeader("x-rapid-host", "v3.football.api-sports.io");
        builder.addHeader("x-rapidapi-key", this.apiKey);
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String jsonResponse = response.body().string();
            JsonArray fixturesParsing = parsingJson(jsonResponse);
            return filterFixtures(fixturesParsing, league);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }


    @Override
    public ArrayList<Event> getEventsByFixture(Fixture fixture) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(this.urlBase + "/fixtures/events?fixture=" + fixture.getId());
        builder.addHeader("x-rapid-host", "v3.football.api-sports.io");
        builder.addHeader("x-rapidapi-key", this.apiKey);
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String jsonResponse = response.body().string();
            JsonArray eventsParsing = parsingJson(jsonResponse);
            return filterEvents(eventsParsing, fixture);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public JsonArray parsingJson(String jsonResponse)
    {
        JsonObject responseJson = JsonParser.parseString(jsonResponse).getAsJsonObject();
        if (!responseJson.get("errors").getAsJsonArray().isEmpty()) {
            System.out.println(responseJson.get("errors").getAsJsonArray().get(0).getAsJsonObject());
            return null;
        }
        return responseJson.get("response").getAsJsonArray();
    }

    public ArrayList<Fixture> filterFixtures(JsonArray fixturesArray, FootballLeague league)
    {
        ArrayList<Fixture> fixtures = new ArrayList<>();
        for (JsonElement fixtureElement : fixturesArray) {
            JsonObject fixtureObject = fixtureElement.getAsJsonObject();
            if (fixtureObject.getAsJsonObject("league").get("id").getAsInt() == league.getId()) {
                JsonObject fixtureData = fixtureObject.getAsJsonObject("fixture");
                JsonObject teams = fixtureObject.getAsJsonObject("teams");
                String status = fixtureData.getAsJsonObject("status").get("short").getAsString();
                fixtures.add(new Fixture(
                        fixtureData.get("id").getAsInt(),
                        teams.getAsJsonObject("home").get("name").getAsString(),
                        teams.getAsJsonObject("away").get("name").getAsString(),
                        LocalDateTime.parse(fixtureData.get("date").getAsString(), DateTimeFormatter.ISO_DATE_TIME),
                        fixtureData.get("timezone").getAsString(),
                        StatusDescription.fromApiValue(status),
                        league
                ));
            }
        }
        return fixtures;
    }

    public ArrayList<Event> filterEvents(JsonArray eventsArray, Fixture fixture) {
        ArrayList<Event> events = new ArrayList<>();
        for (JsonElement event : eventsArray) {
            JsonObject eventObject = event.getAsJsonObject();
            events.add(new Event(
                    fixture.getFixture(),
                    eventObject.getAsJsonObject("time").get("elapsed").getAsInt(),
                    eventObject.getAsJsonObject("team").get("name").getAsString(),
                    eventObject.getAsJsonObject("player").get("name").getAsString(),
                    eventObject.get("type").getAsString(),
                    eventObject.get("detail").getAsString()
            ));
        }
        return events;
    }
}
