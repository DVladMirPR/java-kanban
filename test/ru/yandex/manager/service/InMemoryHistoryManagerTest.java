package ru.yandex.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager manager;
    private Task task;
    private Epic epic;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefaults();
        task = new Task(5, "Исходная", "Собираемся быть исторической");
        manager.addTask(task);
    }

    @DisplayName("Check that the task is saved in the history")
    @Test
    void shouldSaveTaskInHistory() {
        manager.getTask(task.getId());
        List<Task> history = manager.getHistory();
        assertTrue(history.contains(task));
    }

    @DisplayName("Check that the size of the history after adding tasks should not be more than 10.")
    @Test
    void shouldContainNoMoreThanTenTasksInTheHistory() {

        for (int i = 0; i < 12; i++) {
            manager.getTask(task.getId());
        }
        List<Task> history = manager.getHistory();
        assertEquals(10, history.size());
    }

}