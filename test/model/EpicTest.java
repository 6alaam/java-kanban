package test.model;
import model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    public void EpicsWithEqualIdShouldBeEqual() {
        Epic epic1 = new Epic(10, "Сделать ремонт", "Уложиться в 2 миллиона", Status.NEW);
        Epic epic2 = new Epic(10, "Подготовиться к собеседованию", "1 июля в 11:00",
                Status.IN_PROGRESS);
        assertEquals(epic1, epic2,
                "Ошибка! Наследники класса Task должны быть равны друг другу, если равен их id;");
    }
}
