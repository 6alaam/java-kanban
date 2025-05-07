package server;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import service.TaskManager;


import java.io.IOException;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    Gson gson;
    TaskManager manager;

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
        gson = GsonBuilder.getGson();

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            String path = httpExchange.getRequestURI().getPath();
            String command = getBaseHandler(path, httpExchange.getRequestMethod());
            String response;
            String[] splitPath = path.split("/");
            int id = 0;

            switch (command) {
                case "get_epics_id_subtasks":
                    try {
                        id = Integer.parseInt(splitPath[2]);

                    } catch (NumberFormatException e) {
                        sendBadRequest(httpExchange, "Некорректный формат id эпика");
                    }
                    if (manager.getEpicById(id) != null) {
                        if (manager.getEpicSubtasks(manager.getEpicById(id)).isEmpty()) {
                            sendNotFound(httpExchange, "Список сабтасков пуст");
                        } else {
                            response = gson.toJson(manager.getEpicSubtasks(manager.getEpicById(id)));
                            sendText(httpExchange, response);
                        }
                    } else {
                        sendNotFound(httpExchange, "Эпик с id " + splitPath[2] + " не найден");
                    }
                    break;
                case "get_epics_id":
                    try {
                        id = Integer.parseInt(splitPath[2]);

                    } catch (NumberFormatException e) {
                        sendBadRequest(httpExchange, "Некорректный формат id эпика");
                    }
                    if (manager.getEpicById(id) != null) {
                        response = gson.toJson(manager.getEpicById(id));
                        sendText(httpExchange, response);
                    } else {
                        sendNotFound(httpExchange, "Эпик с id " + splitPath[2] + " не найден");
                    }
                    break;

                case "get_epics":
                    if (manager.getAllEpic().isEmpty()) {
                        sendNotFound(
                                httpExchange, "Список эпиков пуст");
                    } else {
                        response = gson.toJson(manager.getAllEpic());
                        sendText(httpExchange, response);
                    }
                    break;
                case "post_epics":
                    Optional<String> body = getBodyForHandler(httpExchange);
                    if (body.isEmpty()) {
                        sendNotFound(httpExchange, "Эпик не передан в теле POST запроса");
                        return;
                    }
                    Epic epic = gson.fromJson(body.get(), Epic.class);
                    manager.addEpic(epic);
                    sendSuccess(httpExchange, "Эпик добавлен");
                    break;

                case "delete_epics_id":
                    try {
                        id = Integer.parseInt(splitPath[2]);

                    } catch (NumberFormatException e) {
                        sendBadRequest(httpExchange, "Некорректный формат id эпика");
                    }
                    if (manager.getEpicById(id) != null) {
                        manager.deleteEpicByID(id);
                        sendText(httpExchange, "Эпик удален");
                    } else {
                        sendNotFound(httpExchange, "Эпик c id " + id + "не найден");
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
