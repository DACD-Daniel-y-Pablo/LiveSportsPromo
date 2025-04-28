package adapters;

import entities.TweetResult;
import ports.TweetSender;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQTweetSender implements TweetSender, AutoCloseable {
    private final Connection connection;
    private final Session session;
    private final MessageProducer producer;

    public ActiveMQTweetSender(String brokerUrl, String produceTopic) throws JMSException {
        ConnectionFactory cf = new ActiveMQConnectionFactory(brokerUrl);
        this.connection = cf.createConnection();
        this.session    = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = session.createTopic(produceTopic);
        this.producer = session.createProducer(dest);
        connection.start();
    }

    @Override
    public void send(TweetResult tr) throws JMSException {
        // Serialización simple
        String payload = String.format(
                "tweet=%s,likes=%d,comments=%d,retweets=%d",
                tr.getText(), tr.getLikes(), tr.getComments(), tr.getRetweets()
        );
        TextMessage msg = session.createTextMessage(payload);
        producer.send(msg);
    }

    @Override
    public void close() throws JMSException {
        session.close();
        connection.close();
    }
}
