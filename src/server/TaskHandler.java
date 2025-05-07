package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;


import java.io.IOException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    Gson gson;
    TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
        gson = GsonBuilder.getGson();

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            String path = httpExchange.getRequestURI().getPath();
            String command = getBaseHandler(path, httpExchange.getRequestMethod());
            String response;
            String[] splitPaths = path.split("/");
            int id = 0;

            switch (command) {
                case "get_tasks_id":

                    try {
                        if (splitPaths.length == 3) {
                            id = Integer.parseInt(splitPaths[2]);
                        }
                    } catch (NumberFormatException e) {
                        sendBadRequest(httpExchange, "Некорректный формат id задачи");
                    }
                    if (manager.getTaskById(id) != null) {
                        response = gson.toJson(manager.getTaskById(id));
                        sendText(httpExchange, response);
                    } else {
                        sendNotFound(httpExchange, "Задача с id " + splitPaths[2] + " не найдена");
                    }
                    break;

                case "get_tasks":
                    if (manager.getAllTasks().isEmpty()) {
                        sendNotFound(httpExchange, "Список задач пуст");
                    } else {
                        response = gson.toJson(manager.getAllTasks());
                        sendText(httpExchange, response);
                    }
                    break;
                case "post_tasks":
                    Optional<String> body = getBodyForHandler(httpExchange);
                    if (body.isEmpty()) {
                        sendNotFound(httpExchange, "Задача не передана в теле POST запроса");
                        return;
                    }
                    String taskFromBody = body.get();
                    Task task = gson.fromJson(taskFromBody, Task.class);
                    if (task.getId() > 0) {
                        if (manager.getTaskById(task.getId()) != null) {
                            manager.updateTask(task);
                            if (manager.getTaskById(task.getId()) != null) {
                                sendSuccess(httpExchange, "Задача обновлена");
                            } else {
                                sendHasInteractions(httpExchange, "Задача пересекается по времени");
                            }
                        } else {
                            sendNotFound(httpExchange, "задачи с id " + task.getId() + " не существует.");
                        }
                    } else if ((task.getId() == 0)) {
                        if (manager.timeMatchCheck(task)) {
                            manager.addTask(task);
                            sendSuccess(httpExchange, "Задача добавлена");
                        } else {
                            sendHasInteractions(httpExchange, "Задача пересекается по времени с существующими");
                        }
                    } else {
                        sendBadRequest(httpExchange, "Некорректный id");
                    }
                    break;

                case "delete_tasks_id":
                    try {
                        if (splitPaths.length == 3) {
                            id = Integer.parseInt(splitPaths[2]);
                        }
                    } catch (NumberFormatException e) {
                        sendBadRequest(httpExchange, "Некорректный формат id задачи");
                    }
                    if (manager.getTaskById(id) != null) {
                        manager.deleteTaskByID(id);
                        sendText(httpExchange, "Задача удалена");
                    } else {
                        sendNotFound(httpExchange, "Задача c id " + id + "не найдена");
                    }
                    break;
                default:
                    sendBadRequest(httpExchange, "Некорректный запрос");

            }

        } catch (Exception e) {
            sendBadRequest(httpExchange, "Возникла ошибка");
        }
    }

}
