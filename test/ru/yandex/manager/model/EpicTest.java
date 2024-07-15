package ru.yandex.manager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.manager.service.InMemoryHistoryManager;
import ru.yandex.manager.service.InMemoryTaskManager;
import ru.yandex.manager.service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {
    private Epic epic;
    private TaskManager taskManager;

    @BeforeEach
    void createEpic() {
        epic = new Epic(1, "Основной", "Донор подзадач");
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskManager.addEpic(epic);
    }

    @DisplayName("All subtasks with status NEW")
    @Test
    void shouldCalculateEpicStatusNew() {
        Subtask subtask1 = new Subtask(2, "Первая", "Описание", Status.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask(3, "Вторая", "Описание2", Status.NEW, epic.getId(), Duration.ofHours(2), LocalDateTime.now().plusHours(2));
        taskManager.addSubtask(epic.getId(), subtask1);
        taskManager.addSubtask(epic.getId(), subtask2);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @DisplayName("All subtasks with status DONE")
    @Test
    void shouldCalculateEpicStatusDone() {
        Subtask subtask1 = new Subtask(2, "Уже сейчас", "время много", Status.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask(3, "Все", "Описание", Status.NEW, epic.getId(), Duration.ofHours(2), LocalDateTime.now().plusHours(2));
        taskManager.addSubtask(epic.getId(), subtask1);
        taskManager.addSubtask(epic.getId(), subtask2);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @DisplayName("Subtasks with statuses NEW and DONE")
    @Test
    void shouldCalculateEpicStatusMixed() {
        Subtask subtask1 = new Subtask(2, "Проверить", "Кофе", Status.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask(3, "Начать", "Наливать", Status.NEW, epic.getId(), Duration.ofHours(2), LocalDateTime.now().plusHours(2));
        taskManager.addSubtask(epic.getId(), subtask1);
        taskManager.addSubtask(epic.getId(), subtask2);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @DisplayName("Subtasks with status IN_PROGRESS")
    @Test
    void shouldCalculateEpicStatusInProgress() {
        Subtask subtask1 = new Subtask(2, "Срочно", "Писать устал", Status.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask(3, "Подзадача", "Очень", Status.NEW, epic.getId(), Duration.ofHours(2), LocalDateTime.now().plusHours(2));
        taskManager.addSubtask(epic.getId(), subtask1);
        taskManager.addSubtask(epic.getId(), subtask2);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}