package task;

import org.junit.jupiter.api.Test;
import enums.Status;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    public void SubtasksWithEqualIdShouldBeEqual() {
        Subtask subtask1 = new Subtask(10, "Купить хлеб", "В Дикси у дома", Status.NEW, 5);
        Subtask subtask2 = new Subtask(10, "Купить молоко", "В Пятерочке", Status.DONE, 5);
        assertEquals(subtask1, subtask2,
                "Ошибка! Наследники класса Task должны быть равны друг другу, если равен их id;");
    }
}