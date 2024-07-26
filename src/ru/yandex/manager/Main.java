package ru.yandex.manager;

import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Status;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.model.Task;
import ru.yandex.manager.service.Managers;
import ru.yandex.manager.service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefaults();

        Task task1 = new Task(1, "Задача 1", "Пройтись", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task(2, "Задача 2", "Выпить кофе", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusHours(1));
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic(3, "Эпик 1", "Дожить до каникул");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask(4, "Подзадача 1", "Сдать проект", Status.NEW, epic1.getId(), Duration.ofMinutes(15), LocalDateTime.now().plusHours(2));
        Subtask subtask2 = new Subtask(5, "Подзадача 2", "Не сойти с ума", Status.NEW, epic1.getId(), Duration.ofMinutes(15), LocalDateTime.now().plusHours(3));
        manager.addSubtask(epic1.getId(), subtask1);
        manager.addSubtask(epic1.getId(), subtask2);

        Epic epic2 = new Epic(6, "Эпик 2", "Увеличить количество часов в сутках");
        manager.addEpic(epic2);
        Subtask subtask3 = new Subtask(7, "Подзадача 3", "Понять бесконечность", Status.NEW, epic2.getId(), Duration.ofMinutes(15), LocalDateTime.now().plusDays(4));
        manager.addSubtask(epic2.getId(), subtask3);

        System.out.println("Все задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(manager.getTask(task.getId()));
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(manager.getEpic(epic.getId()));
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(manager.getSubtask(subtask.getId()));
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
