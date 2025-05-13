package adapters;

import entities.TweetResult;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TwitterStoreTest {
    private static Connection sharedConn;
    private TwitterStore store;

    @BeforeAll
    static void initAll() throws Exception {
        // URL de memoria compartida
        String url = "jdbc:sqlite:file:memdb?mode=memory&cache=shared";
        sharedConn = DriverManager.getConnection(url);
    }

    @AfterAll
    static void tearDownAll() throws Exception {
        if (sharedConn != null && !sharedConn.isClosed()) {
            sharedConn.close();
        }
    }

    @BeforeEach
    void setUp() {
        // Instancia usando conexión compartida
        store = new TwitterStore(sharedConn);
        store.initializeDatabase();
    }

    @Test
    void testInsertTweetResultsInMemory() throws Exception {
        TweetResult t1 = new TweetResult("hello world", 123, 45, 67, 7);
        store.insertTweetResults(List.of(t1));

        try (Statement stmt = sharedConn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT text, likes, retweets, comments, score FROM tweets")) {

            assertTrue(rs.next(), "Debe haber al menos una fila");
            assertEquals("hello world", rs.getString("text"));
            assertEquals(123, rs.getInt("likes"));
            assertEquals(67, rs.getInt("retweets"));
            assertEquals(45, rs.getInt("comments"));
            assertEquals(7, rs.getInt("score"));
            assertFalse(rs.next(), "Solo esperábamos una fila");
        }
    }
}
