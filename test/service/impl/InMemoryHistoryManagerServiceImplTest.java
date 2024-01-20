package service.impl;

import java.util.List;
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
    private InMemoryTaskManagerServiceImpl inMemoryTaskManagerService;

    @BeforeEach
    void setUp() {
        inMemoryTaskManagerService = new InMemoryTaskManagerServiceImpl();
        task1 = new Task("Закончить выполнение ТЗ", "Желательно сегодня");
        task2 = new Task("Поиграть с котом", "давно с ним не играли");
        epic = new Epic("Сходить в магазин", "сегодня");

    }

    @Test
    @DisplayName("Тестирование получения истории просмотров")
    void test_get_history_successful() {
        Task task = inMemoryTaskManagerService.createTask(task1);
        Epic epic1 = inMemoryTaskManagerService.createEpic(epic);
        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", epic1.getId());
        Subtask subTask = inMemoryTaskManagerService.createSubTask(subtask1ForEpic);
        inMemoryTaskManagerService.getTaskById(task.getId());
        inMemoryTaskManagerService.getEpicById(epic1.getId());
        inMemoryTaskManagerService.getSubTaskById(subTask.getId());

        task2.setId(task.getId());
        inMemoryTaskManagerService.updateTask(task2);

        Assertions.assertEquals(List.of(task, epic1, subTask), inMemoryTaskManagerService.getHistory());
    }

    @Test
    @DisplayName("Тестирование добавления в историю просмотров 11 элемента")
    void test_add_when_browsing_history_contains_10_elements() {
        IntStream.range(0, 10).forEach(i -> {
            Task task = inMemoryTaskManagerService.createTask(new Task(task1.getTitle(), task1.getDescription()));
            inMemoryTaskManagerService.getTaskById(task.getId());
        });
        Task task = inMemoryTaskManagerService.createTask(task2);
        inMemoryTaskManagerService.getTaskById(task.getId());

        Assertions.assertEquals(2, inMemoryTaskManagerService.getHistory().getFirst().getId());
    }
}