package model;

import enums.TaskStatus;

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

    public static Subtask fromString(String subtaskString) {
        String[] subtaskData = subtaskString.split(",");
        return new Subtask(Integer.parseInt(subtaskData[0]), subtaskData[2], subtaskData[4],
                TaskStatus.valueOf(subtaskData[3]), Integer.parseInt(subtaskData[5]));
    }
}
