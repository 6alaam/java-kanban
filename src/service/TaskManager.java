package service;

import model.*;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    List<Epic> getAllEpic();

    List<Subtask> getAllSubtask();

    List<Subtask> getEpicSubtasks(Epic epic);

    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubtask();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Epic updateEpic(Epic epic);

    Task updateTask(Task task);

    Subtask updateSubtask(Subtask subtask);

    Task deleteTaskByID(int id);

    Epic deleteEpicByID(int id);

    Subtask deleteSubtaskByID(int id);

    List<Task> getHistory();
}
