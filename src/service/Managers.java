package service;

import service.impl.FileBackedTaskManager;
import service.impl.InMemoryHistoryManagerService;

public class Managers {

    private static final String SAVE_FILE = "save/saveFile.csv";

    public static TaskManagerService getDefault() {
        return new FileBackedTaskManager(SAVE_FILE);
    }

    public static HistoryManagerService getDefaultHistory() {
        return new InMemoryHistoryManagerService();
    }
}
