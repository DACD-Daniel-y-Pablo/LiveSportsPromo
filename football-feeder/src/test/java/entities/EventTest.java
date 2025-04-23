package entities;

import org.junit.Test;

import static org.junit.Assert.*;

public class EventTest {
    @Test
    public void testConstructorAndGetters() {
        // Arrange
        String expectedFixture = "Barcelona vs Real Madrid";
        int expectedTimeElapsed = 23;
        String expectedTeamName = "Barcelona";
        String expectedPlayerName = "Lionel Messi";
        String expectedTypeEvent = "Goal";
        String expectedDetailEvent = "Left foot shot from center box";

        // Act
        Event event = new Event(
                expectedFixture,
                expectedTimeElapsed,
                expectedTeamName,
                expectedPlayerName,
                expectedTypeEvent,
                expectedDetailEvent
        );

        // Assert
        assertEquals(expectedFixture, event.getFixture());
        assertEquals(expectedTimeElapsed, event.getTimeElapsed());
        assertEquals(expectedTeamName, event.getTeamName());
        assertEquals(expectedPlayerName, event.getPlayerName());
        assertEquals(expectedTypeEvent, event.getTypeEvent());
        assertEquals(expectedDetailEvent, event.getDetailEvent());
    }


    @Test
    public void testBoundaryValues() {
        // Test para tiempo mínimo (0 minutos)
        Event minTimeEvent = new Event(
                "Fixture",
                0,
                "Team",
                "Player",
                "Type",
                "Detail"
        );
        assertEquals(0, minTimeEvent.getTimeElapsed());

        // Test para tiempo máximo (120 minutos - tiempo extra)
        Event maxTimeEvent = new Event(
                "Fixture",
                120,
                "Team",
                "Player",
                "Type",
                "Detail"
        );
        assertEquals(120, maxTimeEvent.getTimeElapsed());
    }

    @Test
    public void testNullValues() {
        // Test con valores nulos (dependiendo de los requisitos)
        assertThrows(NullPointerException.class, () -> {
            new Event(null, 10, "Team", "Player", "Type", "Detail");
        });

        assertThrows(NullPointerException.class, () -> {
            new Event("Fixture", 10, null, "Player", "Type", "Detail");
        });
    }

    @Test
    public void testSpecialCharacters() {
        // Test con caracteres especiales en nombres
        Event specialCharEvent = new Event(
                "Fixtüre #123",
                45,
                "Tëam Námë",
                "Jürgen Klöpp",
                "Yéllow Cärd",
                "Föul commïtted"
        );

        assertEquals("Fixtüre #123", specialCharEvent.getFixture());
        assertEquals("Tëam Námë", specialCharEvent.getTeamName());
        assertEquals("Jürgen Klöpp", specialCharEvent.getPlayerName());
    }
}
