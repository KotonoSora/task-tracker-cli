# JSON Utils

## Requirements

- Java 25+

## Read, Parse, and Write a File

```java
import java.io.IOException;

import object.JsonObject;
import parser.JsonParser;
import serializer.JsonSerializer;
import utils.JsonUtils;

public class Example {

    public static void main(String[] args) throws IOException {

        String json = JsonUtils.read("user.json");

        Object data = new JsonParser(json).parse();

        if (data instanceof JsonObject user) {
            System.out.println(user.get("name"));
            user.put("status", "active");
        }

        String output = new JsonSerializer().serializer(data);

        JsonUtils.write("output.json", output);
    }
}
```

`JsonUtils.read` and `JsonUtils.write` also accept a `java.nio.file.Path`.

```java
import java.nio.file.Path;

String json = JsonUtils.read(Path.of("data", "user.json"));
JsonUtils.write(Path.of("data", "output.json"), output);
```

`write` creates missing parent directories and overwrites the file if it exists.

## Parsing

```java
import parser.DuplicateKeyPolicy;
import parser.JsonParser;

// Default: reject duplicate keys, max depth 256
JsonParser parser = new JsonParser(json);

// Keep the last value for duplicate keys
JsonParser parser = new JsonParser(json, DuplicateKeyPolicy.KEEP_LAST);

// Keep the first value for duplicate keys, max depth 64
JsonParser parser = new JsonParser(json, DuplicateKeyPolicy.KEEP_FIRST, 64);

Object data = parser.parse();
```

Parsed values map to Java types as follows:

| JSON    | Java         |
|---------|--------------|
| object  | `JsonObject` |
| array   | `JsonArray`  |
| string  | `String`     |
| number  | `BigDecimal` |
| boolean | `Boolean`    |
| null    | `null`       |

## Working with Objects and Arrays

```java
import array.JsonArray;
import object.JsonObject;

JsonObject task = new JsonObject();
task.put("id", "1");
task.put("status", "todo");

task.get("status");          // "todo"
task.containsKey("id");      // true
task.remove("status");       // "todo"
task.size();                 // 1
task.keys();                 // Set<String>, read-only
task.toMap();                // mutable copy as Map<String, Object>

JsonObject empty = JsonObject.createObject();

JsonArray tasks = new JsonArray();
tasks.add(task);
tasks.add(0, empty);         // insert at index 0
tasks.get(0);                // empty
tasks.set(0, task);          // replace element, returns the old value
tasks.indexOf(task);         // 0
tasks.remove(0);             // removes by index
tasks.remove(task);          // removes by value
tasks.size();
tasks.isEmpty();
tasks.toList();              // mutable copy as List<Object>
```

## Serializing

```java
import formatter.JsonFormatStyle;
import serializer.JsonSerializer;

JsonSerializer serializer = new JsonSerializer();

String pretty  = serializer.serializer(data);                          // default: PRETTY
String compact = serializer.serializer(data, JsonFormatStyle.COMPACT);
```

`serializer` accepts `JsonObject`, `JsonArray`, `Map`, `List`, Java arrays, `String`, `Number`, `Boolean`, and `null`.

## Converting

```java
import converter.JsonConverter;

JsonConverter converter = new JsonConverter();

converter.toString(42);                        // "42"
converter.toInteger("42");                     // 42
converter.toInteger(true);                     // 1
converter.toLong(new BigDecimal("9000000000")); // 9000000000
converter.toDouble("3.14");                    // 3.14

converter.toMap(jsonObject);                   // Map<String, Object>
converter.toList(jsonArray);                   // List<Object>

// Deep conversion of a parsed document to plain Java types
Object plain = converter.toConvert(data);
```

Numeric conversions throw `IllegalArgumentException` when the value does not fit exactly, for example `converter.toInteger(new BigDecimal("1.5"))`.

## JsonPath

```java
import path.JsonPath;
import path.JsonPathGetter;
import path.JsonPathHas;
import path.JsonPathRemove;
import path.JsonPathSetter;

JsonPath path = new JsonPath(data);

// Get
JsonPathGetter getter = path.getter();
String status    = getter.getString("tasks[0].status");
Integer priority = getter.getInteger("tasks[0].priority");
String nickname  = getter.getStringOrDefault("user.nickname", "guest");
Integer count    = getter.getLength("tasks");

// Set (missing intermediate objects and arrays are created)
JsonPathSetter setter = path.setter();
setter.set("tasks[0].status", "done");
setter.set("settings.theme.color", "dark");

// Check
JsonPathHas has = path.has();
boolean exists   = has.has("tasks[0].id");
boolean hasValue = has.hasValue("tasks[0].id");

// Remove
JsonPathRemove remove = path.remove();
remove.remove("tasks[0].description");
boolean removed = remove.removeIfExists("tasks[5]");
```

Path syntax uses dots for properties and brackets for array indexes, for example `tasks[0].status`.

Typed getters return `null` for a JSON `null`. They throw `JsonPathException` when the value has a different type.

## Errors

| Exception                    | Thrown when                                       |
|------------------------------|---------------------------------------------------|
| `JsonSyntaxException`        | Invalid JSON, with the position in the input      |
| `JsonUnexpectedEndException` | Input ends before the JSON is complete            |
| `JsonPathException`          | Invalid path, missing property, or type mismatch  |
| `JsonSerializationException` | Value cannot be written as JSON                   |
| `JsonFormatException`        | Formatting failed                                 |
| `JsonParserException`        | General parser failure                            |
| `IOException`                | File read or write failed                         |

## License

This project is licensed under the MIT License.