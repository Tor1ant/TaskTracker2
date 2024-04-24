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
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

@DisplayName("Тестирование обработчика подзадач")
class SubTaskHandlerTest {

    private static final URI SUBTASK_URI = URI.create("http://localhost:8080/subtasks");
    private static final URI EPIC_URI = URI.create("http://localhost:8080/epics");
    private static final Type LIST_OF_SUBTASKS = new TypeToken<ArrayList<Subtask>>() {
    }.getType();
    private static final Type LIST_OF_STRINGS = new TypeToken<ArrayList<String>>() {
    }.getType();
    private static final String JSON_SUBTASK = """
            {
              "epicId": 1,
              "id": 0,
              "title": "Купить молоко",
              "description": "Простоквашино",
              "status": "NEW",
              "duration": "PT24H",
              "startTime": "2024-04-17T22:11:02.645204"
            }""";
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

    private final Subtask subtask = new Subtask("Закончить выполнение ТЗ", "Желательно сегодня", 1,
            Duration.ofDays(1), LocalDateTime.now());

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
    @DisplayName("Проверка создания подзадачи")
    void createSubtask() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(EPIC_URI)
                .POST(BodyPublishers.ofString(JSON_EPIC))
                .build();
        subtask.setId(2);

        HttpRequest createSubtask = HttpRequest.newBuilder(SUBTASK_URI)
                .POST(BodyPublishers.ofString(JSON_SUBTASK))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(httpRequest, handler);
        HttpResponse<String> response = client.send(createSubtask, handler);

        List<Subtask> subtaskFromServer = gson.fromJson(response.body(), LIST_OF_SUBTASKS);
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(subtask, subtaskFromServer.getFirst());
    }

    @Test
    @DisplayName("Проверка создания подзадачи, которая пересекается с другой")
    void createSubtaskWithIntersects() throws IOException, InterruptedException {
        String expectedResult = "Задача пересекается с одной из существующих. Выберете другое время.";
        HttpRequest httpRequest = HttpRequest.newBuilder(EPIC_URI)
                .POST(BodyPublishers.ofString(JSON_EPIC))
                .build();

        HttpRequest createSubtask = HttpRequest.newBuilder(SUBTASK_URI)
                .POST(BodyPublishers.ofString(JSON_SUBTASK))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(httpRequest, handler);
        client.send(createSubtask, handler);

        //пересекающаяся по времени подзадача
        HttpResponse<String> response = client.send(createSubtask, handler);

        List<String> errorMessage = gson.fromJson(response.body(), LIST_OF_STRINGS);
        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals(expectedResult, errorMessage.getFirst());
    }

    @Test
    @DisplayName("Проверка получения подзадачи по id")
    void getSubtaskById() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(EPIC_URI)
                .POST(BodyPublishers.ofString(JSON_EPIC))
                .build();

        HttpRequest createSubtask = HttpRequest.newBuilder(SUBTASK_URI)
                .POST(BodyPublishers.ofString(JSON_SUBTASK))
                .build();
        HttpRequest getSubtaskById = HttpRequest.newBuilder(URI.create(SUBTASK_URI + "/" + 2))
                .GET()
                .build();

        subtask.setId(2);

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(httpRequest, handler);
        client.send(createSubtask, handler);
        HttpResponse<String> response = client.send(getSubtaskById, handler);

        List<Task> taskFromServer = gson.fromJson(response.body(), LIST_OF_SUBTASKS);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(subtask, taskFromServer.getFirst());
    }

    @Test
    @DisplayName("Проверка получения несуществующей подзадачи по id")
    void getSubTaskByIdFailed() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(EPIC_URI)
                .POST(BodyPublishers.ofString(JSON_EPIC))
                .build();

        HttpRequest createSubtask = HttpRequest.newBuilder(SUBTASK_URI)
                .POST(BodyPublishers.ofString(JSON_SUBTASK))
                .build();
        HttpRequest getSubtaskById = HttpRequest.newBuilder(URI.create(SUBTASK_URI + "/" + 3))
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(httpRequest, handler);
        client.send(createSubtask, handler);
        HttpResponse<String> response = client.send(getSubtaskById, handler);

        List<Task> taskFromServer = gson.fromJson(response.body(), LIST_OF_SUBTASKS);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertTrue(taskFromServer.isEmpty());
    }

    @Test
    @DisplayName("Проверка удаления подзадачи")
    void removeSubtask() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(EPIC_URI)
                .POST(BodyPublishers.ofString(JSON_EPIC))
                .build();

        HttpRequest createSubtask = HttpRequest.newBuilder(SUBTASK_URI)
                .POST(BodyPublishers.ofString(JSON_SUBTASK))
                .build();

        HttpRequest deleteSubtask = HttpRequest.newBuilder(URI.create(SUBTASK_URI + "/" + 2))
                .DELETE()
                .build();
        subtask.setId(2);

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        client.send(httpRequest, handler);
        client.send(createSubtask, handler);
        HttpResponse<String> response = client.send(deleteSubtask, handler);

        List<Task> taskFromServer = gson.fromJson(response.body(), LIST_OF_SUBTASKS);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(subtask, taskFromServer.getFirst());
    }
}