package ru.yandex.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @DisplayName("Subtasks with the same Id must be equal")
    @Test
    void subtaskEqualityById() {

        Subtask subtask1 = new Subtask(1, "Подзадача 1", "Описание 1", 1);
        Subtask subtask2 = new Subtask(1, "Подзадача 2", "Описание 2", 2);


        boolean subtasksEqual = subtask1.equals(subtask2);


        assertTrue(subtasksEqual, "Подзадача с одинаковым Id должны быть равны");
    }
}