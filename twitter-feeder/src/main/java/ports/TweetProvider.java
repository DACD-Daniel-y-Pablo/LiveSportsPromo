package ports;

import entities.TweetResult;

public interface TweetProvider {
    TweetResult generate(String type, String player);
}
