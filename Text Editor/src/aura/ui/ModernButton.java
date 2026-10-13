package aura.ui;

import aura.theme.Theme;
import aura.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern aesthetic button with rounded corners, hover transitions, and flat styling.
 */
public class ModernButton extends JButton {
    private boolean hovered = false;
    private boolean pressed = false;
    private boolean primary = false;
    private int cornerRadius = 6;

    public ModernButton(String text) {
        this(text, null);
    }

    public ModernButton(Icon icon) {
        this("", icon);
    }

    public ModernButton(String text, Icon icon) {
        super(text, icon);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(5, 10, 5, 10));
        setFont(new Font("-apple-system", Font.PLAIN, 12));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
        repaint();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        Theme theme = ThemeManager.getTheme();
        int w = getWidth();
        int h = getHeight();

        Color bg;
        Color fg;

        if (primary) {
            bg = hovered ? theme.accentHover() : theme.accent();
            fg = theme.isDark() ? new Color(0x11, 0x11, 0x1b) : Color.WHITE;
        } else {
            if (pressed) {
                bg = theme.bgActive();
            } else if (hovered) {
                bg = theme.bgHover();
            } else {
                bg = new Color(0, 0, 0, 0); // transparent default
            }
            fg = isEnabled() ? (hovered ? theme.fgEditor() : theme.fgHeader()) : theme.fgMuted();
        }

        if (bg.getAlpha() > 0) {
            g2d.setColor(bg);
            g2d.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        }

        if (primary) {
            g2d.setColor(theme.accent());
            g2d.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        g2d.dispose();

        setForeground(fg);
        super.paintComponent(g);
    }
}
