package service.impl;

import enums.TaskStatus;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
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
    private InMemoryHistoryManagerServiceImpl managerService;

    @BeforeEach
    void setUp() {
        task1 = new Task("Закончить выполнение ТЗ", "Желательно сегодня");
        task2 = new Task("Поиграть с котом", "давно с ним не играли");
        epic = new Epic("Сходить в магазин", "сегодня");
        managerService = new InMemoryHistoryManagerServiceImpl();
        task1.setId(1);
        task2.setId(2);
        epic.setId(3);
    }

    @Test
    @DisplayName("Тестирование получения истории просмотров")
    void testGetHistorySuccessful() {
        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", epic.getId());
        subtask1ForEpic.setId(4);
        managerService.add(task1);
        managerService.add(epic);
        managerService.add(subtask1ForEpic);

        Assertions.assertEquals(List.of(task1, epic, subtask1ForEpic), managerService.getHistory());
    }

    @Test
    @DisplayName("Тестирование добавления в историю просмотров 11 элемента")
    void testAddWhenBrowsingHistoryContains10Elements() {
        AtomicInteger counter = new AtomicInteger(1);
        IntStream.range(0, 10).forEach(i -> {
            Task task = new Task(counter.getAndIncrement(), task1.getTitle(), task1.getDescription(), TaskStatus.NEW);
            managerService.add(task);
        });
        managerService.add(task2);

        Assertions.assertEquals(2, managerService.getHistory().getFirst().getId());
    }
}