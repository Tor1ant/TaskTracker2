package service;

import service.impl.InMemoryHistoryManagerServiceImpl;
import service.impl.InMemoryTaskManagerServiceImpl;

public class Managers {

    public static TaskManagerService getDefault() {
        return new InMemoryTaskManagerServiceImpl();
    }

    public static HistoryManagerService getDefaultHistory() {
        return new InMemoryHistoryManagerServiceImpl();
    }
}
