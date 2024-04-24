package server.handler.impl;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

import com.sun.net.httpserver.HttpExchange;
import java.util.List;
import java.util.Map;
import model.Epic;
import model.Subtask;
import server.handler.AbstractHttpHandler;
import server.model.RequestParams;
import service.TaskManagerService;

public class EpicHandler extends AbstractHttpHandler<Epic> {

    private final TaskManagerService taskManagerService;

    public EpicHandler(TaskManagerService taskManagerService) {
        this.taskManagerService = taskManagerService;
    }

    @Override
    public void handle(HttpExchange exchange) {
        RequestParams result = getRequestParams(exchange);

        Epic epic = getTaskFromJson(result.requestBodyInString(), Epic.class);

        boolean haveEpicId = result.requestParam().isPresent();

        if (haveEpicId && exchange.getRequestURI().getPath().contains("subtasks")) {
            processGetEpicSubtasksReq(result.requestParam().get());
            return;
        }

        Map<Integer, List<Epic>> response = processRequest(
                result.httpMethod(),
                result.requestParam().isPresent() ? result.requestParam().get() : null,
                epic,
                taskManagerService::getEpicById,
                taskManagerService::getEpics,
                taskManagerService::createEpic,
                taskManagerService::removeAllEpics
        );
        sendResponse(response);
    }


    private void processGetEpicSubtasksReq(int epicId) {
        List<Subtask> epicSubTasks = taskManagerService.getEpicSubTasks(epicId);
        if (epicSubTasks.isEmpty()) {
            sendResponse(Map.of(HTTP_NOT_FOUND, List.of("Подзадачи для эпика с id " + epicId + " не найдены")));
            return;
        }
        sendResponse(Map.of(HTTP_OK, epicSubTasks));
    }
}
