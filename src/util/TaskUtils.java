package util;

import enums.TaskStatus;
import enums.TaskType;
import java.time.Duration;
import java.time.LocalDateTime;
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
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s", subtask.getId(), subtask.getTaskType(), subtask.getTitle(),
                    subtask.getStatus(), subtask.getDescription(), subtask.getEpicId(), subtask.getDuration(),
                    subtask.getStartTime());
        }
        if (task.getTaskType() == TaskType.EPIC) {
            Epic epic = (Epic) task;
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s", epic.getId(), epic.getTaskType(), epic.getTitle(),
                    epic.getStatus(), epic.getDescription(), epic.getDuration(), epic.getStartTime(),
                    epic.getEndTime());
        }
        return String.format("%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getTaskType(), task.getTitle(),
                task.getStatus(),
                task.getDescription(), task.getDuration(), task.getStartTime());
    }

    public static Task taskFromString(String task) {
        String[] parts = task.split(",");
        TaskType taskType = TaskType.valueOf(parts[1]);
        LocalDateTime startTime;

        if (taskType.equals(TaskType.EPIC) || taskType.equals(TaskType.TASK)) {
            startTime = parts[6].equals("null") ? null : LocalDateTime.parse(parts[6]);
        } else {
            startTime = parts[7].equals("null") ? null : LocalDateTime.parse(parts[7]);
        }

        return switch (taskType) {
            case TASK -> new Task(
                    Integer.parseInt(parts[0]),
                    parts[2],
                    parts[4],
                    TaskStatus.valueOf(parts[3]),
                    Duration.parse(parts[5]),
                    startTime
            );
            case EPIC -> new Epic(
                    Integer.parseInt(parts[0]),
                    parts[2],
                    parts[4],
                    TaskStatus.valueOf(parts[3]),
                    Duration.parse(parts[5]),
                    startTime,
                    parts[7].equals("null") ? null : LocalDateTime.parse(parts[7])
            );
            case SUBTASK -> new Subtask(
                    Integer.parseInt(parts[0]),
                    parts[2],
                    parts[4],
                    TaskStatus.valueOf(parts[3]),
                    Integer.parseInt(parts[5]),
                    Duration.parse(parts[6]),
                    startTime
            );
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
