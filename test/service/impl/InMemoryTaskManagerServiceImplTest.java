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
    @DisplayName("Проверка того, что экземпляры класса Task с равным id равны друг другу")
    void test_tasks_with_same_id_is_equals() {
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);
    }

    @Test
    @DisplayName("Проверка того, что экземпляры класса SubTask с равным id равны друг другу")
    void test_subTasks_with_same_id_is_equals() {
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);
    }

    @Test
    @DisplayName("Проверка того, что экземпляры класса Epic с равным id равны друг другу")
    void test_epics_with_same_id_is_equals() {
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);
    }

    @Test
    @DisplayName("Проверка того, что объект Epic нельзя добавить в самого себя в виде подзадачи")
    void test_epic_cant_add_himself_like_subtask() {
        Epic createdEpic = inMemoryTaskManagerService.createEpic(epic);
        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", createdEpic.getId());
        Subtask subtask2ForEpic = new Subtask("Купить шоколадку", "милка", createdEpic.getId());
        inMemoryTaskManagerService.createSubTask(subtask1ForEpic);
        inMemoryTaskManagerService.createSubTask(subtask2ForEpic);
        Subtask unexpectedSubtask = new Subtask(createdEpic.getTitle(), createdEpic.getDescription(),
                createdEpic.getId());
        unexpectedSubtask.setId(createdEpic.getId());
        Subtask expectedResult = inMemoryTaskManagerService.updateSubTask(unexpectedSubtask);

        Assertions.assertNull(expectedResult);
    }

    @Test
    @DisplayName("Проверка того, что объект Subtask нельзя сделать своим же эпиком")
    void test_subtask_cant_add_himself_like_his_epic() {
        Epic createdEpic = inMemoryTaskManagerService.createEpic(epic);
        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", createdEpic.getId());
        Subtask subtask2ForEpic = new Subtask("Купить шоколадку", "милка", createdEpic.getId());
        inMemoryTaskManagerService.createSubTask(subtask1ForEpic);
        inMemoryTaskManagerService.createSubTask(subtask2ForEpic);
        Epic expectedResult = inMemoryTaskManagerService.updateEpic(new Epic(subtask1ForEpic.getId(),
                subtask1ForEpic.getTitle(), subtask1ForEpic.getDescription()));

        Assertions.assertNull(expectedResult);
    }

    @Test
    @DisplayName("Проверка того, что inMemoryTaskManagerService сохраняет задачи разного типа и может их достать по id")
    void test_inMemoryTaskManagerService_can_save_and_get_tasks_by_id() {
        inMemoryTaskManagerService.createTask(task1);
        inMemoryTaskManagerService.createEpic(epic);
        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", epic.getId());
        Subtask subTask = inMemoryTaskManagerService.createSubTask(subtask1ForEpic);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(task1, inMemoryTaskManagerService.getTaskById(task1.getId()));
            Assertions.assertEquals(epic, inMemoryTaskManagerService.getEpicById(epic.getId()));
            Assertions.assertEquals(subTask, inMemoryTaskManagerService.getSubTaskById(subTask.getId()));
        });
    }

    @Test
    @DisplayName("Проверка того, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера")
    void test_setting_id_for_task_dont_conflict_with_generated() {
        inMemoryTaskManagerService.createTask(task1);
        inMemoryTaskManagerService.createEpic(epic);
        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", epic.getId());
        inMemoryTaskManagerService.createSubTask(subtask1ForEpic);

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
    void test_remove_subtask() {
        inMemoryTaskManagerService.createEpic(epic);

        Assertions.assertEquals(epic.getStatus(), TaskStatus.NEW);
        Assertions.assertTrue(epic.getSubTasksIds().isEmpty());

        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", epic.getId());
        subtask1ForEpic.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManagerService.createSubTask(subtask1ForEpic);

        Assertions.assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS);
        Assertions.assertTrue(epic.getSubTasksIds().contains(2));

        inMemoryTaskManagerService.removeSubTask(2);

        Assertions.assertEquals(epic.getStatus(), TaskStatus.NEW);
        Assertions.assertTrue(epic.getSubTasksIds().isEmpty());
    }

    @Test
    @DisplayName("Проверка удаления эпика по id")
    void test_remove_epic_by_id() {
        inMemoryTaskManagerService.createEpic(epic);

        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", epic.getId());
        subtask1ForEpic.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManagerService.createSubTask(subtask1ForEpic);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(inMemoryTaskManagerService.getEpics().getFirst(), epic);
            Assertions.assertEquals(inMemoryTaskManagerService.getSubTasks().getFirst(), subtask1ForEpic);
        });

        inMemoryTaskManagerService.removeEpicById(epic.getId());

        Assertions.assertAll(() -> {
            Assertions.assertTrue(inMemoryTaskManagerService.getEpics().isEmpty());
            Assertions.assertTrue(inMemoryTaskManagerService.getSubTasks().isEmpty());
        });
    }

    @Test
    @DisplayName("Проверка удаления всех подзадач")
    void test_remove_all_subtasks() {
        inMemoryTaskManagerService.createEpic(epic);

        Subtask subtask1ForEpic = new Subtask("Купить молоко", "Простоквашино", epic.getId());
        subtask1ForEpic.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManagerService.createSubTask(subtask1ForEpic);

        Assertions.assertEquals(inMemoryTaskManagerService.getSubTasks().getFirst(), subtask1ForEpic);
        Assertions.assertEquals(2, inMemoryTaskManagerService.getEpics().getFirst().getSubTasksIds().getFirst());

        inMemoryTaskManagerService.removeAllSubTasks();

        Assertions.assertTrue(inMemoryTaskManagerService.getSubTasks().isEmpty());
        Assertions.assertTrue(inMemoryTaskManagerService.getEpics().getFirst().getSubTasksIds().isEmpty());
    }


}