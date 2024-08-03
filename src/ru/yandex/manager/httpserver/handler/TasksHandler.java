package ru.yandex.manager.httpserver.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.manager.model.Task;
import ru.yandex.manager.service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager taskManager, Gson gson) {
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
                        List<Task> tasks = taskManager.getAllTasks();
                        String json = gson.toJson(tasks);
                        sendText(exchange, json, 200);
                    } else if (pathParts.length == 3) {
                        int taskId = Integer.parseInt(pathParts[2]);
                        Task task = taskManager.getTask(taskId);
                        if (task == null) {
                            sendNotFound(exchange);
                        } else {
                            String json = gson.toJson(task);
                            sendText(exchange, json, 200);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    if (exchange.getRequestBody().available() == 0) {
                        sendError(exchange, 400, "Пустое тело запроса");
                        return;
                    }
                    Reader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(reader, Task.class);
                    if (task.getId() == null) {
                        taskManager.addTask(task);
                        sendText(exchange, "Задача успешно добавлена", 201);
                    } else {
                        taskManager.updateTask(task);
                        sendText(exchange, "Задача успешно обновлена", 201);
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        int taskId = Integer.parseInt(pathParts[2]);
                        if (taskManager.getTask(taskId) != null) {
                            taskManager.deleteTask(taskId);
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
