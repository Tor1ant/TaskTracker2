package service.impl;

import enumerated.TaskStatus;
import exception.ManagerSaveException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;
import model.Epic;
import model.Subtask;
import model.Task;
import service.HistoryManagerService;
import service.Managers;
import service.TaskManagerService;

public class InMemoryTaskManagerService implements TaskManagerService {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Logger logger = Logger.getLogger(getClass().getName());
    protected final HistoryManagerService historyManagerService = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    protected int taskCount = 0;

    //tasks
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.keySet().forEach(historyManagerService::remove);
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManagerService.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        checkingIntersections(task);
        taskCount++;
        task.setId(taskCount);
        tasks.put(taskCount, task);
        addToPrioritizedTasks(task);
        return task;
    }

    @Override
    public Task updateTask(Task taskForUpdate) {
        if (!tasks.containsKey(taskForUpdate.getId())) {
            logger.info("Задача с id " + taskForUpdate.getId() + " не найдена");
            return null;
        }
        checkingIntersections(taskForUpdate);
        addToPrioritizedTasks(taskForUpdate);
        return tasks.put(taskForUpdate.getId(), taskForUpdate);
    }

    @Override
    public Task removeTask(int taskId) {
        historyManagerService.remove(taskId);
        Task removedTask = tasks.remove(taskId);
        prioritizedTasks.remove(removedTask);
        return removedTask;
    }

    //subtasks
    @Override
    public List<Subtask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllSubTasks() {
        subtasks.keySet().forEach(historyManagerService::remove);
        epics.values()
                .forEach(epic -> epic.getSubTasksIds()
                        .removeAll(new ArrayList<>(subtasks.keySet())));
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
    }

    @Override
    public Subtask getSubTaskById(int subTaskId) {
        Subtask subtask = subtasks.get(subTaskId);
        historyManagerService.add(subtask);
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        checkingIntersections(subtask);
        taskCount++;
        subtask.setId(taskCount);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubTasksIds()
                .add(subtask.getId());
        subtasks.put(taskCount, subtask);
        addToPrioritizedTasks(subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public Subtask updateSubTask(Subtask subTaskForUpdate) {
        if (!subtasks.containsKey(subTaskForUpdate.getId())) {
            logger.info("Подзадача с id " + subTaskForUpdate.getId() + " не найдена");
            return null;
        }
        checkingIntersections(subTaskForUpdate);
        updateEpicStatus(epics.get(subTaskForUpdate.getEpicId()));
        addToPrioritizedTasks(subTaskForUpdate);
        return subtasks.put(subTaskForUpdate.getId(), subTaskForUpdate);
    }

    @Override
    public Subtask removeSubTask(int subTaskId) {
        Epic epic = epics.get(subtasks.get(subTaskId).getEpicId());
        Subtask removedSubTask = subtasks.remove(subTaskId);
        epic.getSubTasksIds().remove(Integer.valueOf(subTaskId));
        updateEpicStatus(epic);
        historyManagerService.remove(subTaskId);
        prioritizedTasks.remove(removedSubTask);
        return removedSubTask;
    }

    //epics
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {
        subtasks.keySet().forEach(historyManagerService::remove);
        epics.keySet().forEach(historyManagerService::remove);
        prioritizedTasks.removeAll(epics.values());
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        historyManagerService.add(epic);
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        checkingIntersections(epic);
        taskCount++;
        epic.setId(taskCount);
        epics.put(taskCount, epic);
        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epicForUpdate) {
        if (!epics.containsKey(epicForUpdate.getId())) {
            logger.info("Эпик с id " + epicForUpdate.getId() + " не найден");
            return null;
        }
        getEpicSubTasks(epicForUpdate.getId()).stream()
                .map(Subtask::getId)
                .forEach(epicForUpdate::addSubTaskId);

        checkingIntersections(epicForUpdate);
        updateEpicStatus(epicForUpdate);
        return epics.put(epicForUpdate.getId(), epicForUpdate);
    }

    @Override
    public Epic removeEpicById(int epicId) {
        getEpicSubTasks(epicId).stream()
                .map(Subtask::getId)
                .forEach(id -> {
                    historyManagerService.remove(id);
                    subtasks.remove(id);
                });
        historyManagerService.remove(epicId);
        prioritizedTasks.remove(epics.get(epicId));
        return epics.remove(epicId);
    }

    @Override
    public List<Subtask> getEpicSubTasks(int epicId) {
        List<Subtask> epicSubTasks = new ArrayList<>();
        if (!epics.containsKey(epicId)) {
            return epicSubTasks;
        }
        epics.get(epicId)
                .getSubTasksIds()
                .forEach(id -> epicSubTasks.add(subtasks.get(id)));
        return epicSubTasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManagerService.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubTasksIds().isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        List<Subtask> epicSubTasks = getEpicSubTasks(epic.getId());

        LocalDateTime epicStartTime = epicSubTasks.stream().map(Subtask::getStartTime).min(LocalDateTime::compareTo)
                .orElse(null);
        epic.setStartTime(epicStartTime);

        Duration epicDuration = epicSubTasks.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(epicDuration);

        LocalDateTime epicEndTime = epicSubTasks.stream().map(Subtask::getEndTime).max(LocalDateTime::compareTo)
                .orElse(null);
        epic.setEndTime(epicEndTime);

        List<String> subtasksStatuses = epicSubTasks.stream()
                .map(subtask -> subtask.getStatus().name())
                .toList();

        if (subtasksStatuses.stream().allMatch(s -> s.equals(TaskStatus.NEW.name()))) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        if (subtasksStatuses.stream().allMatch(s -> s.equals(TaskStatus.DONE.name()))) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }
        epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    private void checkingIntersections(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        if (task.getStartTime() != null && task.getEndTime() != null) {
            boolean anyMatch = getPrioritizedTasks()
                    .stream()
                    .filter(t -> !t.getId().equals(task.getId()))
                    .anyMatch(t -> task.getStartTime().isBefore(t.getEndTime()) && (task.getStartTime()
                                                                                            .isAfter(t.getStartTime())
                                                                                    || task.getStartTime().isEqual(
                            t.getStartTime())));

            if (anyMatch) {
                logger.info("Задача пересекается с существующей. Выберете другое время.");
                throw new ManagerSaveException("Задача пересекается с существующей. Выберете другое время.");
            }
        }
    }

    protected void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        prioritizedTasks.remove(task);
        prioritizedTasks.add(task);
    }
}
