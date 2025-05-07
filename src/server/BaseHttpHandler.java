package server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler {
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendBadRequest(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(400, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendSuccess(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(201, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }


    protected String getBaseHandler(String path, String metod) throws IOException {
        String[] splitPath = path.split("/");
        switch (splitPath[1]) {

            case "subtasks":
                if (metod.equals("GET")) {
                    if (splitPath.length == 3) {
                        return "get_subtasks_id";
                    } else if (splitPath.length == 2) {
                        return "get_subtasks";
                    } else {
                        return "unknown";
                    }
                } else if (metod.equals("POST")) {
                    return "post_subtasks";

                } else if (metod.equals("DELETE") && (splitPath.length == 3)) {
                    return "delete_subtasks_id";
                } else {
                    return "unknown";
                }
            case "tasks":
                if (metod.equals("GET")) {
                    if (splitPath.length == 3) {
                        return "get_tasks_id";
                    } else if (splitPath.length == 2) {
                        return "get_tasks";
                    } else {
                        return "unknown";
                    }
                } else if (metod.equals("POST")) {
                    return "post_tasks";

                } else if (metod.equals("DELETE") && (splitPath.length == 3)) {
                    return "delete_tasks_id";
                } else {
                    return "unknown";
                }
            case "epics":
                if (metod.equals("GET")) {
                    if (splitPath.length == 3) {
                        return "get_epics_id";
                    } else if (splitPath.length == 2) {
                        return "get_epics";

                    } else if (splitPath.length == 4) {
                        return "get_epics_id_subtasks";
                    } else {
                        return "unknown";
                    }
                } else if (metod.equals("POST") && (splitPath.length == 2)) {
                    return "post_epics";
                } else if (metod.equals("DELETE") && (splitPath.length == 3)) {
                    return "delete_epics_id";
                } else {
                    return "unknown";
                }
            case "history":
                if (metod.equals("GET") && (splitPath.length == 2)) {
                    return "get_history";
                } else {
                    return "unknown";
                }

            case "prioritized":
                if (metod.equals("GET") && (splitPath.length == 2)) {
                    return "get_prioritized";
                } else {
                    return "unknown";
                }

            default:
                return "unknown";
        }
    }

    protected Optional<String> getBodyForHandler(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        inputStream.close();
        if (body.isBlank()) {
            return Optional.empty();
        } else {
            return Optional.of(body);
        }
    }
}