package service;

import model.Epic;
import model.Subtask;
import model.Task;

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

    Task addTask(Task task) throws TaskIntersectionException;

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask) throws TaskIntersectionException;

    Epic updateEpic(Epic epic);

    Task updateTask(Task task) throws TaskIntersectionException;

    Subtask updateSubtask(Subtask subtask) throws TaskIntersectionException;

    Task deleteTaskByID(int id);

    Epic deleteEpicByID(int id);

    Subtask deleteSubtaskByID(int id);

    List<Task> getHistory();
}
