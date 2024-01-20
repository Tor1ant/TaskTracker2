package service;

import service.impl.InMemoryTaskManagerServiceImpl;

public class Managers {

    public static TaskManagerService getDefault() {
        return new InMemoryTaskManagerServiceImpl();
    }
}
