package ru.yandex.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() {

        manager = Managers.getDefaults();


        task = new Task(1, "Задача 1", "Описание 1");
        epic = new Epic(2, "Эпик 1", "Описание эпика");
        subtask = new Subtask(3, "Подзадача 1", "Подзадача Эпика 1", epic.getId());

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(epic.getId(), subtask);

    }

    @DisplayName("Checks that the created task/epic/subtask matches the added one")
    @Test
    void add_ShouldBeEqualWithCreatedTasks() {
        Task expectedTask = manager.getTask(task.getId());
        Epic expectedEpic = manager.getEpic(epic.getId());
        Subtask expectedSubtask = manager.getSubtask(subtask.getId());

        assertEquals(task, expectedTask);
        assertEquals(epic, expectedEpic);
        assertEquals(subtask, expectedSubtask);
    }

    @DisplayName("Checks that the created task is not null")
    @Test
    void ShouldCheckExistTaskNotNull() {
        Task expectedTask = manager.getTask(task.getId());
        Epic expectedEpic = manager.getEpic(epic.getId());
        Subtask expectedSubtask = manager.getSubtask(subtask.getId());

        assertNotNull(expectedTask);
        assertNotNull(expectedEpic);
        assertNotNull(expectedSubtask);
    }

    @DisplayName("Checks that tasks with the same Id do not conflict")
    @Test
    void shouldCheckTasksWithSameIdNotConflict() {

        Task TaskWithSameId = new Task(task.getId(), "Задача с установленным ID", "И другим описанием");
        manager.addTask(TaskWithSameId);

        assertTrue(manager.getAllTasks().contains(TaskWithSameId));
    }

    @DisplayName("Checks the immutability of the task when adding a task to the manager")
    @Test
    void ShouldCheckImmutabilityParameters() {

        Task TaskFromManager = manager.getTask(task.getId());


        assertEquals(task.getId(), TaskFromManager.getId());
        assertEquals(task.getTitle(), TaskFromManager.getTitle());
        assertEquals(task.getDescription(), TaskFromManager.getDescription());
        assertEquals(task.getStatus(), TaskFromManager.getStatus());
    }
}