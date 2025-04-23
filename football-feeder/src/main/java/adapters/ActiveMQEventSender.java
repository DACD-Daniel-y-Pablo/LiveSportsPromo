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
    private static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "EventsTopic";

    private final String brokerUrl;
    private final String topicName;
    private final Set<String> sentEventsCache; // Para evitar duplicados

    public ActiveMQEventSender() {
        this(DEFAULT_BROKER_URL, TOPIC_NAME);
    }

    public ActiveMQEventSender(String brokerUrl, String topicName) {
        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
        this.sentEventsCache = new HashSet<>();
    }

    @Override
    public void store(ArrayList<Event> events) throws JMSException, IOException {
        if (events == null || events.isEmpty()) {
            return;
        }

        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection =connectionFactory.createConnection();
            connection.start();

            //Creating a non-transactional session to send/receive JMS message.
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createTopic(topicName);
            MessageProducer producer = session.createProducer(destination);
            for (Event event : events) {
                String eventKey = generateEventKey(event);
                if (!sentEventsCache.contains(eventKey)) {
                    // Crear y enviar mensaje
                    TextMessage message = session.createTextMessage(serializeEvent(event));
                    producer.send(message);

                    // Registrar evento como enviado
                    sentEventsCache.add(eventKey);

                    System.out.println("Evento enviado a ActiveMQ: " + eventKey);
                } else {
                    System.out.println("Omitiendo evento ya enviado: " + eventKey);
                }
            }
            session.close();
            connection.close();
        } catch (JMSException e) {
            throw new IOException("Error al enviar mensajes a ActiveMQ", e);
        }
    }

    // Genera una clave única para el evento (similar a tu PRIMARY KEY en SQLite)
    private String generateEventKey(Event event) {
        return String.format("%s|%d|%s|%s",
                event.getFixture(),
                event.getTimeElapsed(),
                event.getPlayerName(),
                event.getTypeEvent());
    }

    // Serializa el evento a formato String (podrías usar JSON)
    private String serializeEvent(Event event) {
        // Implementación básica - considera usar una librería JSON como Jackson
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

    // Método para limpiar la caché si es necesario
    public void clearCache() {
        sentEventsCache.clear();
    }
}
