package adapters;

import entities.TweetResult;
import usecases.Controller;

import javax.jms.JMSException;
import java.util.List;

public class DebugMain {
    public static void main(String[] args) throws JMSException {
        String brokerUrl    = "tcp://localhost:61616";
        String produceTopic = "tweets";

        ActiveMQTweetSender sender = new ActiveMQTweetSender(brokerUrl, produceTopic);

        MockTwitterProvider mock = new MockTwitterProvider();

        List<String> eventos  = List.of("goal", "card", "subst", "var");
        List<String> jugadores = List.of("Mbappé", "Messi", "Ramos", "Modric");

        System.out.println("=== DEBUG: Generando y enviando JSONs de prueba ===\n");

        for (String evento : eventos) {
            for (String jugador : jugadores) {
                TweetResult tr = mock.generate(evento, jugador);
                sender.send(tr);
            }
        }

        System.out.println("\n✅ FIN debug. Cierra el sender.");
        sender.close();
    }
}
