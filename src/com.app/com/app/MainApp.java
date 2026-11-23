package com.app;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import parser.JsonParser;
import utils.JsonUtils;

public class MainApp {

    public static void main(String[] args) {

        try {
            // 1. Read JSON file
            String jsonString = JsonUtils.read("data/tasks.json");

            // 2. Parse JSON
            JsonParser parser = new JsonParser(jsonString);
            Object data = parser.parse();

            // 3. Verify parsed data
            if (data instanceof List<?> tasks) {

                System.out.println("JSON parsed successfully!");
                System.out.println("Total tasks: " + tasks.size());

                for (Object item : tasks) {

                    if (item instanceof Map<?, ?> task) {

                        System.out.println("----------------------");
                        System.out.println("ID: " + task.get("id"));
                        System.out.println("Description: " + task.get("description"));
                        System.out.println("Status: " + task.get("status"));
                        System.out.println("Created At: " + task.get("createdAt"));
                        System.out.println("Updated At: " + task.get("updatedAt"));

                    }
                }

            } else {
                System.out.println("Invalid JSON structure: Expected an array.");
            }

        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());

        } catch (RuntimeException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
        }
    }
}
