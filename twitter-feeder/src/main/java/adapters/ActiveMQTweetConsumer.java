package adapters;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.function.Consumer;

public class ActiveMQTweetConsumer {
    private final Connection connection;
    private final Session session;
    private Consumer<String> listener;

    public ActiveMQTweetConsumer(String brokerUrl, String consumeTopic) throws JMSException {
        ConnectionFactory cf = new ActiveMQConnectionFactory(brokerUrl);
        this.connection = cf.createConnection();
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        setupConsumer(consumeTopic);
    }

    public void registerListener(Consumer<String> listener) {
        this.listener = listener;
    }

    private void setupConsumer(String consumeTopic) throws JMSException {
        Destination dest = session.createTopic(consumeTopic);
        MessageConsumer consumer = session.createConsumer(dest);
        consumer.setMessageListener(msg -> {
            if (!(msg instanceof TextMessage)) return;
            try {
                this.listener.accept(((TextMessage) msg).getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        connection.start();
    }
}
