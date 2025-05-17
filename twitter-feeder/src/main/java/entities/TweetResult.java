package entities;

public class TweetResult {
    private final String text;
    private final int likes;
    private final int comments;
    private final int retweets;
    private final int score;
    private final String id;

    public TweetResult(String id, String text, int likes, int comments, int retweets, int score) {
        this.id = id;
        this.text     = text;
        this.likes    = likes;
        this.comments = comments;
        this.retweets = retweets;
        this.score    = score;
    }

    public String getId() {
        return id;
    }
    public String getText()       { return text; }
    public int    getLikes()      { return likes; }
    public int    getComments()   { return comments; }
    public int    getRetweets()   { return retweets; }
    public int    getScore()      { return score; }

    @Override
    public String toString() {
        return "id" + id
                + "Tweet: "   + text
                + ", Likes: "    + likes
                + ", Comments: " + comments
                + ", Retweets: " + retweets
                + ", Score: "    + score;
    }
}
