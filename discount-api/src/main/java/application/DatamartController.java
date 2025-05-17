package application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Discount;
import domain.Event;
import domain.Tweet;
import infrastructure.adapters.MysqlRepository;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.LocalDate;
import java.util.UUID;

public class DatamartController {
    private final MysqlRepository database;
    private static final String URL = "tcp://localhost:61616";
    private static final String[] TOPICS = {"EventsTopic", "tweets"};
    private final ObjectMapper objectMapper;

    public DatamartController(MysqlRepository db) {
        this.database = db;
        this.objectMapper = new ObjectMapper();
    }

    public void run() throws JMSException {
        Session session = connectAndCreateSession();
        for (String topicName : TOPICS) {
            setupConsumer(session, topicName);
        }
        System.out.println("Listening to topics: EventsTopic and tweets");
    }

    private Session connectAndCreateSession() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(URL);
        Connection connection = connectionFactory.createConnection();
        connection.setClientID("businessUnit");
        connection.start();
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private void setupConsumer(Session session, String topicName) throws JMSException {
        Destination destination = session.createTopic(topicName);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(message -> processMessage(message, topicName));
    }

    void processMessage(Message message, String topic) {
        try {
            if (message instanceof TextMessage) {
                String json = ((TextMessage) message).getText();

                if ("EventsTopic".equals(topic)) {
                    JsonNode node = objectMapper.readTree(json);
                    Event event = new Event(
                            node.get("id").asText(),
                            node.get("fixture").asText(),
                            node.get("time_elapsed").asInt(),
                            node.get("team").asText(),
                            node.get("player").asText(),
                            node.get("type").asText(),
                            node.get("detail").asText(),
                            LocalDate.parse(node.get("ts").asText().substring(0, 10)).atStartOfDay()
                    );
                    database.save(event);
                    System.out.println("Evento guardado: " + event.getId());

                } else if ("tweets".equals(topic)) {
                    JsonNode node = objectMapper.readTree(json);
                    Tweet tweet = new Tweet(
                            node.get("event_id").asText(),
                            node.get("text").asText(),
                            node.get("likes").asInt(),
                            node.get("comments").asInt(),
                            node.get("retweets").asInt(),
                            node.get("score").asInt()
                    );
                    database.save(tweet);
                    System.out.println("Tweet guardado: " + tweet.getEvent_id());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
