package Server;



import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import service.TaskManager;
import service.*;

import java.io.IOException;


public class EpicsHandler extends BaseHttpHandler {

    protected EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exc) throws IOException {
        super.handle(exc);

        switch (method) {
            case "POST":
                System.out.println("POST epics");
                Epic epic = gson.fromJson(body, Epic.class);
                if (idString.isEmpty()) { //добавление нового Epic
                    epic = taskManager.createNewEpic(epic);
                    response = "Новая задача " + epic.getType() + " с id = " + epic.getId() + " создана";
                } else {
                    epic.setId(idInt);
                    try {
                        taskManager.updateEpic(epic); // обноеление Epic
                        response = "Задача " + epic.getType() + " с id = " + idInt + " обновлена";
                    } catch (TaskNotFoundException e) {
                        sendNotFound(exc, idInt);
                    }
                }
                sendText(exc, response, 201);
                break;
            case "GET":
                System.out.println("GET epics");
                if (idString.isEmpty()) {
                    response = gson.toJson(taskManager.getAllEpics());
                } else {
                    try {
                        response = gson.toJson(taskManager.getEpicById(idInt));
                    } catch (TaskNotFoundException e) {
                        sendNotFound(exc, idInt);
                    }
                }
                if (!subtasksString.isEmpty()) {
                    try {
                        response = gson.toJson(taskManager.getEpicSubtasks(idInt));
                    } catch (TaskNotFoundException e) {
                        sendNotFound(exc, idInt);
                    }
                }
                sendText(exc, response, 200);
                break;
            case "DELETE":
                System.out.println("DELETE epics");
                if (idString.isEmpty() || idInt == 0) {
                    response = "Ошибка в запросе - укажите порядковый id задачи в числовом виде, начиная с 1";
                    sendText(exc, response, 400);
                } else {
                    try {
                        taskManager.deleteEpic(idInt);
                        response = "Задача " + TaskType.EPIC + " с id= " + idInt + " удалена";
                    } catch (TaskNotFoundException e) {
                        sendNotFound(exc, idInt);
                    }
                    sendText(exc, response, 200);
                }
                break;
            default:
                response = "Метод не разрешен! Доступные методы для epics: GET, POST, DELETE.";
                sendText(exc, response, 405);
        }
    }
}
