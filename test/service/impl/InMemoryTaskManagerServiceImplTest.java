package service.impl;

import enums.TaskStatus;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Тестирование класса InMemoryTaskManagerServiceImpl")
class InMemoryTaskManagerServiceImplTest {

    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;
    private InMemoryTaskManagerServiceImpl inMemoryTaskManagerService;


    @BeforeEach
    void setUp() {
        inMemoryTaskManagerService = new InMemoryTaskManagerServiceImpl();
        task1 = new Task("Закончить выполнение ТЗ", "Желательно сегодня");
        task2 = new Task("Поиграть с котом", "давно с ним не играли");
        epic1 = new Epic("Сходить в магазин", "сегодня");
        epic2 = new Epic("Навести порядок в квартире", "завтра");
        subtask1 = new Subtask("Купить молоко", "Простоквашино", epic1.getId());
        subtask2 = new Subtask("Купить мясо", "Свинину", epic1.getId());
    }

    @Test
    @DisplayName("Проверка того, что экземпляры класса Task с равным id равны друг другу")
    void testTasksWithSameIdIsEquals() {
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);
    }

    @Test
    @DisplayName("Проверка того, что экземпляры класса SubTask с равным id равны друг другу")
    void testSubTasksWithSameIdIsEquals() {
        subtask1.setId(1);
        subtask2.setId(1);
        Assertions.assertEquals(task1, task2);
    }

    @Test
    @DisplayName("Проверка того, что экземпляры класса Epic с равным id равны друг другу")
    void testEpicsWithSameIdIsEquals() {
        epic1.setId(1);
        epic2.setId(1);
        Assertions.assertEquals(task1, task2);
    }

    @Test
    @DisplayName("Проверка того, что объект Epic нельзя добавить в самого себя в виде подзадачи")
    void testEpicCantAddHimselfLikeSubtask() {
        Epic createdEpic = inMemoryTaskManagerService.createEpic(epic1);
        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", createdEpic.getId());
        Subtask subtask2ForEpic = new Subtask("Купить шоколадку", "милка", createdEpic.getId());
        inMemoryTaskManagerService.createSubtask(subtask1ForEpic);
        inMemoryTaskManagerService.createSubtask(subtask2ForEpic);
        Subtask unexpectedSubtask = new Subtask(createdEpic.getTitle(), createdEpic.getDescription(),
                createdEpic.getId());
        unexpectedSubtask.setId(createdEpic.getId());
        Subtask expectedResult = inMemoryTaskManagerService.updateSubTask(unexpectedSubtask);

        Assertions.assertNull(expectedResult);
    }

    @Test
    @DisplayName("Проверка того, что объект Subtask нельзя сделать своим же эпиком")
    void testSubtaskCantAddHimselfLikeHisEpic() {
        Epic createdEpic = inMemoryTaskManagerService.createEpic(epic1);
        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", createdEpic.getId());
        Subtask subtask2ForEpic = new Subtask("Купить шоколадку", "милка", createdEpic.getId());
        inMemoryTaskManagerService.createSubtask(subtask1ForEpic);
        inMemoryTaskManagerService.createSubtask(subtask2ForEpic);
        Epic expectedResult = inMemoryTaskManagerService.updateEpic(new Epic(subtask1ForEpic.getId(),
                subtask1ForEpic.getTitle(), subtask1ForEpic.getDescription()));

        Assertions.assertNull(expectedResult);
    }

    @Test
    @DisplayName("Проверка того, что inMemoryTaskManagerService сохраняет задачи разного типа и может их достать по id")
    void testInMemoryTaskManagerServiceCanSaveAndGetTasksById() {
        inMemoryTaskManagerService.createTask(task1);
        inMemoryTaskManagerService.createEpic(epic1);
        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", epic1.getId());
        Subtask subTask = inMemoryTaskManagerService.createSubtask(subtask1ForEpic);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(task1, inMemoryTaskManagerService.getTaskById(task1.getId()));
            Assertions.assertEquals(epic1, inMemoryTaskManagerService.getEpicById(epic1.getId()));
            Assertions.assertEquals(subTask, inMemoryTaskManagerService.getSubTaskById(subTask.getId()));
        });
    }

    @Test
    @DisplayName("Проверка того, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера")
    void testSettingIdForTaskDontConflictWithGenerated() {
        inMemoryTaskManagerService.createTask(task1);
        inMemoryTaskManagerService.createEpic(epic1);
        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", epic1.getId());
        inMemoryTaskManagerService.createSubtask(subtask1ForEpic);

        Assertions.assertAll(() -> {
            Assertions.assertNull(inMemoryTaskManagerService.updateTask(new Task(10, "test", "desc",
                    TaskStatus.NEW)));
            Assertions.assertNull(inMemoryTaskManagerService.updateEpic(new Epic(10, "test", "desc")));
            Assertions.assertNull(inMemoryTaskManagerService.updateSubTask(new Subtask(10, "test",
                    "desc", TaskStatus.NEW, 1)));
        });
    }

    @Test
    @DisplayName("Проверка удаления сабтаски у эпика")
    void testRemoveSubtask() {
        inMemoryTaskManagerService.createEpic(epic1);

        Assertions.assertEquals(TaskStatus.NEW, epic1.getStatus());
        Assertions.assertTrue(epic1.getSubTasksIds().isEmpty());

        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", epic1.getId());
        subtask1ForEpic.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManagerService.createSubtask(subtask1ForEpic);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
        Assertions.assertTrue(epic1.getSubTasksIds().contains(2));

        inMemoryTaskManagerService.removeSubTask(2);

        Assertions.assertEquals(TaskStatus.NEW, epic1.getStatus());
        Assertions.assertTrue(epic1.getSubTasksIds().isEmpty());
    }

    @Test
    @DisplayName("Проверка удаления эпика по id")
    void testRemoveEpicById() {
        inMemoryTaskManagerService.createEpic(epic1);

        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", epic1.getId());
        subtask1ForEpic.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManagerService.createSubtask(subtask1ForEpic);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(inMemoryTaskManagerService.getEpics().getFirst(), epic1);
            Assertions.assertEquals(inMemoryTaskManagerService.getSubTasks().getFirst(), subtask1ForEpic);
        });

        inMemoryTaskManagerService.removeEpicById(epic1.getId());

        Assertions.assertAll(() -> {
            Assertions.assertTrue(inMemoryTaskManagerService.getEpics().isEmpty());
            Assertions.assertTrue(inMemoryTaskManagerService.getSubTasks().isEmpty());
        });
    }

    @Test
    @DisplayName("Проверка удаления всех подзадач")
    void testRemoveAllSubtasks() {
        inMemoryTaskManagerService.createEpic(epic1);

        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", epic1.getId());
        subtask1ForEpic.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManagerService.createSubtask(subtask1ForEpic);

        Assertions.assertEquals(inMemoryTaskManagerService.getSubTasks().getFirst(), subtask1ForEpic);
        Assertions.assertEquals(2, inMemoryTaskManagerService.getEpics().getFirst().getSubTasksIds().getFirst());

        inMemoryTaskManagerService.removeAllSubTasks();

        Assertions.assertTrue(inMemoryTaskManagerService.getSubTasks().isEmpty());
        Assertions.assertTrue(inMemoryTaskManagerService.getEpics().getFirst().getSubTasksIds().isEmpty());
    }
}