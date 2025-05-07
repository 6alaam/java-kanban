package test.server;

import com.google.gson.Gson;
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
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        // Создаем локальную переменную taskManager
        TaskManager taskManager = Managers.getDefault();
        server = new HttpTaskServer(taskManager);
        server.start();
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void testServerCreation() {
        assertNotNull(server, "Сервер не был создан");
    }

    @Test
    void testClientCreation() {
        assertNotNull(client, "HTTP клиент не был создан");
    }

    @Test
    void testAddTask() throws Exception {
        Task task = new Task("Test", "Description");
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void testGetTask() throws Exception {
        // Сначала создаем задачу
        Task task = new Task("Test", "Description");
        String json = gson.toJson(task);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        Task createdTask = gson.fromJson(postResponse.body(), Task.class);

        // Затем получаем ее
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=" + createdTask.getId()))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
    }

    @Test
    void testDeleteTask() throws Exception {
        // Сначала создаем задачу
        Task task = new Task("Test", "Description");
        String json = gson.toJson(task);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        Task createdTask = gson.fromJson(postResponse.body(), Task.class);

        // Затем удаляем ее
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=" + createdTask.getId()))
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, deleteResponse.statusCode());
    }

    @Test
    void testGetNonExistentTask() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}