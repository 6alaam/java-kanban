package manage;

import resources.Epic;
import resources.Subtask;
import resources.Task;

import java.util.List;

public interface TaskManager {
    int getNextId();

    List<Task> getAllTasks();

    List<Epic> getAllEpic();

    List<Subtask> getAllSubtask();

    List<Subtask> getEpicSubtasks(Epic epic);

    void deleteAllTask();

    void deleteTask(int id);

    void deleteAllEpic();

    void deleteAllSubtask();

    Task getTaskById(int id);

    Epic getEpicById(int Id);

    Subtask getSubtaskById(int Id);

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Epic updateEpic(Epic epic);

    Task updateTask(Task task);

    Subtask updateSubtask(Subtask subtask);

    void deleteTaskByID(int id);

    void deleteEpicByID(int id);

    void deleteSubtaskByID(int id);

    void updateEpicStatus(Epic epic);

    List<Task> getHistory();
}
