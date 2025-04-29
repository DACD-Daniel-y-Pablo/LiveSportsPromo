import adapters.*;
import javax.jms.JMSException;

public class Main {
    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        String consumeTopic = "events";
        String produceTopic = "tweets";

        try (
                ActiveMQTweetSender sender = new ActiveMQTweetSender(brokerUrl, produceTopic);
                ActiveMQTweetConsumer consumer = new ActiveMQTweetConsumer(
                        brokerUrl,
                        consumeTopic,
                        new ActiveMQTweetEventHandler(new MockTwitterProvider(), sender)
                )
        ) {
            System.out.println("✅ Consumidor escuchando mensajes. Presiona Ctrl+C para salir...");
            Thread.currentThread().join();
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

