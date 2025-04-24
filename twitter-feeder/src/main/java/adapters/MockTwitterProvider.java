package adapters;

import entities.TweetResult;
import usecases.GenerateTweetUseCase;

import java.util.*;

public class MockTwitterProvider implements GenerateTweetUseCase {

    private static final Random random = new Random();

    @Override
    public TweetResult generate(String evento, String jugador) {
        Map<String, List<String>> diccionario = new HashMap<>();

        List<String> golTweets = Arrays.asList(
                "¡Vaya golazo de " + jugador + "!",
                "¡Ha entrado con un poco de suerte!",
                jugador + " ha marcado un gol de bandera"
        );

        List<String> tarjetaTweets = Arrays.asList(
                "Vaya animal, " + jugador + " ha entrado con los tacos a la altura de la rodilla.",
                "Entrada de tarjeta: " + jugador + " será amonestado 4 partidos.",
                "El árbitro se equivoca con la tarjeta señalada a " + jugador + "."
        );

        diccionario.put("gol", golTweets);
        diccionario.put("tarjeta", tarjetaTweets);

        List<String> posiblesTweets = diccionario.getOrDefault(evento.toLowerCase(), List.of("Evento desconocido de " + jugador));
        int tweetIndex = random.nextInt(posiblesTweets.size());
        String tweet = posiblesTweets.get(tweetIndex);

        int likes = random.nextInt(10001);
        int comments = random.nextInt(5001);
        int retweets = random.nextInt(7501);

        return new TweetResult(tweet, likes, comments, retweets);
    }
}
