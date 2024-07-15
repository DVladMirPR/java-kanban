package ru.yandex.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.manager.exception.ValidationException;
import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Status;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    public void setUp() {
        manager = (InMemoryTaskManager) Managers.getDefaults();

        Task task = new Task(1, "Задача 1", "Описание 1", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2024, 7, 10, 15, 0));
        Epic epic = new Epic(2, "Эпик 1", "Описание эпика");
        Subtask subtask = new Subtask(3, "Подзадача 1", "Подзадача Эпика 1", Status.NEW, 2, Duration.ofMinutes(30), LocalDateTime.of(2024, 7, 10, 16, 10));

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(epic.getId(), subtask);
    }

    @DisplayName("Checks that the created task/epic/subtask matches the added one")
    @Test
    void ShouldBeEqualWithCreatedTasks() {
        Task expectedTask = manager.getTask(1);
        Epic expectedEpic = manager.getEpic(2);
        Subtask expectedSubtask = manager.getSubtask(3);

        assertEquals(new Task(1, "Задача 1", "Описание 1", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2024, 7, 15, 15, 0)), expectedTask);
        assertEquals(new Epic(2, "Эпик 1", "Описание эпика"), expectedEpic);
        assertEquals(new Subtask(3, "Подзадача 1", "Подзадача Эпика 1", Status.NEW, 2, Duration.ofMinutes(30), LocalDateTime.of(2024, 7, 10, 16, 10)), expectedSubtask);
    }

    @DisplayName("Checks that the created task is not null")
    @Test
    void ShouldCheckExistTaskNotNull() {
        Task expectedTask = manager.getTask(1);
        Epic expectedEpic = manager.getEpic(2);
        Subtask expectedSubtask = manager.getSubtask(3);

        assertNotNull(expectedTask);
        assertNotNull(expectedEpic);
        assertNotNull(expectedSubtask);
    }

    @DisplayName("Checks that tasks with the same Id do not conflict")
    @Test
    void shouldCheckTasksWithSameIdNotConflict() {
        Task taskWithSameId = new Task(1, "Задача с установленным ID", "И другим описанием", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 7, 15, 17, 0));
        manager.addTask(taskWithSameId);

        assertTrue(manager.getAllTasks().contains(taskWithSameId));
    }

    @DisplayName("Checks the immutability of the task when adding a task to the manager")
    @Test
    void ShouldCheckImmutabilityParameters() {
        Task taskFromManager = manager.getTask(1);

        assertEquals(1, taskFromManager.getId());
        assertEquals("Задача 1", taskFromManager.getTitle());
        assertEquals("Описание 1", taskFromManager.getDescription());
        assertEquals(Status.NEW, taskFromManager.getStatus());
    }

    @DisplayName("Should throw ValidationException when task not valid")
    @Test
    void shouldThrowValidationExceptionWhenTaskNotValid() {
        Task wrongTimeTask = new Task(4, "Пересекающаяся задача", "Описание", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 7, 10, 16, 5));
        assertThrows(ValidationException.class, () -> manager.addTask(wrongTimeTask));
    }

    @DisplayName("Should not throw ValidationException when task valid")
    @Test
    void shouldNotThrowValidationExceptionWhenTaskValid() {
        Task correctTimeTask = new Task(4, "Непересекающаяся задача", "Описание", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 7, 10, 16, 40));
        assertDoesNotThrow(() -> manager.addTask(correctTimeTask));
    }
}
