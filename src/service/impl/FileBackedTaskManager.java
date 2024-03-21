package service.impl;

import enums.TaskType;
import exception.ManagerSaveException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import model.Epic;
import model.Subtask;
import model.Task;
import service.HistoryManagerService;

public class FileBackedTaskManager extends InMemoryTaskManagerServiceImpl {

    private static final Logger logger = Logger.getLogger(FileBackedTaskManager.class.getName());
    private final String saveFile;

    public FileBackedTaskManager(String saveFile) {
        this.saveFile = saveFile;
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        save();
        return task;
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Task updateTask(Task taskForUpdate) {
        Task updatedTask = super.updateTask(taskForUpdate);
        save();
        return updatedTask;
    }

    @Override
    public Task removeTask(int taskId) {
        Task removedTask = super.removeTask(taskId);
        save();
        return removedTask;
    }

    @Override
    public Subtask getSubTaskById(int subTaskId) {
        Subtask subtask = super.getSubTaskById(subTaskId);
        save();
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public Subtask updateSubTask(Subtask subTaskForUpdate) {
        Subtask updatedSubTask = super.updateSubTask(subTaskForUpdate);
        save();
        return updatedSubTask;
    }

    @Override
    public Subtask removeSubTask(int subTaskId) {
        Subtask removedSubTask = super.removeSubTask(subTaskId);
        save();
        return removedSubTask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = super.getEpicById(epicId);
        save();
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Epic removeEpicById(int epicId) {
        Epic removedEpic = super.removeEpicById(epicId);
        save();
        return removedEpic;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    private void save() {
        final String tasksFields = "id,type,name,status,description,epic";
        Path path = Paths.get(this.saveFile);
        List<Task> tasksToSave = new ArrayList<>();
        tasksToSave.addAll(getTasks());
        tasksToSave.addAll(getEpics());
        tasksToSave.addAll(getSubTasks());

        try {
            Files.deleteIfExists(path);
            Files.writeString(path, tasksFields + "\n", StandardOpenOption.CREATE);
            Files.write(path, tasksToSave.stream()
                    .map(Task::toString)
                    .collect(Collectors.toList()), StandardOpenOption.APPEND);
            Files.writeString(path, historyToString(historyManagerService), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.log(java.util.logging.Level.WARNING, "Can't save tasks to " + saveFile + "\n", e);
            throw new ManagerSaveException(e);
        }
    }

    private static String historyToString(HistoryManagerService historyManagerService) {
        String historyMarker = "\n";
        return historyMarker + new StringBuilder().append(historyManagerService
                .getHistory()
                .stream()
                .map(Task::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","))).reverse();

    }

    private static void historyFromString(String history, FileBackedTaskManager fileBackedTaskManager) {
        if (history.isBlank()) {
            return;
        }
        Arrays.stream(history.split(","))
                .map(Integer::parseInt)
                .toList()
                .forEach(id -> {
                    if (fileBackedTaskManager.tasks.containsKey(id)) {
                        fileBackedTaskManager.historyManagerService.add(fileBackedTaskManager.tasks.get(id));
                    }
                    if (fileBackedTaskManager.epics.containsKey(id)) {
                        fileBackedTaskManager.historyManagerService.add(fileBackedTaskManager.epics.get(id));
                    }
                    if (fileBackedTaskManager.subtasks.containsKey(id)) {
                        fileBackedTaskManager.historyManagerService.add(fileBackedTaskManager.subtasks.get(id));
                    }
                });
    }

    public static FileBackedTaskManager loadFromFile(String saveFile) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(saveFile);
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(saveFile));
        } catch (IOException e) {
            logger.log(java.util.logging.Level.WARNING, "Can't load tasks from " + saveFile + "\n", e);
            throw new ManagerSaveException(e);
        }

        if (lines.isEmpty()) {
            return fileBackedTaskManager;
        }

        lines.removeFirst();
        lines.forEach(line -> {
            if (line.isEmpty() || line.isBlank()) {
                logger.info("Все задачи десериализованы");
            } else if (!line.contains(TaskType.TASK.toString())
                       && !line.contains(TaskType.SUBTASK.toString())
                       && !line.contains(TaskType.EPIC.toString())) {
                historyFromString(line, fileBackedTaskManager);
            } else {
                String[] parts = line.split(",");
                if (parts[1].equals(TaskType.EPIC.toString())) {
                    Epic epic = Epic.fromString(line);
                    fileBackedTaskManager.taskCount = setTasksCount(epic.getId(), fileBackedTaskManager.taskCount);
                    fileBackedTaskManager.epics.put(epic.getId(), epic);
                } else if (parts[1].equals(TaskType.SUBTASK.toString())) {
                    Subtask subtask = Subtask.fromString(line);
                    fileBackedTaskManager.taskCount = setTasksCount(subtask.getId(), fileBackedTaskManager.taskCount);
                    fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                } else {
                    Task task = Task.fromString(line);
                    fileBackedTaskManager.taskCount = setTasksCount(task.getId(), fileBackedTaskManager.taskCount);
                    fileBackedTaskManager.tasks.put(task.getId(), task);
                }
            }
        });
        fileBackedTaskManager.subtasks.values().forEach(subtask -> {
            if (fileBackedTaskManager.epics.containsKey(subtask.getEpicId())) {
                fileBackedTaskManager.epics.get(subtask.getEpicId()).addSubTaskId(subtask.getId());
            }
        });
        return fileBackedTaskManager;
    }

    private static int setTasksCount(int taskId, int taskCount) {
        return Math.max(taskCount, taskId);
    }
}
