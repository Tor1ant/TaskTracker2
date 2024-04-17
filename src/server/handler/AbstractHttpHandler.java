package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import config.DurationTypeAdapter;
import config.LocalDateTimeTypeAdapter;
import exception.ManagerSaveException;
import exception.MethondNotAllowed;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import server.enumerated.HttpMethod;
import server.model.RequestParams;

public abstract class AbstractHttpHandler<T> implements HttpHandler {

    private static final Logger logger = Logger.getLogger(AbstractHttpHandler.class.getName());
    protected final int HTTP_OK = 200;
    protected final int HTTP_CREATED = 201;
    protected final int NOT_FOUND = 404;
    protected final int NOT_ACCEPTABLE = 406;
    protected final int METHOD_NOT_ALLOWED = 405;
    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .setPrettyPrinting()
            .create();
    protected HttpExchange httpExchange;

    protected <I, S extends List<T>> Map<Integer, List<T>> processRequest(
            HttpMethod httpMethod,
            I id,
            T task,
            Function<I, T> byIdFunction,
            Supplier<S> getAllFunction,
            Function<T, T> createFunction,
            Runnable removeAllFunction
    ) {
        boolean isNull = id == null;
        return switch (httpMethod) {
            case GET -> {
                if (!isNull) {
                    yield getById(byIdFunction, id);
                } else {
                    yield getAll(getAllFunction);
                }
            }
            case POST -> {
                if (!isNull) {
                    yield update(createFunction, task);
                } else {
                    yield create(createFunction, task);
                }
            }
            case DELETE -> {
                if (!isNull) {
                    yield removeById(byIdFunction, id);
                } else {
                    yield removeAll(removeAllFunction);
                }
            }
        };
    }

    protected RequestParams getRequestParams(HttpExchange exchange) {
        this.httpExchange = exchange;
        HttpMethod httpMethod = getHttpMethod(exchange);
        Optional<Integer> taskIdFromPath = getTaskIdFromPath(exchange);

        InputStream requestBody = exchange.getRequestBody();

        String requestBodyInString;
        try {
            requestBodyInString = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.info("Тело запроса пустое");
            requestBodyInString = null;
        }
        return new RequestParams(httpMethod, taskIdFromPath, requestBodyInString);
    }

    public T getTaskFromJson(String jsonTask, Class<T> taskClass) {
        return gson.fromJson(jsonTask, taskClass);
    }

    protected <V> void sendResponse(Map<Integer, List<V>> response) {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "application/json; charset=utf-8");
        String jsonResponse = gson.toJson(response.values().iterator().next());
        byte[] jsonData = jsonResponse.getBytes(StandardCharsets.UTF_8);
        try {
            httpExchange.sendResponseHeaders(response.keySet().iterator().next(), jsonData.length);
            try (OutputStream outputStream = httpExchange.getResponseBody();
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
                bufferedOutputStream.write(jsonData);
                bufferedOutputStream.flush();
            }
        } catch (IOException e) {
            logger.info("Произошла ошибка при отправке запроса");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected HttpMethod getHttpMethod(HttpExchange httpExchange) {
        String requestMethod = httpExchange.getRequestMethod();

        if (requestMethod.equals(HttpMethod.GET.toString())) {
            return HttpMethod.GET;
        }
        if (requestMethod.equals(HttpMethod.POST.toString())) {
            return HttpMethod.POST;
        }
        if (requestMethod.equals(HttpMethod.DELETE.toString())) {
            return HttpMethod.DELETE;
        }
        sendResponse(Map.of(METHOD_NOT_ALLOWED, List.of("Метод " + requestMethod + " не поддерживается")));
        throw new MethondNotAllowed("Метод " + requestMethod + " не поддерживается");
    }

    protected Optional<Integer> getTaskIdFromPath(HttpExchange httpExchange) {
        String fullPath = httpExchange.getRequestURI().getPath();
        String[] endpoints = fullPath.split("/");
        if (endpoints.length >= 3) {
            return Optional.of(Integer.valueOf(endpoints[2]));
        }
        return Optional.empty();
    }

    protected Map<Integer, List<T>> update(Function<T, T> function, T t) {
        try {
            if (function.apply(t) == null) {
                return Map.of(NOT_FOUND, Collections.emptyList());
            }
            return Map.of(HTTP_CREATED, List.of(function.apply(t)));
        } catch (ManagerSaveException e) {
            String message = "Задача пересекается с одной из существующих. Выберете другое время.";
            logger.info(message);
            sendResponse(Map.of(NOT_ACCEPTABLE, List.of(message)));
            throw new RuntimeException(e);
        }
    }


    protected Map<Integer, List<T>> create(Function<T, T> function, T t) {
        try {
            return Map.of(HTTP_CREATED, List.of(function.apply(t)));
        } catch (ManagerSaveException e) {
            String message = "Задача пересекается с одной из существующих. Выберете другое время.";
            logger.info(message);
            sendResponse(Map.of(NOT_ACCEPTABLE, List.of(message)));
            throw new RuntimeException(e);
        }
    }

    protected <S extends List<T>> Map<Integer, List<T>> getAll(Supplier<S> service) {
        return Map.of(HTTP_OK, service.get());
    }

    protected <I> Map<Integer, List<T>> getById(Function<I, T> function, I i) {
        T task = function.apply(i);
        if (task == null) {
            return Map.of(NOT_FOUND, Collections.emptyList());
        }
        return Map.of(HTTP_OK, List.of(task));
    }

    protected <V> Map<Integer, List<V>> removeAll(Runnable service) {
        service.run();
        return Map.of(HTTP_OK, Collections.emptyList());
    }

    protected <I> Map<Integer, List<T>> removeById(Function<I, T> function, I i) {
        T removedTask = function.apply(i);
        return Map.of(HTTP_OK, List.of(removedTask));
    }
}
