package test.service;


import model.Task;

import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    @Test
    void shouldSaveTaskToFile() {
        File file = new File("test-save.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = new Task("Test", "Description");
        manager.addTask(task);

        assertTrue(file.exists(), "Файл должен быть создан после сохранения задачи");
    }



    @Test
    void shouldHandleEmptyFile() throws IOException {
        File file = new File("empty.csv");
        file.createNewFile(); // создаёт файл, если его нет

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loaded.getAllTasks().isEmpty(), "Задачи должны быть пустыми при загрузке из пустого файла");
    }
}