package test.server;

import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Status;
import model.Task;
import org.junit.jupiter.api.*;
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
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTasksTest {
    private static final int TEST_PORT = 8081; // Используем другой порт для тестов
    private static final AtomicInteger serverCounter = new AtomicInteger(0);

    private TaskManager taskManager;
    private HttpTaskServer server;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    void setUp() {
        try {
            // Уникальный порт для каждого теста
            int port = TEST_PORT + serverCounter.getAndIncrement();
            taskManager = Managers.getDefault();
            gson = createGson();
            server = new HttpTaskServer(taskManager, gson, port);
            server.start();
            client = HttpClient.newHttpClient();
        } catch (IOException e) {
            fail("Не удалось запустить сервер: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    private Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Test
    void testAddTask() {
        try {
            Task task = createTestTask("Test Task", "Description", LocalDateTime.now());
            String taskJson = gson.toJson(task);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(server.getBaseUrl() + "/tasks"))
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Неверный статус код при создании задачи");
        } catch (Exception e) {
            fail("Ошибка при выполнении теста: " + e.getMessage());
        }
    }

//    @Test
//    void testGetNonExistentTask() {
//        try {
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(server.getBaseUrl() + "/tasks?id=999"))
//                    .GET()
//                    .build();
//
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            assertEquals(404, response.statusCode(), "Ожидался 404 для несуществующей задачи");
//        } catch (Exception e) {
//            fail("Ошибка при выполнении теста: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testGetTask() {
//        try {
//            Task task = createTestTask("Test Task", "Description", LocalDateTime.now());
//            taskManager.addTask(task);
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(server.getBaseUrl() + "/tasks?id=" + task.getId()))
//                    .GET()
//                    .build();
//
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            assertEquals(200, response.statusCode(), "Неверный статус код при получении задачи");
//        } catch (Exception e) {
//            fail("Ошибка при выполнении теста: " + e.getMessage());
//        }
//    }

    @Test
    void testDeleteTask() {
        try {
            Task task = createTestTask("Test Task", "Description", LocalDateTime.now());
            taskManager.addTask(task);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(server.getBaseUrl() + "/tasks?id=" + task.getId()))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Неверный статус код при удалении задачи");
        } catch (Exception e) {
            fail("Ошибка при выполнении теста: " + e.getMessage());
        }
    }

    private Task createTestTask(String name, String description, LocalDateTime startTime) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(startTime);
        return task;
    }
}