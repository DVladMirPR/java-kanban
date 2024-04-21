package ru.yandex.manager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @DisplayName("check the return of a ready-to-work instance of the manager")
    @Test
     void ShouldReturnNotNullObject() {

        TaskManager manager = Managers.getDefaults();
        assertNotNull(manager, "Менеджер должен быть готов к работе");
    }

}