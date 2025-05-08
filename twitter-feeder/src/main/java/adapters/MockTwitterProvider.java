package adapters;

import entities.TweetResult;
import ports.TweetProvider;

import java.io.*;
import java.util.*;

public class MockTwitterProvider implements TweetProvider {

    private static final Random random = new Random();
    private static final String BASE_PATH =
            "/Users/pablo/Desktop/Trabajo DACD/LiveSportsPromo/twitter-feeder/src/main/resources/";
    private static final int NUM_TWEETS = 5;

    @Override
    public TweetResult generate(String evento, String jugador) {
        List<String> frases = leerLineas(BASE_PATH + evento.toLowerCase() + ".txt");
        String tweet = seleccionar(frases).replace("{jugador}", jugador);
        return crearTweetResult(tweet);
    }

    private List<String> leerLineas(String path) {
        try (BufferedReader r = new BufferedReader(new FileReader(path))) {
            List<String> frases = new ArrayList<>();
            String l;
            while ((l = r.readLine()) != null) frases.add(l.trim());
            return frases;
        } catch (IOException e) {
            throw new UncheckedIOException(e); // Nunca ocurrirá, según tu garantía
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

    private TweetResult crearTweetResult(String tweet) {
        return new TweetResult(tweet, r(10000), r(5000), r(7500));
    }

    private int r(int max) {
        return random.nextInt(max + 1);
    }
}
