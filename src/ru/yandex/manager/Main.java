package ru.yandex.manager;

import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.model.Task;
import ru.yandex.manager.service.Managers;
import ru.yandex.manager.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefaults();

        Task task1 = new Task(1, "Задача 1", "Пройтись");
        Task task2 = new Task(2, "Задача 2", "Выпить кофе");
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic(1, "Эпик 1", "Дожить до каникул");
        manager.addEpic(epic1);
        manager.addSubtask(epic1.getId(), new Subtask(1, "Подзадача 1", "Сдать проект", epic1.getId()));
        manager.addSubtask(epic1.getId(), new Subtask(2, "Подзадача 2", "Не сойти с ума", epic1.getId()));

        Epic epic2 = new Epic(2, "Эпик 2", "Увеличить количество часов в сутках");
        manager.addEpic(epic2);
        manager.addSubtask(epic2.getId(), new Subtask(3, "Подзадача 3", "Понять бесконечность", epic2.getId()));

        for (Task task : manager.getAllTasks()) {
            manager.getTask(task.getId());
        }

        for (Epic epic : manager.getAllEpics()) {
            manager.getEpic(epic.getId());
        }

        for (Subtask subtask : manager.getAllSubtasks()) {
            manager.getSubtask(subtask.getId());
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

    }
}