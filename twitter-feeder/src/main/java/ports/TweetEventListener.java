package ports;

public interface TweetEventListener {
    void onEvent(String payload);
}
