import provider.TwitterProvider;
import store.TwitterStore;
import model.Tweet;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TwitterProvider provider = new TwitterProvider();
        TwitterStore store = new TwitterStore();

        store.initializeDatabase();

        Scanner scanner = new Scanner(System.in);
        System.out.print("🔐 Introduce tu bearer token: ");
        String bearerToken = scanner.nextLine();

        System.out.print("🔍 Introduce tu query para el tweet: ");
        String query = scanner.nextLine();

        try {
            List<Tweet> tweets = provider.fetchRecentTweets(bearerToken, query);
            store.insertTweets(tweets);
            System.out.println("✅ Tweets almacenados con éxito.");
        } catch (Exception e) {
            System.err.println("❌ Error al procesar los tweets: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
