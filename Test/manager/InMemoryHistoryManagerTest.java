
package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import manage.*;
import resources.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    public class InMemoryHistoryManagerTest {
        private static final HistoryManager historyManager = Manager.getDefaultHistory();
        private static Task task1;
        private static Task task2;
        private static Epic task3;
        private static Subtask task4;
        private static Subtask task5;
        private static int testId = 1000;

        @BeforeAll
        static void createTasks() {
            task1 = new Task("New Task1", "Test description 1");
            task1.setId(testId++);
            task2 = new Task("New Task1", "Test description 1");
            task2.setId(testId++);
            task3 = new Epic("New Task1", "Test description 1");
            task3.setId(testId++);
            task4 = new Subtask(testId,"New Task1", "Test description 1", Status.NEW, task3.getId());
            task4.setId(testId++);
            task3.addSubtask(task4.getId());
            task5 = new Subtask("New Task1", "Test description 1", Status.NEW, task3.getId());
            task5.setId(testId++);
            task3.addSubtask(task5.getId());
        }

        @Test
        void add() {
            historyManager.add(task1);
            List<Task> history = historyManager.getHistory();
            assertNotNull(history, "История пустая.");
            assertEquals(1, history.size(), "История не добавляется.");
            historyManager.add(task3);
            historyManager.add(task5);
            historyManager.add(task2);
            history = historyManager.getHistory();
            assertEquals(4, history.size(), "История не добавляется.");
        }

        @Test
        void addTheSameTaskThreeTimes() {
            historyManager.add(task1);
            historyManager.add(task1);
            historyManager.add(task1);
            List<Task> history = historyManager.getHistory();
            assertEquals(1, history.size(), "История той же задачи не перезаписалась.");
        }

        @Test
        void displayInTheOrderTaskWasAdded() {
            historyManager.add(task1);
            historyManager.add(task3);
            historyManager.add(task5);
            historyManager.add(task2);
            historyManager.add(task4);
            historyManager.add(task1);
            List<Task> listForTest = Arrays.asList(task3, task5, task2, task4, task1);
            assertArrayEquals(listForTest.toArray(), historyManager.getHistory().toArray(), "Просмотренные задачи не совпадают");
        }


    }
}
