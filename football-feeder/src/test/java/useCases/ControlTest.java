package useCases;

import adapters.ApiFixtureProvider;
import adapters.SqliteEventStore;
import entities.Event;
import entities.Fixture;
import entities.FootballLeague;
import entities.StatusDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ControlTest {

    @Mock
    private ApiFixtureProvider mockApi;

    @Mock
    private SqliteEventStore mockDb;

    @Mock
    private ScheduledExecutorService mockScheduler;

    private Control control;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Crear instancia de Control con el scheduler mockeado
        control = new Control(mockApi, mockDb) {
            private ScheduledExecutorService createScheduler() {
                return mockScheduler;
            }
        };
    }

    @Test
    void testRun_NoFixturesToday() throws IOException {
        // Configurar mock para devolver lista vacía
        when(mockApi.getFixturesByDate(any(LocalDate.class), any(FootballLeague.class)))
                .thenReturn(new ArrayList<>());

        control.run(FootballLeague.LA_LIGA);

        // Verificar que se programa la próxima ejecución en 24 horas
        verify(mockScheduler).schedule(any(Runnable.class), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testRun_WithFixtures() throws IOException {
        // Crear fixture de prueba
        LocalDateTime futureDateTime = LocalDateTime.now().plusHours(2);
        Fixture testFixture = new Fixture(
                1,
                "HomeTeam",
                "AwayTeam",
                LocalDateTime.of(LocalDate.now(), LocalTime.now().plusHours(1)),
                "UTC",
                StatusDescription.NOT_STARTED,
                FootballLeague.LA_LIGA);
        List<Fixture> fixtures = List.of(testFixture);

        // Configurar mocks
        when(mockApi.getFixturesByDate(any(LocalDate.class), any(FootballLeague.class)))
                .thenReturn((ArrayList<Fixture>) fixtures);
        when(mockApi.getCallsLimit()).thenReturn(10);

        control.run(FootballLeague.LA_LIGA);

        // Verificar que se programa el listener para el fixture
        ArgumentCaptor<Runnable> listenerCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockScheduler).schedule(
                listenerCaptor.capture(),
                anyLong(),
                eq(TimeUnit.SECONDS)
        );

        // Verificar que se programa la próxima ejecución en 24 horas
        verify(mockScheduler).schedule(any(Runnable.class), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testRun_IOException() throws IOException {
        // Configurar mock para lanzar excepción
        when(mockApi.getFixturesByDate(any(LocalDate.class), any(FootballLeague.class)))
                .thenThrow(new IOException("API error"));

        assertThrows(RuntimeException.class, () -> control.run(FootballLeague.LA_LIGA));
    }
}