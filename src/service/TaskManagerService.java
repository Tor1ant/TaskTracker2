package service;

import java.util.List;
import model.Epic;
import model.Subtask;
import model.Task;

public interface TaskManagerService {

    List<Task> getTasks();

    void removeAllTasks();

    Task getTaskById(int taskId);

    Task createTask(Task task);

    Task updateTask(Task taskForUpdate);

    Task removeTask(int taskId);

    List<Subtask> getSubTasks();

    void removeAllSubTasks();

    Subtask getSubTaskById(int subTaskId);

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubTask(Subtask subTaskForUpdate);

    Subtask removeSubTask(int subTaskId);

    List<Epic> getEpics();

    void removeAllEpics();

    Epic getEpicById(int epicId);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    Epic removeEpicById(int epicId);

    List<Subtask> getEpicSubTasks(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
