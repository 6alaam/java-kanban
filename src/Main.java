import manage.TaskManager;
import resources.*;

// здесь только тестировал
public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Task task = new Task(1, "task1", "Taaaaask1", Status.NEW);
        System.out.println(task);


    }
}