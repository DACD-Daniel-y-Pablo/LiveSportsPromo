    package ports;

    import entities.TweetResult;
    import javax.jms.JMSException;

    public interface TweetSender {
        void send(TweetResult tweetResult) throws JMSException;
    }