package service;

import java.util.List;
import model.Task;

public interface HistoryManagerService {

    List<Task> getHistory();

    void add(Task task);

    void remove(int id);

}
