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
import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Status;
import ru.yandex.manager.model.Subtask;
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

public class SubtaskHandlerTest {

    private final InMemoryTaskManager manager;
    private final HttpTaskServer taskServer;
    private final Gson gson;

    public SubtaskHandlerTest() {
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
    @DisplayName("Test Add Subtask")
    public void testAddSubtask() {
        Epic epic = new Epic(1, "Epic 5", "Testing epic 5");
        manager.addEpic(epic);

        Subtask subtask = new Subtask(null, "Subtask 1", "Testing subtask 1", Status.NEW, epic.getId(),
                Duration.ofMinutes(5), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            List<Subtask> subtasksFromManager = manager.getAllSubtasks();
            assertNotNull(subtasksFromManager);
            assertEquals(1, subtasksFromManager.size());
            assertEquals("Subtask 1", subtasksFromManager.get(0).getTitle());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Add Intersecting Subtasks")
    public void testAddIntersectingSubtasks() {
        Epic epic = new Epic(1, "Epic 6", "Testing epic 6");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(null, "Subtask 1", "Testing subtask 1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask(null, "Subtask 2", "Testing subtask 2", Status.NEW, epic.getId(),
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        String subtaskJson1 = gson.toJson(subtask1);
        String subtaskJson2 = gson.toJson(subtask2);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson1)).build();
            HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();

            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response1.statusCode());

            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response2.statusCode());

            List<Subtask> subtasksFromManager = manager.getAllSubtasks();
            assertEquals(1, subtasksFromManager.size());
            assertEquals("Subtask 1", subtasksFromManager.get(0).getTitle());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Get All Subtasks")
    public void testGetAllSubtasks() {
        Epic epic = new Epic(1, "Epic 7", "Testing epic 7");
        manager.addEpic(epic);

        Subtask subtask = new Subtask(null, "Subtask 2", "Testing subtask 2", Status.NEW, epic.getId(),
                Duration.ofMinutes(5), LocalDateTime.now());
        manager.addSubtask(epic.getId(), subtask);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            List<Subtask> subtasksFromManager = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
            }.getType());
            assertEquals(1, subtasksFromManager.size());
            assertEquals("Subtask 2", subtasksFromManager.get(0).getTitle());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Get Subtask by ID")
    public void testGetSubtaskById() {
        Epic epic = new Epic(1, "Epic 8", "Testing epic 8");
        manager.addEpic(epic);

        Subtask subtask = new Subtask(2, "Subtask 3", "Testing subtask 3", Status.NEW, epic.getId(),
                Duration.ofMinutes(5), LocalDateTime.now());
        manager.addSubtask(epic.getId(), subtask);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            Subtask retrievedSubtask = gson.fromJson(response.body(), Subtask.class);
            assertEquals("Subtask 3", retrievedSubtask.getTitle());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Delete Subtask")
    public void testDeleteSubtask() {
        Epic epic = new Epic(1, "Epic 9", "Testing epic 9");
        manager.addEpic(epic);

        Subtask subtask = new Subtask(2, "Subtask 4", "Testing subtask 4", Status.NEW, epic.getId(),
                Duration.ofMinutes(5), LocalDateTime.now());
        manager.addSubtask(epic.getId(), subtask);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());

            List<Subtask> subtasksFromManager = manager.getAllSubtasks();
            assertEquals(0, subtasksFromManager.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Delete Nonexistent Subtask")
    public void testDeleteNonexistentSubtask() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/subtasks/999");
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Update Subtask")
    public void testUpdateSubtask() {
        Epic epic = new Epic(1, "Epic 10", "Testing epic 10");
        manager.addEpic(epic);

        Subtask subtask = new Subtask(null, "Subtask 5", "Testing subtask 5", Status.NEW, epic.getId(),
                Duration.ofMinutes(5), LocalDateTime.now());
        manager.addSubtask(epic.getId(), subtask);

        Subtask updatedSubtask = new Subtask(subtask.getId(), "Subtask 5 updated", "Testing subtask 5 updated", Status.IN_PROGRESS, epic.getId(),
                Duration.ofMinutes(5), LocalDateTime.now());
        String updatedSubtaskJson = gson.toJson(updatedSubtask);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            Subtask subtaskFromManager = manager.getSubtask(subtask.getId());
            assertEquals(updatedSubtask.getId(), subtaskFromManager.getId());
            assertEquals(updatedSubtask.getTitle(), subtaskFromManager.getTitle());
            assertEquals(updatedSubtask.getDescription(), subtaskFromManager.getDescription());
            assertEquals(updatedSubtask.getStatus(), subtaskFromManager.getStatus());
            assertEquals(updatedSubtask.getEpicId(), subtaskFromManager.getEpicId());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }
}