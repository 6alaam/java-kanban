package service;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;


    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            //  заголовок
            writer.write("id,type,name,status,description,epic\n");
            // вроде так
            writeTasks(writer, getAllTasks());
            writeTasks(writer, getAllEpic());
            writeTasks(writer, getAllEpic());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задач в файл", e);
        }
    }


    private void writeTasks(Writer writer, List<? extends Task> tasks) throws IOException {
        for (Task task : tasks) {
            writer.write(task.toString() + "\n");
        }


    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Пропустить заголовок
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.addEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.addSubtask((Subtask) task);
                } else {
                    manager.addTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка загрузки задачи из файла", e);
        }
        return manager;
    }


    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                task = new Subtask(name, description, epicId);
                break;
            default:
                throw new IllegalArgumentException("Unknown task type");
        }
        task.setId(id);
        task.setStatus(status);
        return task;
    }


    @Override
    public Task addTask(Task task) {
        save();
        return super.addTask(task);

    }

    @Override
    public Epic addEpic(Epic epic) {
        save();
        return super.addEpic(epic);
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        save();
        return super.addSubtask(subtask);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        save();
        return super.updateEpic(epic);
    }

    @Override
    public Task updateTask(Task task) {
        save();
        return super.updateTask(task);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        save();
        return super.updateSubtask(subtask);
    }

    @Override
    public Task deleteTaskByID(int id) {
        return super.deleteTaskByID(id);
    }

    @Override
    public Epic deleteEpicByID(int id) {
        return super.deleteEpicByID(id);
    }

    @Override
    public Subtask deleteSubtaskByID(int id) {
        return super.deleteSubtaskByID(id);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}
