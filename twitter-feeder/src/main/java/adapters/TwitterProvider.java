package adapters;

import entities.TweetResult;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.SentimentAnalyzer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TwitterProvider {
    private static final String BASE_URL = "https://api.twitter.com/2/tweets/search/recent";
    private static final int TIME_WINDOW_SECONDS = 180;
    private final String bearerToken;

    public TwitterProvider() {
        this.bearerToken = loadToken("Twitter_token.txt");
    }

    private String loadToken(String resourceName) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new RuntimeException("No se encontró " + resourceName + " en classpath");
            }
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
                return r.readLine().trim();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo token de Twitter", e);
        }
    }

    public List<TweetResult> fetchRecentTweets(String query) throws Exception {
        String url  = buildUrl(query);
        String body = doHttpGet(url);
        return parseTweetResults(body);
    }

    private String buildUrl(String query) {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String start = Instant.now()
                .minusSeconds(TIME_WINDOW_SECONDS)
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT);
        return String.format(
                "%s?query=%s&tweet.fields=public_metrics&start_time=%s&max_results=10",
                BASE_URL, encoded, start
        );
    }

    private String doHttpGet(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + bearerToken)
                .GET().build();
        HttpResponse<String> res = HttpClient.newHttpClient()
                .send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new RuntimeException("Twitter API responded: " + res.statusCode());
        }
        return res.body();
    }

    private List<TweetResult> parseTweetResults(String body) {
        JSONObject json = new JSONObject(body);
        List<TweetResult> out = new ArrayList<>();
        if (!json.has("data")) return out;

        JSONArray arr = json.getJSONArray("data");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject t = arr.getJSONObject(i);
            JSONObject m = t.getJSONObject("public_metrics");

            String text = t.getString("text");
            int likes = m.optInt("like_count", 0);
            int comments = m.optInt("reply_count", 0);
            int retweets = m.optInt("retweet_count", 0);
            int score = SentimentAnalyzer.score(text);
            out.add(new TweetResult(text, likes, comments, retweets, score));
        }
        return out;
    }
}
