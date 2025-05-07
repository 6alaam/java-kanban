package test.server;


import server.HttpTaskServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode());

            List<Task> tasksFromManager = taskManager.getAllTasks();
            Task task = tasksFromManager.getFirst();
            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
            assertEquals("task1_name", task.getName(), "Некорректное имя задачи");
        } catch (InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, URL-адрес и " +
                    "повторите попытку");
        } catch (IllegalArgumentException e) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова");
        }
    }

    @Test
    public void testUpdateTask() throws IOException {
        // создаём задачу
        Task task1 = new Task();
        task1.setName("task1_name");
        task1.setDescription("task1_description");
        task1.setStatus(Status.NEW);
        task1.setDuration(Duration.ofMinutes(20));
        task1.setStartTime(LocalDateTime.of(2022, 12, 1, 10, 25, 0));
        String taskJson1 = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/");
            //создаем успешный запрос
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());

            Task task2 = new Task();
            task2.setName("task2_name");
            task2.setDescription("task2_description");
            task2.setStatus(Status.NEW);
            task2.setDuration(Duration.ofMinutes(20));
            task2.setStartTime(LocalDateTime.of(2022, 12, 2, 10, 25, 0));
            String taskJson2 = gson.toJson(task2);

            url = URI.create("http://localhost:8080/tasks");
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());

            //обновляем задачу
            Task task1Updated = new Task();
            task1Updated.setName("task1_updated_name");
            task1Updated.setDescription("task2_description");
            task1Updated.setStatus(Status.NEW);
            task1Updated.setDuration(Duration.ofMinutes(20));
            task1Updated.setStartTime(LocalDateTime.of(2022, 12, 3, 10, 25,
                    0));
            String taskJson3 = gson.toJson(task1Updated);

            // создаём запрос на обновление задачи
            url = URI.create("http://localhost:8080/tasks/1");
            //создаем успешный запрос
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson3))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(201, response.statusCode());


            //создаем задачу, которая не будет записана
            Task task1NotUpdated = new Task();
            task1NotUpdated.setName("task1_updated_name");
            task1NotUpdated.setDescription("task2_description");
            task1NotUpdated.setStatus(Status.NEW);
            task1NotUpdated.setDuration(Duration.ofMinutes(20));
            task1NotUpdated.setStartTime(LocalDateTime.of(2022, 12, 2, 10, 25,
                    0));
            String taskJson4 = gson.toJson(task1NotUpdated);

            // создаём запрос на обновление задачи
            url = URI.create("http://localhost:8080/tasks/1");

            //создаем неудачный запрос (задача не будет добавлена из-за пересечения)
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson4))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode());

            //создаем неудачный запрос отсутствующей задачи
            url = URI.create("http://localhost:8080/tasks/3");

            //создаем неудачный запрос (задача не будет добавлена из-за пересечения)
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());

            // проверяем, что сохранилась обновленная задача с корректным именем
            List<Task> tasksFromManager = taskManager.getAllTasks();
            Task task = tasksFromManager.getFirst();

            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            //проверяем количество созданных задач
            assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
            //проеряем обновление задачи
            assertEquals("task1_updated_name", task.getName(), "Некорректное имя " +
                    "задачи");
        } catch (InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, URL-адрес и" +
                    " повторите попытку");
        } catch (IllegalArgumentException e) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова");
        }
    }

    @Test
    public void testGetTask() throws IOException {
        // создаём задачу
        Task task1 = new Task();
        task1.setName("task1_name");
        task1.setDescription("task1_description");
        task1.setStatus(Status.NEW);
        task1.setDuration(Duration.ofMinutes(20));
        task1.setStartTime(LocalDateTime.of(2022, 12, 1, 10, 25, 0));
        String taskJson1 = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        try {
            URI url = URI.create("http://localhost:8080/tasks");
            //создаем задачу в менеджере
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                    .build();

            // вызываем рест, отвечающий за создание задач
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(201, response.statusCode());

            url = URI.create("http://localhost:8080/tasks/1");
            //получаем задачу
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            //создаем запрос на получение отсутствующей задачи
            url = URI.create("http://localhost:8080/tasks/2");
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());


            //создаем запрос на получение всех задач
            url = URI.create("http://localhost:8080/tasks/");
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            class TasksListTypeToken extends TypeToken<List<Task>> {
            }

            List<Task> tasksFromJson = gson.fromJson(response.body(), new TasksListTypeToken().getType());
            Task task = tasksFromJson.getFirst();

            assertNotNull(tasksFromJson, "Задачи не возвращаются");
            //проверяем количество созданных задач
            assertEquals(1, tasksFromJson.size(), "Некорректное количество задач");
            //проеряем обновление задачи
            assertEquals("task1_name", task.getName(), "Некорректное имя задачи");
        } catch (InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, URL-адрес и" +
                    " повторите попытку");
        } catch (IllegalArgumentException e) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова");
        }
    }

    @Test
    public void testDeleteTask() throws IOException {
        // создаём задачу
        Task task1 = new Task();
        task1.setName("task1_name");
        task1.setDescription("task1_description");
        task1.setStatus(Status.NEW);
        task1.setDuration(Duration.ofMinutes(20));
        task1.setStartTime(LocalDateTime.of(2022, 12, 1, 10, 25, 0));
        String taskJson1 = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        try {
            URI url = URI.create("http://localhost:8080/tasks");
            //создаем задачу в менеджере
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                    .build();

            // вызываем рест, отвечающий за создание задач
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(201, response.statusCode());

            url = URI.create("http://localhost:8080/tasks/2");
            //пытаемся удалить несуществующую задачу
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(404, response.statusCode());

            //создаем запрос на удаление существующей задачи
            url = URI.create("http://localhost:8080/tasks/1");
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            //создаем запрос на получение всех задач
            url = URI.create("http://localhost:8080/tasks/");
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            class TasksListTypeToken extends TypeToken<List<Task>> {
            }

            List<Task> tasksFromJson = gson.fromJson(response.body(), new TasksListTypeToken().getType());

            //проверяем количество созданных задач (едонственная задача удалена, список пуст)
            assertEquals(0, tasksFromJson.size(), "Некорректное количество задач");
        } catch (InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, URL-адрес и " +
                    "повторите попытку");
        } catch (IllegalArgumentException e) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова");
        }
    }
}