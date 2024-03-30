package taskManagerSprint4.service;

import taskManagerSprint4.model.Epic;
import taskManagerSprint4.model.Subtask;
import taskManagerSprint4.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, ArrayList<Integer>> epicSubtasks = new HashMap<>();
    private static int currentId = 0;

    private int generateId() {
        return currentId++;
    }
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
        tasks.put(subtask.getId(), subtask);
    }


    public void updateTaskStatus(int taskId, Status newStatus) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setStatus(newStatus);
        }
    }

    public void updateEpic(int epicId, String newTitle, String newDescription) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.setTitle(newTitle);
            epic.setDescription(newDescription);
        }
    }

    public void updateSubtaskStatus(int subtaskId, Status newStatus) {
        for (Integer epicId : epicSubtasks.keySet()) {
            ArrayList<Integer> subtaskIds = epicSubtasks.get(epicId);
            if (subtaskIds.contains(subtaskId)) {
                Subtask subtask = (Subtask) tasks.get(subtaskId);
                if (subtask != null) {
                    subtask.setStatus(newStatus);
                    return;
                }
            }
        }
    }


    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteEpic(int epicId) {
        ArrayList<Integer> subtasks = epicSubtasks.get(epicId);
        if (subtasks != null) {
            for (Integer subtaskId : subtasks) {
                tasks.remove(subtaskId);
            }
        }
        epics.remove(epicId);
    }

    public void deleteSubtask(int subtaskId) {
        for (ArrayList<Integer> subtaskList : epicSubtasks.values()) {
            subtaskList.remove((Integer) subtaskId);
        }
        tasks.remove(subtaskId);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }
}
