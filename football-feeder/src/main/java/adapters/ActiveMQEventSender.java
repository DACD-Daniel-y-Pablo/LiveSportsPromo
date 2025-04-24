package adapters;

import entities.Event;
import org.apache.activemq.ActiveMQConnectionFactory;
import ports.FixtureEventStore;

import javax.jms.*;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ActiveMQEventSender implements FixtureEventStore {
    private final String brokerUrl;
    private final String topicName;
    private final Set<String> sentEventsCache;

    public ActiveMQEventSender(String brokerUrl, String topicName) {
        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
        this.sentEventsCache = new HashSet<>();
    }

    @Override
    public void store(ArrayList<Event> events) throws JMSException, IOException {
        if (events == null || events.isEmpty()) return;
        try {
            Connection connection = createConnection();
            Session session = connection.createSession(false,  Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = createProducer(session);
            for (Event event : events) {
                sendEventIfNotSentBefore(event, session, producer);
            }
            session.close();
            connection.close();
        } catch (JMSException e) {
            throw new IOException("Error al enviar mensajes a ActiveMQ", e);
        }
    }

    private void sendEventIfNotSentBefore(Event event, Session session, MessageProducer producer) throws JMSException {
        if (!sentEventsCache.contains(event.generateEventKey())) {
            TextMessage message = session.createTextMessage(serializeEvent(event));
            producer.send(message);
            sentEventsCache.add(event.generateEventKey());
            System.out.println("Evento enviado a ActiveMQ: " + event.generateEventKey());
        } else {
            System.out.println("Omitiendo evento ya enviado: " + event.generateEventKey());
        }
    }

    private Connection createConnection() throws JMSException {
        Connection connection = new ActiveMQConnectionFactory(brokerUrl).createConnection();
        connection.start();
        return connection;
    }

    private MessageProducer createProducer(Session session) throws JMSException {
        Destination destination = session.createTopic(topicName);
        return session.createProducer(destination);
    }

    private String serializeEvent(Event event) {
        return String.format(
                "ts=%s,ss=football-feeder,fixture=%s,time_elapsed=%d,team=%s,player=%s,type=%s,detail=%s",
                Instant.now().toString(),
                event.getFixture(),
                event.getTimeElapsed(),
                event.getTeamName(),
                event.getPlayerName(),
                event.getTypeEvent(),
                event.getDetailEvent()
        );
    }

    public void clearCache() {
        sentEventsCache.clear();
    }
}
