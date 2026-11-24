package services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import models.Task;
import models.TaskStatus;

public final class TaskManager {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<Task> tasks;

    public TaskManager(List<Task> tasks) {
        this.tasks = new ArrayList<Task>(tasks);
    }

    public Task add(String desription) {
        String now = now();
        Task task = new Task(nextId(), requireText(desription), TaskStatus.TODO, now, now);
        tasks.add(task);
        return task;
    }

    private static String now() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(FORMAT);
    }

    private int nextId() {
        return tasks.stream().mapToInt(Task::id).max().orElse(0) + 1;
    }

    private static String requireText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Description cannot be blank");
        }
        return text.strip();
    }

}
