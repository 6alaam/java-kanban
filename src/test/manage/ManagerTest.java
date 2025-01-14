package test.manage;

import manage.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagerTest {

    @Test
    void getDefaultShouldInitializeInMemoryTaskManager() {
        assertInstanceOf(InMemoryTaskManager.class, Manager.getDefault());
    }

    @Test
    void getDefaultHistoryShouldInitializeInMemoryHistoryManager() {
        assertInstanceOf(InMemoryHistoryManager.class, Manager.getDefaultHistory());
    }
}