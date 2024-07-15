package ru.yandex.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.model.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File file;

    @BeforeEach
    public void setUp() {
        try {
            file = File.createTempFile("test", ".csv");
            file.deleteOnExit();
            manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать файл", e);
        }
    }

    @DisplayName("Save and load multiple tasks")
    @Test
    void shouldSaveAndLoadMultipleTasks() {
        Task task1 = new Task(1, "Задача1", "ТАСК");
        Task task2 = new Task(2, "Задача2", "Задача2");
        Epic epic1 = new Epic(3, "Эпик1", "Гигант");
        Subtask subtask1 = new Subtask(4, "Подзадача1", "Подзадача эпика1", epic1.getId());

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addSubtask(epic1.getId(), subtask1);

        FileBackedTaskManager testManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(manager.getAllTasks(), testManager.getAllTasks());
        assertEquals(manager.getAllEpics(), testManager.getAllEpics());
        assertEquals(manager.getAllSubtasks(), testManager.getAllSubtasks());
    }

    @DisplayName("Ensure tasks are saved in correct format")
    @Test
    void shouldSaveTasksInCorrectFormat() {
        Task task = new Task(0, "Задача", "Проверка");
        Task task2 = new Task(1, "Задача1", "Проверка");
        manager.addTask(task);
        manager.addTask(task2);

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals("1,TASK,Задача1,NEW,Проверка,null", lines.get(2));
    }

}
