package aura.ui;

import aura.theme.Theme;
import aura.theme.ThemeManager;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Ultra-sleek, minimalist modern scrollbar with rounded pill thumb and no arrow buttons.
 */
public class ModernScrollBarUI extends BasicScrollBarUI {
    private static final int THICKNESS = 10;
    private static final int MIN_THUMB_SIZE = 30;
    private static final int ARC = 8;

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(MIN_THUMB_SIZE, MIN_THUMB_SIZE);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            return new Dimension(THICKNESS, super.getPreferredSize(c).height);
        } else {
            return new Dimension(super.getPreferredSize(c).width, THICKNESS);
        }
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        btn.setMinimumSize(new Dimension(0, 0));
        btn.setMaximumSize(new Dimension(0, 0));
        btn.setFocusable(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        return btn;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Theme theme = ThemeManager.getTheme();
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(theme.scrollTrack());
        g2d.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2d.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;

        Theme theme = ThemeManager.getTheme();
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color thumbColor = isThumbRollover() ? theme.scrollThumbHover() : theme.scrollThumb();
        g2d.setColor(thumbColor);

        int margin = 2;
        int x = thumbBounds.x + margin;
        int y = thumbBounds.y + margin;
        int w = Math.max(thumbBounds.width - margin * 2, 4);
        int h = Math.max(thumbBounds.height - margin * 2, 4);

        g2d.fillRoundRect(x, y, w, h, ARC, ARC);
        g2d.dispose();
    }
}
