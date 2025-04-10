package useCases;

import adapters.ApiFixtureProvider;
import adapters.SqliteEventStore;
import entities.Event;
import entities.Fixture;
import entities.FootballLeague;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;


public class Control {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ApiFixtureProvider api;
    private final SqliteEventStore db;

    public Control(ApiFixtureProvider api, SqliteEventStore db) {
        this.api = api;
        this.db = db;
    }


    public void run(FootballLeague league) {
        try {
            ArrayList<Fixture> fixtures = this.api.getFixturesByDate(LocalDate.now(), league);
            if (fixtures.isEmpty()) {
                System.out.println("No hay Encuentros hoy. Reintentando en 24 horas...");
            }
            for (Fixture fixture : fixtures) {
                int callsPerFixture = api.getCallsLimit() / fixtures.size();
                System.out.println("Procesando encuentro: " + fixture.getFixture());
                long delayPlusTenMinutes = Duration.between(LocalDateTime.now(), fixture.getDateTime()).getSeconds() + (60 * 10);

                if (delayPlusTenMinutes > 0) {
                    scheduler.schedule(
                            () -> startEventListener(fixture, callsPerFixture),
                            delayPlusTenMinutes,
                            TimeUnit.SECONDS
                    );
                    System.out.println("Listener programado para: " + fixture.getDateTime().plusMinutes(10));
                } else {
                    System.out.println("El partido ya comenzó o finalizo");
                }
            }
            // Programar próxima ejecución en 24 horas
            scheduler.schedule(
                    () -> this.run(league),
                    24,
                    TimeUnit.HOURS
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startEventListener(Fixture fixture, int callsPerFixture) {
        final int estimatedMatchDurationMinutes = 100;
        final long totalExecutionTime = estimatedMatchDurationMinutes * 60; // en segundos

        // Calcular el intervalo entre llamadas
        final long interval = totalExecutionTime / callsPerFixture;

        ScheduledExecutorService executor = newSingleThreadScheduledExecutor();

        // Tarea que se ejecutará periódicamente
        Runnable task = () -> {
            try {
                ArrayList<Event> events = this.api.getEventsByFixture(fixture);
                this.db.store(events);
                System.out.println("Eventos obtenidos y almacenados para: " + fixture.getFixture());
            } catch (Exception e) {
                System.err.println("Error al obtener eventos: " + e.getMessage());
            }
        };

        // Programar la tarea para que se ejecute periódicamente
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(
                task,
                0,
                interval,
                TimeUnit.SECONDS
        );

        // Programar la finalización después del tiempo estimado del partido
        executor.schedule(() -> {
            future.cancel(true);
            executor.shutdown();
            System.out.println("Listener detenido para: " + fixture.getFixture());
        }, totalExecutionTime, TimeUnit.SECONDS);
    }
}
