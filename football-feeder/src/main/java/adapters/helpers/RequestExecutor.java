package adapters.helpers;

import com.google.gson.*;
import okhttp3.*;

import java.io.IOException;

public class RequestExecutor {
    private final OkHttpClient client;
    private final String baseUrl, apiKey;

    public RequestExecutor(OkHttpClient client, String baseUrl, String apiKey) {
        this.client = client;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public JsonArray getJsonArray(String endpoint) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .addHeader("x-rapid-host", "v3.football.api-sports.io")
                .addHeader("x-rapidapi-key", apiKey)
                .build();

        try (Response res = client.newCall(request).execute()) {
            if (res.body() == null) throw new IOException("No response body");
            JsonObject json = JsonParser.parseString(res.body().string()).getAsJsonObject();
            if (!json.getAsJsonArray("errors").isEmpty())
                return new JsonArray();
            return json.getAsJsonArray("response");
        }
    }
}
