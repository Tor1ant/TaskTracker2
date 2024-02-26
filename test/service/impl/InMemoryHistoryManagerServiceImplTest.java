package service.impl;

import java.util.Collections;
import java.util.List;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Тестирование класса InMemoryHistoryManagerServiceImpl")
class InMemoryHistoryManagerServiceImplTest {

    private Task task1;
    private Task task2;
    private Epic epic;
    private Subtask subtask;
    private InMemoryHistoryManagerServiceImpl managerService;

    @BeforeEach
    void setUp() {
        task1 = new Task("Закончить выполнение ТЗ", "Желательно сегодня");
        task2 = new Task("Поиграть с котом", "давно с ним не играли");
        epic = new Epic("Сходить в магазин", "сегодня");
        subtask = new Subtask("Купить молоко", "Простоквашино", epic.getId());
        managerService = new InMemoryHistoryManagerServiceImpl();
        task1.setId(1);
        task2.setId(2);
        epic.setId(3);
        subtask.setId(4);
        epic.getSubTasksIds().add(subtask.getId());
    }

    @Test
    @DisplayName("Тестирование получения истории просмотров")
    void testGetHistorySuccessful() {
        managerService.add(task1);
        managerService.add(epic);
        managerService.add(subtask);
        List<Task> history = managerService.getHistory();
        Assertions.assertEquals(List.of(subtask, epic, task1), history);
    }

    @Test
    @DisplayName("Тестирование удаления первой задачи из истории просмотров")
    void testFirstTaskRemoveSuccessful() {
        managerService.add(task1);
        managerService.add(epic);
        managerService.add(subtask);
        managerService.remove(task1.getId());
        Assertions.assertEquals(List.of(subtask, epic), managerService.getHistory());
    }

    @Test
    @DisplayName("Тестирование удаления второй задачи из истории просмотров")
    void testSecondTaskRemoveSuccessful() {
        managerService.add(task1);
        managerService.add(task2);
        managerService.add(epic);
        managerService.add(subtask);
        managerService.remove(task2.getId());
        Assertions.assertEquals(List.of(subtask, epic, task1), managerService.getHistory());
    }

    @Test
    @DisplayName("Тестирование удаления единственной задачи из истории просмотров")
    void testSingleTaskRemoveSuccessful() {
        managerService.add(task1);
        managerService.remove(task1.getId());
        Assertions.assertEquals(Collections.emptyList(), managerService.getHistory());
    }

    @Test
    @DisplayName("Тестирование удаления последней задачи из истории просмотров")
    void testLastTaskRemoveSuccessful() {
        managerService.add(task1);
        managerService.add(epic);
        managerService.add(subtask);
        managerService.remove(subtask.getId());
        Assertions.assertEquals(List.of(epic, task1), managerService.getHistory());
    }
}