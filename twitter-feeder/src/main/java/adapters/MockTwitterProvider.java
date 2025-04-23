package adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MockTwitterProvider {

    private static final Random random = new Random();

    public static void main(String[] args) {
        for (int x = 0; x < 100; x++) {
            TweetResult result = mockTwitters("gol", "vinicus");
            System.out.println(result);
        }
    }

    public static TweetResult mockTwitters(String evento, String jugador) {
        Map<String, List<String>> diccionario = new HashMap<>();

        List<String> golTweets = new ArrayList<>();
        golTweets.add("vaya golazo de " + jugador);
        golTweets.add("ha entrado con un poco de suerte");
        golTweets.add(jugador + " ha marcado un gol de bandera");

        List<String> tarjetaTweets = new ArrayList<>();
        tarjetaTweets.add("vaya animal, " + jugador + " entrado con los tacos a la altura de la rodilla");
        tarjetaTweets.add("entrada de tarjeta " + jugador + " será amonestado 4 partidos");
        tarjetaTweets.add("El árbitro se equivoca con la tarjeta señalada a " + jugador);

        diccionario.put("gol", golTweets);
        diccionario.put("tarjeta", tarjetaTweets);

        int numeroTweets = random.nextInt(diccionario.get(evento).size() - 1) + 1;

        List<String> tweets = tweetsSelector(diccionario, numeroTweets, evento);

        for (String tweet : tweets) {
            int likes = random.nextInt(10001);
            int comments = random.nextInt(5001);
            int retweets = random.nextInt(7501);
            return new TweetResult(tweet, likes, comments, retweets);
        }

        return null; // This line is theoretically unreachable
    }

    private static List<String> tweetsSelector(Map<String, List<String>> diccionario,
                                               int numeroTweets, String evento) {
        List<String> tweets = new ArrayList<>();
        List<Integer> controlador = new ArrayList<>();

        for (int i = 0; i < numeroTweets; i++) {
            int numero = random.nextInt(diccionario.get(evento).size());

            if (!controlador.contains(numero)) {
                String tweet = diccionario.get(evento).get(numero);
                controlador.add(numero);
                tweets.add(tweet);
            }
        }

        return tweets;
    }

    static class TweetResult {
        private final String tweet;
        private final int likes;
        private final int comments;
        private final int retweets;

        public TweetResult(String tweet, int likes, int comments, int retweets) {
            this.tweet = tweet;
            this.likes = likes;
            this.comments = comments;
            this.retweets = retweets;
        }

        @Override
        public String toString() {
            return "Tweet: " + tweet +
                    ", Likes: " + likes +
                    ", Comments: " + comments +
                    ", Retweets: " + retweets;
        }
    }
}
