package ru.yandex.manager.service;

import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.model.Task;

import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(int epicId, Subtask subtask);

    void updateTask(Task taskToUpdate);

    void updateEpic(Epic epicToUpdate);

    void updateSubtask(Subtask subtaskToUpdate);

    void deleteTask(int taskId);

    void deleteEpic(int epicId);

    void deleteSubtask(int subtaskId);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getSubtasksOfEpic(int epicId);

    List<Task> getHistory();

    Epic getEpic(int id);

    Task getTask(int id);

    Subtask getSubtask(int id);
}