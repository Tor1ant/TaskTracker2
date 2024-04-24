package server.handler.impl;

import com.sun.net.httpserver.HttpExchange;
import java.util.List;
import java.util.Map;
import model.Task;
import server.handler.AbstractHttpHandler;
import server.model.RequestParams;
import service.TaskManagerService;

public class TaskHandler extends AbstractHttpHandler<Task> {

    private final TaskManagerService taskManagerService;

    public TaskHandler(TaskManagerService taskManagerService) {
        this.taskManagerService = taskManagerService;
    }

    @Override
    public void handle(HttpExchange exchange) {
        RequestParams requestParams = getRequestParams(exchange);

        Task task = getTaskFromJson(requestParams.requestBodyInString(), Task.class);

        Map<Integer, List<Task>> response = processRequest(
                requestParams.httpMethod(),
                requestParams.requestParam().isPresent() ? requestParams.requestParam().get() : null,
                task,
                taskManagerService::getTaskById,
                taskManagerService::getTasks,
                taskManagerService::createTask,
                taskManagerService::removeAllTasks
        );

        sendResponse(response);
    }
}
