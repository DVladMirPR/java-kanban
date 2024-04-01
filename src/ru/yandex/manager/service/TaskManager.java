package ru.yandex.manager.service;

import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, ArrayList<Integer>> epicSubtasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int currentId = 0;

    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(int epicId, Subtask subtask) {
        subtask.setId(generateId());
        subtask.setEpicId(epicId);
        ArrayList<Integer> epicSubtaskList = epicSubtasks.get(epicId);
        if (epicSubtaskList == null) {
            epicSubtaskList = new ArrayList<>();
            epicSubtasks.put(epicId, epicSubtaskList);
        }
        epicSubtaskList.add(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
    }

    public void updateTask(Task taskToUpdate) {
        tasks.put(taskToUpdate.getId(), taskToUpdate);
    }

    public void updateEpic(Epic epicToUpdate) {
        epics.put(epicToUpdate.getId(), epicToUpdate);
    }

    public void updateSubtask(Subtask subtaskToUpdate) {
        subtasks.put(subtaskToUpdate.getId(), subtaskToUpdate);
    }

    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteEpic(int epicId) {
        epics.remove(epicId);
        epicSubtasks.remove(epicId);
    }

    public void deleteSubtask(int subtaskId) {
        subtasks.remove(subtaskId);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        ArrayList<Integer> subtaskIds = epicSubtasks.get(epicId);
        ArrayList<Subtask> epicSubtasksList = new ArrayList<>();
        if (subtaskIds != null) {
            for (Integer subtaskId : subtaskIds) {
                epicSubtasksList.add(subtasks.get(subtaskId));
            }
        }
        return epicSubtasksList;
    }

    private int generateId() {
        return currentId++;
    }
}