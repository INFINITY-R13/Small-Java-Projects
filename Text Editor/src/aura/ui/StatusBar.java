package aura.ui;

import aura.theme.Theme;
import aura.theme.ThemeManager;
import aura.util.FileType;

import javax.swing.*;
import java.awt.*;

/**
 * Bottom status bar providing document metrics, cursor position, encoding, and status messages.
 */
public class StatusBar extends JPanel {
    private final JLabel lblStatus;
    private final JLabel lblCursor;
    private final JLabel lblMetrics;
    private final JLabel lblEncoding;
    private final JLabel lblLanguage;
    private final JLabel lblZoom;
    private Timer messageTimer;

    public StatusBar() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 26));
        setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));

        lblStatus = new JLabel("Ready");
        lblCursor = new JLabel("Ln 1, Col 1");
        lblMetrics = new JLabel("0 words  •  0 chars");
        lblEncoding = new JLabel("UTF-8");
        lblLanguage = new JLabel("Plain Text");
        lblZoom = new JLabel("100%");

        Font statusFont = new Font("-apple-system", Font.PLAIN, 11);
        lblStatus.setFont(statusFont);
        lblCursor.setFont(statusFont);
        lblMetrics.setFont(statusFont);
        lblEncoding.setFont(statusFont);
        lblLanguage.setFont(statusFont);
        lblZoom.setFont(statusFont);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(lblCursor);
        rightPanel.add(lblMetrics);
        rightPanel.add(lblLanguage);
        rightPanel.add(lblEncoding);
        rightPanel.add(lblZoom);

        add(lblStatus, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        ThemeManager.addListener(this::applyTheme);
    }

    public void setStatusMessage(String message) {
        lblStatus.setText(message);
        if (messageTimer != null && messageTimer.isRunning()) {
            messageTimer.stop();
        }
        messageTimer = new Timer(4000, e -> lblStatus.setText("Ready"));
        messageTimer.setRepeats(false);
        messageTimer.start();
    }

    public void updateCaret(int line, int col) {
        lblCursor.setText("Ln " + line + ", Col " + col);
    }

    public void updateMetrics(int words, int chars) {
        lblMetrics.setText(words + " words  •  " + chars + " chars");
    }

    public void updateLanguage(FileType fileType) {
        lblLanguage.setText(fileType != null ? fileType.getDisplayName() : "Plain Text");
    }

    public void updateZoom(int percentage) {
        lblZoom.setText(percentage + "%");
    }

    private void applyTheme(Theme theme) {
        setBackground(theme.bgStatusBar());
        lblStatus.setForeground(theme.fgMuted());
        lblCursor.setForeground(theme.fgMuted());
        lblMetrics.setForeground(theme.fgMuted());
        lblEncoding.setForeground(theme.fgMuted());
        lblLanguage.setForeground(theme.accent());
        lblZoom.setForeground(theme.fgMuted());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Theme theme = ThemeManager.getTheme();
        g.setColor(theme.border());
        g.drawLine(0, 0, getWidth(), 0);
    }
}
