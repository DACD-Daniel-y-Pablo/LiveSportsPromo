package adapters;

import adapters.helpers.EventParser;
import adapters.helpers.FixtureParser;
import adapters.helpers.RequestExecutor;
import com.google.gson.JsonArray;
import ports.FixtureProvider;
import entities.*;
import okhttp3.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ApiFixtureProvider implements FixtureProvider {
    private final String urlBase, apiKey;
    private final OkHttpClient client = new OkHttpClient();

    public ApiFixtureProvider(String urlBase, String apiKey) {
        this.urlBase = urlBase;
        this.apiKey = apiKey;
    }

    @Override
    public ArrayList<Fixture> getFixturesByDate(LocalDate date, FootballLeague league) throws IOException {
        String endpoint = "/fixtures?date=" + date;
        JsonArray data = new RequestExecutor(client, urlBase, apiKey).getJsonArray(endpoint);
        return new FixtureParser().parse(data, league);
    }

    @Override
    public ArrayList<Event> getEventsByFixture(Fixture fixture) throws IOException {
        String endpoint = "/fixtures/events?fixture=" + fixture.getId();
        JsonArray data = new RequestExecutor(client, urlBase, apiKey).getJsonArray(endpoint);
        return new EventParser().parse(data, fixture);
    }

    public int getCallsLimit() {
        return 100;
    }
}
