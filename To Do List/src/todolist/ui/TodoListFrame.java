package todolist.ui;

import todolist.model.Priority;
import todolist.model.Task;
import todolist.storage.TaskStorage;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Main window for the To-Do List desktop application.
 */
public class TodoListFrame extends JFrame {
    private final TaskStorage storage;
    private TaskTableModel tableModel;
    private JTable taskTable;
    private TableRowSorter<TaskTableModel> rowSorter;

    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JLabel statusCountLabel;
    private JLabel statusFileLabel;

    public TodoListFrame(TaskStorage storage) {
        super("To-Do List Manager");
        this.storage = storage;

        initData();
        initUI();
        updateStatus();
    }

    private void initData() {
        List<Task> initialTasks;
        try {
            initialTasks = storage.loadTasks();
        } catch (IOException e) {
            initialTasks = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                    "Failed to load tasks from " + storage.getStorageLocation() + ":\n" + e.getMessage(),
                    "Storage Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        tableModel = new TaskTableModel(initialTasks, this::onTasksChanged);
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 620);
        setMinimumSize(new Dimension(750, 450));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Save on window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveTasksToStorage();
            }
        });
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(15, 10));
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(220, 224, 230)),
                new EmptyBorder(16, 20, 16, 20)
        ));
        header.setBackground(Color.WHITE);

        // Title and description
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("To-Do List");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 43, 54));

        JLabel subLabel = new JLabel("Track your priorities, deadlines, and daily progress");
        subLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subLabel.setForeground(new Color(120, 130, 140));

        titlePanel.add(titleLabel);
        titlePanel.add(subLabel);
        header.add(titlePanel, BorderLayout.WEST);

        // Search & Filter controls
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        filterPanel.add(searchLabel);

        searchField = new JTextField(14);
        searchField.setToolTipText("Filter by title or description");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        filterPanel.add(searchField);

        filterComboBox = new JComboBox<>(new String[]{
                "All Tasks",
                "Active Tasks",
                "Completed Tasks",
                "High Priority",
                "Due Today / Overdue"
        });
        filterComboBox.addActionListener(e -> applyFilters());
        filterPanel.add(filterComboBox);

        JButton btnAddTask = new JButton("+ New Task");
        btnAddTask.setFont(btnAddTask.getFont().deriveFont(Font.BOLD, 12f));
        btnAddTask.setBackground(new Color(25, 118, 210));
        btnAddTask.addActionListener(e -> openAddTaskDialog());
        filterPanel.add(btnAddTask);

        header.add(filterPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(12, 20, 10, 20));

        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(34);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setShowGrid(false);
        taskTable.setIntercellSpacing(new Dimension(0, 1));
        taskTable.setGridColor(new Color(240, 240, 240));
        taskTable.getTableHeader().setReorderingAllowed(false);
        taskTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        taskTable.getTableHeader().setPreferredSize(new Dimension(0, 32));

        // Setup row sorter with filter
        rowSorter = new TableRowSorter<>(tableModel);
        taskTable.setRowSorter(rowSorter);

        // Setup custom renderers
        TableColumn colStatus = taskTable.getColumnModel().getColumn(TaskTableModel.COL_STATUS);
        colStatus.setPreferredWidth(50);
        colStatus.setMaxWidth(60);

        TableColumn colTitle = taskTable.getColumnModel().getColumn(TaskTableModel.COL_TITLE);
        colTitle.setPreferredWidth(230);
        colTitle.setCellRenderer(new TaskCellRenderers.TitleRenderer());

        TableColumn colPriority = taskTable.getColumnModel().getColumn(TaskTableModel.COL_PRIORITY);
        colPriority.setPreferredWidth(100);
        colPriority.setMaxWidth(120);
        colPriority.setCellRenderer(new TaskCellRenderers.PriorityRenderer());

        TableColumn colDueDate = taskTable.getColumnModel().getColumn(TaskTableModel.COL_DUE_DATE);
        colDueDate.setPreferredWidth(130);
        colDueDate.setCellRenderer(new TaskCellRenderers.DueDateRenderer());

        TableColumn colDesc = taskTable.getColumnModel().getColumn(TaskTableModel.COL_DESCRIPTION);
        colDesc.setPreferredWidth(280);
        colDesc.setCellRenderer(new TaskCellRenderers.DescriptionRenderer());

        // Double-click row to edit
        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && taskTable.getSelectedRow() != -1) {
                    int col = taskTable.columnAtPoint(e.getPoint());
                    if (col != TaskTableModel.COL_STATUS) {
                        openEditTaskDialog();
                    }
                }
            }
        });

        // Keyboard actions: Space toggles completion, Delete deletes, Enter edits
        taskTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int viewRow = taskTable.getSelectedRow();
                if (viewRow == -1) return;

                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    toggleSelectedTaskStatus();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    deleteSelectedTask();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    openEditTaskDialog();
                    e.consume();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 230, 235)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        center.add(scrollPane, BorderLayout.CENTER);

        return center;
    }

    private JPanel createBottomPanel() {
        JPanel bottomContainer = new JPanel(new BorderLayout());

        // Action Toolbar
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actionToolbar.setBorder(new EmptyBorder(0, 15, 4, 15));

        JButton btnAdd = new JButton("➕ Add");
        btnAdd.addActionListener(e -> openAddTaskDialog());
        actionToolbar.add(btnAdd);

        JButton btnEdit = new JButton("✏️ Edit");
        btnEdit.addActionListener(e -> openEditTaskDialog());
        actionToolbar.add(btnEdit);

        JButton btnToggle = new JButton("✔ Toggle Status");
        btnToggle.addActionListener(e -> toggleSelectedTaskStatus());
        actionToolbar.add(btnToggle);

        JButton btnDelete = new JButton("🗑️ Delete");
        btnDelete.addActionListener(e -> deleteSelectedTask());
        actionToolbar.add(btnDelete);

        JButton btnClearDone = new JButton("🧹 Clear Completed");
        btnClearDone.addActionListener(e -> clearCompletedTasks());
        actionToolbar.add(btnClearDone);

        bottomContainer.add(actionToolbar, BorderLayout.NORTH);

        // Status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(225, 225, 225)),
                new EmptyBorder(6, 20, 6, 20)
        ));
        statusBar.setBackground(new Color(248, 249, 250));

        statusCountLabel = new JLabel("Total: 0 | Active: 0 | Completed: 0");
        statusCountLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusCountLabel.setForeground(new Color(80, 90, 100));

        statusFileLabel = new JLabel("Storage: " + storage.getStorageLocation());
        statusFileLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        statusFileLabel.setForeground(new Color(130, 140, 150));

        statusBar.add(statusCountLabel, BorderLayout.WEST);
        statusBar.add(statusFileLabel, BorderLayout.EAST);
        bottomContainer.add(statusBar, BorderLayout.SOUTH);

        return bottomContainer;
    }

    private void applyFilters() {
        String query = searchField.getText().trim().toLowerCase();
        int filterIndex = filterComboBox.getSelectedIndex();

        RowFilter<TaskTableModel, Integer> filter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends TaskTableModel, ? extends Integer> entry) {
                TaskTableModel model = entry.getModel();
                int row = entry.getIdentifier();
                Task task = model.getTaskAt(row);
                if (task == null) return false;

                // 1. Text filter
                if (!query.isEmpty()) {
                    boolean titleMatches = task.getTitle().toLowerCase().contains(query);
                    boolean descMatches = task.getDescription().toLowerCase().contains(query);
                    if (!titleMatches && !descMatches) {
                        return false;
                    }
                }

                // 2. Combo filter
                return switch (filterIndex) {
                    case 1 -> !task.isCompleted(); // Active
                    case 2 -> task.isCompleted();  // Completed
                    case 3 -> task.getPriority() == Priority.HIGH; // High
                    case 4 -> task.isDueToday() || task.isOverdue(); // Today / Overdue
                    default -> true; // All
                };
            }
        };

        rowSorter.setRowFilter(filter);
    }

    private void openAddTaskDialog() {
        TaskDialog dialog = new TaskDialog(this, "New Task", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Task newTask = dialog.getTaskResult();
            if (newTask != null) {
                tableModel.addTask(newTask);
                taskTable.setRowSelectionInterval(0, 0);
            }
        }
    }

    private void openEditTaskDialog() {
        int selectedViewRow = taskTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a task to edit.",
                    "No Task Selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = taskTable.convertRowIndexToModel(selectedViewRow);
        Task current = tableModel.getTaskAt(modelRow);
        if (current == null) return;

        TaskDialog dialog = new TaskDialog(this, "Edit Task", current);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Task updated = dialog.getTaskResult();
            if (updated != null) {
                tableModel.updateTask(modelRow, updated);
            }
        }
    }

    private void toggleSelectedTaskStatus() {
        int selectedViewRow = taskTable.getSelectedRow();
        if (selectedViewRow == -1) return;

        int modelRow = taskTable.convertRowIndexToModel(selectedViewRow);
        Task task = tableModel.getTaskAt(modelRow);
        if (task != null) {
            tableModel.setValueAt(!task.isCompleted(), modelRow, TaskTableModel.COL_STATUS);
        }
    }

    private void deleteSelectedTask() {
        int selectedViewRow = taskTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a task to delete.",
                    "No Task Selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = taskTable.convertRowIndexToModel(selectedViewRow);
        Task task = tableModel.getTaskAt(modelRow);
        if (task == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete task:\n\"" + task.getTitle() + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeTask(modelRow);
        }
    }

    private void clearCompletedTasks() {
        int completed = tableModel.getCompletedCount();
        if (completed == 0) {
            JOptionPane.showMessageDialog(this,
                    "There are no completed tasks to clear.",
                    "Clear Completed",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove all " + completed + " completed tasks?",
                "Confirm Clear Completed",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeCompletedTasks();
        }
    }

    private void onTasksChanged() {
        updateStatus();
        saveTasksToStorage();
    }

    private void updateStatus() {
        int total = tableModel.getRowCount();
        int completed = tableModel.getCompletedCount();
        int active = tableModel.getActiveCount();

        statusCountLabel.setText(String.format("Total: %d  |  Active: %d  |  Completed: %d", total, active, completed));
    }

    private void saveTasksToStorage() {
        try {
            storage.saveTasks(tableModel.getTasks());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving tasks: " + e.getMessage(),
                    "Storage Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
