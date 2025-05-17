import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.fasterxml.jackson.databind.*;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class ConsumerTopics {
    private static final String URL = "tcp://localhost:61616";
    private static final String[] TOPICS = {"EventsTopic", "tweets"};
    private static final ObjectMapper mapper = new ObjectMapper();

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
        connection.setClientID("EventStoreBuilder");
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
            String text = ((TextMessage) message).getText();
            JsonNode root = mapper.readTree(text);
            String ss = root.has("ss") ? root.get("ss").asText() : "unknown";
            String ts = root.has("ts") ? root.get("ts").asText() : Instant.now().toString();
            saveEvent(topic, ss, formatDate(ts), text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String formatDate(String ts) {
        LocalDate date = Instant.parse(ts).atZone(ZoneId.of("UTC")).toLocalDate();
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    void saveEvent(String topic, String ss, String date, String msg) throws IOException {
        String dir = "eventstore/" + topic + "/" + ss;
        File f = new File(dir);
        if (!f.exists() && !f.mkdirs()) throw new IOException("Could not create directory: " + f.getAbsolutePath());
        try (BufferedWriter w = new BufferedWriter(new FileWriter(dir + "/" + date + ".events", true))) {
            w.write(msg);
            w.newLine();
        }
    }
}
