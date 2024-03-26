package model;

import enums.TaskStatus;
import enums.TaskType;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subTasksIds = new ArrayList<>();

    public Epic(Integer id, String title, String description) {
        super(id, title, description, TaskStatus.NEW);
    }

    public Epic(String title, String description) {
        this(0, title, description);
    }

    /**
     * Конструктор для десериализации
     */
    public Epic(Integer id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public void addSubTaskId(int id) {
        subTasksIds.add(id);

    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    @Override
    public String toString() {
        return id + ",EPIC," + title + "," + status + "," + description + ",";
    }
}
