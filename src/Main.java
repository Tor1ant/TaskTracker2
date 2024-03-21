import java.time.OffsetDateTime;
import model.Epic;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManagerService;

public class Main {

    public static void main(String[] args) {
        System.out.println("Программа стартовала в: " + OffsetDateTime.now());
        TaskManagerService taskManager = Managers.getDefault();

        Task task1 = new Task("Закончить выполнение ТЗ", "Желательно сегодня");
        Task task2 = new Task("Поиграть с котом", "давно с ним не играли");
        Epic epic1 = new Epic("Сходить в магазин", "сегодня");
        Epic epic2 = new Epic("Навести порядок в квартире", "завтра");

        Task createdTask = taskManager.createTask(task1);
        Task createdTask2 = taskManager.createTask(task2);
        Epic createdEpic = taskManager.createEpic(epic1);
        Epic createdEpic2 = taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Купить молоко", "Простоквашино", createdEpic.getId());
        Subtask subtask2 = new Subtask("Купить мясо", "Свинину", createdEpic.getId());
        Subtask subtask3 = new Subtask("Купить картошку", "Мытую", createdEpic.getId());

        Subtask createdSubtask = taskManager.createSubtask(subtask1);
        Subtask createdSubtask2 = taskManager.createSubtask(subtask2);
        Subtask createdSubtask3 = taskManager.createSubtask(subtask3);

        System.out.println(taskManager.getTaskById(createdTask.getId()));
        System.out.println("--------------ИСТОРИЯ ПРОСМОТРОВ--------------");
        System.out.println(taskManager.getHistory());
        System.out.println("----------------------------------------------");
        System.out.println(taskManager.getSubTaskById(createdSubtask3.getId()));
        System.out.println("--------------ИСТОРИЯ ПРОСМОТРОВ--------------");
        System.out.println(taskManager.getHistory());
        System.out.println("----------------------------------------------");
        System.out.println(taskManager.getEpicById(createdEpic.getId()));
        System.out.println("--------------ИСТОРИЯ ПРОСМОТРОВ--------------");
        System.out.println(taskManager.getHistory());
        System.out.println("----------------------------------------------");
        System.out.println(taskManager.getTaskById(createdTask2.getId()));
        System.out.println("--------------ИСТОРИЯ ПРОСМОТРОВ--------------");
        System.out.println(taskManager.getHistory());
        System.out.println("----------------------------------------------");
        System.out.println(taskManager.getEpicById(createdEpic2.getId()));
        System.out.println("--------------ИСТОРИЯ ПРОСМОТРОВ--------------");
        System.out.println(taskManager.getHistory());
        System.out.println("----------------------------------------------");
        System.out.println(taskManager.getSubTaskById(createdSubtask2.getId()));
        System.out.println("--------------ИСТОРИЯ ПРОСМОТРОВ--------------");
        System.out.println(taskManager.getHistory());
        System.out.println("----------------------------------------------");
        System.out.println(taskManager.getSubTaskById(createdSubtask.getId()));
        System.out.println("--------------ИСТОРИЯ ПРОСМОТРОВ--------------");
        System.out.println(taskManager.getHistory());
        System.out.println("----------------------------------------------");
        System.out.println(taskManager.getTaskById(createdTask.getId()));
        System.out.println("--------------ИСТОРИЯ ПРОСМОТРОВ ПЕРВОЙ ТАСКИ НЕ ДОЛЖНО БЫТЬ В КОНЦЕ--------------");
        System.out.println(taskManager.getHistory());
        System.out.println("----------------------------------------------");
        System.out.println(taskManager.getHistory().size());
        taskManager.removeEpicById(epic1.getId());
        System.out.println(
                "--------------ИСТОРИЯ ПРОСМОТРОВ ДОЛЖНА БЫТЬ БЕЗ ПЕРВОГО ЭПИКА И ЕГО САБТАСОК--------------");
        System.out.println(taskManager.getHistory());
        System.out.println("----------------------------------------------");
        System.out.println(taskManager.getHistory().size());
    }
}
