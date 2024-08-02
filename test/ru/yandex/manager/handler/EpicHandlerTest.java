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
import ru.yandex.manager.model.Epic;
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

public class EpicHandlerTest {

    private final InMemoryTaskManager manager;
    private final HttpTaskServer taskServer;
    private final Gson gson;

    public EpicHandlerTest() {
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
    @DisplayName("Test Add Epic")
    public void testAddEpic() {
        Epic epic = new Epic(null, "Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            List<Epic> epicsFromManager = manager.getAllEpics();
            assertNotNull(epicsFromManager);
            assertEquals(1, epicsFromManager.size());
            assertEquals("Epic 1", epicsFromManager.get(0).getTitle());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Update Epic")
    public void testUpdateEpic() {
        Epic epic = new Epic(null, "Epic 1", "Testing epic 1");
        manager.addEpic(epic);

        Epic updatedEpic = new Epic(epic.getId(), "Epic 1 updated", "Testing epic 33 updated");
        String updatedEpicJson = gson.toJson(updatedEpic);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedEpicJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            Epic epicFromManager = manager.getEpic(epic.getId());
            assertEquals(updatedEpic.getId(), epicFromManager.getId());
            assertEquals(updatedEpic.getTitle(), epicFromManager.getTitle());
            assertEquals(updatedEpic.getDescription(), epicFromManager.getDescription());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Get All Epics")
    public void testGetAllEpics() {
        Epic epic = new Epic(1, "Epic 2", "Testing epic 2");
        manager.addEpic(epic);
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            List<Epic> epicsFromManager = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
            }.getType());
            assertEquals(1, epicsFromManager.size());
            assertEquals("Epic 2", epicsFromManager.get(0).getTitle());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Get Epic by ID")
    public void testGetEpicById() {
        Epic epic = new Epic(0, "Epic 3", "Testing epic 3");
        manager.addEpic(epic);
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            Epic retrievedEpic = gson.fromJson(response.body(), Epic.class);
            assertEquals("Epic 3", retrievedEpic.getTitle());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Delete Epic")
    public void testDeleteEpic() {
        Epic epic = new Epic(null, "Epic 4", "Testing epic 4");
        manager.addEpic(epic);
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());

            List<Epic> epicsFromManager = manager.getAllEpics();
            assertEquals(0, epicsFromManager.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Get Nonexistent Epic")
    public void testGetNonexistentEpic() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/epics/999");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }
}