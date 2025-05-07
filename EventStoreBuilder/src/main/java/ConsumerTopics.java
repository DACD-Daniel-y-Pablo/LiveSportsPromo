import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class ConsumerTopics {
    private static final String url = "tcp://localhost:61616";
    private static final String topic = "EventsTopic"; // Topic name
    private static final ObjectMapper mapper = new ObjectMapper();

    public void run () throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.setClientID("daniel");
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic(topic);
        MessageConsumer consumer = session.createConsumer(destination);

        consumer.setMessageListener(message -> {
            try {
                String jsonText = ((TextMessage) message).getText();
                JsonNode root = mapper.readTree(jsonText);

                String ss = root.get("ss").asText();
                String timestamp = root.get("ts").asText();

                // Convert timestamp to LocalDate
                LocalDate date = Instant.parse(timestamp).atZone(ZoneId.of("UTC")).toLocalDate();
                String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                saveEventToFile(ss, formattedDate, jsonText);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println("running");
    }

    private static void saveEventToFile(String ss, String date, String message) throws IOException {
        String dirPath = "eventstore/" + topic + "/" + ss;
        String filePath = dirPath + "/" + date + ".events";

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(message);
            writer.newLine();
        }
    }
}
