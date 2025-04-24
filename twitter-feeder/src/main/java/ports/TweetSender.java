package ports;

import entities.TweetResult;

public interface TweetSender {
    void send(TweetResult tweet);
}
