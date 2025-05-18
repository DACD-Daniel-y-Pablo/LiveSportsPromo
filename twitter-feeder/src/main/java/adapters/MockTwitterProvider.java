package adapters;

import entities.TweetResult;
import ports.TweetProvider;
import utils.SentimentAnalyzer;

import java.io.*;
import java.net.http.HttpTimeoutException;
import java.util.*;

public class MockTwitterProvider implements TweetProvider {

    private static final Random random = new Random();
    private static final String BASE_PATH = "/tweets/";   // ruta en classpath
    private static final int NUM_TWEETS = 5;

    private final TwitterProvider twitterProvider;

    public MockTwitterProvider(String bearertoken) {
        this.twitterProvider = new TwitterProvider(bearertoken);
    }

    @Override
    public TweetResult generate(String evento, String jugador, String id) {
        try {
            String query = evento + " " + jugador;
            var tweets = twitterProvider.fetchRecentTweets(query);
            if (!tweets.isEmpty()) {
                TweetResult elegido = tweets.get(random.nextInt(tweets.size()));
                return new TweetResult(
                        id,
                        elegido.getText(),
                        elegido.getLikes(),
                        elegido.getComments(),
                        elegido.getRetweets(),
                        elegido.getScore()
                );
            }
            System.out.println("ℹ️ API devolvió 0 tweets, uso mock");
        } catch (RuntimeException | HttpTimeoutException e) {
            System.err.println("⚠️ Error Twitter API (" + e.getMessage() + "), uso mock local");
        } catch (Exception e) {
            System.err.println("⚠️ Excepción al llamar a Twitter API, uso mock: " + e.getMessage());
        }

        List<String> frases = leerLineas(BASE_PATH + evento.toLowerCase() + ".txt");
        String texto = seleccionar(frases).replace("{jugador}", jugador);
        return crearTweetResult(id, texto);
    }

    private List<String> leerLineas(String relativePath) {
        try (InputStream is = getClass().getResourceAsStream(relativePath)) {
            if (is == null) throw new FileNotFoundException("Archivo no encontrado en classpath: " + BASE_PATH + relativePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            List<String> frases = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) frases.add(line.trim());
            return frases;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    private String seleccionar(List<String> frases) {
        List<String> algunas = seleccionarAleatorias(frases, NUM_TWEETS);
        return algunas.get(random.nextInt(algunas.size()));
    }

    private List<String> seleccionarAleatorias(List<String> frases, int cantidad) {
        Set<Integer> idx = new HashSet<>();
        while (idx.size() < Math.min(cantidad, frases.size())) {
            idx.add(random.nextInt(frases.size()));
        }
        List<String> resultado = new ArrayList<>();
        for (int i : idx) resultado.add(frases.get(i));
        return resultado;
    }

    private TweetResult crearTweetResult(String id, String tweet) {
        int likes = r(10000);
        int comments = r(5000);
        int retweets = r(7500);
        int score = SentimentAnalyzer.score(tweet);
        return new TweetResult(id, tweet, likes, comments, retweets, score);
    }

    private int r(int max) {
        return random.nextInt(max + 1);
    }
}
