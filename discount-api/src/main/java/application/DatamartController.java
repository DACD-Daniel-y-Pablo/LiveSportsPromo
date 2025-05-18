package application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Discount;
import domain.Event;
import domain.Tweet;
import infrastructure.adapters.MysqlRepository;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatamartController {
    private final MysqlRepository database;
    private  final String URL;
    private final String[] TOPICS;
    private final ObjectMapper mapper = new ObjectMapper();

    public DatamartController(MysqlRepository db, String url, String[] topics) {
        this.database = db;
        this.URL = url;
        this.TOPICS = topics;
    }

    public void run() throws JMSException {
        Session session = connect();
        for (String topic : TOPICS) listenToTopic(session, topic);
        scheduleCleanUp();
        scheduleDiscounts();
        System.out.println("Listening to: EventsTopic and tweets");
    }

    private Session connect() throws JMSException {
        Connection conn = new ActiveMQConnectionFactory(URL).createConnection();
        conn.setClientID("businessUnit");
        conn.start();
        return conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private void listenToTopic(Session session, String topic) throws JMSException {
        Destination dest = session.createTopic(topic);
        MessageConsumer consumer = session.createConsumer(dest);
        consumer.setMessageListener(msg -> handleMessage(msg, topic));
    }

    private void handleMessage(Message message, String topic) {
        try {
            if (message instanceof TextMessage) {
                processText(((TextMessage) message).getText(), topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processText(String json, String topic) throws Exception {
        JsonNode node = mapper.readTree(json);
        if ("EventsTopic".equals(topic)) saveEvent(node);
        else if ("tweets".equals(topic)) saveTweet(node);
    }

    private void saveEvent(JsonNode node) throws SQLException {
        Event event = new Event(
                node.get("id").asText(), node.get("fixture").asText(),
                node.get("time_elapsed").asInt(), node.get("team").asText(),
                node.get("player").asText(), node.get("type").asText(),
                node.get("detail").asText(),
                LocalDateTime.parse(node.get("ts").asText().substring(0, 10) + "T00:00")
        );
        database.save(event);
        System.out.println("Evento guardado: " + event.getId());
    }

    private void saveTweet(JsonNode node) throws SQLException {
        Tweet tweet = new Tweet(
                node.get("event_id").asText(), node.get("text").asText(),
                node.get("likes").asInt(), node.get("comments").asInt(),
                node.get("retweets").asInt(), node.get("score").asInt()
        );
        database.save(tweet);
        System.out.println("Tweet guardado: " + tweet.getEvent_id());
    }

    private void scheduleDiscounts() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try { applyDiscounts(); } catch (Exception e) { e.printStackTrace(); }
        }, 10, 30, TimeUnit.SECONDS);
    }

    private void applyDiscounts() throws SQLException {
        for (Event e : database.getEventsByType("Goal")) {
            ArrayList<Tweet> tweets = database.getTweetByEventId(e.getId().toString());
            if (tweets.isEmpty()) continue;
            double avg = tweets.stream().mapToInt(Tweet::getScore).average().orElse(0.0);
            if (avg > 0 && !database.isDiscountApplied(e.getPlayerName())) {
                database.save(new Discount(e.getPlayerName(), 15, e.getTeamName(), e.getTimestamp().toLocalDate().plusDays(1)));
                System.out.println("Descuento aplicado a: " + e.getPlayerName());
            }
        }
    }

    private void scheduleCleanUp() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try { clean(); } catch (Exception e) { e.printStackTrace(); }
        }, 0, 1, TimeUnit.HOURS);
    }

    private void clean() throws SQLException {
        database.deleteExpiredDiscounts();
        for (Event e : database.getAllEvents()) {
            if (e.getTimestamp().isBefore(LocalDateTime.now().minusHours(6))) {
                String id = e.getId().toString();
                database.deleteTweetsByEventId(id);
                database.deleteEventById(id);
                System.out.println("Eliminado evento y tweets ID: " + id);
            }
        }
    }
}
