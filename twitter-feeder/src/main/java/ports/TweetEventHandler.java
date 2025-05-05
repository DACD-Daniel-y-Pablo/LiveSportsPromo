package ports;

import javax.jms.JMSException;

public interface TweetEventHandler {
    void handle(String player, String event) throws JMSException;
}
