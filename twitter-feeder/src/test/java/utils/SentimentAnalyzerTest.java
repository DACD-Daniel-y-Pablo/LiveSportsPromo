package utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SentimentAnalyzerTest {

    @Test void positiveTweetGivesHighScore() {
        String text = "This is a brilliant and excellent performance!";
        int s = SentimentAnalyzer.score(text);
        assertTrue(s >= 7 && s <= 10);
    }

    @Test void negativeTweetGivesLowScore() {
        String text = "This was a horrible and terrible mistake.";
        int s = SentimentAnalyzer.score(text);
        assertTrue(s >= 0 && s <= 3);
    }

    @Test void neutralTweetGivesMiddleScore() {
        String text = "The match ended at 1-1 with no major incidents.";
        int s = SentimentAnalyzer.score(text);
        assertTrue(s >= 2 && s <= 8);
    }
}
