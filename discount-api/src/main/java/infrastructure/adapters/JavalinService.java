package infrastructure.adapters;

import infrastructure.ports.DiscountService;
import infrastructure.ports.Repository;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;;

public class JavalinService implements DiscountService {
    private final Javalin app;

    public JavalinService() {
        app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson());
        });
    }


    @Override
    public void startServer(int port) {
        app.start(port);
    }

    @Override
    public void registerDiscountEndpoint(Repository db) {
        app.get("/discounts", ctx -> {
            try {
                ctx.json(db.getAllDiscounts());
            } catch (Exception e) {
                e.printStackTrace(); // <-- Añade esto para ver el error real en la consola
                ctx.status(500).result("Error fetching discounts");
            }
        });
    }
}
