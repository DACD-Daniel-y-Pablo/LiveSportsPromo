package entities;

public class TweetResult {
    private final String text;
    private final int likes;
    private final int comments;
    private final int retweets;

    public TweetResult(String text, int likes, int comments, int retweets) {
        this.text = text;
        this.likes = likes;
        this.comments = comments;
        this.retweets = retweets;
    }

    public String getText() {
        return text;
    }

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }

    public int getRetweets() {
        return retweets;
    }

    @Override
    public String toString() {
        return "Tweet: " + text +
                ", Likes: " + likes +
                ", Comments: " + comments +
                ", Retweets: " + retweets;
    }
}
