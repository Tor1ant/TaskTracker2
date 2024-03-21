package model;

import enums.TaskStatus;
import java.util.Objects;

public class Task {

    protected Integer id;
    protected String title;
    protected String description;

    protected TaskStatus status;

    public Task(String title, String description) {
        this(0, title, description, TaskStatus.NEW);
    }

    public Task(Integer id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (this.id != 0) {
            return;
        }
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(getId(), task.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return id + ",TASK," + title + "," + status + "," + description + ",";
    }

    public static Task fromString(String task) {
        String[] parts = task.split(",");
        return new Task(Integer.valueOf(parts[0]), parts[2], parts[4], TaskStatus.valueOf(parts[3]));
    }
}
