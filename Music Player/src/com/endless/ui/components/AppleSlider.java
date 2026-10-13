package com.endless.ui.components;

import com.endless.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Apple macOS styled slider component featuring a sleek capsule track,
 * dynamic theme adaptation, hover expansion, and smooth thumb knob rendering.
 */
public class AppleSlider extends JComponent {

    public interface ValueChangeListener {
        void onValueChanged(float value);
    }

    private float value = 0.85f; // 0.0 to 1.0
    private boolean hovered = false;
    private boolean dragging = false;
    private Color customAccent = null;

    private ValueChangeListener listener = null;

    public AppleSlider(float initialValue) {
        this.value = Math.max(0.0f, Math.min(1.0f, initialValue));
        setPreferredSize(new Dimension(88, 18));
        setOpaque(false);
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
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragging = true;
                    updateValueFromMouse(e.getX());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragging = false;
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    updateValueFromMouse(e.getX());
                }
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);

        Theme.addListener(isDark -> repaint());
    }

    public void setValueChangeListener(ValueChangeListener listener) {
        this.listener = listener;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float val) {
        float clamped = Math.max(0.0f, Math.min(1.0f, val));
        if (Math.abs(this.value - clamped) > 0.001f) {
            this.value = clamped;
            repaint();
        }
    }

    public void setCustomAccent(Color accent) {
        this.customAccent = accent;
        repaint();
    }

    private void updateValueFromMouse(int mouseX) {
        int w = getWidth();
        int pad = 6;
        int trackW = w - (pad * 2);
        if (trackW <= 0) return;

        float newVal = (float) (mouseX - pad) / trackW;
        newVal = Math.max(0.0f, Math.min(1.0f, newVal));
        if (Math.abs(this.value - newVal) > 0.001f) {
            this.value = newVal;
            repaint();
            if (listener != null) {
                listener.onValueChanged(this.value);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityHints(g2);

        int w = getWidth();
        int h = getHeight();

        int pad = 6;
        int trackW = w - (pad * 2);
        int trackH = (hovered || dragging) ? 6 : 4;
        int trackY = (h - trackH) / 2;

        // Inactive Track Background (adapts dynamically to Dark/Light theme)
        Color trackBg = Theme.scrubberBackground();
        g2.setColor(trackBg);
        g2.fill(new RoundRectangle2D.Float(pad, trackY, trackW, trackH, trackH, trackH));

        // Active Track Fill
        float fillW = trackW * value;
        Color accent = customAccent != null ? customAccent : Theme.accent();
        Color activeFill;
        if (Theme.isDarkMode()) {
            activeFill = (hovered || dragging) ? accent : Color.WHITE;
        } else {
            activeFill = (hovered || dragging) ? accent : accent.darker();
        }

        if (fillW > 0) {
            g2.setColor(activeFill);
            g2.fill(new RoundRectangle2D.Float(pad, trackY, fillW, trackH, trackH, trackH));
        }

        // Apple Thumb Knob (clean rounded handle with subtle shadow and highlight)
        float knobX = pad + fillW;
        float knobY = h / 2f;
        float knobR = (hovered || dragging) ? 6.0f : 5.0f;

        // Drop shadow for knob
        g2.setColor(new Color(0, 0, 0, Theme.isDarkMode() ? 70 : 40));
        g2.fill(new Ellipse2D.Float(knobX - knobR, knobY - knobR + 1.2f, knobR * 2, knobR * 2));

        // White knob surface
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Float(knobX - knobR, knobY - knobR, knobR * 2, knobR * 2));

        // Subtle border around knob
        g2.setColor(new Color(0, 0, 0, 25));
        g2.setStroke(new BasicStroke(0.8f));
        g2.draw(new Ellipse2D.Float(knobX - knobR, knobY - knobR, knobR * 2, knobR * 2));

        // Inner accent dot
        if (hovered || dragging) {
            g2.setColor(accent);
            g2.fill(new Ellipse2D.Float(knobX - 2f, knobY - 2f, 4f, 4f));
        }

        g2.dispose();
    }
}
