package test.server;

import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTasksTest {

    TaskManager taskManager = Managers.getDefault();
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;


    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        // Создаем Gson с адаптером для LocalDateTime
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        server = new HttpTaskServer(taskManager, gson); // Передаем наш Gson
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }



    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task();
        task.setName("Test Task");
        task.setDescription("Description");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());

        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void testGetNonExistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testGetTask() throws IOException, InterruptedException {
        // Сначала создаем задачу
        Task task = new Task();
        task.setName("Test Task");
        task.setDescription("Description");
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=" + task.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        // Сначала создаем задачу
        Task task = new Task();
        task.setName("Test Task");
        task.setDescription("Description");
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }
}
