package adapters;

import entities.Tweet;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public List<Tweet> fetchRecentTweets(String bearerToken, String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String startTime = calculateStartTime();

        String url = BASE_URL + "?query=" + encodedQuery
                + "&tweet.fields=created_at,public_metrics"
                + "&max_results=10"
                + "&start_time=" + startTime;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + bearerToken)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Error en la solicitud a Twitter. Código: "
                    + response.statusCode() + " - " + response.body());
        }
        return parseTweets(response.body());
    }

    private String calculateStartTime() {
        return Instant.now().minusSeconds(TIME_WINDOW_SECONDS).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    private List<Tweet> parseTweets(String responseBody) {
        JSONObject json = new JSONObject(responseBody);
        List<Tweet> tweets = new ArrayList<>();
        if (!json.has("data")) {
            return tweets;
        }
        JSONArray dataArray = json.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject tweetJson = dataArray.getJSONObject(i);
            tweets.add(new Tweet(tweetJson));
        }
        return tweets;
    }
}
