package application;

import infrastructure.adapters.MysqlRepository;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class DatamartController {
    private final MysqlRepository database;
    private static final String URL = "tcp://localhost:61616";
    private static final String[] TOPICS = {"EventsTopic", "tweets"};

    public DatamartController(MysqlRepository db) {
        this.database = db;
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
        connection.setClientID("daniel");
        connection.start();
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private void setupConsumer(Session session, String topicName) throws JMSException {
        Destination destination = session.createTopic(topicName);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(message -> processMessage(message, topicName));
    }

    void processMessage(Message message, String topic) {
        return;
    }
}
