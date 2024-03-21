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
        this(0, title, description);
    }

    /**
     * Конструктор для десериализации
     */
    private Epic(Integer id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
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

    public static Epic fromString(String str) {
        String[] split = str.split(",");
        return new Epic(Integer.parseInt(split[0]), split[2], split[4], TaskStatus.valueOf(split[3]));
    }
}
