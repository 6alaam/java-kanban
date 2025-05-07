package server;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;



public class TasksHandler extends BaseHttpHandler {

    protected TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // Получаем параметры запроса
            String method = exchange.getRequestMethod();
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String query = exchange.getRequestURI().getQuery();

            // Парсим ID из query параметров
            int taskId = 0;
            String idString = "";
            if (query != null && query.startsWith("id=")) {
                idString = query.substring(3);
                try {
                    taskId = Integer.parseInt(idString);
                } catch (NumberFormatException e) {
                    // Оставим taskId = 0 для невалидных ID
                }
            }

            switch (method) {
                case "POST":
                    handlePostRequest(exchange, body, idString, taskId);
                    break;
                case "GET":
                    handleGetRequest(exchange, idString, taskId);
                    break;
                case "DELETE":
                    handleDeleteRequest(exchange, idString, taskId);
                    break;
                default:
                    sendResponse(exchange,
                            "Метод не разрешен! Доступные методы: GET, POST, DELETE.",
                            405,
                            "text/plain");
            }
        } catch (Exception e) {
            sendResponse(exchange,
                    "Внутренняя ошибка сервера: " + e.getMessage(),
                    500,
                    "text/plain");
        }
    }

    private void handlePostRequest(HttpExchange exchange, String body, String idString, int taskId)
            throws IOException {
        try {
            Task task = gson.fromJson(body, Task.class);

            if (idString.isEmpty()) {
                // Создание новой задачи
                try {
                    Task createdTask = taskManager.addTask(task);
                    sendResponse(exchange,
                            gson.toJson(createdTask),
                            201,
                            "application/json");
                } catch (TimeCrossException e) {
                    sendResponse(exchange,
                            "Конфликт времени выполнения задач",
                            406,
                            "text/plain");
                }
            } else {
                // Обновление существующей задачи
                task.setId(taskId);
                try {
                    taskManager.updateTask(task);
                    sendResponse(exchange,
                            gson.toJson(task),
                            200,
                            "application/json");
                } catch (TimeCrossException e) {
                    sendResponse(exchange,
                            "Конфликт времени выполнения задач",
                            406,
                            "text/plain");
                } catch (TaskNotFoundException e) {
                    sendNotFound(exchange, taskId);
                }
            }
        } catch (JsonSyntaxException e) {
            sendResponse(exchange,
                    "Неверный формат JSON",
                    400,
                    "text/plain");
        }
    }

    private void handleGetRequest(HttpExchange exchange, String idString, int taskId)
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
                Task task = taskManager.getTaskById(taskId);
                sendResponse(exchange,
                        gson.toJson(task),
                        200,
                        "application/json");
            } catch (TaskNotFoundException e) {
                sendNotFound(exchange, taskId);
            }
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String idString, int taskId)
            throws IOException {
        if (idString.isEmpty() || taskId == 0) {
            sendResponse(exchange,
                    "Ошибка в запросе - укажите корректный id задачи",
                    400,
                    "text/plain");
            return;
        }

        try {
            taskManager.deleteTaskByID(taskId);
            sendResponse(exchange,
                    "Задача с id=" + taskId + " успешно удалена",
                    200,
                    "text/plain");
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, taskId);
        }
    }

    protected void sendNotFound(HttpExchange exchange, int taskId) throws IOException {
        sendResponse(exchange,
                "Задача с id=" + taskId + " не найдена",
                404,
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
