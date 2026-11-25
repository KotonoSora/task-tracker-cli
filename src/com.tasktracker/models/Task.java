package models;

public record Task(
        int id,
        String description,
        TaskStatus status,
        String createdAt,
        String updatedAt) {

    public Task withDescription(String description, String now) {
        return new Task(id, description, status, createdAt, now);
    }

    public Task withStatus(TaskStatus status, String now) {
        return new Task(id, description, status, createdAt, now);
    }
}