package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    InMemoryHistoryManager historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();

    private int nextId = 1;

    protected final SortedSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo))
                    .thenComparing(Task::getId)
    );


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
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            for (Subtask subtask : epic.getSubtaskList()) {
                historyManager.remove(subtask.getId());
            }
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Task addTask(Task task) throws TaskIntersectionException {
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
    public Subtask addSubtask(Subtask subtask) throws TaskIntersectionException {
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
    public Task updateTask(Task task) throws TaskIntersectionException {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws TaskIntersectionException {
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
    public Task deleteTaskByID(int id) {
        historyManager.remove(id);
        return tasks.remove(id);
    }

    @Override
    public Epic deleteEpicByID(int id) {
        Epic epic = epics.remove(id);
        historyManager.remove(id);
        for (Subtask subtaskId : epic.getSubtaskList()) {
            subtasks.remove(subtaskId.getId());
        }
        return epics.remove(id);
    }

    @Override
    public Subtask deleteSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.remove(id);
        if (subtask == null) {
            return null;
        }

        int epicID = subtask.getEpicID();
        Subtask deletedSubtask = subtasks.remove(id);
        Epic epic = epics.get(epicID);
        ArrayList<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(subtask);
        epic.setSubtaskList(subtaskList);
        updateEpicStatus(epic);
        return deletedSubtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

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


    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean tasksIntersect(Task t1, Task t2) {
        if (t1.getStartTime() == null || t1.getDuration() == null ||
                t2.getStartTime() == null || t2.getDuration() == null) {
            return false;
        }
        LocalDateTime start1 = t1.getStartTime();
        LocalDateTime end1 = t1.getEndTime();
        LocalDateTime start2 = t2.getStartTime();
        LocalDateTime end2 = t2.getEndTime();
        return start1.isBefore(end2) && start2.isBefore(end1);
    }


    public void checkIntersection(Task task) throws TaskIntersectionException {
        if (task.getStartTime() == null || task.getDuration() == null) {
            return;
        }
        boolean intersect = getPrioritizedTasks().stream()
                .filter(t -> t.getId() != task.getId())
                .anyMatch(t -> tasksIntersect(task, t));
        if (intersect) {
            throw new TaskIntersectionException("Task time intersects with another task: " + task);
        }
    }
}






