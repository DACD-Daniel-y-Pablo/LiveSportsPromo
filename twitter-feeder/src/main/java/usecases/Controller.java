package usecases;

import adapters.ActiveMQTweetConsumer;
import ports.TweetProvider;
import ports.TweetSender;

import javax.jms.JMSException;

public class Controller {
    private final TweetSender sender;
    private final ActiveMQTweetConsumer consumer;
    private final TweetProvider provider;

    public Controller(TweetSender sender, ActiveMQTweetConsumer consumer, TweetProvider provider) {
        this.sender = sender;
        this.consumer = consumer;
        this.provider = provider;
    }

    public void run() {
        consumer.registerListener(this::processMessage);
    }

    private void processMessage(String msg) {
        handleIfValid(extract(msg, "player"), extract(msg, "type"), msg);
    }

    private void handleIfValid(String player, String event, String payload) {
        if (player != null && event != null) sendEvent(player, event);
        else System.err.println("⛔ Payload inválido: " + payload);
    }

    private void sendEvent(String player, String event) {
        try {
            sender.send(provider.generate(event, player));
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private String extract(String payload, String key) {
        for (String part : payload.split(",")) {
            String[] kv = part.split("=", 2);
            if (isKeyMatch(kv, key)) return kv[1].trim();
        }
        return null;
    }

    private boolean isKeyMatch(String[] kv, String key) {
        return kv.length == 2 && kv[0].trim().equalsIgnoreCase(key);
    }

}
