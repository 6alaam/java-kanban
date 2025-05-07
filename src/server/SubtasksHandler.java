package server;


import com.sun.net.httpserver.HttpExchange;
import model.Subtask;
import service.TaskManager;
import service.TaskNotFoundException;
import service.TimeCrossException;

import java.io.IOException;

/**
 * Обработчик запросов subtasks
 */

public class SubtasksHandler extends BaseHttpHandler {

    protected SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exc) throws IOException {
        super.handle(exc);

        switch (method) {
            case "POST":
                System.out.println("POST subtasks");
                Subtask subtask = gson.fromJson(body, Subtask.class);
                int epicId = subtask.getEpicID();
                if (idString.isEmpty()) {
                    try {
                        subtask = taskManager.addSubtask(subtask);
                        response = "Новая задача " + " с id = " + subtask.getId() + " создана";
                    } catch (TimeCrossException e) {
                        sendHasInteractions(exc);
                    } catch (TaskNotFoundException e) {
                        System.out.println(e.getMessage());
                        sendNotFound(exc, epicId);
                    }
                    sendText(exc, response, 201);
                } else {
                    subtask.setId(idInt);
                    try {
                        taskManager.updateSubtask(subtask);
                        response = "Задача " + " с id = " + idInt + " обновлена";
                    } catch (TimeCrossException e) {
                        sendHasInteractions(exc);
                    } catch (TaskNotFoundException e) {
                        sendNotFound(exc, idInt);
                    }
                }
                sendText(exc, response, 201);
                break;
            case "GET":
                System.out.println("GET subtasks");
                if (idString.isEmpty()) {
                    response = gson.toJson(taskManager.getAllSubtask());
                } else {
                    try {
                        response = gson.toJson(taskManager.getSubtaskById(idInt));
                    } catch (TaskNotFoundException e) {
                        sendNotFound(exc, idInt);
                    }
                }
                sendText(exc, response, 200);
                break;
            case "DELETE":
                System.out.println("DELETE subtasks");
                if (idString.isEmpty() || idInt == 0) {
                    response = "Ошибка в запросе - укажите id задачи в числовом виде";
                    sendText(exc, response, 400);
                } else {
                    try {
                        taskManager.deleteSubtaskByID(idInt);
                        response = "Задача " + " с id = " + idInt + " удалена";
                    } catch (TaskNotFoundException e) {
                        sendNotFound(exc, idInt);
                    }
                    sendText(exc, response, 200);
                }
                break;
            default:
                response = "Метод не разрешен! Доступные методы для subtasks: GET, POST, DELETE.";
                sendText(exc, response, 405);
        }
    }
}
