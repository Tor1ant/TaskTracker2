package model;

import enumerated.TaskStatus;
import enumerated.TaskType;
import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {


    private Integer epicId;

    public Subtask(Integer id, String title, String description, TaskStatus status, Integer epicId, Duration duration,
            LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, Integer epicId, Duration duration, LocalDateTime startTime) {
        super(title, description, duration, startTime);
        this.epicId = epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return id + ",SUBTASK," + title + "," + status + "," + description + "," + epicId;
    }
}
