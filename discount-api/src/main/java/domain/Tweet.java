package domain;

import java.util.UUID;

public class Tweet {
    private final String text;
    private final UUID event_id;
    private final int likes;
    private final int comments;
    private final int retweets;
    private final int score;

    public Tweet(String eventId, String text, int likes, int comments, int retweets, int score) {
        this.event_id = UUID.fromString(eventId);
        this.text     = text;
        this.likes    = likes;
        this.comments = comments;
        this.retweets = retweets;
        this.score    = score;
    }

    public String getText()         { return text; }
    public int    getLikes()        { return likes; }
    public int    getComments()     { return comments; }
    public int    getRetweets()     { return retweets; }
    public int    getScore()        { return score; }
    public UUID getEvent_id()       { return event_id; }

    @Override
    public String toString() {
        return "Tweet: "   + text
                + ", Likes: "    + likes
                + ", Comments: " + comments
                + ", Retweets: " + retweets
                + ", Score: "    + score;
    }
}
