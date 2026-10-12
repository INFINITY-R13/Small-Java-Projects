package todolist.ui;

import todolist.model.Priority;
import todolist.model.Task;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for managing and displaying tasks in a JTable.
 */
public class TaskTableModel extends AbstractTableModel {
    public static final int COL_STATUS = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_PRIORITY = 2;
    public static final int COL_DUE_DATE = 3;
    public static final int COL_DESCRIPTION = 4;

    private static final String[] COLUMN_NAMES = {
            "Done", "Task Title", "Priority", "Due Date", "Description"
    };

    private static final Class<?>[] COLUMN_CLASSES = {
            Boolean.class, String.class, Priority.class, LocalDate.class, String.class
    };

    private final List<Task> tasks;
    private final Runnable onDataChanged;

    public TaskTableModel(List<Task> initialTasks, Runnable onDataChanged) {
        this.tasks = new ArrayList<>(initialTasks != null ? initialTasks : List.of());
        this.onDataChanged = onDataChanged;
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_CLASSES[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == COL_STATUS; // Only checkbox is directly editable inline
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= tasks.size()) return null;
        Task task = tasks.get(rowIndex);
        return switch (columnIndex) {
            case COL_STATUS -> task.isCompleted();
            case COL_TITLE -> task.getTitle();
            case COL_PRIORITY -> task.getPriority();
            case COL_DUE_DATE -> task.getDueDate();
            case COL_DESCRIPTION -> task.getDescription();
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= tasks.size()) return;
        Task task = tasks.get(rowIndex);

        if (columnIndex == COL_STATUS && aValue instanceof Boolean completed) {
            task.setCompleted(completed);
            fireTableRowsUpdated(rowIndex, rowIndex);
            notifyDataChanged();
        }
    }

    public Task getTaskAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < tasks.size()) {
            return tasks.get(rowIndex);
        }
        return null;
    }

    public void addTask(Task task) {
        tasks.add(0, task); // Add new tasks to the top
        fireTableRowsInserted(0, 0);
        notifyDataChanged();
    }

    public void updateTask(int rowIndex, Task updated) {
        if (rowIndex >= 0 && rowIndex < tasks.size()) {
            Task task = tasks.get(rowIndex);
            task.setTitle(updated.getTitle());
            task.setDescription(updated.getDescription());
            task.setPriority(updated.getPriority());
            task.setDueDate(updated.getDueDate());
            task.setCompleted(updated.isCompleted());
            fireTableRowsUpdated(rowIndex, rowIndex);
            notifyDataChanged();
        }
    }

    public void removeTask(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < tasks.size()) {
            tasks.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
            notifyDataChanged();
        }
    }

    public List<Task> removeCompletedTasks() {
        List<Task> removed = new ArrayList<>();
        for (int i = tasks.size() - 1; i >= 0; i--) {
            if (tasks.get(i).isCompleted()) {
                removed.add(tasks.remove(i));
                fireTableRowsDeleted(i, i);
            }
        }
        if (!removed.isEmpty()) {
            notifyDataChanged();
        }
        return removed;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public int getCompletedCount() {
        int count = 0;
        for (Task t : tasks) {
            if (t.isCompleted()) count++;
        }
        return count;
    }

    public int getActiveCount() {
        return tasks.size() - getCompletedCount();
    }

    private void notifyDataChanged() {
        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }
}
