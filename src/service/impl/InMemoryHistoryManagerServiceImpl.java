package service.impl;

import java.util.ArrayList;
import java.util.List;
import model.Task;
import service.HistoryManagerService;

public class InMemoryHistoryManagerServiceImpl implements HistoryManagerService {

    private final List<Task> browsingHistory = new ArrayList<>(10);

    @Override
    public List<Task> getHistory() {
        return browsingHistory;
    }

    @Override
    public void add(Task task) {
        if (browsingHistory.size() == 10) {
            browsingHistory.removeFirst();
        }
        browsingHistory.add(task);
    }
}
