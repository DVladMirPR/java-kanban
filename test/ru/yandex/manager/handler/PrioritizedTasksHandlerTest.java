package ru.yandex.manager.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import ru.yandex.manager.httpserver.Gson.TimeAdapter;
import ru.yandex.manager.httpserver.HttpTaskServer;
import ru.yandex.manager.model.Status;
import ru.yandex.manager.model.Task;
import ru.yandex.manager.service.InMemoryHistoryManager;
import ru.yandex.manager.service.InMemoryTaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrioritizedTasksHandlerTest {

    private final InMemoryTaskManager manager;
    private final HttpTaskServer taskServer;
    private final Gson gson;

    public PrioritizedTasksHandlerTest() throws IOException {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskServer = new HttpTaskServer(manager);
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new TimeAdapter.DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new TimeAdapter.LocalDateTimeAdapter())
                .create();
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Test Get Prioritized Tasks")
    public void testGetPrioritizedTasks() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        manager.addTask(task1);
        manager.addTask(task2);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/prioritized");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
            }.getType());
            assertEquals(2, prioritizedTasks.size());
            assertEquals("Task 1", prioritizedTasks.get(0).getTitle());
            assertEquals("Task 2", prioritizedTasks.get(1).getTitle());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }
}
