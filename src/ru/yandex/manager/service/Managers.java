package ru.yandex.manager.service;

import java.io.File;

public class Managers {
    private static final File file = new File("tasks.csv");

    public static TaskManager getDefaults() {
        return new FileBackedTaskManager(getDefaultHistory(), file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
