package model;

import enums.TaskStatus;
import enums.TaskType;

public class Subtask extends Task {

    private final TaskType taskType;

    private Integer epicId;

    public Subtask(Integer id, String title, String description, TaskStatus status, Integer epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
        taskType = TaskType.SUBTASK;
    }

    public Subtask(String title, String description, Integer epicId) {
        super(title, description);
        this.epicId = epicId;
        taskType = TaskType.SUBTASK;
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
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
