package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;



public class TasksHandler extends BaseHttpHandler {

    protected TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        String idString = "";
        int idInt = 0;
        if (query != null && query.startsWith("id=")) {
            idString = query.substring(3);
            try {
                idInt = Integer.parseInt(idString);
            } catch (NumberFormatException e) {
                sendResponse(exchange, "Некорректный ID задачи", 400, "text/plain");
                return;
            }
        }

        try {
            switch (method) {
                case "POST":
                    handlePostRequest(exchange, body, idString, idInt);
                    break;
                case "GET":
                    handleGetRequest(exchange, idString, idInt);
                    break;
                case "DELETE":
                    handleDeleteRequest(exchange, idString, idInt);
                    break;
                default:
                    sendResponse(exchange,
                            "Метод не разрешен! Доступные методы: GET, POST, DELETE.",
                            405,
                            "text/plain");
            }
        } catch (JsonSyntaxException e) {
            sendResponse(exchange, "Неверный формат JSON", 400, "text/plain");
        } catch (Exception e) {
            sendResponse(exchange, "Внутренняя ошибка сервера", 500, "text/plain");
        }
    }

    private void handlePostRequest(HttpExchange exchange, String body, String idString, int idInt)
            throws IOException {
        Task task = gson.fromJson(body, Task.class);

        if (idString.isEmpty()) {
            // Создание новой задачи
            try {
                Task createdTask = taskManager.addTask(task);
                sendResponse(exchange,
                        gson.toJson(createdTask),
                        201,  // Тест ожидает 201 для создания
                        "application/json");
            } catch (TimeCrossException e) {
                sendResponse(exchange,
                        "Конфликт времени выполнения задач",
                        406,  // Тест ожидает 406 для конфликта
                        "text/plain");
            }
        } else {
            // Обновление существующей задачи
            task.setId(idInt);
            try {
                taskManager.updateTask(task);
                sendResponse(exchange,
                        gson.toJson(task),
                        201,  // Тест ожидает 201 для обновления
                        "application/json");
            } catch (TimeCrossException e) {
                sendResponse(exchange,
                        "Конфликт времени выполнения задач",
                        406,
                        "text/plain");
            } catch (TaskNotFoundException e) {
                sendNotFound(exchange, idInt);
            }
        }
    }


    private void handleGetRequest(HttpExchange exchange, String idString, int idInt)
            throws IOException {
        if (idString.isEmpty()) {
            // Получение всех задач
            sendResponse(exchange,
                    gson.toJson(taskManager.getAllTasks()),
                    200,
                    "application/json");
        } else {
            // Получение задачи по ID
            try {
                Task task = taskManager.getTaskById(idInt);
                sendResponse(exchange,
                        gson.toJson(task),
                        200,
                        "application/json");
            } catch (TaskNotFoundException e) {
                sendNotFound(exchange, idInt);  // Тест ожидает 404
            }
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String idString, int idInt)
            throws IOException {
        if (idString.isEmpty()) {
            sendResponse(exchange,
                    "Не указан ID задачи",
                    400,
                    "text/plain");
            return;
        }

        try {
            taskManager.deleteTaskByID(idInt);
            sendResponse(exchange,
                    "Задача успешно удалена",
                    200,
                    "text/plain");
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, idInt);  // Тест ожидает 404
        }
    }

    protected void sendNotFound(HttpExchange exchange, int taskId) throws IOException {
        sendResponse(exchange,
                "Задача с id=" + taskId + " не найдена",
                404,  // Тест ожидает 404 для не найденной задачи
                "text/plain");
    }


    private void sendResponse(HttpExchange exchange, String response, int statusCode, String contentType)
            throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
