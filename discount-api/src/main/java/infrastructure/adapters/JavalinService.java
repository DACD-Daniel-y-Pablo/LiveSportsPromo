package infrastructure.adapters;

import infrastructure.ports.DiscountService;
import infrastructure.ports.Repository;
import io.javalin.Javalin;

public class JavalinService implements DiscountService {
    private final Javalin app;

    public JavalinService() {
        app = Javalin.create();
    }


    @Override
    public void startServer(int port) {
        app.start(port);
    }

    @Override
    public void registerDiscountEndpoint(Repository db) {
        app.get("/discounts", ctx -> {
            try {
                // ctx.json(db.getAllDiscounts());
                ctx.result("Descuentos");
            } catch (Exception e) {
                ctx.status(500).result("Error fetching discounts");
            }
        });
    }
}
