package application;

import infrastructure.adapters.JavalinService;
import infrastructure.adapters.MysqlRepository;

public class ServiceController {
    private final JavalinService apiService;
    private final MysqlRepository database;

    public ServiceController(JavalinService apiService, MysqlRepository database) {
        this.apiService = apiService;
        this.database = database;
    }

    public void run() {
        apiService.registerDiscountEndpoint(database);
        apiService.startServer(8080);
    }
}
