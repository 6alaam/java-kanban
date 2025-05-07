package server;


import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;

/**
 * Обработчик http запросов prioritized
 */

public class PrioritizedHandler extends BaseHttpHandler {

    protected PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exc) throws IOException {
        super.handle(exc);

        if (method.equals("GET")) {
            System.out.println("GET prioritized");
            response = gson.toJson(taskManager.getPrioritizedTasks());
            sendText(exc, response, 200);
        } else {
            response = "Метод не разрешен! Доступный метод для prioritized: GET.";
            sendText(exc, response, 405);
        }
    }
}
