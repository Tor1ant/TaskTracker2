package service.impl;

import enums.TaskStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManagerService;

public class TaskManagerServiceImpl implements TaskManagerService {

    private int taskCount = 0;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Logger logger = Logger.getLogger(getClass().getName());

    //tasks
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Task createTask(Task task) {
        taskCount++;
        task.setId(taskCount);
        tasks.put(taskCount, task);
        return task;
    }

    public Task updateTask(Task taskForUpdate) {
        if (!tasks.containsKey(taskForUpdate.getId())) {
            logger.info("Задача с id " + taskForUpdate.getId() + " не найдена");
            return null;
        }
        return tasks.put(taskForUpdate.getId(), taskForUpdate);
    }

    public Task removeTask(int taskId) {
        return tasks.remove(taskId);
    }

    //subtasks
    public List<Subtask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllSubTasks() {
        epics.values()
                .forEach(epic -> epic.getSubTasksIds()
                        .removeAll(new ArrayList<>(subtasks.keySet())));
        subtasks.clear();
    }

    public Subtask getSubTaskById(int subTaskId) {
        return subtasks.get(subTaskId);
    }

    public Subtask createSubTask(Subtask subtask) {
        taskCount++;
        subtask.setId(taskCount);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubTasksIds()
                .add(subtask.getId());
        subtasks.put(taskCount, subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    public Subtask updateSubTask(Subtask subTaskForUpdate) {
        if (!subtasks.containsKey(subTaskForUpdate.getId())) {
            logger.info("Подзадача с id " + subTaskForUpdate.getId() + " не найдена");
            return null;
        }
        updateEpicStatus(epics.get(subTaskForUpdate.getEpicId()));
        return subtasks.put(subTaskForUpdate.getId(), subTaskForUpdate);
    }

    public Subtask removeSubTask(int subTaskId) {
        epics.get(subtasks.get(subTaskId).getEpicId())
                .getSubTasksIds()
                .remove(Integer.valueOf(subTaskId));
        return subtasks.remove(subTaskId);
    }

    //epics
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public Epic createEpic(Epic epic) {
        taskCount++;
        epic.setId(taskCount);
        epics.put(taskCount, epic);
        updateEpicStatus(epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) {
        if (!tasks.containsKey(epic.getId())) {
            logger.info("Эпик с id " + epic.getId() + " не найден");
            return null;
        }
        updateEpicStatus(epic);
        return epics.put(epic.getId(), epic);
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
                .forEach(subtasks::remove);
        return epics.remove(epicId);
    }

    public List<Subtask> getEpicSubTasks(int epicId) {
        List<Subtask> epicSubTasks = new ArrayList<>();

        epics.get(epicId)
                .getSubTasksIds()
                .forEach(id -> epicSubTasks.add(subtasks.get(id)));
        return epicSubTasks;
    }
}
