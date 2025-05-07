package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Класс для предварительноой обработки http запросов, из которого остальные обработчки далее принимают метод,
 * тело запроса и запрашиваемый id задачи.
 * Хранит поля:
 * response - http ответ;
 * method - http метод;
 * idString - id задачи из пути запроса в String;
 * idInt - id задачи в int;
 * inputStream - входящий поток;
 * body - тело http заппроса;
 * subtasksString - временное поле для реализации логики обрадотки получения подзадач в EpicsHandler;
 * gson - экземпляр gson, получаемый из HttpTaskServer
 */

public class BaseHttpHandler implements HttpHandler {

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected HistoryManager historyManager;
    protected TaskManager taskManager;
    protected String response;
    protected String method;
    protected String idString;
    protected int idInt;
    protected InputStream inputStream;
    protected String body;
    protected String subtasksString = "";

    protected final Gson gson = HttpTaskServer.getGson();

    protected BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void handle(HttpExchange exc) throws IOException {
        //Предварительная обработка запроса
        System.out.println("Началась обработка запроса от клиента.");

        method = exc.getRequestMethod(); //вызываем метод запроса
        String path = exc.getRequestURI().getPath(); //получаем путь запроса
        String[] splitStrings = path.split("/"); //делим путь на составляющие
        idString = "";

        if (splitStrings.length >= 3) { // получаем переданный в пути id задачи
            idString = splitStrings[2];
            try {
                idInt = Integer.parseInt(idString);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        if (splitStrings.length == 4 && splitStrings[3].equals("subtasks")) {
            subtasksString = splitStrings[3];
        }
        inputStream = exc.getRequestBody();
        body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
    }

    protected void sendText(HttpExchange exc, String text, int code) throws IOException {
        exc.sendResponseHeaders(code, 0);
        if (code == 200) {
            exc.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        }
        try (OutputStream os = exc.getResponseBody()) {
            os.write(text.getBytes(DEFAULT_CHARSET));
        }
    }

    protected void sendNotFound(HttpExchange exc, int id) throws IOException {
        byte[] resp = ("Не найдена задача указанным id=" + id).getBytes(DEFAULT_CHARSET);
        exc.sendResponseHeaders(404, resp.length);
        exc.getResponseBody().write(resp);
        exc.close();
    }

    protected void sendHasInteractions(HttpExchange exc) throws IOException {
        byte[] resp = ("Задача пересекается с существующими. " +
                "Обновление или создание данной задачи невозможно").getBytes(DEFAULT_CHARSET);
        exc.sendResponseHeaders(406, resp.length);
        exc.getResponseBody().write(resp);
        exc.close();
    }
}