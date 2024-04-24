package server.handler.impl;

import com.sun.net.httpserver.HttpExchange;
import java.util.Map;
import model.Task;
import server.handler.AbstractHttpHandler;
import service.TaskManagerService;

public class HistoryHandler extends AbstractHttpHandler<Task> {

    private final TaskManagerService taskManagerService;

    public HistoryHandler(TaskManagerService taskManagerService) {
        this.taskManagerService = taskManagerService;
    }

    @Override
    public void handle(HttpExchange exchange) {
        getRequestParams(exchange);
        sendResponse(Map.of(HTTP_OK, taskManagerService.getHistory()));
    }
}
