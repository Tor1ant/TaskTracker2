package model;

import enums.TaskStatus;
import enums.TaskType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subTasksIds = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(Integer id, String title, String description) {
        super(id, title, description, TaskStatus.NEW, Duration.ZERO, null);
    }

    public Epic(String title, String description) {
        this(0, title, description);
    }

    /**
     * Конструктор для десериализации
     */
    public Epic(Integer id, String title, String description, TaskStatus status, Duration duration,
            LocalDateTime startTime, LocalDateTime endTime) {
        super(id, title, description, status, duration, startTime);
        this.endTime = endTime;
    }

    public void addSubTaskId(int id) {
        subTasksIds.add(id);
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    @Override
    public String toString() {
        return id + ",EPIC," + title + "," + status + "," + description + ",";
    }
}
