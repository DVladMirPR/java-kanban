package ru.yandex.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.manager.service.Managers;
import ru.yandex.manager.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @DisplayName("Subtasks with the same Id must be equal")
    @Test
    void subtaskEqualityById() {

        Subtask subtask1 = new Subtask(1, "Подзадача 1", "Описание 1", 1);
        Subtask subtask2 = new Subtask(1, "Подзадача 2", "Описание 2", 2);


        boolean subtasksEqual = subtask1.equals(subtask2);


        assertEquals(true, subtasksEqual, "Подзадача с одинаковым Id должны быть равны");
    }

    @DisplayName("Subtask doesn't have to be Epic for Itself")
    @Test
    void subtaskShouldNotBecomeItsOwnEpic() {
        TaskManager manager = Managers.getDefaults();
        Subtask subtask = new Subtask(1, "Подзадача 1", "Описание 1", 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.addSubtask(subtask.getEpicId(), subtask);
        });

        assertEquals("Измените тип", exception.getMessage());
    }
}


