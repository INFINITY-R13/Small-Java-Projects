package todolist.ui;

import todolist.model.Priority;
import todolist.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Modal dialog for creating and editing tasks.
 */
public class TaskDialog extends JDialog {
    private final JTextField titleField = new JTextField(25);
    private final JTextArea descriptionArea = new JTextArea(4, 25);
    private final JComboBox<Priority> priorityCombo = new JComboBox<>(Priority.values());
    private final JTextField dueDateField = new JTextField(12);
    private final JCheckBox completedCheckbox = new JCheckBox("Mark as completed");

    private boolean confirmed = false;
    private final Task initialTask;

    public TaskDialog(Window parent, String dialogTitle, Task taskToEdit) {
        super(parent, dialogTitle, ModalityType.APPLICATION_MODAL);
        this.initialTask = taskToEdit;

        initUI();
        populateFields();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setResizable(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(15, 20, 10, 20));

        // 1. Title section
        contentPanel.add(createFieldLabel("Task Title: *"));
        titleField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        contentPanel.add(titleField);
        contentPanel.add(Box.createVerticalStrut(12));

        // 2. Priority section
        contentPanel.add(createFieldLabel("Priority:"));
        priorityCombo.setSelectedItem(Priority.MEDIUM);
        contentPanel.add(priorityCombo);
        contentPanel.add(Box.createVerticalStrut(12));

        // 3. Due Date section
        contentPanel.add(createFieldLabel("Due Date (YYYY-MM-DD):"));
        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dateRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        dueDateField.setToolTipText("e.g. 2026-10-15");
        dateRow.add(dueDateField);

        JButton btnToday = new JButton("Today");
        btnToday.addActionListener(e -> dueDateField.setText(LocalDate.now().toString()));
        dateRow.add(btnToday);

        JButton btnTomorrow = new JButton("Tomorrow");
        btnTomorrow.addActionListener(e -> dueDateField.setText(LocalDate.now().plusDays(1).toString()));
        dateRow.add(btnTomorrow);

        JButton btnNextWeek = new JButton("+7 Days");
        btnNextWeek.addActionListener(e -> dueDateField.setText(LocalDate.now().plusDays(7).toString()));
        dateRow.add(btnNextWeek);

        JButton btnClearDate = new JButton("Clear");
        btnClearDate.addActionListener(e -> dueDateField.setText(""));
        dateRow.add(btnClearDate);

        contentPanel.add(dateRow);
        contentPanel.add(Box.createVerticalStrut(12));

        // 4. Description section
        contentPanel.add(createFieldLabel("Description (optional):"));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descScroll);
        contentPanel.add(Box.createVerticalStrut(10));

        // 5. Completion checkbox (for edits)
        completedCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(completedCheckbox);

        add(contentPanel, BorderLayout.CENTER);

        // Buttons section
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 225, 225)));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton(initialTask == null ? "Add Task" : "Save Changes");
        saveButton.setFont(saveButton.getFont().deriveFont(Font.BOLD));
        saveButton.setBackground(new Color(25, 118, 210));
        saveButton.addActionListener(e -> onSave());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Keyboard actions
        getRootPane().setDefaultButton(saveButton);
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        pack();
        setLocationRelativeTo(getParent());
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 4, 0));
        return label;
    }

    private void populateFields() {
        if (initialTask != null) {
            titleField.setText(initialTask.getTitle());
            descriptionArea.setText(initialTask.getDescription());
            priorityCombo.setSelectedItem(initialTask.getPriority());
            if (initialTask.getDueDate() != null) {
                dueDateField.setText(initialTask.getDueDate().toString());
            }
            completedCheckbox.setSelected(initialTask.isCompleted());
            completedCheckbox.setVisible(true);
        } else {
            completedCheckbox.setVisible(false);
        }
    }

    private void onSave() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Task title cannot be empty.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            titleField.requestFocusInWindow();
            return;
        }

        String dateStr = dueDateField.getText().trim();
        LocalDate dueDate = null;
        if (!dateStr.isEmpty()) {
            try {
                dueDate = LocalDate.parse(dateStr);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Please use YYYY-MM-DD (e.g. 2026-10-15).",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                dueDateField.requestFocusInWindow();
                return;
            }
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Task getTaskResult() {
        if (!confirmed) return null;

        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        Priority priority = (Priority) priorityCombo.getSelectedItem();
        String dateStr = dueDateField.getText().trim();
        LocalDate dueDate = dateStr.isEmpty() ? null : LocalDate.parse(dateStr);
        boolean completed = completedCheckbox.isSelected();

        if (initialTask != null) {
            return new Task(
                    initialTask.getId(),
                    title,
                    description,
                    priority,
                    dueDate,
                    completed,
                    initialTask.getCreatedAt()
            );
        } else {
            return new Task(title, description, priority, dueDate);
        }
    }
}
