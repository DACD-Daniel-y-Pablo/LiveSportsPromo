import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
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

    public ArrayList<Fixture> filterFixtures(JsonArray fixturesArray, FootballLeague league)
    {
        ArrayList<Fixture> fixtures = new ArrayList<>();
        for (JsonElement fixtureElement : fixturesArray) {
            JsonObject fixtureObject = fixtureElement.getAsJsonObject();
            JsonObject leagues = fixtureObject.getAsJsonObject("league");
            // LeagueID
            int leagueId = leagues.get("id").getAsInt();
            if (leagueId == league.getId()) {
                // LeagueName
                String leagueName = leagues.get("name").getAsString();
                JsonObject fixtureData = fixtureObject.getAsJsonObject("fixture");
                // Obtenemos el fixtureID
                int fixtureId = fixtureData.get("id").getAsInt();
                // Parsear y separar
                String date = fixtureData.get("date").getAsString();
                OffsetDateTime fixtureDate = OffsetDateTime.parse(date);
                // Fixturedate
                String onlyDate = fixtureDate.toLocalDate().toString();
                // FixtureHour
                String hour = fixtureDate.toLocalTime().toString();
                JsonObject teams = fixtureObject.getAsJsonObject("teams");
                // HomeTeamId
                int homeId = teams.getAsJsonObject("home").get("id").getAsInt();
                // AwayTeamId
                int awayId = teams.getAsJsonObject("away").get("id").getAsInt();
                // nameHomeTeam
                String homeTeamName = teams.getAsJsonObject("home").get("name").getAsString();
                // nameAwayTeam
                String awayTeamName = teams.getAsJsonObject("away").get("name").getAsString();

                // StatusFixture
                String status = fixtureData.getAsJsonObject("status").get("short").getAsString();
                Team homeTeam = new Team(homeId, homeTeamName);
                Team awayTeam = new Team(awayId, awayTeamName);
                Fixture fixtureInterested = new Fixture(
                        homeTeam,
                        awayTeam,
                        fixtureId,
                        LocalDate.parse(onlyDate),
                        LocalTime.parse(hour),
                        StatusDescription.fromApiValue(status),
                        league
                );
                fixtures.add(fixtureInterested);
            }
        }
        return fixtures;
    }
}
