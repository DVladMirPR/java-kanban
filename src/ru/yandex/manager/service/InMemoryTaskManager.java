package ru.yandex.manager.service;

import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Status;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, ArrayList<Integer>> epicSubtasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager;
    protected int currentId = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;

    }

    @Override
    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(int epicId, Subtask subtask) {
        subtask.setId(generateId());
        subtask.setEpicId(epicId);
        ArrayList<Integer> epicSubtaskList = epicSubtasks.computeIfAbsent(epicId, k -> new ArrayList<>());
        epicSubtaskList.add(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epicId);
    }


    @Override
    public void updateTask(Task taskToUpdate) {
        tasks.put(taskToUpdate.getId(), taskToUpdate);
    }

    @Override
    public void updateEpic(Epic epicToUpdate) {
        Epic epic = epics.get(epicToUpdate.getId());
        if (epic != null) {
            epic.setTitle(epicToUpdate.getTitle());
            epic.setDescription(epicToUpdate.getDescription());
        }
    }

    @Override
    public void updateSubtask(Subtask subtaskToUpdate) {
        subtasks.put(subtaskToUpdate.getId(), subtaskToUpdate);
        updateEpicStatus(subtaskToUpdate.getEpicId());
    }

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        epics.remove(epicId);
        epicSubtasks.remove(epicId);
        historyManager.remove(epicId);
        ArrayList<Integer> subtaskIdsToRemove = epicSubtasks.get(epicId);
        if (subtaskIdsToRemove != null) {
            for (Integer subtaskId : subtaskIdsToRemove) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
        }
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            ArrayList<Integer> subtaskList = epicSubtasks.get(epicId);
            if (subtaskList != null) {
                subtaskList.remove((Integer) subtaskId);
                updateEpicStatus(epicId);
            }
            historyManager.remove(subtaskId);
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        epics.clear();
        epicSubtasks.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
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

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
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

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            ArrayList<Subtask> subtasksOfEpic = getSubtasksOfEpic(epicId);
            int countNew = 0;
            int countDone = 0;
            for (Subtask subtask : subtasksOfEpic) {
                Status status = subtask.getStatus();
                if (status == Status.NEW) {
                    countNew++;
                } else if (status == Status.DONE) {
                    countDone++;
                }
            }
            int size = subtasksOfEpic.size();
            if (countNew == size) {
                epic.setStatus(Status.NEW);
            } else if (countDone == size) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }


    private int generateId() {
        return currentId++;
    }
}