package ru.yandex.manager.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.manager.exception.ManagerSaveException;
import ru.yandex.manager.exception.NotFoundException;
import ru.yandex.manager.exception.ValidationException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] responseBytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
        }
    }

    protected void handleException(HttpExchange exchange, Exception e) throws IOException {
        if (e instanceof NotFoundException) {
            sendNotFound(exchange);
        } else if (e instanceof ValidationException) {
            sendHasInteractions(exchange);
        } else if (e instanceof ManagerSaveException) {
            sendFileError(exchange, e.getMessage());
        } else {
            sendServerError(exchange, e);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendError(exchange, 404, "Такой задачи нет");
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendError(exchange, 406, "Задача пересекается по времени с другой задачей");
    }

    protected void sendFileError(HttpExchange exchange, String errorMessage) throws IOException {
        sendError(exchange, 400, errorMessage);
    }

    protected void sendServerError(HttpExchange exchange, Exception e) throws IOException {
        e.printStackTrace();
        sendError(exchange, 500, "Ошибка обработки запроса");
    }

    protected void sendError(HttpExchange exchange, int statusCode, String errorMessage) throws IOException {
        sendText(exchange, String.format("{\"error\":\"%s\"}", errorMessage), statusCode);
    }
}
