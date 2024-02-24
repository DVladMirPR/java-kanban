package taskManager;

import java.util.ArrayList;
import java.util.HashMap;

class TaskManager {
    private final HashMap<Integer, Task> tasks;

    public TaskManager(IdGenerator idGenerator) {
        tasks = new HashMap<>();
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateTaskStatus(int taskId, Status newStatus) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setStatus(newStatus);
        }
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getClass() == Subtask.class) {
                Subtask subtask = (Subtask) task;
                if (subtask.getEpicId() == epic.getId()) {
                    subtasks.add(subtask);
                }
            }
        }
        return subtasks;
    }

    public void updateEpicStatus(Epic epic) {
        boolean allSubtasksDone = true;
        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() != Status.DONE) {
                allSubtasksDone = false;
                break;
            }
        }

        if (allSubtasksDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
