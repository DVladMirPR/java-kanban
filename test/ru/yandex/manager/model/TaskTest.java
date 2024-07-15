package ru.yandex.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    @DisplayName("Comparing two tasks with the same id")
    @Test
    void ShouldBeEqualityById() {
        Task task1 = new Task(1, "Задача1", "Описание 1");
        Task task2 = new Task(1, "Задача2", "Описание 2");

        assertEquals(task1, task2, "Задачи должны быть одинаковые по Id");
    }
}