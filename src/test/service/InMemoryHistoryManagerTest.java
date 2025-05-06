package test.service;

import model.*;
import service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class InMemoryHistoryManagerTest {

    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    @Test
    public void getHistoryShouldReturnOldTaskAfterUpdate() {
        try {
            Task washFloor = new Task("Написать проект", "Сдать в срок");
            taskManager.addTask(washFloor);
            taskManager.getTaskById(washFloor.getId());
            taskManager.updateTask(new Task(washFloor.getId(), "Отправить на ревью",
                    "Переписать половину", Status.IN_PROGRESS));
            List<Task> tasks = taskManager.getHistory();
            Task oldTask = tasks.get(0); // Изменено с getFirst() на get(0)
            assertEquals(washFloor.getName(), oldTask.getName(), "В истории не сохранилась старая версия задачи");
            assertEquals(washFloor.getDescription(), oldTask.getDescription(),
                    "В истории не сохранилась старая версия задачи");
        } catch (TaskIntersectionException e) {
            fail("Не должно было быть пересечения задач", e);
        }
    }

    @Test
    public void getHistoryShouldReturnOldEpicAfterUpdate() {
        try {
            Epic flatRenovation = new Epic("Задача", "Разбитая на подзадачи");
            taskManager.addEpic(flatRenovation);
            taskManager.getEpicById(flatRenovation.getId());
            taskManager.updateEpic(new Epic(flatRenovation.getId(), "Новое имя", "новое описание",
                    Status.IN_PROGRESS));
            List<Task> epics = taskManager.getHistory();
            Epic oldEpic = (Epic) epics.get(0); // Изменено с getFirst() на get(0)
            assertEquals(flatRenovation.getName(), oldEpic.getName(),
                    "В истории не сохранилась старая версия эпика");
            assertEquals(flatRenovation.getDescription(), oldEpic.getDescription(),
                    "В истории не сохранилась старая версия эпика");
        } catch (TaskIntersectionException e) {
            fail("Не должно было быть пересечения задач", e);
        }
    }

    @Test
    public void getHistoryShouldReturnOldSubtaskAfterUpdate() {
        try {
            Epic flatRenovation = new Epic("Новая задача", "Проверить");
            taskManager.addEpic(flatRenovation);
            Subtask flatRenovationSubtask3 = new Subtask("Заказать", "На озоне",
                    flatRenovation.getId());
            taskManager.addSubtask(flatRenovationSubtask3);
            taskManager.getSubtaskById(flatRenovationSubtask3.getId());
            taskManager.updateSubtask(new Subtask(flatRenovationSubtask3.getId(), "Новое имя",
                    "новое описание", Status.IN_PROGRESS, flatRenovation.getId()));
            List<Task> subtasks = taskManager.getHistory();
            Subtask oldSubtask = (Subtask) subtasks.get(0); // Изменено с getFirst() на get(0)
            assertEquals(flatRenovationSubtask3.getName(), oldSubtask.getName(),
                    "В истории не сохранилась старая версия подзадачи");
            assertEquals(flatRenovationSubtask3.getDescription(), oldSubtask.getDescription(),
                    "В истории не сохранилась старая версия подзадачи");
        } catch (TaskIntersectionException e) {
            fail("Не должно было быть пересечения задач", e);
        }
    }

    @Test
    void testAddTask() {
        try {
            Task task1 = new Task(1);
            Task task2 = new Task(2);

            historyManager.add(task1);
            historyManager.add(task2);

            List<Task> history = historyManager.getHistory();
            assertEquals(2, history.size());
            assertTrue(history.contains(task1));
            assertTrue(history.contains(task2));
        } catch (TaskIntersectionException e) {
            fail("Не должно было быть пересечения задач", e);
        }
    }

    @Test
    void testAddDuplicateTask() {
        try {
            Task washFloor = new Task("Написать проект", "Сдать в срок");
            taskManager.addTask(washFloor);
            taskManager.getTaskById(washFloor.getId());
            taskManager.getTaskById(washFloor.getId());
            List<Task> task = taskManager.getHistory();
            assertEquals(1, task.size());
        } catch (TaskIntersectionException e) {
            fail("Не должно было быть пересечения задач", e);
        }
    }

    @Test
    void testRemoveAllTask() {
        try {
            Task washFloor = new Task("Написать проект", "Сдать в срок");
            Task goToTeach = new Task("пойти в школу", "Учить информатику");
            Epic flatRenovation = new Epic("Новая задача", "Проверить");
            taskManager.addTask(washFloor);
            taskManager.addTask(goToTeach);
            taskManager.addEpic(flatRenovation);
            taskManager.getTaskById(washFloor.getId());
            taskManager.getTaskById(goToTeach.getId());
            taskManager.getEpicById(flatRenovation.getId());
            taskManager.deleteAllEpic();
            List<Task> task = taskManager.getHistory();
            assertEquals(2, task.size());
        } catch (TaskIntersectionException e) {
            fail("Не должно было быть пересечения задач", e);
        }
    }

    @Test
    void testRemoveTask() {
        Task task1 = new Task(1);
        Task task2 = new Task(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(1); // Удаляем task1

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertFalse(history.contains(task1)); // task1 должен быть удален
        assertTrue(history.contains(task2));
    }

    @Test
    void testGetHistoryEmpty() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty()); // История должна быть пустой
    }

    @Test
    void testGetHistoryAfterRemoval() {
        Task task1 = new Task(1);
        Task task2 = new Task(2);
        Task task3 = new Task(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2); // Удаляем task2

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task3));
        assertFalse(history.contains(task2)); // task2 должен быть удален
    }
}