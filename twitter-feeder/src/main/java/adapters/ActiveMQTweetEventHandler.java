package adapters;

import entities.TweetResult;
import ports.TweetEventHandler;
import ports.TweetSender;
import usecases.GenerateTweetUseCase;

import javax.jms.JMSException;

public class ActiveMQTweetEventHandler implements TweetEventHandler {
    private final GenerateTweetUseCase useCase;
    private final TweetSender sender;

    public ActiveMQTweetEventHandler(GenerateTweetUseCase useCase, TweetSender sender) {
        this.useCase = useCase;
        this.sender  = sender;
    }

    @Override
    public void handle(String player, String event) throws JMSException {
        TweetResult tweet = useCase.generate(event, player);
        sender.send(tweet);
    }
}
