package server;


import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;



public class HttpTaskServer {

    private static final int PORT = 8080;
    private static HttpServer server;

    // содаем gson
    protected static Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter()) // Добавьте эту строку
            .create();
    private static int port;

    public HttpTaskServer(TaskManager taskManager, Gson gson, int port) throws IOException {
        this.gson = gson;
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }



    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту");
    }

    public void stop(int delay) {
        server.stop(delay);
        System.out.println("HTTP-сервер остановлен");
    }

    public static Gson getGson() {
        return gson;
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager, gson,port);
        httpTaskServer.start();
    }

    public String getBaseUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }
}