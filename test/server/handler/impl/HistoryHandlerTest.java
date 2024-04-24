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

@DisplayName("Тестирование обработчика получения истории просмотров")
class HistoryHandlerTest {

    private static final URI HISTORY_URL = URI.create("http://localhost:8080/history");
    private static final URI TASK_URI = URI.create("http://localhost:8080/tasks");
    private static final Type LIST_OF_TASKS = new TypeToken<ArrayList<Task>>() {
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
    @DisplayName("Проверка получения списка истории просмотров ")
    void getHistory() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(TASK_URI)
                .POST(BodyPublishers.ofString(TASK_JSON))
                .build();
        HttpRequest getTaskById = HttpRequest.newBuilder(URI.create(TASK_URI + "/" + 1))
                .GET()
                .build();

        HttpRequest getHistory = HttpRequest.newBuilder(HISTORY_URL)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(httpRequest, handler);
        HttpResponse<String> response = client.send(getTaskById, handler);
        HttpResponse<String> history = client.send(getHistory, handler);

        List<Task> taskFromServer = gson.fromJson(response.body(), LIST_OF_TASKS);
        List<Task> historyFromServer = gson.fromJson(history.body(), LIST_OF_TASKS);
        Assertions.assertEquals(200, history.statusCode());
        Assertions.assertEquals(taskFromServer, historyFromServer);
    }
}