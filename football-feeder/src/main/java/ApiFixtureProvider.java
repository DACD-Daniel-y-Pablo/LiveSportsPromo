import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDate;
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


    @Override
    public ArrayList<Fixture> getFixturesByDate(LocalDate date) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(this.urlBase + "/fixtures?date=" + date.toString());
        builder.addHeader("x-rapid-host", "v3.football.api-sports.io");
        builder.addHeader("x-rapidapi-key", this.apiKey);
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String jsonResponse = response.body().string();
            JsonArray fixturesParsing = parsingJson(jsonResponse);
            return filterFixtures(fixturesParsing);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Team getTeamByFixture(Fixture fixture) throws IOException {
        return null;
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

    public ArrayList<Fixture> filterFixtures(JsonArray fixturesArray)
    {
        ArrayList<Fixture> fixtures = new ArrayList<>();
        for (JsonElement fixtureElement : fixturesArray) {
            JsonObject fixtureObject = fixtureElement.getAsJsonObject();


            JsonObject fixtureData = fixtureObject.getAsJsonObject("fixture");
            int fixtureId = fixtureData.get("id").getAsInt();
            String date = fixtureData.get("date").getAsString();

            JsonObject teams = fixtureObject.getAsJsonObject("teams");
            String homeTeam = teams.getAsJsonObject("home").get("name").getAsString();
            String awayTeam = teams.getAsJsonObject("away").get("name").getAsString();


            JsonObject leagues = fixtureObject.getAsJsonObject("league");
            int leagueId = leagues.get("id").getAsInt();

            if (leagueId == 140) {
                System.out.println("Partido ID: " + fixtureId);
                System.out.println("Fecha: " + date);
                System.out.println("Equipos: " + homeTeam + " vs " + awayTeam);
                System.out.println("----------------------");
            }
        }
        return fixtures;
    }
}
