package adapters;

import entities.TweetResult;
import ports.TweetSender;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.JSONObject;
import utils.SentimentAnalyzer;

import java.time.Instant;

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
    public void send(TweetResult tweet) throws JMSException {
        int score = SentimentAnalyzer.score(tweet.getText());
        String payload = new JSONObject()
                .put("event_id", tweet.getId())
                .put("ss",       "twitter-feeder")
                .put("ts",       Instant.now().toString())
                .put("text",     tweet.getText())
                .put("likes",    tweet.getLikes())
                .put("retweets", tweet.getRetweets())
                .put("comments", tweet.getComments())
                .put("score",    score).toString();
        System.out.println("📤 JSON enviado: " + payload);
        producer.send(session.createTextMessage(payload));
    }



    @Override
    public void close() throws JMSException {
        session.close();
        connection.close();
    }
}
