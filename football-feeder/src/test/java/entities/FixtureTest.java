package entities;


import org.junit.Test;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;


public class FixtureTest {

    @Test
    public void testConstructorAndGetters() {
        int expectedId = 1;
        String expectedHomeTeam = "Real Madrid";
        String expectedAwayTeam = "Barcelona";
        LocalDateTime expectedDateTime = LocalDateTime.of(2023, 10, 28, 20, 45);
        String expectedTimeZone = "UTC";
        StatusDescription expectedStatus = StatusDescription.NOT_STARTED;
        FootballLeague expectedLeague = FootballLeague.LALIGA;
        String expectedFixture = "Real Madrid vs Barcelona";

        Fixture fixture = new Fixture(
                expectedId,
                expectedHomeTeam,
                expectedAwayTeam,
                expectedDateTime,
                expectedTimeZone,
                expectedStatus,
                expectedLeague
        );

        // Assert
        assertEquals(expectedId, fixture.getId());
        assertEquals(expectedHomeTeam, fixture.getHomeTeam());
        assertEquals(expectedAwayTeam, fixture.getAwayTeam());
        assertEquals(expectedFixture, fixture.getFixture());
        assertEquals(expectedDateTime.plusHours(1), fixture.getDateTime());
        assertEquals(expectedStatus, fixture.getStatus());
        assertEquals(expectedLeague, fixture.getLeague());
    }
}
