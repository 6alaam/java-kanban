package Server;

import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;

/**
 * Класс обрадотчика запросов history
 */

public class HistoryHandler extends BaseHttpHandler {

    protected HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exc) throws IOException {
        super.handle(exc);

        if (method.equals("GET")) {
            System.out.println("GET history");
            response = gson.toJson(historyManager.getHistory());
            sendText(exc, response, 200);
        } else {
            response = "Метод не разрешен! Доступный метод для history: GET.";
            sendText(exc, response, 405);
        }
    }
}
