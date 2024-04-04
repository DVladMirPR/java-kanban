package ru.yandex.manager.service;

import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Status;
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
        ArrayList<Integer> epicSubtaskList = epicSubtasks.computeIfAbsent(epicId, k -> new ArrayList<>());
        epicSubtaskList.add(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epicId);
    }

    public void updateTask(Task taskToUpdate) {
        tasks.put(taskToUpdate.getId(), taskToUpdate);
    }

    public void updateEpic(Epic epicToUpdate) {
        Epic epic = epics.get(epicToUpdate.getId());
        if (epic != null) {
            epic.setTitle(epicToUpdate.getTitle());
            epic.setDescription(epicToUpdate.getDescription());
            updateEpicStatus(epicToUpdate.getId());
        }
    }

    public void updateSubtask(Subtask subtaskToUpdate) {
        subtasks.put(subtaskToUpdate.getId(), subtaskToUpdate);
        updateEpicStatus(subtaskToUpdate.getEpicId());
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
        for (ArrayList<Integer> subtaskList : epicSubtasks.values()) {
            if (subtaskList != null) {
                for (int i = 0; i < subtaskList.size(); i++) {
                    if (subtaskList.get(i) == subtaskId) {
                        subtaskList.remove(i);
                        break;
                    }
                }
            }
        }
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        epicSubtasks.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (ArrayList<Integer> subtaskList : epicSubtasks.values()) {
            if (subtaskList != null) {
                subtaskList.clear();
            }
        }
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
        }
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

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            ArrayList<Subtask> subtasksOfEpic = getSubtasksOfEpic(epicId);
            boolean allSubtasksNew = true;
            for (Subtask subtask : subtasksOfEpic) {
                if (subtask.getStatus() != Status.NEW) {
                    allSubtasksNew = false;
                    break;
                }
            }
            if (allSubtasksNew || subtasksOfEpic.isEmpty()) {
                epic.setStatus(Status.NEW);
            } else {
                boolean allDone = true;
                for (Subtask subtask : subtasksOfEpic) {
                    if (subtask.getStatus() != Status.DONE) {
                        allDone = false;
                        break;
                    }
                }
                if (allDone) {
                    epic.setStatus(Status.DONE);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        }
    }


    private int generateId() {
        return currentId++;
    }
}
