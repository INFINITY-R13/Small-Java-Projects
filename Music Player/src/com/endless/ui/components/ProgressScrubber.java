package com.endless.ui.components;

import com.endless.model.Track;
import com.endless.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Apple Music styled progress scrubber with hover expansion, interactive seeking,
 * glowing thumb handle, and elapsed/remaining time indicators.
 */
public class ProgressScrubber extends JPanel {

    public interface SeekListener {
        void onSeek(long targetMs);
    }

    private SeekListener seekListener = null;

    private long positionMs = 0;
    private long durationMs = 0;

    private boolean hovered = false;
    private boolean dragging = false;
    private long dragPositionMs = 0;

    private Color customAccent = null;

    public ProgressScrubber() {
        setOpaque(false);
        setPreferredSize(new Dimension(360, 24));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                if (!dragging) {
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && durationMs > 0) {
                    dragging = true;
                    updateDragPosition(e.getX());
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragging && SwingUtilities.isLeftMouseButton(e)) {
                    dragging = false;
                    if (seekListener != null && durationMs > 0) {
                        seekListener.onSeek(dragPositionMs);
                    }
                    positionMs = dragPositionMs;
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging && durationMs > 0) {
                    updateDragPosition(e.getX());
                    repaint();
                }
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);

        Theme.addListener(isDark -> repaint());
    }

    public void setSeekListener(SeekListener listener) {
        this.seekListener = listener;
    }

    public void setProgress(long pos, long dur) {
        if (!dragging) {
            this.positionMs = Math.max(0, pos);
            this.durationMs = Math.max(0, dur);
            repaint();
        }
    }

    public void setCustomAccent(Color accent) {
        this.customAccent = accent;
        repaint();
    }

    private void updateDragPosition(int mouseX) {
        int labelWidth = 46;
        int trackX = labelWidth;
        int trackW = getWidth() - (labelWidth * 2);
        if (trackW <= 0) return;

        float fraction = (float) (mouseX - trackX) / trackW;
        fraction = Math.max(0.0f, Math.min(1.0f, fraction));
        dragPositionMs = (long) (fraction * durationMs);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityHints(g2);

        int w = getWidth();
        int h = getHeight();

        int labelWidth = 46;
        int trackX = labelWidth;
        int trackW = Math.max(10, w - (labelWidth * 2));
        int trackH = (hovered || dragging) ? 6 : 4;
        int trackY = (h - trackH) / 2;

        long currentDisplayPos = dragging ? dragPositionMs : positionMs;
        float progressFraction = (durationMs > 0) ? (float) currentDisplayPos / durationMs : 0f;
        progressFraction = Math.max(0f, Math.min(1f, progressFraction));

        // 1. Elapsed Time Label (Left)
        g2.setFont(Theme.font(11f, Font.PLAIN));
        g2.setColor(Theme.textSecondary());
        FontMetrics fm = g2.getFontMetrics();
        String elapsedStr = Track.formatTime(currentDisplayPos);
        int ey = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(elapsedStr, 4, ey);

        // 2. Track Background
        g2.setColor(Theme.scrubberBackground());
        g2.fill(new RoundRectangle2D.Float(trackX, trackY, trackW, trackH, trackH, trackH));

        // 3. Progress Fill
        float fillW = trackW * progressFraction;
        if (fillW > 0) {
            Color accent = customAccent != null ? customAccent : Theme.accent();
            Color fillColor = (hovered || dragging) ? accent : (Theme.isDarkMode() ? Color.WHITE : accent);
            g2.setColor(fillColor);
            g2.fill(new RoundRectangle2D.Float(trackX, trackY, fillW, trackH, trackH, trackH));
        }

        // 4. Glowing Knob Handle (Apple thumb)
        if (hovered || dragging) {
            float knobX = trackX + fillW;
            float knobR = 6f; // radius
            float knobY = h / 2f;

            // Outer soft glow
            Color accent = customAccent != null ? customAccent : Theme.accent();
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
            g2.fill(new Ellipse2D.Float(knobX - knobR - 3, knobY - knobR - 3, (knobR + 3) * 2, (knobR + 3) * 2));

            // Solid knob
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Float(knobX - knobR, knobY - knobR, knobR * 2, knobR * 2));

            // Inner accent dot
            g2.setColor(accent);
            g2.fill(new Ellipse2D.Float(knobX - 2.5f, knobY - 2.5f, 5f, 5f));
        }

        // 5. Remaining Time Label (Right)
        long remainingMs = Math.max(0, durationMs - currentDisplayPos);
        String remainingStr = "-" + Track.formatTime(remainingMs);
        int rw = fm.stringWidth(remainingStr);
        g2.setColor(Theme.textSecondary());
        g2.drawString(remainingStr, w - rw - 4, ey);

        g2.dispose();
    }
}
