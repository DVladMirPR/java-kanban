package ru.yandex.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @DisplayName("Epics must be equal in ID")
    @Test
    void epic_EqualityById() {
        Epic epic = new Epic(1, "Epic 1", "Описание1");
        Epic epicExpected = new Epic(1, "Epic 2", "Описание 2");

        assertEquals(epicExpected, epic, "Эпики должны совпадать по ID");
    }
}


