import application.DatamartController;
import application.ServiceController;
import infrastructure.adapters.JavalinService;
import infrastructure.adapters.MysqlRepository;

import javax.jms.JMSException;

public class Main {
    public static void main(String[] args) throws JMSException {
        JavalinService apiService = new JavalinService();
        MysqlRepository database = new MysqlRepository();

        ServiceController apiController = new ServiceController(apiService, database);
        DatamartController datamartHandler = new DatamartController(database);

        apiController.run();
        datamartHandler.run();
    }
}
