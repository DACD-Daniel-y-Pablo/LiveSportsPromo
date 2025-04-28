package adapters;

import entities.Tweet;
import org.apache.activemq.ActiveMQConnectionFactory;
import ports.TweetSaver;

import javax.jms.*;

public class ActiveMQTweetConsumer implements MessageListener, AutoCloseable {
    private final Connection connection;
    private final Session session;
    private final TweetSaver tweetSaver;

    public ActiveMQTweetConsumer(String brokerUrl, TweetSaver tweetSaver) throws JMSException {
        this.tweetSaver = tweetSaver;

        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        this.connection = factory.createConnection();
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination consumeDestination = session.createTopic("TweetsTopic");
        MessageConsumer consumer = session.createConsumer(consumeDestination);
        consumer.setMessageListener(this);

        connection.start();
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                String payload = textMessage.getText();

                // sin terminar
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws JMSException {
        session.close();
        connection.close();
    }
}
