package adapters;

import ports.TweetEventHandler;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQTweetConsumer implements MessageListener, AutoCloseable {
    private final Connection connection;
    private final Session session;
    private final TweetEventHandler handler;

    public ActiveMQTweetConsumer(String brokerUrl, String consumeTopic, TweetEventHandler handler) throws JMSException {
        this.handler = handler;
        ConnectionFactory cf = new ActiveMQConnectionFactory(brokerUrl);
        this.connection = cf.createConnection();
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        setupConsumer(consumeTopic);
    }

    private void setupConsumer(String consumeTopic) throws JMSException {
        Destination dest = session.createTopic(consumeTopic);
        MessageConsumer consumer = session.createConsumer(dest);
        consumer.setMessageListener(this);
        connection.start();
    }

    @Override
    public void onMessage(Message msg) {
        if (!(msg instanceof TextMessage)) return;
        try {
            processMessage((TextMessage) msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMessage(TextMessage msg) throws JMSException {
        String payload = msg.getText();
        String player = extract(payload, "player");
        String event = extract(payload, "type");
        handleIfValid(player, event, payload);
    }

    private void handleIfValid(String player, String event, String payload) throws JMSException {
        if (player != null && event != null) {
            handler.handle(player, event);
        } else {
            System.err.println("⛔ Payload inválido: " + payload);
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

    @Override
    public void close() throws JMSException {
        session.close();
        connection.close();
    }
}
