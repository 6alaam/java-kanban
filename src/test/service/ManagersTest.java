package test.service;


import org.junit.jupiter.api.Test;

import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultShouldInitializeInMemoryTaskManager() {
        assertInstanceOf(TaskManager.class, Managers.getDefault());
    }


}