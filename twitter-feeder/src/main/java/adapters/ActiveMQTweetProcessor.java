package adapters;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import ports.TweetEventListener;
import ports.TweetSender;
import usecases.GenerateTweetUseCase;
import entities.TweetResult;

public class ActiveMQTweetProcessor implements MessageListener, AutoCloseable, TweetEventListener {
    private final Session session;
    private final MessageProducer producer;
    private final GenerateTweetUseCase tweetUseCase;

    public ActiveMQTweetProcessor(String brokerUrl, GenerateTweetUseCase tweetUseCase) throws JMSException {
        this.tweetUseCase = tweetUseCase;
        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        Connection connection = factory.createConnection();
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination topic = session.createTopic("EventsTopic");
        MessageConsumer consumer = session.createConsumer(topic);
        consumer.setMessageListener(this);
        this.producer = session.createProducer(session.createTopic("TweetsTopic"));
        connection.start();
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String payload = ((TextMessage) message).getText();
                onEvent(payload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(String payload) {
        String type = extractField(payload, "type");
        String player = extractField(payload, "player");
        TweetResult tweet = tweetUseCase.generate(type, player);
        try {
            TextMessage msg = session.createTextMessage(tweet.toString());
            producer.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private String extractField(String payload, String key) {
        for (String part : payload.split(",")) {
            if (part.trim().startsWith(key + "=")) {
                return part.substring(part.indexOf('=') + 1);
            }
        }
        throw new IllegalArgumentException("Missing field: " + key);
    }

    @Override
    public void close() throws JMSException {
        session.close();
    }
}
