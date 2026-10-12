package todolist.storage;

import todolist.model.Priority;
import todolist.model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class JsonTaskStorageTest {
    public static void main(String[] args) throws IOException {
        Path tempFile = Files.createTempFile("tasks_test_", ".json");
        try {
            JsonTaskStorage storage = new JsonTaskStorage(tempFile);

            // 1. Initial load should populate sample tasks
            List<Task> initial = storage.loadTasks();
            assert !initial.isEmpty() : "Initial tasks should not be empty";
            System.out.println("PASS: Initial load created " + initial.size() + " sample tasks.");

            // 2. Add custom task with special characters and multiline text
            Task customTask = new Task(
                    "Test \"Quotes\" and \\backslashes\\ & special chars",
                    "Line 1\nLine 2\nLine 3 with tab\t!",
                    Priority.HIGH,
                    LocalDate.of(2026, 12, 25)
            );
            initial.add(customTask);
            storage.saveTasks(initial);

            // 3. Reload and verify
            List<Task> reloaded = storage.loadTasks();
            assert reloaded.size() == initial.size() : "Size mismatch after reload";

            Task found = reloaded.stream()
                    .filter(t -> t.getTitle().contains("Quotes"))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Custom task not found"));

            assert found.getPriority() == Priority.HIGH : "Priority mismatch";
            assert LocalDate.of(2026, 12, 25).equals(found.getDueDate()) : "Due date mismatch";
            assert found.getDescription().contains("Line 2") : "Description mismatch";
            System.out.println("PASS: Save and reload with special characters succeeded.");
            System.out.println("ALL TESTS PASSED SUCCESSFULLY!");
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
