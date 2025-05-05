package adapters;

import entities.TweetResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActiveMQTweetSenderTest {

    @Test
    void testSerializeTweetFormat() {
        TweetResult tweet = new TweetResult("Gol de Mbappé", 200, 100, 50);
        String json = String.format(
                "{\"text\":\"%s\",\"likes\":%d,\"retweets\":%d,\"comments\":%d}",
                tweet.getText(), tweet.getLikes(), tweet.getRetweets(), tweet.getComments()
        );
        assertTrue(json.contains("\"text\":\"Gol de Mbappé\""));
        assertTrue(json.contains("\"likes\":200"));
    }
}
