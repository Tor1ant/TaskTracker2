package service.impl;

import enumerated.TaskStatus;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Тестирование FileBackedTaskManager")
class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;
    private FileBackedTaskManager taskManagerForSave;
    private FileBackedTaskManager taskManagerForRead;

    @BeforeEach
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void setUp() throws IOException {
        String tempFilesDirectory = "test/resources";
        try (Stream<Path> files = Files.walk(Path.of(tempFilesDirectory))) {
            files.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

        tempFile = File.createTempFile("temp", "csv", new File(tempFilesDirectory));
        taskManagerForSave = new FileBackedTaskManager(tempFile.getCanonicalPath());
        super.taskManagerService = taskManagerForSave;
        task1 = new Task("Закончить выполнение ТЗ", "Желательно сегодня", Duration.ofDays(1), LocalDateTime.now());
        task2 = new Task("Поиграть с котом", "давно с ним не играли", Duration.ofDays(1),
                LocalDateTime.now().plusDays(2));
        epic1 = new Epic("Сходить в магазин", "сегодня");
        epic2 = new Epic("Навести порядок в квартире", "завтра");
        subtask1 = new Subtask("Купить молоко", "Простоквашино", epic1.getId(), Duration.ofDays(1),
                LocalDateTime.now().plusDays(4));
        subtask2 = new Subtask("Купить мясо", "Свинину", epic1.getId(), Duration.ofDays(1),
                LocalDateTime.now().plusDays(6));
    }


    @Test
    @DisplayName("Проверка сохранения и загрузки пустого файла")
    void saveAndLoadEmptyFile() {
        taskManagerForRead = FileBackedTaskManager.loadFromFile(tempFile.toString());
        Assertions.assertAll(() -> {
            Assertions.assertEquals(0, taskManagerForSave.getTasks().size());
            Assertions.assertEquals(0, taskManagerForSave.getEpics().size());
            Assertions.assertEquals(0, taskManagerForSave.getSubTasks().size());
        });
    }

    @Test
    @DisplayName("Проверка создания и сохранения задач")
    void createTasksTest() {
        taskManagerForSave.createTask(task1);
        taskManagerForSave.createTask(task2);
        taskManagerForSave.createEpic(epic1);
        taskManagerForSave.createEpic(epic2);
        subtask1.setEpicId(epic1.getId());
        subtask2.setEpicId(epic1.getId());
        taskManagerForSave.createSubtask(subtask1);
        taskManagerForSave.createSubtask(subtask2);

        taskManagerForRead = FileBackedTaskManager.loadFromFile(tempFile.toString());

        Assertions.assertAll(
                () -> {
                    Assertions.assertEquals(taskManagerForSave.getTasks(), taskManagerForRead.getTasks());
                    Assertions.assertEquals(taskManagerForSave.getEpics(), taskManagerForRead.getEpics());
                    Assertions.assertEquals(taskManagerForSave.getSubTasks(), taskManagerForRead.getSubTasks());
                }
        );
    }

    @Test
    @DisplayName("Проверка получения задач по id и корректного маппинга задач в истории")
    void getTaskByIdTest() {
        taskManagerForSave.createTask(task1);
        taskManagerForSave.createTask(task2);
        taskManagerForSave.createEpic(epic1);
        taskManagerForSave.createEpic(epic2);

        subtask1.setEpicId(epic1.getId());
        subtask2.setEpicId(epic1.getId());
        taskManagerForSave.createSubtask(subtask1);
        taskManagerForSave.createSubtask(subtask2);

        taskManagerForSave.getTaskById(task1.getId());
        taskManagerForSave.getEpicById(epic1.getId());

        taskManagerForRead = FileBackedTaskManager.loadFromFile(tempFile.toString());

        Assertions.assertAll(() -> {
            Assertions.assertEquals(taskManagerForSave.getHistory(), taskManagerForRead.getHistory());
            Assertions.assertEquals(taskManagerForSave.getTasks(), taskManagerForRead.getTasks());
            Assertions.assertEquals(taskManagerForSave.getEpics(), taskManagerForRead.getEpics());
            Assertions.assertEquals(taskManagerForSave.getSubTasks(), taskManagerForRead.getSubTasks());
        });
    }

    @Test
    @DisplayName("Проверка обновления задачи")
    void updateTaskTest() {
        taskManagerForSave.createTask(task1);
        taskManagerForSave.createTask(task2);

        Task updatedTask = new Task(task1.getId(), "Закончить выполнение ТЗ", "Желательно сегодня",
                TaskStatus.IN_PROGRESS, Duration.ofDays(5), task1.getStartTime());

        taskManagerForSave.updateTask(updatedTask);

        taskManagerForRead = FileBackedTaskManager.loadFromFile(tempFile.toString());

        Assertions.assertEquals(taskManagerForRead.getTaskById(task1.getId()).getTitle(), updatedTask.getTitle());
    }

    @Test
    @DisplayName("Проверка удаления задачи")
    void removeTaskTest() {
        taskManagerForSave.createEpic(epic1);
        taskManagerForSave.createEpic(epic2);

        subtask1.setEpicId(epic1.getId());
        subtask2.setEpicId(epic1.getId());
        taskManagerForSave.createSubtask(subtask1);
        taskManagerForSave.createSubtask(subtask2);

        taskManagerForSave.removeSubTask(subtask1.getId());

        taskManagerForRead = FileBackedTaskManager.loadFromFile(tempFile.toString());

        Assertions.assertEquals(taskManagerForSave.getSubTasks(), taskManagerForRead.getSubTasks());
    }

    @Test
    @DisplayName("Проверка получения приоритетных задач после восстановления из файла")
    void getPrioritizedTasks() {
        taskManagerForSave.createTask(task1);
        taskManagerForSave.createTask(task2);

        taskManagerForRead = FileBackedTaskManager.loadFromFile(tempFile.toString());

        Assertions.assertIterableEquals(taskManagerForSave.getPrioritizedTasks(),
                taskManagerForRead.getPrioritizedTasks());
    }
}