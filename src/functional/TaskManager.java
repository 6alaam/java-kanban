package functional;
import Resourse.Task;
import java.util.*;


public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private int nextID = 1;

    public int getNextID() {
        return nextID++;
    }

    public Task addTask(Task task) {
        task.setId(getNextID());
        tasks.put(task.getId(), task);
        return task;
    }

    public Task updateTask(Task task) {
        Integer taskID = task.getId();
        if (!tasks.containsKey(taskID)) {
            return null;
        }
        tasks.replace(taskID, task);
        return task;
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public Task removeIDTask(Integer id) {
        return tasks.remove(id);
    }
    public Task getTaskByID(int id) {
        return tasks.get(id);
    }
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

}
