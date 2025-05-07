package test.server;


import com.google.gson.Gson;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.GsonBuilder;
import server.HttpTaskServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerTest {
    private HttpTaskServer server;
    private TaskManager manager;
    private Gson gson = GsonBuilder.getGson();
    String endpoint;

    @BeforeEach
    void init() throws IOException {
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager);
        server.start();

    }

    @AfterEach
    void stop() {
        manager.deleteAllTask();
        server.stop();
    }

    HttpResponse<String> httpMethodPOST(String endpoint, String jsonTask) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + endpoint);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    HttpResponse<String> httpMethodGET(String endpoint) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + endpoint);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    HttpResponse<String> httpMethodDELETE(String endpoint) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + endpoint);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .DELETE().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    @Test
    void createTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        String jsonTask = gson.toJson(task);
        endpoint = "/tasks";

        HttpResponse<String> response = httpMethodPOST(endpoint, jsonTask);
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllTasks().size(), "Количество задач не совпадает");
    }

    @Test
    void createEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        String jsonTask = gson.toJson(epic);
        endpoint = "/epics";
        HttpResponse<String> response = httpMethodPOST(endpoint, jsonTask);
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpic().size(), "Количество эпиков не совпадает");
    }

    @Test
    void createSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        Subtask subTask1 = new Subtask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        String jsonTask = gson.toJson(subTask1);
        endpoint = "/subtasks";

        HttpResponse<String> response = httpMethodPOST(endpoint, jsonTask);
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubtask().size(), "Количество задач не совпадает");
        assertEquals(1, manager.getEpicSubtasks(epic).size(), "Количество подзадач внутри эпика не совпадает");
    }

    @Test
    void updateTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        Task task1 = new Task("Task1", "Task1", 1, Status.IN_PROGRESS, "11.03.2024 10:20", 10L);
        String jsonTask = gson.toJson(task1);
        endpoint = "/tasks/1";

        HttpResponse<String> response = httpMethodPOST(endpoint, jsonTask);
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllTasks().size(), "Количество задач не совпадает");
        assertEquals(Status.IN_PROGRESS, manager.getTaskById(1).getStatus(), "Статус не совпадает");
    }

    @Test
    void updateSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubtask(subTask);
        Subtask subTask1 = new Subtask("Subtask1", "subtask1", epic.getId(), 2, Status.IN_PROGRESS, "11.03.2024 22:22", 17);
        String jsonTask = gson.toJson(subTask1);
        endpoint = "/subtasks/2";

        HttpResponse<String> response = httpMethodPOST(endpoint, jsonTask);
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubtask().size(), "Количество задач не совпадает");
        assertEquals(Status.IN_PROGRESS, manager.getSubtaskById(2).getStatus(), "Статус не совпадает");
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(1).getStatus(), "Статус эпика не совпадает");
    }

    @Test
    void getTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        String jsonTask = gson.toJson(task);
        endpoint = "/tasks/1";

        HttpResponse<String> response = httpMethodGET(endpoint);
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

    @Test
    void getAllTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        endpoint = "/tasks";

        HttpResponse<String> response = httpMethodGET(endpoint);
        String jsonTasks = gson.toJson(manager.getAllTasks());
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач");
        assertEquals(jsonTasks, response.body(), "Список вернулся неверно");
    }

    @Test
    void getSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubtask(subTask);
        String jsonTask = gson.toJson(subTask);
        endpoint = "/subtasks/2";

        HttpResponse<String> response = httpMethodGET(endpoint);
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllSubtask().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

    @Test
    void getAllSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubtask(subTask);
        String jsonTask = gson.toJson(manager.getAllSubtask());
        endpoint = "/subtasks";
        HttpResponse<String> response = httpMethodGET(endpoint);
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllSubtask().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

    @Test
    void getEpicsTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        String jsonTask = gson.toJson(epic);
        endpoint = "/epics/1";

        HttpResponse<String> response = httpMethodGET(endpoint);
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllEpic().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

//    @Test
//    void getAllEpicsTest() throws IOException, InterruptedException {
//        Epic epic = new Epic("Epic1", "Epic1");
//        manager.addEpic(epic);
//        String jsonTask = gson.toJson(manager.getAllEpic());
//        endpoint = "/epics";
//
//        HttpResponse<String> response = httpMethodGET(endpoint);
//        assertEquals(200, response.statusCode());
//        assertEquals(1, manager.getAllEpic().size(), "Неверное количество задач");
//        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
//    }

    @Test
    void getSubtaskInEpicsTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubtask(subTask);
        String jsonTask = gson.toJson(manager.getEpicSubtasks(epic));
        endpoint = "/epics/1/subtasks";

        HttpResponse<String> response = httpMethodGET(endpoint);
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getEpicSubtasks(epic).size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

    @Test
    void getHistoryTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        manager.getTaskById(task.getId());
        String jsonTask = gson.toJson(manager.getHistory());
        endpoint = "/history";

        HttpResponse<String> response = httpMethodGET(endpoint);
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getHistory().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

    @Test
    void getPrioritizedTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        String jsonTask = gson.toJson(manager.getPrioritizedTasks());
        endpoint = "/prioritized";

        HttpResponse<String> response = httpMethodGET(endpoint);
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getPrioritizedTasks().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }


    @Test
    void deleteTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        endpoint = "/tasks/1";

        HttpResponse<String> response = httpMethodDELETE(endpoint);
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty(), "Список задач не пуст");
    }

    @Test
    void deleteSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubtask(subTask);
        endpoint = "/subtasks/2";

        HttpResponse<String> response = httpMethodDELETE(endpoint);
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllSubtask().isEmpty(), "Список подзадач не пуст");
        assertTrue(manager.getEpicSubtasks(epic).isEmpty(), "Список подзадач в эпике не пуст");
    }

    @Test
    void deleteEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubtask(subTask);
        endpoint = "/epics/1";

        HttpResponse<String> response = httpMethodDELETE(endpoint);
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllSubtask().isEmpty(), "Список подзадач не пуст");
        assertTrue(manager.getAllEpic().isEmpty(), "Список подзадач в эпике не пуст");
    }

    @Test
    void createTimeIntersectionTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        Task task1 = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        String jsonTask = gson.toJson(task1);
        endpoint = "/tasks";

        HttpResponse<String> response = httpMethodPOST(endpoint, jsonTask);
        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getAllTasks().size(), "Количество задач не совпадает");
    }

//    @Test
//    void getNotExistentTaskTest() throws IOException, InterruptedException {
//        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
//        manager.addTask(task);
//        endpoint = "/tasks/2";
//
//        HttpResponse<String> response = httpMethodGET(endpoint);
//        assertEquals(404, response.statusCode());
//        assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач");
//
//    }

//    @Test
//    void getNotExistentEpicsTest() throws IOException, InterruptedException {
//        Epic epic = new Epic("Epic1", "Epic1");
//        manager.addEpic(epic);
//        Subtask subTask = new Subtask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
//        manager.addSubtask(subTask);
//        endpoint = "/epics/2";
//
//
//        HttpResponse<String> response = httpMethodGET(endpoint);
//        assertEquals(404, response.statusCode());
//        assertEquals(1, manager.getAllEpic().size(), "Неверное количество задач");
//
//    }

//    @Test
//    void getNotExistentSubTaskTest() throws IOException, InterruptedException {
//        Epic epic = new Epic("Epic1", "Epic1");
//        manager.addEpic(epic);
//        Subtask subTask = new Subtask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
//        manager.addSubtask(subTask);
//        endpoint = "/subtask/1";
//
//        HttpResponse<String> response = httpMethodGET(endpoint);
//        assertEquals(404, response.statusCode());
//        assertEquals(1, manager.getAllSubtask().size(), "Неверное количество задач");
//
//    }

//    @Test
//    void getSubtaskInNotExistentEpicsTest() throws IOException, InterruptedException {
//        Epic epic = new Epic("Epic1", "Epic1");
//        manager.addEpic(epic);
//        Subtask subTask = new Subtask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
//        manager.addSubtask(subTask);
//        endpoint = "/epics/2/subtasks";
//
//        HttpResponse<String> response = httpMethodGET(endpoint);
//        assertEquals(404, response.statusCode());
//    }
//
//    @Test
//    void createTimeIntersectionSubTaskTest() throws IOException, InterruptedException {
//        Epic epic = new Epic("Epic1", "Epic1");
//        manager.addEpic(epic);
//        Subtask subTask = new Subtask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
//        manager.addSubtask(subTask);
//        Subtask subTask1 = new Subtask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
//        String jsonTask = gson.toJson(subTask1);
//        endpoint = "/subtasks";
//
//        HttpResponse<String> response = httpMethodPOST(endpoint, jsonTask);
//        assertEquals(406, response.statusCode());
//        assertEquals(1, manager.getAllSubtask().size(), "Количество задач не совпадает");
//    }
}
