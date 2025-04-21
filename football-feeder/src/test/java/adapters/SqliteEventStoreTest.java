package adapters;

import entities.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SqliteEventStoreTest {
    private SqliteEventStore eventStore;

    @BeforeEach
    void setUp() throws SQLException {
        // Usamos una base de datos en memoria para pruebas
        eventStore = new SqliteEventStore(":memory:");

        // Verificar que la conexión se estableció
        assertNotNull(eventStore.getConnection(), "La conexión a la base de datos no se estableció");
    }

    @AfterEach
    void tearDown() {
        if (eventStore != null) {
            eventStore.close();
        }
    }

    @Test
    void shouldStoreEventsCorrectly() throws IOException {
        // Arrange
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event(
                "TeamA vs TeamB",
                25,
                "TeamA",
                "Player1",
                "goal",
                "normal goal"
        ));
        events.add(new Event(
                "TeamA vs TeamB",
                30,
                "TeamA",
                "Player2",
                "card",
                "yellow card"
        ));

        // Act
        eventStore.store(events);

        // Assert - Verificar que los eventos se almacenaron correctamente
        // Necesitarías un método getEventsForFixture en SqliteEventStore para verificar
        // O podrías verificar directamente en la base de datos
    }

    @Test
    void shouldIgnoreDuplicateEvents() throws IOException {
        // Arrange
        ArrayList<Event> events = new ArrayList<>();
        Event event = new Event(
                "TeamA vs TeamB",
                25,
                "TeamA",
                "Player1",
                "goal",
                "normal goal"
        );
        events.add(event);
        events.add(event); // Duplicado

        // Act
        eventStore.store(events);

        // Assert - Debería insertar solo 1 evento
        // Necesitarías un método countEvents en SqliteEventStore
    }

    @Test
    void shouldHandleEmptyEventsList() {
        assertDoesNotThrow(() -> {
            eventStore.store(new ArrayList<>());
        });
    }
}