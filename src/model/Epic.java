package model;

import enums.TaskStatus;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subTasksIds;

    public Epic(Integer id, String title, String description) {
        super(id, title, description, null);
        this.subTasksIds = new ArrayList<>();
    }

    public Epic(String title, String description) {
        super(title, description, null);
        this.subTasksIds = new ArrayList<>();
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
               "subTasksIds=" + subTasksIds +
               ", id=" + id +
               ", title='" + title + '\'' +
               ", description='" + description + '\'' +
               ", status=" + status +
               "} ";
    }
}
