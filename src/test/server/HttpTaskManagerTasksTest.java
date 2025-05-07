package test.server;


import server.HttpTaskServer;
import com.google.gson.Gson;
import model.Status;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.deleteAllTask();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop(0);
    }

    @Test
    public void testAddTask() throws IOException {
        // создаём задачу
        Task task1 = new Task();
        task1.setName("task1_name");
        task1.setDescription("task1_description");
        task1.setStatus(Status.NEW);
        task1.setDuration(Duration.ofMinutes(20));
        task1.setStartTime(LocalDateTime.of(2022, 12, 1, 10, 25, 0));
        String taskJson1 = gson.toJson(task1);

        Task task2 = new Task();
        task2.setName("task2_name");
        task2.setDescription("task2_description");
        task2.setStatus(Status.NEW);
        task2.setDuration(Duration.ofMinutes(20));
        task2.setStartTime(LocalDateTime.of(2022, 12, 1, 10, 25, 0));
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        try {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            // Попытка добавить пересекающуюся задачу
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode());

            List<Task> tasksFromManager = taskManager.getAllTasks();
            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
            assertEquals("task1_name", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        } catch (InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, URL-адрес и повторите попытку");
        } catch (IllegalArgumentException e) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова");
        }
    }



    @Test
    public void testDeleteTask() throws IOException {
        Task task1 = new Task();
        task1.setName("task1_name");
        task1.setDescription("task1_description");
        task1.setStatus(Status.NEW);
        task1.setDuration(Duration.ofMinutes(20));
        task1.setStartTime(LocalDateTime.of(2022, 12, 1, 10, 25, 0));
        String taskJson1 = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        try {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());

            // Удаляем задачу
            URI deleteUrl = URI.create("http://localhost:8080/tasks/1");
            HttpRequest deleteRequest = HttpRequest
                    .newBuilder()
                    .uri(deleteUrl)
                    .DELETE()
                    .build();

            HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, deleteResponse.statusCode());

            // Проверяем, что задача была удалена
            List<Task> tasksFromManager = taskManager.getAllTasks();
            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(0, tasksFromManager.size(), "Задача не была удалена");
        } catch (InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, URL-адрес и повторите попытку");
        } catch (IllegalArgumentException e) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова");
        }
    }
}