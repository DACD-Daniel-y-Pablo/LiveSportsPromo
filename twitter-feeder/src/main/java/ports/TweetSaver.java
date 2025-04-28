package ports;

import entities.Tweet;

public interface TweetSaver {
    void save(Tweet tweet) throws Exception;
}
