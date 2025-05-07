package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;


import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    Gson gson;
    TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
        gson = GsonBuilder.getGson();

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            String path = httpExchange.getRequestURI().getPath();
            String command = getBaseHandler(path, httpExchange.getRequestMethod());
            String response;


            switch (command) {
                case "get_history":
                    if (manager.getHistory().isEmpty()) {
                        sendNotFound(httpExchange, "История пуста");
                    } else {
                        response = gson.toJson(manager.getHistory());
                        sendText(httpExchange, response);
                    }
                    break;
                default:
                    sendBadRequest(httpExchange, "Некорректный запрос");

            }

        } catch (Exception e) {
            System.out.println(String.format("Возникла ошибка", e.getMessage()));
        }
    }

}
