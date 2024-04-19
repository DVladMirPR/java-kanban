package ru.yandex.manager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.manager.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @DisplayName("Check that the original version of the task is saved in the History list, and not the updated one.")
    @Test
    void inMemoryHistoryManager_ShouldSaveOriginalVersionOfTheTaskInHistoryNotUpdate() {
        TaskManager manager = Managers.getDefaults();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task(5, "Исходная", "Собираемся быть исторической");
        manager.addTask(task);
        Task taskBeForUpdate = new Task(task.getId(), task.getTitle(), task.getDescription());
        historyManager.addToHistory(task);
        task.setTitle("Измененная");
        task.setDescription("История не светит");
        manager.updateTask(task);
        List<Task> history = historyManager.getHistory();
        assertTrue(history.contains(taskBeForUpdate));

    }

}