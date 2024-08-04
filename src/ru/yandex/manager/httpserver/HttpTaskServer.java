package ru.yandex.manager.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.manager.httpserver.gson.TimeAdapter;
import ru.yandex.manager.httpserver.handler.*;
import ru.yandex.manager.service.Managers;
import ru.yandex.manager.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;

    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new TimeAdapter.DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new TimeAdapter.LocalDateTimeAdapter())
                .create();
        try {
            httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        httpServer.createContext("/tasks", new TasksHandler(manager, gson));
        httpServer.createContext("/subtasks", new SubtasksHandler(manager, gson));
        httpServer.createContext("/epics", new EpicsHandler(manager, gson));
        httpServer.createContext("/history", new HistoryHandler(manager, gson));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(manager, gson));
    }

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaults();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

        httpTaskServer.start();
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
