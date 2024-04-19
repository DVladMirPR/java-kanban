package ru.yandex.manager.service;

import ru.yandex.manager.model.Task;

import java.util.List;

public interface HistoryManager {
    void addToHistory(Task task);

    List<Task> getHistory();

}
