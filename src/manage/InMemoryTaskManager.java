package manage;

import resources.Epic;
import resources.Status;
import resources.Subtask;
import resources.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Manager.getDefaultHistory();
    private int nextId = 1;


    @Override
    public int getNextId() {
        return nextId++;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtaskList();
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int Id) {
        return epics.get(Id);
    }

    @Override
    public Subtask getSubtaskById(int Id) {
        return subtasks.get(Id);
    }

    @Override
    public Task addTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }


    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(getNextId());
        Epic epic = epics.get(subtask.getEpicID());
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Task updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Integer subtaskId = subtask.getId();
        Integer epicId = subtask.getEpicID();
        Subtask oldSubtask = subtasks.get(subtaskId);
        subtasks.replace(subtaskId, subtask);
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(oldSubtask);
        subtaskList.add(subtask);
        epic.setSubtaskList(subtaskList);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public void deleteTaskByID(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicByID(int id) {
        Epic epic = epics.remove(id);
        for (Subtask subtaskId : epic.getSubtaskList()) {
            subtasks.remove(subtaskId.getId());
        }
    }

    @Override
    public void deleteSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        int epicID = subtask.getEpicID();
        subtasks.remove(id);
        Epic epic = epics.get(epicID);
        ArrayList<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(subtask);
        epic.setSubtaskList(subtaskList);
        updateEpicStatus(epic);
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        int allIsDone = 0;
        int allIsInNew = 0;
        ArrayList<Subtask> list = epic.getSubtaskList();
        for (Subtask subtask : list) {
            if (subtask.getStatus() == Status.DONE) {
                allIsDone++;
            }
            if (subtask.getStatus() == Status.NEW) {
                allIsInNew++;
            }
        }
        if (allIsDone == list.size()) {
            epic.setStatus(Status.DONE);
        } else if (allIsInNew == list.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}