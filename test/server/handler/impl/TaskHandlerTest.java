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
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

@DisplayName("Тестирование обработчика задач")
class TaskHandlerTest {

    private static final URI TASK_URI = URI.create("http://localhost:8080/tasks");
    private static final Type LIST_OF_TASKS = new TypeToken<ArrayList<Task>>() {
    }.getType();
    private static final Type LIST_OF_STRINGS = new TypeToken<ArrayList<String>>() {
    }.getType();
    private static final String TASK_JSON = """
            {
                "id": 0,
                "title": "Закончить выполнение ТЗ",
                "description": "Желательно сегодня",
                "status": "NEW",
                "duration": "PT24H",
                "startTime": "2024-04-13T15:16:10.7915916"
            }""";
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .setPrettyPrinting()
            .create();
    private final Task task = new Task("Закончить выполнение ТЗ", "Желательно сегодня", Duration.ofDays(1),
            LocalDateTime.now());


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
    @DisplayName("Проверка создания задачи")
    void createTask() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(TASK_URI)
                .POST(BodyPublishers.ofString(TASK_JSON))
                .build();
        task.setId(1);

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(httpRequest, handler);

        List<Task> taskFromServer = gson.fromJson(response.body(), LIST_OF_TASKS);
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(task, taskFromServer.getFirst());
    }

    @Test
    @DisplayName("Проверка создания задачи, которая пересекается с другой")
    void createTaskWithIntersects() throws IOException, InterruptedException {
        String expectedResult = "Задача пересекается с одной из существующих. Выберете другое время.";
        HttpRequest httpRequest = HttpRequest.newBuilder(TASK_URI)
                .POST(BodyPublishers.ofString(TASK_JSON))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(httpRequest, handler);

        //пересекающаяся по времени задача
        HttpResponse<String> response = client.send(httpRequest, handler);

        List<String> taskFromServer = gson.fromJson(response.body(), LIST_OF_STRINGS);
        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals(expectedResult, taskFromServer.getFirst());
    }

    @Test
    @DisplayName("Проверка получения задачи по id")
    void getTaskById() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(TASK_URI)
                .POST(BodyPublishers.ofString(TASK_JSON))
                .build();
        HttpRequest getTaskById = HttpRequest.newBuilder(URI.create(TASK_URI + "/" + 1))
                .GET()
                .build();
        task.setId(1);

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(httpRequest, handler);
        HttpResponse<String> response = client.send(getTaskById, handler);

        List<Task> taskFromServer = gson.fromJson(response.body(), LIST_OF_TASKS);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(task, taskFromServer.getFirst());
    }

    @Test
    @DisplayName("Проверка получения несуществующей задачи по id")
    void getTaskByIdFailed() throws IOException, InterruptedException {
        HttpRequest getTaskById = HttpRequest.newBuilder(URI.create(TASK_URI + "/" + 1))
                .GET()
                .build();
        task.setId(1);

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(getTaskById, handler);

        List<Task> taskFromServer = gson.fromJson(response.body(), LIST_OF_TASKS);
        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertTrue(taskFromServer.isEmpty());
    }

    @Test
    @DisplayName("Проверка удаления задачи")
    void removeTask() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(TASK_URI)
                .POST(BodyPublishers.ofString(TASK_JSON))
                .build();

        HttpRequest deleteTask = HttpRequest.newBuilder(URI.create(TASK_URI + "/" + 1))
                .DELETE()
                .build();
        task.setId(1);

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        client.send(httpRequest, handler);
        HttpResponse<String> response = client.send(deleteTask, handler);

        List<Task> taskFromServer = gson.fromJson(response.body(), LIST_OF_TASKS);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(task, taskFromServer.getFirst());
    }
}