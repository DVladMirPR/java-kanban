package ru.yandex.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Status;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @BeforeEach
    protected abstract void setUp();

    @DisplayName("Add task")
    @Test
    void shouldAddTask() {
        Task task = new Task(1, "Task 1", "Description");
        manager.addTask(task);
        assertEquals(task, manager.getTask(task.getId()));
    }

    @DisplayName("Update task")
    @Test
    void shouldUpdateTask() {
        Task task = new Task(1, "Task 1", "Description");
        manager.addTask(task);
        task.setTitle("Updated Task 1");
        manager.updateTask(task);
        assertEquals("Updated Task 1", manager.getTask(task.getId()).getTitle());
    }

    @DisplayName("Add epic")
    @Test
    void shouldAddEpic() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        manager.addEpic(epic);
        assertEquals(epic, manager.getEpic(epic.getId()));
    }

    @DisplayName("Update epic")
    @Test
    void shouldUpdateEpic() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        manager.addEpic(epic);
        epic.setTitle("Updated Epic 1");
        manager.updateEpic(epic);
        assertEquals("Updated Epic 1", manager.getEpic(epic.getId()).getTitle());
    }

    @DisplayName("Add subtask")
    @Test
    void shouldAddSubtask() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask(2, "Subtask 1", "Description", Status.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        manager.addSubtask(epic.getId(), subtask);
        assertEquals(subtask, manager.getSubtask(subtask.getId()));
    }

    @DisplayName("Update subtask")
    @Test
    void shouldUpdateSubtask() {
        Epic epic = new Epic(1, "Epic 1", "Description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask(2, "Subtask 1", "Description", Status.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        manager.addSubtask(epic.getId(), subtask);
        subtask.setTitle("Updated Subtask 1");
        manager.updateSubtask(subtask);
        assertEquals("Updated Subtask 1", manager.getSubtask(subtask.getId()).getTitle());
    }
}
