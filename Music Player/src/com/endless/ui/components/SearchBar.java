package com.endless.ui.components;

import com.endless.ui.Theme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Apple Spotlight-style search bar with rounded pill geometry, placeholder text,
 * and quick-clear action.
 */
public class SearchBar extends JPanel {

    public interface SearchListener {
        void onSearchQueryChanged(String query);
    }

    private final JTextField textField;
    private final String placeholder = "Search songs, artists, albums (⌘F)...";
    private boolean focused = false;
    private SearchListener searchListener = null;

    public SearchBar() {
        setOpaque(false);
        setPreferredSize(new Dimension(280, 32));
        setLayout(new BorderLayout());

        textField = new JTextField();
        textField.setOpaque(false);
        textField.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 26));
        textField.setFont(Theme.font(12f, Font.PLAIN));
        textField.setCaretColor(Theme.accent());

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                repaint();
            }
        });

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { notifyQuery(); }
            @Override
            public void removeUpdate(DocumentEvent e) { notifyQuery(); }
            @Override
            public void changedUpdate(DocumentEvent e) { notifyQuery(); }
        });

        // Click on panel focuses textfield
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // If clicked near clear button
                if (e.getX() >= getWidth() - 26 && !textField.getText().isEmpty()) {
                    textField.setText("");
                } else {
                    textField.requestFocusInWindow();
                }
            }
        });

        add(textField, BorderLayout.CENTER);

        Theme.addListener(isDark -> {
            textField.setForeground(Theme.textPrimary());
            textField.setCaretColor(Theme.accent());
            repaint();
        });
    }

    public void setSearchListener(SearchListener listener) {
        this.searchListener = listener;
    }

    public void focusSearch() {
        textField.requestFocusInWindow();
        textField.selectAll();
    }

    public void clear() {
        textField.setText("");
    }

    private void notifyQuery() {
        repaint();
        if (searchListener != null) {
            searchListener.onSearchQueryChanged(textField.getText().trim());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityHints(g2);

        int w = getWidth();
        int h = getHeight();

        // Capsule background
        g2.setColor(Theme.searchBarBg());
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, h, h));

        // Border / focus ring
        if (focused) {
            g2.setColor(new Color(Theme.accent().getRed(), Theme.accent().getGreen(), Theme.accent().getBlue(), 120));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(0.75f, 0.75f, w - 1.5f, h - 1.5f, h, h));
        } else {
            g2.setColor(Theme.border());
            g2.setStroke(new BasicStroke(1.0f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, h, h));
        }

        // Search icon (Left)
        SFIcon.draw(g2, SFIcon.Type.SEARCH, 9, (h - 14) / 2f, 14, focused ? Theme.accent() : Theme.textTertiary());

        // Placeholder text if empty
        if (textField.getText().isEmpty()) {
            g2.setFont(Theme.font(12f, Font.PLAIN));
            g2.setColor(Theme.textTertiary());
            FontMetrics fm = g2.getFontMetrics();
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 30, ty);
        }

        // Clear button (Right) if has text
        if (!textField.getText().isEmpty()) {
            int cx = w - 18;
            int cy = h / 2;
            g2.setColor(Theme.textTertiary());
            g2.fillOval(cx - 7, cy - 7, 14, 14);
            g2.setColor(Theme.searchBarBg());
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(cx - 3, cy - 3, cx + 3, cy + 3);
            g2.drawLine(cx + 3, cy - 3, cx - 3, cy + 3);
        }

        // Synchronize textfield foreground color
        textField.setForeground(Theme.textPrimary());

        g2.dispose();
        super.paintComponent(g);
    }
}
