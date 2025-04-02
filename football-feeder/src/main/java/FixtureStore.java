import java.io.IOException;

public interface FixtureStore {
    void store(Fixture fixture) throws IOException;
}
