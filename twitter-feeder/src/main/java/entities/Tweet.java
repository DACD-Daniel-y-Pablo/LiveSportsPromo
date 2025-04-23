package entities;

import org.json.JSONObject;

public class Tweet {
    private final String text;
    private final String createdAt;
    private final int retweetCount;
    private final int likeCount;
    private final int replyCount;
    private Integer score;


    public Tweet(JSONObject tweetJson) {
        this.text = tweetJson.getString("text");
        this.createdAt = tweetJson.getString("created_at");
        JSONObject metrics = tweetJson.getJSONObject("public_metrics");
        this.retweetCount = metrics.optInt("retweet_count", 0);
        this.likeCount = metrics.optInt("like_count", 0);
        this.replyCount = metrics.optInt("reply_count", 0);
        this.score = null;
    }

    public String getText() {
        return text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "text='" + text + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", retweetCount=" + retweetCount +
                ", likeCount=" + likeCount +
                ", replyCount=" + replyCount +
                ", score=" + score +
                '}';
    }
}
