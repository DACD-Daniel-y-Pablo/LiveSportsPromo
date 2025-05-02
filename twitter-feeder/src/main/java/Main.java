import adapters.*;
import usecases.Controller;

import javax.jms.JMSException;

public class Main {
    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        String consumeTopic = "EventsTopic";
        String produceTopic = "tweets";
        try {
            new Controller(new ActiveMQTweetSender(brokerUrl, produceTopic), new ActiveMQTweetConsumer(brokerUrl, consumeTopic), new MockTwitterProvider()).run();
            System.out.println("✅ Consumidor escuchando mensajes. Presiona Ctrl+C para salir...");
            Thread.currentThread().join();
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

