package model;

import enums.TaskStatus;
import enums.TaskType;

public class Subtask extends Task {


    private Integer epicId;

    public Subtask(Integer id, String title, String description, TaskStatus status, Integer epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, Integer epicId) {
        super(title, description);
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
