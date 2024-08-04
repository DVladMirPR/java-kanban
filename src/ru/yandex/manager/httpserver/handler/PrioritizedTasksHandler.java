package ru.yandex.manager.httpserver.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.manager.model.Task;
import ru.yandex.manager.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedTasksHandler extends BaseHttpHandler {

    public PrioritizedTasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();

            if ("GET".equals(method)) {
                List<Task> tasks = taskManager.getPrioritizedTasks();
                String json = gson.toJson(tasks);
                sendText(exchange, json, 200);
            } else {
                sendFileError(exchange, "Ошибка при работе с файлом");
            }
        } catch (Exception e) {
            handleException(exchange, e);
        } finally {
            exchange.close();
        }
    }
}