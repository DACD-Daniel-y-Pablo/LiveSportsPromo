import adapters.*;
import usecases.Controller;

import javax.jms.JMSException;

public class Main {
    public static void main(String[] args) {

        String brokerUrl = args[0];
        String consumeTopic = args[1];
        String produceTopic = args[2];
        String bearerToken = args[3];

        try {
            new Controller(new ActiveMQTweetSender(brokerUrl, produceTopic), new ActiveMQTweetConsumer(brokerUrl, consumeTopic), new MockTwitterProvider(bearerToken)).run();
            System.out.println("✅ Consumidor escuchando mensajes");
            Thread.currentThread().join();
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

