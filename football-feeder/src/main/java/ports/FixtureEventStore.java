package ports;

import entities.Event;

import java.io.IOException;
import java.util.ArrayList;

public interface FixtureEventStore {
    void store(ArrayList<Event> events) throws IOException;
}
