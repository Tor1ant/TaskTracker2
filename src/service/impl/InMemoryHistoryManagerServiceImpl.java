package service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.Task;
import service.HistoryManagerService;

public class InMemoryHistoryManagerServiceImpl implements HistoryManagerService {

    private static final int MAX_BROWSING_TASKS = 10;

    private final List<Task> browsingHistory = new ArrayList<>(MAX_BROWSING_TASKS);

    @Override
    public List<Task> getHistory() {
        return List.copyOf(browsingHistory);
    }

    @Override
    public void add(Task task) {
        if (Objects.isNull(task)) {
            return;
        }

        if (browsingHistory.size() == 10) {
            browsingHistory.removeFirst();
        }
        browsingHistory.add(task);
    }
}
