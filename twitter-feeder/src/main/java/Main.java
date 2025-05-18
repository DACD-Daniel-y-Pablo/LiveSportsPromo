import adapters.ActiveMQTweetConsumer;
import adapters.ActiveMQTweetSender;
import adapters.MockTwitterProvider;
import adapters.TwitterProvider;
import usecases.Controller;

import javax.jms.JMSException;

public class Main {
    private static void printUsage() {
        System.out.println("Uso: java Main <brokerUrl> <consumeTopic> <produceTopic> <bearerToken>");
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Error: parámetros insuficientes.");
            printUsage();
            System.exit(1);
        }

        String brokerUrl = args[0];
        String consumeTopic = args[1];
        String produceTopic = args[2];
        String bearerToken = args[3];

        try {
            TwitterProvider twitterProvider = new TwitterProvider(bearerToken);
            MockTwitterProvider mockTwitterProvider = new MockTwitterProvider(twitterProvider);

            Controller controller = new Controller(
                    new ActiveMQTweetSender(brokerUrl, produceTopic),
                    new ActiveMQTweetConsumer(brokerUrl, consumeTopic),
                    mockTwitterProvider
            );

            controller.run();
            System.out.println("✅ Consumidor escuchando mensajes");
            Thread.currentThread().join();
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
}
