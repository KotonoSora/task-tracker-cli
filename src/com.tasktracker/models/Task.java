package models;

public record Task(
        int id,
        String description,
        TaskStatus status,
        String createdAt,
        String updatedAt) {

    public Task withDescription(String description) {
        return new Task(id, description, status, createdAt, updatedAt);
    }

    public Task withStatus(TaskStatus status) {
        return new Task(id, description, status, createdAt, updatedAt);
    }
}