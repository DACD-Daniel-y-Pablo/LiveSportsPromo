package usecases;

import adapters.ActiveMQTweetConsumer;
import ports.TweetProvider;
import ports.TweetSender;
import org.json.JSONObject;
import org.json.JSONException;

import javax.jms.JMSException;

public class Controller {
    private final TweetSender sender;
    private final ActiveMQTweetConsumer consumer;
    private final TweetProvider provider;
    private int eventCount = 0;

    public Controller(TweetSender sender, ActiveMQTweetConsumer consumer, TweetProvider provider) {
        this.sender = sender;
        this.consumer = consumer;
        this.provider = provider;
    }

    public void run() {
        consumer.registerListener(this::processMessage);
    }

    void processMessage(String msg) {
        System.out.println("📥 Mensaje recibido: " + msg);
        try {
            handleIfValid(extract(msg, "player"), extract(msg, "type"), msg);
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    private void handleIfValid(String player, String event, String payload) {
        if (player != null && event != null) {
            try {
                System.out.println("Evento leído número " + eventCount + ": " + payload);
                sender.send(provider.generate(event, player));
            } catch (JMSException e) {
                System.err.println("Failed to send tweet: " + e.getMessage());
            }
        } else {
            System.err.println("⛔ Invalid payload: " + payload);
        }
    }

    private String extract(String payload, String key) {
        try {
            JSONObject json = new JSONObject(payload);

            Object value = json.opt(key);
            return value != null ? value.toString() : null;
        } catch (JSONException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return null;
        }
    }
}