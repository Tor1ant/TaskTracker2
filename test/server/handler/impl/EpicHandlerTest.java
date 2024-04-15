package server.handler.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import config.DurationTypeAdapter;
import config.LocalDateTimeTypeAdapter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

@DisplayName("Тестирование обработчика эпиков")
class EpicHandlerTest {

    private static final Type LIST_OF_EPICS = new TypeToken<ArrayList<Epic>>() {
    }.getType();
    private static final URI EPIC_URI = URI.create("http://localhost:8080/epics");
    private static final Type LIST_OF_STRINGS = new TypeToken<ArrayList<String>>() {
    }.getType();
    private static final Type LIST_OF_SUBTASKS = new TypeToken<ArrayList<Subtask>>() {
    }.getType();
    private static final String JSON_EPIC = """
            {
              "subTasksIds": [],
              "id": 0,
              "title": "Сходить в магазин",
              "description": "сегодня или завтра",
              "status": "NEW",
              "duration": "PT0S"
            }""";
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .setPrettyPrinting()
            .create();
    private final Epic epic = new Epic("Сходить в магазин", "сегодня или завтра");

    private HttpClient client;
    private HttpTaskServer server;

    @BeforeEach
    void setUp() throws IOException {
        client = HttpClient.newHttpClient();

        HttpTaskServer.dropSave();
        server = new HttpTaskServer();
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    @DisplayName("Проверка создания эпика")
    void createEpic() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(EPIC_URI)
                .POST(BodyPublishers.ofString(JSON_EPIC))
                .build();
        epic.setId(1);

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(httpRequest, handler);

        List<Epic> taskFromServer = gson.fromJson(response.body(), LIST_OF_EPICS);
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(epic, taskFromServer.getFirst());
    }

    @Test
    @DisplayName("Проверка получения эпика по id")
    void getEpicById() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(EPIC_URI)
                .POST(BodyPublishers.ofString(JSON_EPIC))
                .build();
        HttpRequest getTaskById = HttpRequest.newBuilder(URI.create(EPIC_URI + "/" + 1))
                .GET()
                .build();
        epic.setId(1);

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(httpRequest, handler);
        HttpResponse<String> response = client.send(getTaskById, handler);

        List<Epic> taskFromServer = gson.fromJson(response.body(), LIST_OF_EPICS);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(epic, taskFromServer.getFirst());
    }

    @Test
    @DisplayName("Проверка получения несуществующего эпика по id")
    void getEpicByIdFailed() throws IOException, InterruptedException {
        HttpRequest getTaskById = HttpRequest.newBuilder(URI.create(EPIC_URI + "/" + 1))
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(getTaskById, handler);

        List<Epic> taskFromServer = gson.fromJson(response.body(), LIST_OF_EPICS);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertTrue(taskFromServer.isEmpty());
    }

    @Test
    @DisplayName("Проверка удаления пика")
    void removeEpic() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(EPIC_URI)
                .POST(BodyPublishers.ofString(JSON_EPIC))
                .build();

        HttpRequest deleteTask = HttpRequest.newBuilder(URI.create(EPIC_URI + "/" + 1))
                .DELETE()
                .build();
        epic.setId(1);

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        client.send(httpRequest, handler);
        HttpResponse<String> response = client.send(deleteTask, handler);

        List<Task> taskFromServer = gson.fromJson(response.body(), LIST_OF_EPICS);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(epic, taskFromServer.getFirst());
    }

    @Test
    @DisplayName("Проверка получения сабтасок эпика")
    void getEpicSubtasks() throws IOException, InterruptedException {
        URI SUBTASK_URI = URI.create("http://localhost:8080/subtasks");
        String jsonSubtask = """
                {
                  "epicId": 1,
                  "id": 0,
                  "title": "Купить молоко",
                  "description": "Простоквашино",
                  "status": "NEW",
                  "duration": "PT24H",
                  "startTime": "2024-04-17T22:11:02.645204"
                }""";
        Subtask subtask = new Subtask("Закончить выполнение ТЗ", "Желательно сегодня", 1,
                Duration.ofDays(1), LocalDateTime.now());
        subtask.setId(2);

        HttpRequest httpRequest = HttpRequest.newBuilder(EPIC_URI)
                .POST(BodyPublishers.ofString(JSON_EPIC))
                .build();
        subtask.setId(2);

        HttpRequest createSubtask = HttpRequest.newBuilder(SUBTASK_URI)
                .POST(BodyPublishers.ofString(jsonSubtask))
                .build();

        HttpRequest getEpicSubtasks = HttpRequest.newBuilder(URI.create(EPIC_URI + "/" + 1 + "/subtasks"))
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(httpRequest, handler);
        client.send(createSubtask, handler);
        HttpResponse<String> response = client.send(getEpicSubtasks, handler);

        List<Subtask> subtaskFromServer = gson.fromJson(response.body(), LIST_OF_SUBTASKS);
        Assertions.assertEquals(subtask, subtaskFromServer.getFirst());
    }

    @Test
    @DisplayName("Проверка получения сабтасок несуществующего эпика")
    void getEpicSubtasksFailed() throws IOException, InterruptedException {
        HttpRequest getEpicSubtasks = HttpRequest.newBuilder(URI.create(EPIC_URI + "/" + 1 + "/subtasks"))
                .GET()
                .build();
        String errorMessage = String.format("Подзадачи для эпика с id %d не найдены", 1);

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(getEpicSubtasks, handler);

        List<String> subtaskFromServer = gson.fromJson(response.body(), LIST_OF_STRINGS);

        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals(errorMessage, subtaskFromServer.getFirst());
    }
}