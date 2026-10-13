package com.endless.ui.components;

import com.endless.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * An Apple-styled interactive button supporting vector icons, pill shapes,
 * hover elevation, and active toggle states.
 */
public class AppleButton extends JButton {

    public enum Style {
        PRIMARY, SECONDARY, ICON_ONLY, PILL
    }

    private Style style = Style.ICON_ONLY;
    private SFIcon.Type iconType = null;
    private float iconSize = 18f;
    private boolean hovered = false;
    private boolean pressed = false;
    private boolean active = false; // toggle state (e.g. shuffle, repeat, favorite)
    private Color customAccentColor = null;

    public AppleButton(SFIcon.Type iconType, Style style) {
        this(null, iconType, style);
    }

    public AppleButton(String text, SFIcon.Type iconType, Style style) {
        super(text);
        this.iconType = iconType;
        this.style = style;
        init();
    }

    private void init() {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Theme.font(13f, Font.PLAIN));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    pressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    pressed = false;
                    repaint();
                }
            }
        });

        Theme.addListener(isDark -> repaint());
    }

    public void setIconType(SFIcon.Type iconType) {
        this.iconType = iconType;
        repaint();
    }

    public void setIconSize(float size) {
        this.iconSize = size;
        repaint();
    }

    public void setActive(boolean active) {
        this.active = active;
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    public void setCustomAccentColor(Color color) {
        this.customAccentColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityHints(g2);

        int w = getWidth();
        int h = getHeight();
        Color accent = customAccentColor != null ? customAccentColor : Theme.accent();

        // Background rendering based on button style
        if (style == Style.PRIMARY) {
            // Elevated pill / circle with vibrant accent
            int r = Math.min(w, h);
            Color base = accent;
            if (pressed) {
                base = base.darker();
            } else if (hovered) {
                base = base.brighter();
            }
            g2.setColor(base);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, r, r));

            // Subtle inner glow
            g2.setColor(new Color(255, 255, 255, hovered ? 45 : 20));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Float(0.6f, 0.6f, w - 1.2f, h - 1.2f, r, r));

        } else if (style == Style.SECONDARY || style == Style.PILL) {
            int r = Math.min(w, h);
            Color bg = active ? accent : (hovered ? Theme.cardHoverBg() : Theme.cardBg());
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, r, r));

            g2.setColor(active ? accent : Theme.border());
            g2.setStroke(new BasicStroke(1.0f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, r, r));

        } else if (style == Style.ICON_ONLY) {
            if (hovered || pressed || active) {
                int r = Math.min(w, h);
                Color hoverBg = active ? (Theme.isDarkMode() ? new Color(255, 255, 255, 25) : new Color(0, 0, 0, 15))
                                       : (Theme.isDarkMode() ? new Color(255, 255, 255, 18) : new Color(0, 0, 0, 10));
                g2.setColor(hoverBg);
                g2.fill(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, r, r));
            }
        }

        // Color for icons and text
        Color iconColor;
        if (style == Style.PRIMARY) {
            iconColor = Color.WHITE;
        } else if (active) {
            iconColor = accent;
        } else if (hovered) {
            iconColor = Theme.textPrimary();
        } else {
            iconColor = Theme.textSecondary();
        }

        // Draw Icon
        if (iconType != null) {
            float ix = (getText() == null || getText().isEmpty()) ? (w - iconSize) / 2f : 12f;
            float iy = (h - iconSize) / 2f;
            SFIcon.draw(g2, iconType, ix, iy, iconSize, iconColor);
        }

        // Draw Text
        String text = getText();
        if (text != null && !text.isEmpty()) {
            g2.setFont(getFont());
            g2.setColor(iconColor);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (iconType != null) ? (int) (12 + iconSize + 8) : (w - fm.stringWidth(text)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);
        }

        // Active indicator dot for toggle buttons (like shuffle)
        if (active && style == Style.ICON_ONLY) {
            g2.setColor(accent);
            g2.fillOval(w / 2 - 2, h - 6, 4, 4);
        }

        g2.dispose();
    }
}
