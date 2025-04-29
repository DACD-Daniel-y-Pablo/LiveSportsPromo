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
        Destination dest = session.createTopic(consumeTopic);
        MessageConsumer consumer = session.createConsumer(dest);
        consumer.setMessageListener(this);
        connection.start();
    }

    @Override
    public void onMessage(Message msg) {
        try {
            if (!(msg instanceof TextMessage)) return;
            String payload = ((TextMessage) msg).getText();
            String player = extract(payload, "player");
            String event  = extract(payload, "type");
            if (player != null && event != null) {
                handler.handle(player, event);
            } else {
                System.err.println("⛔ Payload inválido: " + payload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String extract(String payload, String key) {
        for (String part : payload.split(",")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && kv[0].trim().equalsIgnoreCase(key)) {
                return kv[1].trim();
            }
        }
        return null;
    }

    @Override
    public void close() throws JMSException {
        session.close();
        connection.close();
    }
}
