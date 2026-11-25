package storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import array.JsonArray;
import converter.JsonConverter;
import models.Task;
import models.TaskStatus;
import object.JsonObject;
import parser.JsonParser;
import serializer.JsonSerializer;
import utils.JsonUtils;

public final class TaskStorage {

    private final Path file;
    private final JsonConverter converter = new JsonConverter();

    public TaskStorage(Path file) {
        this.file = file;
    }

    public List<Task> load() throws IOException {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }

        Object root = new JsonParser(JsonUtils.read(file)).parse();

        if (!(root instanceof JsonArray array)) {
            throw new IllegalArgumentException("File must contain a JSON array");
        }

        List<Task> tasks = new ArrayList<>();
        for (Object item : array.values()) {
            tasks.add(fromJson(item));
        }
        return tasks;
    }

    public void save(List<Task> tasks) throws IOException {
        JsonArray array = new JsonArray();
        for (Task task : tasks) {
            array.add(JsonObject.createObject(toJson(task)));
        }
        JsonUtils.write(file, new JsonSerializer().serializer(array));
    }

    private Task fromJson(Object item) {
        if (!(item instanceof JsonObject json)) {
            throw new IllegalArgumentException("Each task must be a JSON object");
        }

        return new Task(
                requireInt(json, "id"),
                requireString(json, "description"),
                TaskStatus.fromCode(requireInt(json, "status")),
                requireString(json, "createdAt"),
                requireString(json, "updatedAt"));
    }

    private int requireInt(JsonObject json, String key) {
        Object value = json.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing field: " + key);
        }
        return converter.toInteger(value);
    }

    private String requireString(JsonObject json, String key) {
        Object value = json.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing field: " + key);
        }
        return converter.toString(value);
    }

    private static Map<String, Object> toJson(Task task) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", task.id());
        map.put("description", task.description());
        map.put("status", task.status().code());
        map.put("createdAt", task.createdAt());
        map.put("updatedAt", task.updatedAt());
        return map;
    }
}
