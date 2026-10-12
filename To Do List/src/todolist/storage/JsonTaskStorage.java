package todolist.storage;

import todolist.model.Priority;
import todolist.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Persists tasks to a local JSON file with zero external dependencies.
 */
public class JsonTaskStorage implements TaskStorage {
    private final Path filePath;

    public JsonTaskStorage(Path filePath) {
        this.filePath = filePath;
    }

    public JsonTaskStorage(String fileName) {
        this(Path.of(fileName));
    }

    @Override
    public String getStorageLocation() {
        return filePath.toAbsolutePath().toString();
    }

    @Override
    public List<Task> loadTasks() throws IOException {
        if (!Files.exists(filePath)) {
            List<Task> initialTasks = createSampleTasks();
            saveTasks(initialTasks);
            return initialTasks;
        }

        String content = Files.readString(filePath, StandardCharsets.UTF_8).trim();
        if (content.isEmpty()) {
            List<Task> initialTasks = createSampleTasks();
            saveTasks(initialTasks);
            return initialTasks;
        }

        List<Map<String, String>> objectList = parseObjectArray(content);
        List<Task> tasks = new ArrayList<>();

        for (Map<String, String> obj : objectList) {
            String id = obj.get("id");
            String title = obj.get("title");
            String description = obj.get("description");
            String priorityStr = obj.get("priority");
            String dueDateStr = obj.get("dueDate");
            String completedStr = obj.get("completed");
            String createdAtStr = obj.get("createdAt");

            Priority priority = Priority.fromString(priorityStr);
            LocalDate dueDate = (dueDateStr != null && !dueDateStr.isBlank()) ? LocalDate.parse(dueDateStr) : null;
            boolean completed = "true".equalsIgnoreCase(completedStr);
            LocalDateTime createdAt = (createdAtStr != null && !createdAtStr.isBlank()) 
                    ? LocalDateTime.parse(createdAtStr) 
                    : LocalDateTime.now();

            tasks.add(new Task(id, title, description, priority, dueDate, completed, createdAt));
        }

        return tasks;
    }

    @Override
    public void saveTasks(List<Task> tasks) throws IOException {
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            sb.append("  {\n");
            sb.append("    \"id\": \"").append(escapeJson(t.getId())).append("\",\n");
            sb.append("    \"title\": \"").append(escapeJson(t.getTitle())).append("\",\n");
            sb.append("    \"description\": \"").append(escapeJson(t.getDescription())).append("\",\n");
            sb.append("    \"priority\": \"").append(t.getPriority().name()).append("\",\n");
            sb.append("    \"dueDate\": ").append(t.getDueDate() != null ? "\"" + t.getDueDate().toString() + "\"" : "null").append(",\n");
            sb.append("    \"completed\": ").append(t.isCompleted()).append(",\n");
            sb.append("    \"createdAt\": \"").append(t.getCreatedAt().toString()).append("\"\n");
            sb.append("  }");
            if (i < tasks.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]\n");

        Files.writeString(filePath, sb.toString(), StandardCharsets.UTF_8);
    }

    private List<Task> createSampleTasks() {
        List<Task> list = new ArrayList<>();
        list.add(new Task(
                "Welcome to your To-Do List!",
                "Explore features: add, edit, mark complete, filter, and prioritize tasks.",
                Priority.HIGH,
                LocalDate.now()
        ));
        list.add(new Task(
                "Learn Java Swing UI components",
                "Understand JTable, TableRowSorter, custom renderers, and event listeners.",
                Priority.MEDIUM,
                LocalDate.now().plusDays(2)
        ));
        list.add(new Task(
                "Check off this completed task",
                "Click the checkbox on the left to toggle task status.",
                Priority.LOW,
                null
        ));
        list.get(2).setCompleted(true);
        return list;
    }

    // --- Lightweight JSON Parser for Array of Objects ---

    private static List<Map<String, String>> parseObjectArray(String json) {
        List<Map<String, String>> list = new ArrayList<>();
        if (json == null || json.isBlank()) return list;

        int i = 0;
        int len = json.length();

        while (i < len && json.charAt(i) != '[') i++;
        if (i >= len) return list;
        i++; // past '['

        while (i < len) {
            while (i < len && Character.isWhitespace(json.charAt(i))) i++;
            if (i >= len || json.charAt(i) == ']') break;
            if (json.charAt(i) == ',') {
                i++;
                continue;
            }

            if (json.charAt(i) == '{') {
                i++;
                Map<String, String> obj = new LinkedHashMap<>();
                while (i < len) {
                    while (i < len && Character.isWhitespace(json.charAt(i))) i++;
                    if (i >= len || json.charAt(i) == '}') {
                        i++;
                        break;
                    }
                    if (json.charAt(i) == ',') {
                        i++;
                        continue;
                    }

                    if (json.charAt(i) != '"') {
                        i++;
                        continue;
                    }

                    String key = readString(json, i);
                    i = skipString(json, i);

                    while (i < len && json.charAt(i) != ':') i++;
                    if (i < len && json.charAt(i) == ':') i++;

                    while (i < len && Character.isWhitespace(json.charAt(i))) i++;

                    if (i < len && json.charAt(i) == '"') {
                        String val = readString(json, i);
                        obj.put(key, val);
                        i = skipString(json, i);
                    } else {
                        int start = i;
                        while (i < len && json.charAt(i) != ',' && json.charAt(i) != '}' && !Character.isWhitespace(json.charAt(i))) {
                            i++;
                        }
                        String val = json.substring(start, i).trim();
                        if ("null".equalsIgnoreCase(val)) {
                            obj.put(key, null);
                        } else {
                            obj.put(key, val);
                        }
                    }
                }
                list.add(obj);
            } else {
                i++;
            }
        }
        return list;
    }

    private static String readString(String json, int startQuoteIdx) {
        StringBuilder sb = new StringBuilder();
        int i = startQuoteIdx + 1;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\') {
                i++;
                if (i < json.length()) {
                    char next = json.charAt(i);
                    switch (next) {
                        case '"' -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        case '/' -> sb.append('/');
                        case 'b' -> sb.append('\b');
                        case 'f' -> sb.append('\f');
                        case 'n' -> sb.append('\n');
                        case 'r' -> sb.append('\r');
                        case 't' -> sb.append('\t');
                        default -> sb.append(next);
                    }
                }
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
            i++;
        }
        return sb.toString();
    }

    private static int skipString(String json, int startQuoteIdx) {
        int i = startQuoteIdx + 1;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\') {
                i += 2;
            } else if (c == '"') {
                return i + 1;
            } else {
                i++;
            }
        }
        return i;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
}
