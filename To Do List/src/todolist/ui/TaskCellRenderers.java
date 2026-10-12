package todolist.ui;

import todolist.model.Priority;
import todolist.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Custom table cell renderers for polished visuals:
 * - Title strikethrough on completion
 * - Color-coded priority badges
 * - Overdue/due date indicators
 */
public final class TaskCellRenderers {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private TaskCellRenderers() {}

    /**
     * Renders title with strikethrough and muted gray when completed.
     */
    public static class TitleRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(new EmptyBorder(0, 10, 0, 10));

            int modelRow = table.convertRowIndexToModel(row);
            TaskTableModel model = (TaskTableModel) table.getModel();
            Task task = model.getTaskAt(modelRow);

            if (task != null && task.isCompleted()) {
                setText("<html><s><font color='#888888'>" + escapeHtml(task.getTitle()) + "</font></s></html>");
                if (!isSelected) {
                    setForeground(Color.GRAY);
                }
            } else {
                setText(value != null ? value.toString() : "");
                if (!isSelected) {
                    setForeground(new Color(33, 33, 33));
                }
                setFont(getFont().deriveFont(Font.PLAIN, 13f));
            }

            return this;
        }
    }

    /**
     * Renders priority as a clean colored badge.
     */
    public static class PriorityRenderer extends DefaultTableCellRenderer {
        private final JLabel badge = new JLabel();
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));

        public PriorityRenderer() {
            badge.setOpaque(true);
            badge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(2, 8, 2, 8),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)
            ));
            badge.setFont(badge.getFont().deriveFont(Font.BOLD, 11f));
            badge.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(badge);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            panel.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250)));

            if (value instanceof Priority priority) {
                badge.setText(priority.getDisplayName().toUpperCase());
                badge.setForeground(priority.getForegroundColor());
                badge.setBackground(priority.getBackgroundColor());
                badge.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(priority.getForegroundColor().brighter(), 1, true),
                        BorderFactory.createEmptyBorder(2, 8, 2, 8)
                ));
            } else {
                badge.setText("-");
                badge.setForeground(Color.GRAY);
                badge.setBackground(Color.LIGHT_GRAY);
            }

            return panel;
        }
    }

    /**
     * Renders due dates with overdue highlights.
     */
    public static class DueDateRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(new EmptyBorder(0, 8, 0, 8));

            int modelRow = table.convertRowIndexToModel(row);
            TaskTableModel model = (TaskTableModel) table.getModel();
            Task task = model.getTaskAt(modelRow);

            if (value instanceof LocalDate date) {
                if (task != null && task.isCompleted()) {
                    setText(date.format(DATE_FORMATTER));
                    if (!isSelected) setForeground(Color.GRAY);
                } else if (task != null && task.isOverdue()) {
                    setText("⚠️ Overdue (" + date.format(DATE_FORMATTER) + ")");
                    if (!isSelected) setForeground(new Color(211, 47, 47));
                    setFont(getFont().deriveFont(Font.BOLD, 12f));
                } else if (task != null && task.isDueToday()) {
                    setText("⭐ Today");
                    if (!isSelected) setForeground(new Color(230, 81, 0));
                    setFont(getFont().deriveFont(Font.BOLD, 12f));
                } else {
                    setText(date.format(DATE_FORMATTER));
                    if (!isSelected) setForeground(new Color(66, 66, 66));
                }
            } else {
                setText("—");
                if (!isSelected) setForeground(Color.LIGHT_GRAY);
            }

            return this;
        }
    }

    /**
     * Renders task description column.
     */
    public static class DescriptionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(new EmptyBorder(0, 8, 0, 8));

            int modelRow = table.convertRowIndexToModel(row);
            TaskTableModel model = (TaskTableModel) table.getModel();
            Task task = model.getTaskAt(modelRow);

            String desc = value != null ? value.toString() : "";
            if (desc.isBlank()) {
                setText("—");
                if (!isSelected) setForeground(Color.LIGHT_GRAY);
            } else {
                setText(desc);
                if (task != null && task.isCompleted()) {
                    if (!isSelected) setForeground(Color.GRAY);
                } else {
                    if (!isSelected) setForeground(new Color(97, 97, 97));
                }
            }

            return this;
        }
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}
