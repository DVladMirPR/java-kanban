package ru.yandex.manager.httpserver.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.service.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            System.out.println("Method: " + method + ", Path: " + path);

            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        List<Epic> epics = taskManager.getAllEpics();
                        String json = gson.toJson(epics);
                        sendText(exchange, json, 200);
                    } else if (pathParts.length == 4) {
                        int epicId = Integer.parseInt(pathParts[2]);
                        List<Subtask> subtasks = taskManager.getSubtasksOfEpic(epicId);
                        if (subtasks != null) {
                            String json = gson.toJson(subtasks);
                            sendText(exchange, json, 200);
                        } else {
                            sendNotFound(exchange);
                        }
                    } else if (pathParts.length == 3) {
                        int epicId = Integer.parseInt(pathParts[2]);
                        Epic epic = taskManager.getEpic(epicId);
                        if (epic != null) {
                            String json = gson.toJson(epic);
                            sendText(exchange, json, 200);
                        } else {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendFileError(exchange, "Ошибка при работе с файлом");
                    }
                    break;
                case "POST":
                    String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                            .lines()
                            .collect(Collectors.joining("\n"));
                    if (requestBody.isEmpty()) {
                        sendError(exchange, 400, "Пустое тело запроса");
                        return;
                    }
                    Epic epic = gson.fromJson(requestBody, Epic.class);
                    if (epic.getId() == null) {
                        taskManager.addEpic(epic);
                        sendText(exchange, "Эпик успешно добавлен", 201);
                    } else {
                        taskManager.updateEpic(epic);
                        sendText(exchange, "Эпик успешно обновлен", 201);
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        int epicId = Integer.parseInt(pathParts[2]);
                        if (taskManager.getEpic(epicId) != null) {
                            taskManager.deleteEpic(epicId);
                            sendText(exchange, "", 204);
                        } else {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendFileError(exchange, "Ошибка при работе с файлом");
                    }
                    break;
                default:
                    sendFileError(exchange, "Ошибка при работе с файлом");
                    break;
            }
        } catch (Exception e) {
            handleException(exchange, e);
        } finally {
            exchange.close();
        }
    }
}
