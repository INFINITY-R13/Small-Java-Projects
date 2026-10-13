package aura.util;

import javax.swing.Icon;
import java.awt.*;
import java.awt.geom.*;

/**
 * HiDPI vector icons drawn directly with Graphics2D for pixel-perfect sharpness.
 */
public class Icons {

    public static Icon createFileIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2d, w, h) -> {
            int pad = 2;
            int fw = w - pad * 2;
            int fh = h - pad * 2;
            int fold = 4;

            Path2D path = new Path2D.Float();
            path.moveTo(pad, pad);
            path.lineTo(pad + fw - fold, pad);
            path.lineTo(pad + fw, pad + fold);
            path.lineTo(pad + fw, pad + fh);
            path.lineTo(pad, pad + fh);
            path.closePath();

            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.draw(path);

            // fold flap
            Path2D flap = new Path2D.Float();
            flap.moveTo(pad + fw - fold, pad);
            flap.lineTo(pad + fw - fold, pad + fold);
            flap.lineTo(pad + fw, pad + fold);
            g2d.draw(flap);
        });
    }

    public static Icon createNewFileIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2d, w, h) -> {
            int pad = 2;
            int fw = w - pad * 2 - 2;
            int fh = h - pad * 2;
            int fold = 3;

            Path2D path = new Path2D.Float();
            path.moveTo(pad, pad);
            path.lineTo(pad + fw - fold, pad);
            path.lineTo(pad + fw, pad + fold);
            path.lineTo(pad + fw, pad + fh);
            path.lineTo(pad, pad + fh);
            path.closePath();

            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.draw(path);

            // small plus sign in bottom-right corner
            int px = w - 4;
            int py = h - 4;
            g2d.drawLine(px - 3, py, px + 3, py);
            g2d.drawLine(px, py - 3, px, py + 3);
        });
    }

    public static Icon createFolderIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2d, w, h) -> {
            int pad = 2;
            int fw = w - pad * 2;
            int fh = h - pad * 2 - 2;
            int tabW = fw * 2 / 5;

            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Folder tab
            Path2D path = new Path2D.Float();
            path.moveTo(pad, pad + 3);
            path.lineTo(pad + tabW, pad + 3);
            path.lineTo(pad + tabW + 2, pad + 5);
            path.lineTo(pad + fw, pad + 5);
            path.lineTo(pad + fw, pad + fh + 3);
            path.lineTo(pad, pad + fh + 3);
            path.closePath();
            g2d.draw(path);
        });
    }

    public static Icon createCloseIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2d, w, h) -> {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int pad = 3;
            g2d.drawLine(pad, pad, w - pad, h - pad);
            g2d.drawLine(w - pad, pad, pad, h - pad);
        });
    }

    public static Icon createPlusIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2d, w, h) -> {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int midX = w / 2;
            int midY = h / 2;
            int pad = 3;
            g2d.drawLine(midX, pad, midX, h - pad);
            g2d.drawLine(pad, midY, w - pad, midY);
        });
    }

    public static Icon createSaveIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2d, w, h) -> {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int pad = 2;
            int fw = w - pad * 2;
            int fh = h - pad * 2;
            int cut = 3;

            Path2D path = new Path2D.Float();
            path.moveTo(pad, pad);
            path.lineTo(pad + fw - cut, pad);
            path.lineTo(pad + fw, pad + cut);
            path.lineTo(pad + fw, pad + fh);
            path.lineTo(pad, pad + fh);
            path.closePath();
            g2d.draw(path);

            // interior shutter
            g2d.drawRect(pad + 3, pad + 2, fw - 6, 4);
            // interior label
            g2d.drawRect(pad + 2, pad + 8, fw - 4, fh - 9);
        });
    }

    public static Icon createSearchIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2d, w, h) -> {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int r = (int)(size * 0.32);
            int cx = size / 2 - 1;
            int cy = size / 2 - 1;
            g2d.drawOval(cx - r, cy - r, r * 2, r * 2);
            int x1 = (int)(cx + r * 0.7);
            int y1 = (int)(cy + r * 0.7);
            g2d.drawLine(x1, y1, size - 2, size - 2);
        });
    }

    public static Icon createReplaceIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2d, w, h) -> {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Two curved arrows / swap
            g2d.drawArc(2, 3, w - 5, h - 6, 30, 200);
            g2d.drawLine(w - 3, 3, w - 7, 3);
            g2d.drawLine(w - 3, 3, w - 3, 7);

            g2d.drawArc(3, 3, w - 5, h - 6, 210, 200);
            g2d.drawLine(3, h - 4, 7, h - 4);
            g2d.drawLine(3, h - 4, 3, h - 8);
        });
    }

    public static Icon createThemeIcon(Color color, int size, boolean isDark) {
        return new VectorIcon(size, size, (g2d, w, h) -> {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (isDark) {
                // Moon icon
                Area moon = new Area(new Ellipse2D.Float(2, 2, w - 4, h - 4));
                Area cut = new Area(new Ellipse2D.Float(w / 3f, 0, w - 3, h - 3));
                moon.subtract(cut);
                g2d.fill(moon);
            } else {
                // Sun icon
                int cx = w / 2;
                int cy = h / 2;
                int r = 3;
                g2d.drawOval(cx - r, cy - r, r * 2, r * 2);
                g2d.drawLine(cx, 1, cx, 3);
                g2d.drawLine(cx, h - 3, cx, h - 1);
                g2d.drawLine(1, cy, 3, cy);
                g2d.drawLine(w - 3, cy, w - 1, cy);
                g2d.drawLine(cx - 3, cy - 3, cx - 4, cy - 4);
                g2d.drawLine(cx + 3, cy + 3, cx + 4, cy + 4);
                g2d.drawLine(cx - 3, cy + 3, cx - 4, cy + 4);
                g2d.drawLine(cx + 3, cy - 3, cx + 4, cy - 4);
            }
        });
    }

    public static Icon createSidebarIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2d, w, h) -> {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawRoundRect(2, 2, w - 4, h - 4, 3, 3);
            int splitX = w / 3 + 1;
            g2d.drawLine(splitX, 2, splitX, h - 2);
        });
    }

    public static Icon createEyeIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2d, w, h) -> {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Eye shape
            Path2D eye = new Path2D.Float();
            eye.moveTo(2, h / 2f);
            eye.quadTo(w / 2f, 1, w - 2, h / 2f);
            eye.quadTo(w / 2f, h - 1, 2, h / 2f);
            eye.closePath();
            g2d.draw(eye);
            int pr = 2;
            g2d.fillOval(w / 2 - pr, h / 2 - pr, pr * 2, pr * 2);
        });
    }

    public static Icon createChevronIcon(Color color, int size, boolean expanded) {
        return new VectorIcon(size, size, (g2d, w, h) -> {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int midX = w / 2;
            int midY = h / 2;
            if (expanded) {
                // Down arrow
                g2d.drawLine(midX - 3, midY - 2, midX, midY + 2);
                g2d.drawLine(midX, midY + 2, midX + 3, midY - 2);
            } else {
                // Right arrow
                g2d.drawLine(midX - 2, midY - 3, midX + 2, midY);
                g2d.drawLine(midX + 2, midY, midX - 2, midY + 3);
            }
        });
    }

    @FunctionalInterface
    public interface IconPainter {
        void paint(Graphics2D g2d, int width, int height);
    }

    private static class VectorIcon implements Icon {
        private final int width;
        private final int height;
        private final IconPainter painter;

        public VectorIcon(int width, int height, IconPainter painter) {
            this.width = width;
            this.height = height;
            this.painter = painter;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(x, y);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            painter.paint(g2d, width, height);
            g2d.dispose();
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
