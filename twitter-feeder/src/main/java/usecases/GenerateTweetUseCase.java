package usecases;

import entities.TweetResult;

public interface GenerateTweetUseCase {
    TweetResult generate(String type, String player);
}
