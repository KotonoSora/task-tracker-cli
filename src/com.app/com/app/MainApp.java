package com.app;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import array.JsonArray;
import object.JsonObject;
import parser.JsonParser;
import serializer.JsonSerializer;
import utils.JsonUtils;

public class MainApp {

    private static final String INPUT_FILE = "data/tasks.json";
    private static final String OUTPUT_FILE = "data/tasks-output.json";

    public static void main(String[] args) {

        try {
            // 1. Read and parse
            JsonArray tasks = loadTasks(INPUT_FILE);
            System.out.println("JSON parsed successfully!");
            System.out.println("Total tasks: " + tasks.size());

            // 2. List
            printTasks(tasks);

            // 3. Update
            updateTask(tasks, "1", "done");

            // 4. Add
            addTask(tasks, "Write unit tests", "todo");

            // 5. Remove
            removeTask(tasks, "2");

            // 6. Serialize and write
            String output = new JsonSerializer().serializer(tasks);
            JsonUtils.write(OUTPUT_FILE, output);
            System.out.println("Saved " + tasks.size() + " tasks to " + OUTPUT_FILE);

        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());

        } catch (RuntimeException e) {
            System.err.println("JSON error: " + e.getMessage());
        }
    }

    // =====================================
    // Load
    // =====================================

    private static JsonArray loadTasks(String path) throws IOException {

        String json = JsonUtils.read(path);
        Object data = new JsonParser(json).parse();

        if (!(data instanceof JsonArray tasks)) {
            throw new IllegalStateException(
                    "Invalid JSON structure: expected an array.");
        }

        return tasks;
    }

    // =====================================
    // List
    // =====================================

    private static void printTasks(JsonArray tasks) {

        for (int i = 0; i < tasks.size(); i++) {

            if (tasks.get(i) instanceof JsonObject task) {

                System.out.println("----------------------");
                System.out.println("ID: " + task.get("id"));
                System.out.println("Description: " + task.get("description"));
                System.out.println("Status: " + task.get("status"));
                System.out.println("Created At: " + task.get("createdAt"));
                System.out.println("Updated At: " + task.get("updatedAt"));
            }
        }
    }

    // =====================================
    // Update
    // =====================================

    private static void updateTask(JsonArray tasks, String id, String status) {

        JsonObject task = findTask(tasks, id);

        if (task == null) {
            System.out.println("Task " + id + " not found, skipping update.");
            return;
        }

        task.put("status", status);
        task.put("updatedAt", Instant.now().toString());
        System.out.println("Updated task " + id + " to '" + status + "'.");
    }

    // =====================================
    // Add
    // =====================================

    private static void addTask(JsonArray tasks, String description, String status) {

        String now = Instant.now().toString();

        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("id", String.valueOf(nextId(tasks)));
        fields.put("description", description);
        fields.put("status", status);
        fields.put("createdAt", now);
        fields.put("updatedAt", now);

        tasks.add(JsonObject.createObject(fields));
        System.out.println("Added task: " + description);
    }

    // =====================================
    // Remove
    // =====================================

    private static void removeTask(JsonArray tasks, String id) {

        for (int i = 0; i < tasks.size(); i++) {

            if (tasks.get(i) instanceof JsonObject task
                    && id.equals(String.valueOf(task.get("id")))) {

                tasks.remove(i);
                System.out.println("Removed task " + id + ".");
                return;
            }
        }

        System.out.println("Task " + id + " not found, skipping removal.");
    }

    // =====================================
    // Helpers
    // =====================================

    private static JsonObject findTask(JsonArray tasks, String id) {

        for (int i = 0; i < tasks.size(); i++) {

            if (tasks.get(i) instanceof JsonObject task
                    && id.equals(String.valueOf(task.get("id")))) {

                return task;
            }
        }

        return null;
    }

    private static long nextId(JsonArray tasks) {

        long max = 0;

        for (int i = 0; i < tasks.size(); i++) {

            if (tasks.get(i) instanceof JsonObject task) {
                max = Math.max(max, Long.parseLong(
                        String.valueOf(task.get("id"))));
            }
        }

        return max + 1;
    }
}