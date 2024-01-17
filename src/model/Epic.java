package model;

import enums.TaskStatus;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subTasksIds = new ArrayList<>();

    public Epic(Integer id, String title, String description) {
        super(id, title, description, TaskStatus.NEW);
    }

    public Epic(String title, String description) {
        this(0,title,description);
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
