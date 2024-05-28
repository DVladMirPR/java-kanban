package ru.yandex.manager.service;

import ru.yandex.manager.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}