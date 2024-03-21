package service;

import service.impl.FileBackedTaskManager;
import service.impl.InMemoryHistoryManagerServiceImpl;

public class Managers {

    private static final String saveFile = "save/saveFile.csv";

    public static TaskManagerService getDefault() {
        return new FileBackedTaskManager(saveFile);
    }

    public static HistoryManagerService getDefaultHistory() {
        return new InMemoryHistoryManagerServiceImpl();
    }
}
