package server;

import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import server.handler.impl.EpicHandler;
import server.handler.impl.HistoryHandler;
import server.handler.impl.PrioritizedHandler;
import server.handler.impl.SubTaskHandler;
import server.handler.impl.TaskHandler;
import service.Managers;
import service.TaskManagerService;
import service.impl.FileBackedTaskManager;

/**
 * Запуск приложения
 */
public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final String TASKS = "/tasks";
    private static final String SUBTASKS = "/subtasks";
    private static final String EPICS = "/epics";
    private static final String HISTORY = "/history";
    private static final String PRIORITIZED = "/prioritized";

    private HttpServer httpServer;

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    public void start() throws IOException {
        String saveFile = "save/saveFile.csv";
        Path path = Path.of(saveFile);
        TaskManagerService service;
        if (Files.exists(path)) {
            service = Files.size(path) == 0 ? Managers.getDefault()
                    : FileBackedTaskManager.loadFromFile(saveFile);
        } else {
            service = Managers.getDefault();
        }
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext(TASKS, new TaskHandler(service));
        httpServer.createContext(EPICS, new EpicHandler(service));
        httpServer.createContext(SUBTASKS, new SubTaskHandler(service));
        httpServer.createContext(HISTORY, new HistoryHandler(service));
        httpServer.createContext(PRIORITIZED, new PrioritizedHandler(service));

        httpServer.start();
        System.out.println("сервер запущен на порту: " + PORT);
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            System.out.println("сервер остановлен");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void dropSave() throws IOException {
        String filesDirectory = "save";
        try (Stream<Path> files = Files.walk(Path.of(filesDirectory))) {
            files.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
