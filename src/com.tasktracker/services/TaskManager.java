package services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import models.Task;
import models.TaskStatus;

public final class TaskManager {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<Task> tasks;

    public TaskManager(List<Task> tasks) {
        this.tasks = new ArrayList<Task>(tasks);
    }

    public List<Task> all() {
        return List.copyOf(tasks);
    }

    public List<Task> listWithStatus(TaskStatus status) {
        return tasks.stream()
                .filter(t -> t.status() == status)
                .toList();
    }

    public Task add(String desription) {
        String now = now();
        Task task = new Task(nextId(), requireText(desription), TaskStatus.TODO, now, now);
        tasks.add(task);
        return task;
    }

    public Task update(int id, String description) {
        String text = requireText(description);
        return replace(id, t -> t.withDescription(text, now()));
    }

    public Task mark(int id, TaskStatus status) {
        return replace(id, t -> t.withStatus(status, now()));
    }

    public Task delete(int id) {
        return tasks.remove(indexOf(id));
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

    private Task replace(int id, UnaryOperator<Task> change) {
        int index = indexOf(id);
        Task updated = change.apply(tasks.get(index));
        tasks.set(index, updated);
        return updated;
    }

    private int indexOf(int id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).id() == id) {
                return i;
            }
        }
        throw new IllegalArgumentException("Task not found with ID: " + id);
    }

}
