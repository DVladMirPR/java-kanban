package ru.yandex.manager.service;

import ru.yandex.manager.model.Task;

import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task>history = new ArrayList<>();

    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            System.out.println("Добавлена в историю задача: " + task.getId());
            if (history.size() > 9) {
                history.remove(0);
            }
            history.add(task);
        }
    }


    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
