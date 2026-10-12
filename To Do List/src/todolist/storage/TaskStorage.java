package todolist.storage;

import todolist.model.Task;
import java.io.IOException;
import java.util.List;

/**
 * Interface defining operations for loading and saving tasks.
 */
public interface TaskStorage {
    List<Task> loadTasks() throws IOException;
    void saveTasks(List<Task> tasks) throws IOException;
    String getStorageLocation();
}
