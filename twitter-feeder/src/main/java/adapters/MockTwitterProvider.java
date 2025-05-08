package adapters;

import entities.TweetResult;
import ports.TweetProvider;

import java.io.*;
import java.util.*;

public class MockTwitterProvider implements TweetProvider {

    private static final Random random = new Random();
    private static final String BASE_PATH = "resources/";
    private static final int NUM_TWEETS = 5;

    @Override
    public TweetResult generate(String evento, String jugador) {
        List<String> frases = cargarTweets(BASE_PATH + evento.toLowerCase() + ".txt");
        List<String> seleccionadas = seleccionarAleatorias(frases, NUM_TWEETS);
        String tweetElegido = seleccionarTweet(seleccionadas).replace("{jugador}", jugador);
        return crearTweetResult(tweetElegido);
    }

    private List<String> cargarTweets(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            List<String> frases = new ArrayList<>();
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    frases.add(linea.trim());
                }
            }
            return frases;
        } catch (IOException e) {
            System.err.println("Error al leer archivo: " + path + " (" + e.getMessage() + ")");
            return Collections.emptyList();
        }
    }

    private List<String> seleccionarAleatorias(List<String> frases, int cantidad) {
        Set<Integer> indices = new HashSet<>();
        while (indices.size() < Math.min(cantidad, frases.size())) {
            indices.add(random.nextInt(frases.size()));
        }
        List<String> seleccionadas = new ArrayList<>();
        for (int idx : indices) {
            seleccionadas.add(frases.get(idx));
        }
        return seleccionadas;
    }

    private String seleccionarTweet(List<String> tweets) {
        return tweets.get(random.nextInt(tweets.size()));
    }

    private TweetResult crearTweetResult(String tweet) {
        int likes = random.nextInt(10001);
        int comments = random.nextInt(5001);
        int retweets = random.nextInt(7501);
        return new TweetResult(tweet, likes, comments, retweets);
    }
}
