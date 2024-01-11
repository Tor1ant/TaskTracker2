package service.impl;

import enums.TaskStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManagerService;

public class TaskManagerServiceImpl implements TaskManagerService {

    private int taskCount = 0;

    private final Map<String, Task> tasks;
    private final Map<String, Epic> epics;
    private final Map<String, Subtask> subtasks;

    public TaskManagerServiceImpl(Map<String, Task> tasks, Map<String, Epic> epics, Map<String, Subtask> subtasks) {
        this.tasks = tasks;
        this.epics = epics;
        this.subtasks = subtasks;
    }

    //tasks
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int taskId) {
        return tasks.get(String.valueOf(taskId));
    }

    public Task createTask(Task task) {
        taskCount++;
        task.setId(taskCount);
        tasks.put(String.valueOf(taskCount), task);
        return task;
    }

    public Task updateTask(Task taskForUpdate) {
        if (taskForUpdate.getId() == null) {
            return createTask(taskForUpdate);
        }
        return tasks.put(String.valueOf(taskForUpdate.getId()), taskForUpdate);
    }

    public Task removeTask(int taskId) {
        return tasks.remove(String.valueOf(taskId));
    }

    //subtasks
    public List<Subtask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllSubTasks() {
        subtasks.clear();
    }

    public Subtask getSubTaskById(int subTaskId) {
        return subtasks.get(String.valueOf(subTaskId));
    }

    public Subtask createSubTask(Subtask subtask) {
        taskCount++;
        subtask.setId(taskCount);
        subtasks.put(String.valueOf(taskCount), subtask);
        return subtask;
    }

    public Subtask updateSubTask(Subtask subTaskForUpdate) {
        if (subTaskForUpdate.getId() == null) {
            return createSubTask(subTaskForUpdate);
        }
        return subtasks.put(String.valueOf(subTaskForUpdate.getId()), subTaskForUpdate);
    }

    public Subtask removeSubTask(int subTaskId) {
        return subtasks.remove(String.valueOf(subTaskId));
    }

    //epics
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeAllEpics() {
        epics.clear();
    }

    public Epic getEpicById(int epicId) {
        return epics.get(String.valueOf(epicId));
    }

    public Epic createEpic(Epic epic) {
        taskCount++;
        epic.setId(taskCount);
        updateEpicStatus(epic);
        epics.put(String.valueOf(taskCount), epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) {
        if (epic.getId() == null) {
            return createEpic(epic);
        }
        updateEpicStatus(epic);
        return epics.put(String.valueOf(epic.getId()), epic);
    }

    private void updateEpicStatus(Epic epic) {
        List<String> subtasksStatuses = getEpicSubTasks(epic.getId()).stream()
                .map(subtask -> subtask.getStatus().name())
                .collect(Collectors.toList());

        if (epic.getSubTasksIds().isEmpty() || subtasksStatuses.stream()
                .allMatch(s -> s.equals(TaskStatus.NEW.name()))) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        if (subtasksStatuses.stream().allMatch(s -> s.equals(TaskStatus.DONE.name()))) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }
        epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    public Epic removeEpicById(int epicId) {
        getEpicSubTasks(epicId).stream()
                .map(Subtask::getId)
                .forEach(subTaskId -> subtasks.remove(String.valueOf(subTaskId)));
        return epics.remove(String.valueOf(epicId));
    }

    public List<Subtask> getEpicSubTasks(int epicId) {
        List<Subtask> epicSubTasks = new ArrayList<>();

        epics.get(String.valueOf(epicId)).getSubTasksIds()
                .forEach(id -> epicSubTasks.add(subtasks.get(String.valueOf(id))));
        return epicSubTasks;
    }
}
