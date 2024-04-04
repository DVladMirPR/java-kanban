package ru.yandex.manager;

import ru.yandex.manager.model.Epic;
import ru.yandex.manager.model.Subtask;
import ru.yandex.manager.model.Task;
import ru.yandex.manager.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task(1, "Задача 1", "Пройтись");
        Task task2 = new Task(2, "Задача 2", "Выпить кофе");
        Epic epic1 = new Epic(1, "Эпик 1", "Дожить до каникул");
        manager.addEpic(epic1);
        manager.addSubtask(epic1.getId(), new Subtask(1, "Подзадача 1", "Сдать проект", epic1.getId()));
        manager.addSubtask(epic1.getId(), new Subtask(2, "Подзадача 2", "Не сойти с ума", epic1.getId()));

        Epic epic2 = new Epic(2, "Эпик 2", "Увеличить количество часов в сутках");
        manager.addEpic(epic2);
        manager.addSubtask(epic2.getId(), new Subtask(3, "Подзадача 3", "Понять бесконечность", epic2.getId()));
        manager.addTask(task1);
        manager.addTask(task2);

        System.out.println("Список задач:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Список эпиков:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        manager.updateTask(task1);
        manager.updateTask(task2);
        manager.updateSubtask(new Subtask(1, "Подзадача NEW", "Накормить себя", epic1.getId()));
        manager.updateEpic(epic1);

        System.out.println("Статусы после изменений:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        manager.deleteTask(1);
        manager.deleteEpic(2);
        manager.deleteSubtask(2);

        System.out.println("После удаления:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
    }
}