package ru.yandex.manager.service;

import ru.yandex.manager.exception.NotFoundException;
import ru.yandex.manager.exception.ValidationException;
import ru.yandex.manager.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, ArrayList<Integer>> epicSubtasks = new HashMap<>();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private final HistoryManager historyManager;
    protected int currentId = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void addTask(Task task) {
        if (!isValid(task)) {
            throw new ValidationException("Задача пересекается с другой задачей или подзадачей");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(int epicId, Subtask subtask) {
        if (!isValid(subtask)) {
            throw new ValidationException("Подзадача пересекается с другой задачей или подзадачей");
        }
        subtask.setId(generateId());
        subtask.setEpicId(epicId);
        getEpic(epicId).addSubtask(subtask);
        prioritizedTasks.add(subtask);
    }

    @Override
    public void updateTask(Task taskToUpdate) {
        Task task = tasks.get(taskToUpdate.getId());
        if (task == null) {
            throw new NotFoundException("Задача не найдена");
        }
        task.setTitle(taskToUpdate.getTitle());
        task.setDescription(taskToUpdate.getDescription());
        task.setStatus(taskToUpdate.getStatus());
        task.setDuration(taskToUpdate.getDuration());
        prioritizedTasks.remove(task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epicToUpdate) {
        Epic epic = epics.get(epicToUpdate.getId());
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        epic.setTitle(epicToUpdate.getTitle());
        epic.setDescription(epicToUpdate.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtaskToUpdate) {
        Subtask subtask = getSubtask(subtaskToUpdate.getId());
        if (subtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }
        subtask.setTitle(subtaskToUpdate.getTitle());
        subtask.setDescription(subtaskToUpdate.getDescription());
        subtask.setStatus(subtaskToUpdate.getStatus());
        subtask.setDuration(subtaskToUpdate.getDuration());
        prioritizedTasks.remove(subtask);
        prioritizedTasks.add(subtask);
        getEpic(subtaskToUpdate.getEpicId()).updateSubtask(subtaskToUpdate);
    }

    @Override
    public void deleteTask(int taskId) {
        Task task = tasks.remove(taskId);
        if (task == null) {
            throw new NotFoundException("Задача не найдена");
        }
        prioritizedTasks.remove(task);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        List<Subtask> subtasksOfEpic = getEpic(epicId).getSubtasks();
        for (Subtask subtask : subtasksOfEpic) {
            deleteSubtask(subtask.getId());
        }
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        Subtask subtask = getSubtask(subtaskId);
        if (subtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }
        prioritizedTasks.remove(subtask);
        int epicId = subtask.getEpicId();
        getEpic(epicId).removeSubtask(subtask);
        historyManager.remove(subtaskId);
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(task -> historyManager.remove(task.getId()));
        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.values().forEach(epic -> historyManager.remove(epic.getId()));
        epics.clear();
        epicSubtasks.clear();
        prioritizedTasks.stream().filter(task -> task.getType() == TaskType.SUBTASK).forEach(task -> historyManager.remove(task.getId()));
    }

    @Override
    public void deleteAllSubtasks() {
        prioritizedTasks.stream().filter(task -> task.getType() == TaskType.SUBTASK).forEach(task -> historyManager.remove(task.getId()));
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return epics.values().stream().flatMap(epic -> epic.getSubtasks().stream()).collect(Collectors.toList());
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        return getEpic(epicId).getSubtasks();
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
        Subtask subtask = epics.values().stream().flatMap(epic -> epic.getSubtasks().stream()).filter(subtask1 -> subtask1.getId() == id).findFirst().orElse(null);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isValid(Task task) {
        return prioritizedTasks.stream().allMatch(t ->
                task.getEndTime().isBefore(t.getStartTime()) ||
                        task.getStartTime().isAfter(t.getEndTime()) ||
                        task.getEndTime().equals(t.getStartTime()) ||
                        task.getStartTime().equals(t.getEndTime())
        );
    }

    private int generateId() {
        return currentId++;
    }
}