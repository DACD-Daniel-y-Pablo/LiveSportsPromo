import adapters.MockTwitterProvider;
import entities.TweetResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockTwitterProviderTest {

    private final MockTwitterProvider provider = new MockTwitterProvider();

    @Test
    void shouldGenerateTweetForGoal() {
        TweetResult result = provider.generate("gol", "Vinicius");
        assertNotNull(result);
        assertTrue(result.getText().contains("Vinicius"));
        assertTrue(result.getLikes() >= 0);
        assertTrue(result.getComments() >= 0);
        assertTrue(result.getRetweets() >= 0);
    }

    @Test
    void shouldReturnFallbackTextForUnknownEvent() {
        TweetResult result = provider.generate("evento_desconocido", "Modric");
        assertNotNull(result);
        assertTrue(result.getText().toLowerCase().contains("modric"));
    }
}
