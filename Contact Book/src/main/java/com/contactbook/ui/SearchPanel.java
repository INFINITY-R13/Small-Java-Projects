package com.contactbook.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * A rounded search field with a magnifier prefix icon and a clear (×) button.
 */
public class SearchPanel extends JPanel {

    private final PlaceholderTextField field;
    private final JButton              clearBtn;
    private Consumer<String>           searchCallback;

    public SearchPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));

        // Outer rounded container
        JPanel container = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.ARC * 2, UIConstants.ARC * 2);
                g2.setColor(UIConstants.DIVIDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConstants.ARC * 2, UIConstants.ARC * 2);
                g2.dispose();
            }
        };
        container.setOpaque(false);

        // Search icon label
        JLabel icon = new JLabel("⌕");
        icon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        icon.setForeground(UIConstants.TEXT_SECONDARY);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 4));

        // Text field with placeholder support
        field = new PlaceholderTextField("Search contacts…");
        field.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
        field.setOpaque(false);
        field.setFont(UIConstants.FONT_BODY);
        field.setForeground(UIConstants.TEXT_PRIMARY);
        field.setCaretColor(UIConstants.ACCENT);

        // Clear button
        clearBtn = new JButton("×");
        clearBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        clearBtn.setForeground(UIConstants.TEXT_SECONDARY);
        clearBtn.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 8));
        clearBtn.setContentAreaFilled(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.setVisible(false);
        clearBtn.addActionListener(e -> {
            field.setText("");
            field.requestFocusInWindow();
        });

        container.add(icon,     BorderLayout.WEST);
        container.add(field,    BorderLayout.CENTER);
        container.add(clearBtn, BorderLayout.EAST);

        add(container, BorderLayout.CENTER);

        // Live search
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { onTextChanged(); }
            @Override public void removeUpdate(DocumentEvent e)  { onTextChanged(); }
            @Override public void changedUpdate(DocumentEvent e) { onTextChanged(); }
        });
    }

    public void setSearchCallback(Consumer<String> callback) {
        this.searchCallback = callback;
    }

    public String getQuery() { return field.getText(); }

    public void clear() { field.setText(""); }

    // ── Internal ─────────────────────────────────────────────────────────────

    private void onTextChanged() {
        boolean hasText = !field.getText().isEmpty();
        clearBtn.setVisible(hasText);
        if (searchCallback != null) searchCallback.accept(field.getText());
    }

    // ── Placeholder text field ────────────────────────────────────────────────

    /**
     * JTextField subclass that paints a placeholder string when empty and unfocused.
     */
    static final class PlaceholderTextField extends JTextField {

        private final String placeholder;

        PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2.setColor(UIConstants.TEXT_SECONDARY);
                g2.setFont(getFont());
                Insets ins = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int y = ins.top + fm.getAscent() + (getHeight() - ins.top - ins.bottom - fm.getHeight()) / 2;
                g2.drawString(placeholder, ins.left, y);
                g2.dispose();
            }
        }
    }
}
