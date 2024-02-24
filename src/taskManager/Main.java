package taskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager(new IdGenerator());

        Task task1 = new Task(IdGenerator.generateId(), "Задача 1", "Пройтись", Status.NEW);
        Task task2 = new Task(IdGenerator.generateId(), "Задача 2", "Выпить кофе", Status.NEW);

        Epic epic1 = new Epic(IdGenerator.generateId(), "Эпик 1", "Дожить до каникул", Status.NEW);
        epic1.addSubtask(new Subtask(IdGenerator.generateId(), "Подзадача 1", "Сдать проект", Status.NEW, epic1.getId()));
        epic1.addSubtask(new Subtask(IdGenerator.generateId(), "Подзадача 2", "Не сойти с Ума", Status.NEW, epic1.getId()));

        Epic epic2 = new Epic(IdGenerator.generateId(), "Эпик 2", "Увеличить количество часов в сутках", Status.NEW);
        epic2.addSubtask(new Subtask(IdGenerator.generateId(), "Подзадача 3", "Понять бесконечность", Status.NEW, epic2.getId()));

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(epic1);
        manager.addTask(epic2);

        System.out.println("Список задач:");
        for (Task task : manager.getAllTasks()) {
            if (task.getClass() == Task.class) {
                System.out.println(task);
            }
        }

        for (Task task : manager.getAllTasks()) {
            if (task.getClass() == Epic.class) {
                Epic epic = (Epic) task;
                System.out.println("Для эпика '" + epic.getTitle() + "':");
                for (Subtask subtask : manager.getSubtasksOfEpic(epic)) {
                    System.out.println(subtask);
                }
            }
        }

        System.out.println("Список эпиков:");
        for (Task task : manager.getAllTasks()) {
            if (task.getClass() == Epic.class) {
                System.out.println(task);
            }
        }

        manager.updateTaskStatus(task1.getId(), Status.IN_PROGRESS);
        manager.updateTaskStatus(task2.getId(), Status.DONE);
        epic1.getSubtasks().get(0).setStatus(Status.DONE);
        manager.updateEpicStatus(epic1);

        System.out.println("Статусы после изменений:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        manager.deleteTaskById(task1.getId());
        manager.deleteTaskById(epic2.getId());

        System.out.println("После удаления:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
    }
}
