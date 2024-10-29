
// весь класс мейн исключительно для тестов

import Resourse.Task;
import functional.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task washFloor = new Task("Название 1", "описание 1", taskManager.getNextID());
        Task washFloorCreated = taskManager.addTask(washFloor);
        System.out.println(washFloorCreated);
        Task washFloor1 = new Task("название 2", "описание 2", taskManager.getNextID());
        taskManager.addTask(washFloor1);
        System.out.println(taskManager.getTaskByID(4));
        System.out.println("дальше печатаем всю мапу");
        System.out.println(taskManager.getTasks());
        taskManager.updateTask(taskManager.getTaskByID(4)); // ????
        System.out.println(taskManager.getTaskByID(4));
        taskManager.removeIDTask(2);
        System.out.println("а тут удаляем по айди");
        System.out.println(taskManager.getTasks());
        System.out.println("а дальше чистим всю мапу");
        taskManager.deleteAllTask();
        System.out.println(taskManager.getTasks());


    }
}