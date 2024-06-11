package ru.yandex.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.manager.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;
    private Task task;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task = new Task(5, "Исходная", "Собираемся быть исторической");
        task2 = new Task(6, "Середина", "Здесь центр");
        task3 = new Task(7, "Хвост", "Зверь с Хвостом");
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
    }

    @DisplayName("Check that the task is saved in the history")
    @Test
    void shouldSaveTaskInHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.contains(task));
    }

    @DisplayName("Remove task from the beginning of the history")
    @Test
    void shouldRemoveTaskFromBeginning() {
        historyManager.remove(task.getId());
        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task));
        assertEquals(2, history.size());
    }

    @DisplayName("Remove task from the end of the history")
    @Test
    void shouldRemoveTaskFromEnd() {
        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task3));
        assertEquals(2, history.size());
    }

    @DisplayName("Remove task from the middle of the history")
    @Test
    void shouldRemoveTaskFromMiddle() {
        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task2));
        assertEquals(2, history.size());
    }

    @DisplayName("Ensure no duplicate tasks in history")
    @Test
    void shouldNotHaveDuplicateTasksInHistory() {
        int sizeBeforeAdd = historyManager.getHistory().size();
        historyManager.add(task);
        int sizeAfterAdd = historyManager.getHistory().size();

        assertEquals(sizeBeforeAdd, sizeAfterAdd, "История содержит дубликаты задач");
    }
}