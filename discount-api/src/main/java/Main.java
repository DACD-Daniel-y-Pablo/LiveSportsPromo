import application.DatamartController;
import application.ServiceController;
import infrastructure.adapters.JavalinService;
import infrastructure.adapters.MysqlRepository;

import javax.jms.JMSException;

public class Main {
    public static void main(String[] args) throws JMSException {
        JavalinService apiService = new JavalinService();
        MysqlRepository database = new MysqlRepository(args[3], args[4], args[5]);

        ServiceController apiController = new ServiceController(apiService, database);
        DatamartController datamartHandler = new DatamartController(database, args[0], new String[]{args[1], args[2]});

        apiController.run();
        datamartHandler.run();
    }
}
