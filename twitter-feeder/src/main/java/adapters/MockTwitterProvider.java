package adapters;

import entities.TweetResult;
import ports.TweetProvider;

import java.util.*;

public class MockTwitterProvider implements TweetProvider {

    private static final Random random = new Random();

    @Override
    public TweetResult generate(String evento, String jugador) {
        Map<String, List<String>> diccionario = crearDiccionario(jugador);
        List<String> posiblesTweets = obtenerTweets(diccionario, evento, jugador);
        String tweet = seleccionarTweet(posiblesTweets);
        return crearTweetResult(tweet);
    }

    private Map<String, List<String>> crearDiccionario(String jugador) {
        Map<String, List<String>> diccionario = new HashMap<>();
        diccionario.put("goal", generarTweetsGol(jugador));
        diccionario.put("card", generarTweetsTarjeta(jugador));
        diccionario.put("subst", generarTweetsSubstitucion(jugador));
        diccionario.put("var", generarTweetsVar(jugador));
        return diccionario;
    }

    private List<String> generarTweetsGol(String jugador) {
        return Arrays.asList(
                "¡Vaya golazo de " + jugador + "!",
                jugador + " remata y la clava en la escuadra.",
                "¡Gol en el último minuto de " + jugador + "!",
                jugador + " aprovecha el rechace y marca.",
                "Gol de " + jugador + ": pura clase y precisión.",
                "El portero ni la vio pasar, golazo de " + jugador + ".",
                jugador + " convierte un penalti con sangre fría."
        );
    }

    private List<String> generarTweetsTarjeta(String jugador) {
        return Arrays.asList(
                jugador + " ve la amarilla tras una dura entrada.",
                "¡Tarjeta roja directa para " + jugador + "!",
                "El árbitro no duda en mostrar la tarjeta a " + jugador + ".",
                jugador + " se pasa de revoluciones y es amonestado.",
                "Protestas de " + jugador + " tras recibir tarjeta injustamente.",
                "Tarjeta clara: " + jugador + " cortó una contra peligrosa."
        );
    }

    private List<String> generarTweetsSubstitucion(String jugador) {
        return Arrays.asList(
                jugador + " sale del campo tras una gran actuación.",
                "Cambio táctico: entra otro por " + jugador + ".",
                jugador + " se retira con una posible lesión.",
                "Ovación para " + jugador + " al ser sustituido.",
                jugador + " se marcha visiblemente enfadado por el cambio.",
                "Sale " + jugador + " para refrescar la banda."
        );
    }

    private List<String> generarTweetsVar(String jugador) {
        return Arrays.asList(
                "¡El VAR revisa una posible mano de " + jugador + "!",
                "El árbitro acude al VAR tras falta cometida por " + jugador + ".",
                jugador + " es perdonado por el VAR tras una dura entrada.",
                "Gol anulado a " + jugador + " por fuera de juego milimétrico.",
                jugador + " celebra el gol... ¡pero el VAR dice que no!",
                "El VAR ratifica la decisión inicial sobre " + jugador + "."
        );
    }

    private List<String> obtenerTweets(Map<String, List<String>> diccionario, String evento, String jugador) {
        return diccionario.getOrDefault(evento.toLowerCase(), List.of("Evento desconocido de " + jugador));
    }

    private String seleccionarTweet(List<String> tweets) {
        int index = random.nextInt(tweets.size());
        return tweets.get(index);
    }

    private TweetResult crearTweetResult(String tweet) {
        int likes = random.nextInt(10001);
        int comments = random.nextInt(5001);
        int retweets = random.nextInt(7501);
        return new TweetResult(tweet, likes, comments, retweets);
    }
}
