package model;

import enums.TaskStatus;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Subtask subtask = (Subtask) o;
        return Objects.equals(getEpicId(), subtask.getEpicId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicId());
    }

    @Override
    public String toString() {
        return "Subtask{" +
               "epicId=" + epicId +
               ", id=" + id +
               ", title='" + title + '\'' +
               ", description='" + description + '\'' +
               ", status=" + status +
               "} ";
    }
}
