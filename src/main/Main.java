package main;

import enums.TaskStatus;
import java.util.HashMap;
import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManagerService;
import service.impl.TaskManagerServiceImpl;

public class Main {

    public static void main(String[] args) { // метод для проверки кода
        TaskManagerService taskManagerService = new TaskManagerServiceImpl(new HashMap<>(), new HashMap<>(),
                new HashMap<>());
        Task task1 = new Task("Закончить выполнение ТЗ", "Желательно сегодня", TaskStatus.NEW);
        Task task2 = new Task("Поиграть с котом", "давно с ним не играли", TaskStatus.NEW);

        Task task1AfterCreate = taskManagerService.createTask(task1);
        Task task2AfterCreate = taskManagerService.createTask(task2);

        Epic epic1 = new Epic("Сходить в магазин", "сегодня");
        Epic epic2 = new Epic("Посмотреть фильмы", "за эту неделю");

        Epic createdEpic1 = taskManagerService.createEpic(epic1);
        Epic createdEpic2 = taskManagerService.createEpic(epic2);

        Subtask subtask1ForEpic1 = new Subtask("Купить молоко", "Простоквашино", TaskStatus.NEW, createdEpic1.getId());
        Subtask subtask2ForEpic1 = new Subtask("Купить шоколадку", "милка", TaskStatus.NEW, createdEpic1.getId());
        Subtask subtask1ForEpic2 = new Subtask("Джон Уик 4", "идёт 3 часа", TaskStatus.NEW, createdEpic2.getId());

        Subtask subtask1ForEpic1AfterCreate = taskManagerService.createSubTask(subtask1ForEpic1);
        Subtask subtask2ForEpic1AfterCreate = taskManagerService.createSubTask(subtask2ForEpic1);
        Subtask subtask1ForEpic2AfterCreate = taskManagerService.createSubTask(subtask1ForEpic2);

        System.out.println(taskManagerService.getTasks());
        System.out.println(taskManagerService.getSubTasks());
        System.out.println(taskManagerService.getEpics());
        System.out.println("--------------------------------------------------");

        task1AfterCreate.setStatus(TaskStatus.DONE);
        task2AfterCreate.setStatus(TaskStatus.IN_PROGRESS);

        taskManagerService.updateTask(task1AfterCreate);
        taskManagerService.updateTask(task2AfterCreate);

        subtask1ForEpic1AfterCreate.setStatus(TaskStatus.NEW);
        subtask2ForEpic1AfterCreate.setStatus(TaskStatus.DONE);
        subtask1ForEpic2AfterCreate.setStatus(TaskStatus.DONE);

        taskManagerService.updateSubTask(subtask1ForEpic1AfterCreate);
        taskManagerService.updateSubTask(subtask2ForEpic1AfterCreate);
        taskManagerService.updateSubTask(subtask1ForEpic2AfterCreate);

        System.out.println(taskManagerService.getTasks());
        System.out.println(taskManagerService.getSubTasks());
        System.out.println(taskManagerService.getEpics());
        System.out.println("--------------------------------------------------");

        taskManagerService.removeTask(task1AfterCreate.getId());
        taskManagerService.removeEpicById(createdEpic2.getId());
        taskManagerService.removeSubTask(subtask2ForEpic1AfterCreate.getId());

        System.out.println(taskManagerService.getTasks());
        System.out.println(taskManagerService.getSubTasks());
        System.out.println(taskManagerService.getEpics());
        System.out.println("--------------------------------------------------");
    }
}
