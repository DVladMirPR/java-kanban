package ru.yandex.manager.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import ru.yandex.manager.httpserver.gson.TimeAdapter;
import ru.yandex.manager.httpserver.HttpTaskServer;
import ru.yandex.manager.model.Task;
import ru.yandex.manager.model.Status;
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

public class TaskHandlerTest {

    private final InMemoryTaskManager manager;
    private final HttpTaskServer taskServer;
    private final Gson gson;

    public TaskHandlerTest() {
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
    @DisplayName("Test Add Task")
    public void testAddTask() {
        Task task = new Task(null, "Test 1", "For test", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            List<Task> tasksFromManager = manager.getAllTasks();
            assertNotNull(tasksFromManager);
            assertEquals(1, tasksFromManager.size());
            assertEquals("Test 1", tasksFromManager.get(0).getTitle());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Add Intersecting Tasks")
    public void testAddIntersectingTasks() {
        Task task1 = new Task(null, "Task 1", "Testing task 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(null, "Task 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
            HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();

            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response1.statusCode());

            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response2.statusCode());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Update Task")
    public void testUpdateTask() {
        Task task = new Task(null, "Task 1", "Testing task 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.addTask(task);

        Task updatedTask = new Task(task.getId(), "Task 1 updated", "Testing task 1 updated", Status.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now());
        String updatedTaskJson = gson.toJson(updatedTask);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            Task taskFromManger = manager.getTask(task.getId());
            assertEquals("Task 1 updated", taskFromManger.getTitle());
            assertEquals("Testing task 1 updated", taskFromManger.getDescription());
            assertEquals(Status.IN_PROGRESS, taskFromManger.getStatus());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Get All Tasks")
    public void testGetAllTasks() {
        Task task = new Task(0, "Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task);
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            List<Task> tasksFromManager = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
            }.getType());
            assertEquals(1, tasksFromManager.size());
            assertEquals("Test 2", tasksFromManager.get(0).getTitle());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Get Task by ID")
    public void testGetTaskById() {
        Task task = new Task(0, "Test 3", "Testing task 3", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task);
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            Task retrievedTask = gson.fromJson(response.body(), Task.class);
            assertEquals("Test 3", retrievedTask.getTitle());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Delete Task")
    public void testDeleteTask() {
        Task task = new Task(0, "Test 4", "Testing task 4", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task);
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());

            List<Task> tasksFromManager = manager.getAllTasks();
            assertEquals(0, tasksFromManager.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Get Nonexistent Task")
    public void testGetNonexistentTask() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/9");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }
}