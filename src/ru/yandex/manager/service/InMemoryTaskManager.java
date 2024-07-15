package ru.yandex.manager.service;

import ru.yandex.manager.exception.NotFoundException;
import ru.yandex.manager.exception.ValidationException;
import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Status;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.model.Task;

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
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private final HistoryManager historyManager;
    protected int currentId = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void addTask(Task task) {
        if (!isValid(task, getPrioritizedTasks())) {
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
        if (!isValid(subtask, getPrioritizedTasks())) {
            throw new ValidationException("Подзадача пересекается с другой задачей или подзадачей");
        }
        subtask.setId(generateId());
        subtask.setEpicId(epicId);
        ArrayList<Integer> epicSubtaskList = epicSubtasks.computeIfAbsent(epicId, k -> new ArrayList<>());
        epicSubtaskList.add(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        getEpic(epicId).addSubtask(subtask);
        updateEpicStatus(epicId);
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
        Subtask subtask = subtasks.get(subtaskToUpdate.getId());
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
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void deleteTask(int taskId) {
        Task task = tasks.remove(taskId);
        if (task == null) {
            throw new NotFoundException("Задача не найдена");
        }
        prioritizedTasks.removeIf(t -> t.getId() == taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        ArrayList<Integer> subtaskIdsToRemove = epicSubtasks.remove(epicId);
        if (subtaskIdsToRemove != null) {
            for (Integer subtaskId : subtaskIdsToRemove) {
                Subtask subtask = subtasks.remove(subtaskId);
                if (subtask == null) {
                    throw new NotFoundException("Подзадача не найдена");
                }
                prioritizedTasks.remove(subtask);
                historyManager.remove(subtaskId);
            }
        }
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }
        prioritizedTasks.remove(subtask);
        int epicId = subtask.getEpicId();
        ArrayList<Integer> subtaskList = epicSubtasks.get(epicId);
        if (subtaskList != null) {
            subtaskList.remove((Integer) subtaskId);
            updateEpicStatus(epicId);
        }
        historyManager.remove(subtaskId);
        getEpic(epicId).removeSubtask(subtask);
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
        subtasks.values().forEach(subtask -> historyManager.remove(subtask.getId()));
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().forEach(subtask -> historyManager.remove(subtask.getId()));
        subtasks.clear();
        epicSubtasks.values().forEach(ArrayList::clear);
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
        }
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
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        ArrayList<Integer> subtaskIds = epicSubtasks.get(epicId);
        return subtaskIds.stream().map(subtasks::get).collect(Collectors.toList());
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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            List<Subtask> subtasksOfEpic = getSubtasksOfEpic(epicId);
            long countNew = subtasksOfEpic.stream().filter(subtask -> subtask.getStatus() == Status.NEW).count();
            long countDone = subtasksOfEpic.stream().filter(subtask -> subtask.getStatus() == Status.DONE).count();
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

    private boolean isValid(Task task, List<Task> prioritizedTasks) {
        return prioritizedTasks.stream().allMatch(t -> task.getEndTime().isBefore(t.getStartTime()) || task.getStartTime().isAfter(t.getEndTime()) || task.getEndTime().equals(t.getStartTime()) || task.getStartTime().equals(t.getEndTime()));
    }


    private int generateId() {
        return currentId++;
    }
}
