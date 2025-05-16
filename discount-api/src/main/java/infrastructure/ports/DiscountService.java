package infrastructure.ports;

public interface DiscountService {
    void startServer(int port);
    void registerDiscountEndpoint(Repository db);
}
