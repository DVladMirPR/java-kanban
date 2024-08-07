package ru.yandex.manager.httpserver.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.service.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class SubtasksHandler extends BaseHttpHandler {
    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
                        List<Subtask> subtasks = taskManager.getAllSubtasks();
                        String json = gson.toJson(subtasks);
                        sendText(exchange, json, 200);
                    } else if (pathParts.length == 3) {
                        int subtaskId = Integer.parseInt(pathParts[2]);
                        Subtask subtask = taskManager.getSubtask(subtaskId);
                        if (subtask != null) {
                            String json = gson.toJson(subtask);
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
                    Subtask subtask = gson.fromJson(requestBody, Subtask.class);
                    if (subtask.getId() == null) {
                        taskManager.addSubtask(subtask.getEpicId(), subtask);
                        sendText(exchange, "Подзадача добавлена", 201);
                    } else {
                        taskManager.updateSubtask(subtask);
                        sendText(exchange, "Подзадача обновлена", 201);
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        int subtaskId = Integer.parseInt(pathParts[2]);
                        if (taskManager.getSubtask(subtaskId) != null) {
                            taskManager.deleteSubtask(subtaskId);
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