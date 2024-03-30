package util;

import enums.TaskStatus;
import enums.TaskType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import model.Epic;
import model.Subtask;
import model.Task;
import service.HistoryManagerService;

public class TaskUtils {

    private TaskUtils() {

    }

    public static String taskToString(Task task) {
        if (task.getTaskType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            return String.format("%s,%s,%s,%s,%s,%s", subtask.getId(), subtask.getTaskType(), subtask.getTitle(),
                    subtask.getStatus(),
                    subtask.getDescription(),
                    subtask.getEpicId());
        }
        return String.format("%s,%s,%s,%s,%s", task.getId(), task.getTaskType(), task.getTitle(), task.getStatus(),
                task.getDescription());
    }

    public static Task taskFromString(String task) {
        String[] parts = task.split(",");
        TaskType taskType = TaskType.valueOf(parts[1]);
        return switch (taskType) {
            case TASK -> new Task(Integer.parseInt(parts[0]), parts[2], parts[4], TaskStatus.valueOf(parts[3]));
            case EPIC -> new Epic(Integer.parseInt(parts[0]), parts[2], parts[4], TaskStatus.valueOf(parts[3]));
            case SUBTASK -> new Subtask(Integer.parseInt(parts[0]), parts[2], parts[4], TaskStatus.valueOf(parts[3]),
                    Integer.parseInt(parts[5]));
        };
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

    public static List<Integer> getHistoryFromString(String history) {
        if (history.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(history.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
