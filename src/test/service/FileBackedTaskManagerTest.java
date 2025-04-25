package test.service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    void addAndSaveTask() {
        Task task = new Task("Task 1", "Description 1");
        manager.addTask(task);
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    void addAndSaveEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        manager.addEpic(epic);
        List<Epic> epics = manager.getAllEpic();
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        epics = manager.getAllEpic();
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));
    }

    @Test
    void addAndSaveSubtask() {
        Epic epic = new Epic(1,"Epic 1","Description 1",Status.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask(2,"Subtask 1","Description 1",Status.NEW,epic.getId());
        manager.addSubtask(subtask);
        List<Subtask> subtasks = manager.getAllSubtask();
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.get(0));
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        subtasks = manager.getAllSubtask();
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.get(0));
    }

    @Test
    void testSaveAndLoadMultipleTasks() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        Task task2 = new Task(2, "Task 2", "Description 2", Status.DONE);
        manager.addTask(task1);
        manager.addTask(task2);
        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        tasks = manager.getAllTasks();
        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = manager.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void testSaveAndLoadAfterDeletingTask() {
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW);
        manager.addTask(task);
        manager.deleteTaskByID(task.getId());
        List<Task> tasks = manager.getAllTasks();
        assertTrue(tasks.isEmpty());
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        tasks = manager.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void testSaveAndLoadAfterUpdatingTask() {
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW);
        manager.addTask(task);
        task.setDescription("Updated Description");
        manager.updateTask(task);

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Updated Description", tasks.get(0).getDescription());

        manager = FileBackedTaskManager.loadFromFile(tempFile);
        tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Updated Description", tasks.get(0).getDescription());
    }

    @Test
    void testSaveAndLoadAfterDeletingEpic() {
        Epic epic = new Epic(1, "Epic 1", "Description 1", Status.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1", Status.NEW, epic.getId());
        manager.addSubtask(subtask);
        List<Epic> epics = manager.getAllEpic();
        List<Subtask> subtasks = manager.getAllSubtask();
        assertEquals(1, epics.size());
        assertEquals(1, subtasks.size());
        manager.deleteEpicByID(epic.getId());
        epics = manager.getAllEpic();
        subtasks = manager.getAllSubtask();
        assertTrue(epics.isEmpty());
        assertTrue(subtasks.isEmpty());
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        epics = manager.getAllEpic();
        subtasks = manager.getAllSubtask();
        assertTrue(epics.isEmpty());
        assertTrue(subtasks.isEmpty());
    }
}