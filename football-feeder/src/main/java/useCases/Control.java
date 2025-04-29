package useCases;

import entities.Event;
import entities.Fixture;
import entities.FootballLeague;
import ports.FixtureEventStore;
import ports.FixtureProvider;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Control {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final FixtureProvider api;
    private final FixtureEventStore db;

    public Control(FixtureProvider api, FixtureEventStore db) {
        this.api = api;
        this.db = db;
    }

    public void run(FootballLeague league) {
        try {
            ArrayList<Fixture> fixtures = this.api.getFixturesByDate(LocalDate.now(), league);
            if (fixtures.isEmpty()) log("No hay Encuentros hoy. Reintentando en 24 horas...");
            fixtures.forEach(fixture -> scheduleListener(fixture, api.getCallsLimit()/fixtures.size()));
            rescheduleTomorrow(league);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void scheduleListener(Fixture f, int calls) {
        long delay = Duration.between(LocalDateTime.now(), f.getDateTime()).getSeconds() + 60;
        if (delay > 0) {
            scheduler.schedule(() -> startEventListener(f, calls), delay, TimeUnit.SECONDS);
            log("Listener programado para: " + f.getDateTime().plusSeconds(60));
        } else log("Partido ya comenzó o finalizó");
    }

    private void rescheduleTomorrow(FootballLeague league) {
        scheduler.schedule(() -> run(league), 24, TimeUnit.HOURS);
    }

    public void startEventListener(Fixture fixture, int callsPerFixture) {
        final int totalMatchTime = 100 * 60;
        final long interval = totalMatchTime / callsPerFixture;
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> fetchAndStore(fixture), 0, interval, TimeUnit.SECONDS);
        executor.schedule(() -> stopListener(future, executor, fixture), totalMatchTime, TimeUnit.SECONDS);
    }

    private void fetchAndStore(Fixture f) {
        try {
            ArrayList<Event> events = api.getEventsByFixture(f);
            db.store(events);
            log("Eventos almacenados: " + f.getFixture());
        } catch (Exception e) {
            log("Error al obtener eventos: " + e.getMessage());
        }
    }

    private void stopListener(ScheduledFuture<?> f, ExecutorService e, Fixture fix) {
        f.cancel(true);
        e.shutdown();
        log("Listener detenido: " + fix.getFixture());
    }

    private void log(String message) {
        System.out.println(message);
    }
}
