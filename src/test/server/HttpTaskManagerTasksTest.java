package test.server;

import com.google.gson.Gson;
import model.Status;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTasksTest {
    private TaskManager taskManager;
    private HttpTaskServer server;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        server = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
        server.start();
        taskManager.deleteAllTask(); // Очищаем менеджер перед каждым тестом
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    private Task createTestTask(String name, String description, LocalDateTime startTime) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(20));
        task.setStartTime(startTime);
        return task;
    }

    @Test
    void testAddTaskSuccess() throws IOException, InterruptedException {
        Task task = createTestTask("Test Task", "Description",
                LocalDateTime.of(2022, 12, 1, 10, 0));
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус код при успешном создании");
        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(1, tasks.size(), "Задача не была добавлена");
        assertEquals("Test Task", tasks.get(0).getName(), "Неверное имя задачи");
    }

    @Test
    void testAddTaskTimeConflict() throws IOException, InterruptedException {
        // Первая успешная задача
        Task task1 = createTestTask("Task 1", "Desc 1",
                LocalDateTime.of(2022, 12, 1, 10, 0));
        String taskJson1 = gson.toJson(task1);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        // Вторая задача с пересекающимся временем
        Task task2 = createTestTask("Task 2", "Desc 2",
                LocalDateTime.of(2022, 12, 1, 10, 10)); // Пересекается с первой
        String taskJson2 = gson.toJson(task2);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();

        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Неверный статус код при конфликте времени");
        assertEquals(1, taskManager.getAllTasks().size(), "Конфликтующая задача была добавлена");
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        Task task = createTestTask("Test Task", "Description",
                LocalDateTime.of(2022, 12, 1, 10, 0));
        taskManager.addTask(task); // Добавляем напрямую, чтобы получить ID

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=" + task.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код при получении задачи");
        Task receivedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getId(), receivedTask.getId(), "Неверный ID задачи");
    }

    @Test
    void testGetTaskNotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=999")) // Несуществующий ID
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный статус код при отсутствии задачи");
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task = createTestTask("Original", "Desc",
                LocalDateTime.of(2022, 12, 1, 10, 0));
        taskManager.addTask(task); // Добавляем напрямую

        // Обновляем задачу
        task.setName("Updated");
        task.setDescription("New Desc");
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=" + task.getId()))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус код при обновлении");
        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated", updatedTask.getName(), "Задача не была обновлена");
    }

    @Test
    void testDeleteTaskSuccess() throws IOException, InterruptedException {
        Task task = createTestTask("To Delete", "Desc",
                LocalDateTime.of(2022, 12, 1, 10, 0));
        taskManager.addTask(task); // Добавляем напрямую

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код при удалении");
        assertEquals(0, taskManager.getAllTasks().size(), "Задача не была удалена");
    }

    @Test
    void testDeleteTaskNotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=999")) // Несуществующий ID
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный статус код при удалении несуществующей задачи");
    }

    @Test
    void testGetAllTasks() throws IOException, InterruptedException {
        // Добавляем несколько задач напрямую
        taskManager.addTask(createTestTask("Task 1", "Desc 1",
                LocalDateTime.of(2022, 12, 1, 10, 0)));
        taskManager.addTask(createTestTask("Task 2", "Desc 2",
                LocalDateTime.of(2022, 12, 1, 11, 0)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код при получении всех задач");
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length, "Неверное количество задач");
    }
}