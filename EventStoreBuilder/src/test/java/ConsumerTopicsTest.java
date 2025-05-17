import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import jakarta.jms.TextMessage;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerTopicsTest {
    private ConsumerTopics consumer;
    private final String topic = "TestTopic";
    private final String ss = "testUser";
    private final String date = "20240508";
    private final String message = "{\"ss\": \"testUser\", \"ts\": \"" + Instant.now().toString() + "\"}";

    @BeforeEach
    void setUp() {
        consumer = new ConsumerTopics();
    }

    @AfterEach
    void tearDown() throws IOException {
        Path path = Paths.get("eventstore", topic, ss);
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    void testFormatDate() {
        String formatted = consumer.formatDate("2024-05-08T12:00:00Z");
        assertEquals("20240508", formatted);
    }

    @Test
    void testSaveEventCreatesFile() throws IOException {
        consumer.saveEvent(topic, ss, date, message);
        File file = new File("eventstore/" + topic + "/" + ss + "/" + date + ".events");
        assertTrue(file.exists());
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("\"ss\": \"testUser\""));
    }

    @Test
    void testProcessMessageParsesJson() throws Exception {
        TextMessage mockMsg = Mockito.mock(TextMessage.class);
        Mockito.when(mockMsg.getText()).thenReturn(message);

        consumer.processMessage(mockMsg, topic);

        // Usa la fecha real generada por formatDate
        String actualDate = consumer.formatDate(Instant.now().toString());
        File file = new File("eventstore/" + topic + "/" + ss + "/" + actualDate + ".events");

        System.out.println("Checking file exists: " + file.getAbsolutePath());
        assertTrue(file.exists());
    }
}
