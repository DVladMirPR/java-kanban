package ru.yandex.manager.service;

import ru.yandex.manager.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        HistoryManager historyManager = Managers.getDefaultHistory();
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, file);
        manager.loadTasksFromFile();
        return manager;
    }


    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(int epicId, Subtask subtask) {
        super.addSubtask(epicId, subtask);
        save();
    }

    @Override
    public void updateTask(Task taskToUpdate) {
        super.updateTask(taskToUpdate);
        save();
    }

    @Override
    public void updateEpic(Epic epicToUpdate) {
        super.updateEpic(epicToUpdate);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtaskToUpdate) {
        super.updateSubtask(subtaskToUpdate);
        save();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Неверный тип", e);
        }
    }

    private void loadTasksFromFile() {
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            int maxId = 0;
            for (int i = 1; i < lines.size(); i++) {
                Task task = fromString(lines.get(i));
                switch (task.getType()) {
                    case TASK -> {
                        tasks.put(task.getId(), task);
                        if (task.getId() > maxId) {
                            maxId = task.getId();
                        }
                    }
                    case EPIC -> {
                        Epic epic = (Epic) task;
                        epics.put(epic.getId(), epic);
                        epicSubtasks.put(epic.getId(), new ArrayList<>());
                        if (epic.getId() > maxId) {
                            maxId = epic.getId();
                        }
                    }
                    case SUBTASK -> {
                        Subtask subtask = (Subtask) task;
                        subtasks.put(subtask.getId(), subtask);
                        epicSubtasks.get(subtask.getEpicId()).add(subtask.getId());
                        updateEpicStatus(subtask.getEpicId());
                        if (subtask.getId() > maxId) {
                            maxId = subtask.getId();
                        }
                    }
                }
            }
            currentId = maxId + 1;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла", e);
        }
    }

    private String toString(Task task) {
        if (task instanceof Subtask subtask) {
            return task.getId() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() + "," + subtask.getEpicId();
        } else {
            return task.getId() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() + ",null";
        }
    }


    private Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String title = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                return new Task(id, title, description, status);
            case EPIC:
                return new Epic(id, title, description);
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, title, description, status, epicId);
            default:
                throw new ManagerSaveException("Неизвестный тип задачи: " + type, null);
        }
    }

}
