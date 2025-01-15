package test.model;
import model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void tasksWithEqualIdShouldBeEqual() {
        Task task1 = new Task(10, "Купить хлеб", "В Магните", Status.NEW);
        Task task2 = new Task(10, "Купить молоко", "В Пятерочке", Status.DONE);
        assertEquals(task1, task2,
                "Ошибка! Экземпляры класса Task должны быть равны друг другу, если равен их id;");
    }
}
