package util;

import enums.TaskStatus;
import java.util.Arrays;
import java.util.stream.Collectors;
import model.Epic;
import model.Subtask;
import model.Task;
import service.HistoryManagerService;
import service.impl.FileBackedTaskManager;

public class TaskUtils {

    private TaskUtils() {

    }

    public static String taskToString(Task task) {
        return String.format("%s,%s,%s,%s,%s", task.getId(), task.getTaskType(), task.getTitle(), task.getStatus(),
                task.getDescription());
    }

    public static String epicToString(Epic epic) {
        return String.format("%s,%s,%s,%s,%s", epic.getId(), epic.getTaskType(), epic.getTitle(), epic.getStatus(),
                epic.getDescription());
    }

    public static String subTaskToString(Subtask task) {
        return String.format("%s,%s,%s,%s,%s,%s", task.getId(), task.getTaskType(), task.getTitle(), task.getStatus(),
                task.getDescription(),
                task.getEpicId());
    }

    public static Task taskFromString(String task) {
        String[] parts = task.split(",");
        return new Task(Integer.valueOf(parts[0]), parts[2], parts[4], TaskStatus.valueOf(parts[3]));
    }

    public static Epic epicFromString(String epic) {
        String[] split = epic.split(",");
        return new Epic(Integer.parseInt(split[0]), split[2], split[4], TaskStatus.valueOf(split[3]));
    }

    public static Subtask subTaskFromString(String subtask) {
        String[] split = subtask.split(",");
        return new Subtask(Integer.parseInt(split[0]), split[2], split[4], TaskStatus.valueOf(split[3]),
                Integer.parseInt(split[5]));
    }

    public static String historyToString(HistoryManagerService historyManagerService) {
        String historyMarker = "\n";
        return historyMarker + new StringBuilder().append(historyManagerService
                .getHistory()
                .stream()
                .map(Task::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","))).reverse();

    }

    public static void historyFromString(String history, FileBackedTaskManager fileBackedTaskManager,
            HistoryManagerService historyManagerService) {
        if (history.isBlank()) {
            return;
        }
        Arrays.stream(history.split(","))
                .map(Integer::parseInt)
                .toList()
                .forEach(id -> {
                    if (fileBackedTaskManager.getTaskById(id) != null) {
                        historyManagerService.add(fileBackedTaskManager.getTaskById(id));
                    }
                    if (fileBackedTaskManager.getEpicById(id) != null) {
                        historyManagerService.add(fileBackedTaskManager.getEpicById(id));
                    }
                    if (fileBackedTaskManager.getSubTaskById(id) != null) {
                        historyManagerService.add(fileBackedTaskManager.getSubTaskById(id));
                    }
                });
    }
}
