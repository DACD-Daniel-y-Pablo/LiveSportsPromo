import jakarta.jms.JMSException;

public class Main {
    public static void main(String[] args) throws JMSException {
        ConsumerTopics consumer = new ConsumerTopics();
        consumer.run();
    }
}
