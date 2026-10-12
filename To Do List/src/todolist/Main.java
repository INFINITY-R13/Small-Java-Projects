package todolist;

import todolist.storage.JsonTaskStorage;
import todolist.storage.TaskStorage;
import todolist.ui.TodoListFrame;

import javax.swing.*;
import java.nio.file.Path;

/**
 * Application entry point for the To-Do List Desktop App.
 */
public class Main {
    public static void main(String[] args) {
        // Set native system look and feel for modern platform appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default Swing look and feel
        }

        // Default storage file in working directory
        Path dataPath = Path.of("tasks.json");
        TaskStorage storage = new JsonTaskStorage(dataPath);

        // Run GUI on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            TodoListFrame frame = new TodoListFrame(storage);
            frame.setVisible(true);
        });
    }
}
