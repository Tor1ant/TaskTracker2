package service.impl;

import enums.TaskStatus;
import exception.ManagerSaveException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.TaskManagerService;

abstract class TaskManagerTest<T extends TaskManagerService> {

    protected T taskManagerService;

    private final Task task1 = new Task("Закончить выполнение ТЗ", "Желательно сегодня", Duration.ofDays(5),
            LocalDateTime.now());
    private final Task task2 = new Task("Поиграть с котом", "давно с ним не играли", Duration.ofDays(2),
            LocalDateTime.now().plusDays(6));
    private final Epic epic1 = new Epic("Сходить в магазин", "сегодня");
    private final Subtask subtask1 = new Subtask("Купить молоко", "Простоквашино", 1, Duration.ofDays(1),
            LocalDateTime.now().plusDays(9));
    private final Subtask subtask2 = new Subtask("Купить мясо", "Свинину", 1, Duration.ofDays(10),
            LocalDateTime.now().plusDays(11));


    @Test
    @DisplayName("Проверка получения списка задач")
    void getTasks() {
        taskManagerService.createTask(task1);
        taskManagerService.createTask(task2);
        Assertions.assertIterableEquals(List.of(task1, task2), taskManagerService.getTasks());
    }

    @Test
    @DisplayName("Проверка удаления всех задач")
    void removeAllTasks() {
        taskManagerService.createTask(task1);
        taskManagerService.createTask(task2);
        taskManagerService.removeAllTasks();
        Assertions.assertTrue(taskManagerService.getTasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка получения задачи по id")
    void getTaskById() {
        taskManagerService.createTask(task1);
        Assertions.assertEquals(task1, taskManagerService.getTaskById(task1.getId()));
    }

    @Test
    @DisplayName("Проверка создания задачи")
    void createTask() {
        taskManagerService.createTask(task1);
        Assertions.assertEquals(task1, taskManagerService.getTasks().getFirst());
    }

    @Test
    @DisplayName("Проверка обновления задачи")
    void updateTask() {
        String newDescription = "Новое описание задачи";
        taskManagerService.createTask(task1);
        taskManagerService.updateTask(new Task(task1.getId(), task1.getTitle(), newDescription, task1.getStatus(),
                task1.getDuration(), task1.getStartTime()));
        Assertions.assertEquals(newDescription, taskManagerService.getTaskById(task1.getId()).getDescription());
    }

    @Test
    @DisplayName("Проверка удаления задачи")
    void removeTask() {
        taskManagerService.createTask(task1);
        taskManagerService.removeTask(task1.getId());
        Assertions.assertTrue(taskManagerService.getTasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка получения списка подзадач")
    void getSubTasks() {
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        taskManagerService.createSubtask(subtask2);
        Assertions.assertIterableEquals(List.of(subtask1, subtask2), taskManagerService.getEpicSubTasks(epic1.getId()));
    }

    @Test
    @DisplayName("Проверка удаления всех подзадач")
    void removeAllSubTasks() {
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        taskManagerService.createSubtask(subtask2);
        taskManagerService.removeAllSubTasks();
        Assertions.assertTrue(taskManagerService.getSubTasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка получения подзадачи по id")
    void getSubTaskById() {
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        Assertions.assertEquals(subtask1, taskManagerService.getSubTaskById(subtask1.getId()));
    }

    @Test
    @DisplayName("Проверка создания подзадачи")
    void createSubtask() {
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        Assertions.assertEquals(subtask1, taskManagerService.getEpicSubTasks(epic1.getId()).getFirst());
    }

    @Test
    @DisplayName("Проверка обновления подзадачи")
    void updateSubTask() {
        String newDescription = "Новое описание подзадачи";
        taskManagerService.createEpic(epic1);
        Subtask createdSubTask = taskManagerService.createSubtask(subtask1);
        Subtask updateSubTask = new Subtask(createdSubTask.getId(), "Купить молоко", newDescription,
                createdSubTask.getStatus(), 1, Duration.ofDays(1), LocalDateTime.now());
        taskManagerService.updateSubTask(updateSubTask);
        Assertions.assertEquals(newDescription, taskManagerService.getSubTaskById(subtask1.getId()).getDescription());
    }

    @Test
    @DisplayName("Проверка удаления подзадачи")
    void removeSubTask() {
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        taskManagerService.removeSubTask(subtask1.getId());
        Assertions.assertTrue(taskManagerService.getEpicSubTasks(epic1.getId()).isEmpty());
    }

    @Test
    @DisplayName("Проверка получения списка эпиков")
    void getEpics() {
        taskManagerService.createEpic(epic1);
        Assertions.assertIterableEquals(List.of(epic1), taskManagerService.getEpics());
    }

    @Test
    @DisplayName("Проверка удаления всех эпиков")
    void removeAllEpics() {
        taskManagerService.createEpic(epic1);
        taskManagerService.removeAllEpics();
        Assertions.assertTrue(taskManagerService.getEpics().isEmpty());
    }

    @Test
    @DisplayName("Проверка получения эпика по id")
    void getEpicById() {
        taskManagerService.createEpic(epic1);
        Assertions.assertEquals(epic1, taskManagerService.getEpicById(epic1.getId()));
    }

    @Test
    @DisplayName("Проверка создания эпика")
    void createEpic() {
        taskManagerService.createEpic(epic1);
        Assertions.assertEquals(epic1, taskManagerService.getEpics().getFirst());
    }

    @Test
    @DisplayName("Проверка обновления эпика")
    void updateEpic() {
        String newDescription = "Новое описание эпика";
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        taskManagerService.updateEpic(new Epic(epic1.getId(), epic1.getTitle(), newDescription));
        Assertions.assertEquals(newDescription, taskManagerService.getEpicById(epic1.getId()).getDescription());
        Assertions.assertFalse(taskManagerService.getEpicSubTasks(epic1.getId()).isEmpty());
    }

    @Test
    @DisplayName("Проверка удаления эпика")
    void removeEpicById() {
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        taskManagerService.removeEpicById(epic1.getId());
        Assertions.assertTrue(taskManagerService.getEpics().isEmpty());
        Assertions.assertTrue(taskManagerService.getSubTasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка получения списка подзадач эпика")
    void getEpicSubTasks() {
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        Assertions.assertIterableEquals(List.of(subtask1), taskManagerService.getEpicSubTasks(epic1.getId()));
    }

    @Test
    @DisplayName("Проверка получения истории задач")
    void getHistory() {
        task1.setStartTime(subtask2.getEndTime().plusDays(1));
        task2.setStartTime(task1.getEndTime().plusDays(4));
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        taskManagerService.createTask(task1);
        taskManagerService.createTask(task2);

        taskManagerService.getTaskById(task1.getId());
        taskManagerService.getTaskById(task2.getId());

        Assertions.assertIterableEquals(List.of(task2, task1), taskManagerService.getHistory());
    }

    @Test
    @DisplayName("Проверка получения списка приоритетных задач")
    void getPrioritizedTasks() {
        taskManagerService.createTask(task1);
        taskManagerService.createTask(task2);
        Assertions.assertIterableEquals(List.of(task1, task2), taskManagerService.getPrioritizedTasks());
    }

    @Test
    @DisplayName("Проверка расчёта статуса Epic -> все подзадачи NEW")
    void calculateEpicStatusAllSubTasksNew() {
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        taskManagerService.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.NEW, taskManagerService.getEpicById(epic1.getId()).getStatus());
    }

    @Test
    @DisplayName("Проверка расчёта статуса Epic -> все подзадачи Done")
    void calculateEpicStatusAllSubTasksDone() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        taskManagerService.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.DONE, taskManagerService.getEpicById(epic1.getId()).getStatus());
    }

    @Test
    @DisplayName("Проверка расчёта статуса Epic -> подзадачи со статусами NEW и DONE")
    void calculateEpicStatusNewAndDoneSubTasks() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.NEW);
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        taskManagerService.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManagerService.getEpicById(epic1.getId()).getStatus());
    }

    @Test
    @DisplayName("Проверка расчёта статуса Epic -> подзадачи со статусами IN_PROGRESS")
    void calculateEpicStatusInProgressSubTasks() {
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManagerService.createEpic(epic1);
        taskManagerService.createSubtask(subtask1);
        taskManagerService.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManagerService.getEpicById(epic1.getId()).getStatus());
    }

    @Test
    @DisplayName("Проверка невозможности создания задачи, которая пересекается существующей")
    void createTaskOverlapping() {
        task2.setStartTime(LocalDateTime.now().plusHours(5));
        taskManagerService.createTask(task1);
        Assertions.assertThrows(ManagerSaveException.class, () -> taskManagerService.createTask(task2));
    }
}
