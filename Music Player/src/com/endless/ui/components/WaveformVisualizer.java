package com.endless.ui.components;

import com.endless.audio.VisualizerDataProvider;
import com.endless.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * 60 FPS real-time audio visualizer featuring Apple Music fluid spectrum bars,
 * organic waveform curves, and ambient energy aura modes.
 */
public class WaveformVisualizer extends JPanel {

    public enum Mode {
        SPECTRUM_BARS, FLUID_WAVE, AMBIENT_AURA
    }

    private final VisualizerDataProvider dataProvider;
    private Mode currentMode = Mode.SPECTRUM_BARS;
    private final Timer animationTimer;

    // Smoothed visualizer values
    private final float[] smoothedBars = new float[32];
    private final float[] peakBars = new float[32];
    private float idlePhase = 0.0f;
    private Color accentColor = Theme.accent();

    public WaveformVisualizer(VisualizerDataProvider provider) {
        this.dataProvider = provider;
        setOpaque(false);
        setPreferredSize(new Dimension(280, 60));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText("Visualizer: Click to toggle style");

        // 60 FPS refresh loop
        animationTimer = new Timer(16, e -> {
            idlePhase += 0.04f;
            updateSmoothedData();
            repaint();
        });
        animationTimer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cycleMode();
            }
        });

        Theme.addListener(isDark -> repaint());
    }

    public void setAccentColor(Color color) {
        this.accentColor = color != null ? color : Theme.accent();
    }

    public void cycleMode() {
        switch (currentMode) {
            case SPECTRUM_BARS:
                currentMode = Mode.FLUID_WAVE;
                break;
            case FLUID_WAVE:
                currentMode = Mode.AMBIENT_AURA;
                break;
            case AMBIENT_AURA:
                currentMode = Mode.SPECTRUM_BARS;
                break;
        }
        repaint();
    }

    public Mode getMode() {
        return currentMode;
    }

    private void updateSmoothedData() {
        boolean playing = (dataProvider != null && dataProvider.isPlaying());
        float[] raw = (dataProvider != null && playing) ? dataProvider.getFrequencyBands(smoothedBars.length) : null;

        for (int i = 0; i < smoothedBars.length; i++) {
            float target;
            if (playing && raw != null && i < raw.length) {
                target = Math.min(1.0f, raw[i] * 1.5f);
            } else {
                // Subtle idle breathing wave
                target = (float) (0.08 + 0.06 * Math.sin(idlePhase + i * 0.3));
            }

            // Smooth interpolation
            if (target > smoothedBars[i]) {
                smoothedBars[i] = smoothedBars[i] * 0.4f + target * 0.6f; // Quick attack
            } else {
                smoothedBars[i] = smoothedBars[i] * 0.85f + target * 0.15f; // Slow decay
            }

            // Peak tracking
            if (smoothedBars[i] > peakBars[i]) {
                peakBars[i] = smoothedBars[i];
            } else {
                peakBars[i] = Math.max(0, peakBars[i] - 0.015f);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityHints(g2);

        int w = getWidth();
        int h = getHeight();

        switch (currentMode) {
            case SPECTRUM_BARS:
                paintSpectrumBars(g2, w, h);
                break;
            case FLUID_WAVE:
                paintFluidWave(g2, w, h);
                break;
            case AMBIENT_AURA:
                paintAmbientAura(g2, w, h);
                break;
        }

        g2.dispose();
    }

    private void paintSpectrumBars(Graphics2D g2, int w, int h) {
        int count = 28;
        float gap = 3.5f;
        float totalGaps = gap * (count - 1);
        float barWidth = Math.max(3f, (w - totalGaps) / count);
        float maxBarHeight = h * 0.85f;

        Color c1 = accentColor;
        Color c2 = Theme.ACCENT_PURPLE;

        for (int i = 0; i < count; i++) {
            float val = smoothedBars[i % smoothedBars.length];
            float barH = Math.max(4f, val * maxBarHeight);
            float bx = i * (barWidth + gap);
            float by = h - barH - 4;

            // Gradient per bar
            float ratio = (float) i / count;
            Color barColor = blend(c1, c2, ratio);
            g2.setColor(barColor);
            g2.fill(new RoundRectangle2D.Float(bx, by, barWidth, barH, barWidth, barWidth));

            // Floating peak dot
            float peakVal = peakBars[i % peakBars.length];
            float peakY = h - (peakVal * maxBarHeight) - 8;
            g2.setColor(new Color(255, 255, 255, 160));
            g2.fill(new RoundRectangle2D.Float(bx, Math.max(2, peakY), barWidth, 2f, 2f, 2f));
        }
    }

    private void paintFluidWave(Graphics2D g2, int w, int h) {
        float[] samples = (dataProvider != null && dataProvider.isPlaying()) ? dataProvider.getWaveformSamples() : null;
        int points = 64;
        float step = (float) w / (points - 1);
        float midY = h / 2f;

        Path2D wavePath = new Path2D.Float();
        wavePath.moveTo(0, midY);

        for (int i = 0; i < points; i++) {
            float x = i * step;
            float amp;
            if (samples != null && samples.length > 0) {
                int sIdx = (int) (((float) i / points) * samples.length);
                amp = samples[sIdx] * (h * 0.42f);
            } else {
                amp = (float) (Math.sin(idlePhase * 1.5 + i * 0.25) * (h * 0.12f));
            }
            float y = midY + amp;
            wavePath.lineTo(x, y);
        }

        // Fill below wave with translucent gradient
        Path2D fillPath = (Path2D) wavePath.clone();
        fillPath.lineTo(w, h);
        fillPath.lineTo(0, h);
        fillPath.closePath();

        GradientPaint fillGrad = new GradientPaint(0, 0,
                new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 55),
                0, h, new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 0));
        g2.setPaint(fillGrad);
        g2.fill(fillPath);

        // Draw crisp glowing waveform line
        g2.setColor(accentColor);
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(wavePath);
    }

    private void paintAmbientAura(Graphics2D g2, int w, int h) {
        float energy = (dataProvider != null && dataProvider.isPlaying())
                ? dataProvider.getInstantaneousEnergy()
                : (float) (0.2 + 0.1 * Math.sin(idlePhase));

        int cx = w / 2;
        int cy = h / 2;
        int maxR = Math.min(w, h) / 2;

        for (int i = 3; i >= 1; i--) {
            float radius = maxR * (0.35f + energy * 0.65f) * (i / 3f);
            int alpha = (int) (40 * (4 - i) * energy);
            alpha = Math.max(5, Math.min(100, alpha));
            g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), alpha));
            g2.fillOval((int) (cx - radius), (int) (cy - radius), (int) (radius * 2), (int) (radius * 2));
        }

        // Center pulsing orb
        float coreR = 6 + energy * 10;
        g2.setColor(accentColor);
        g2.fillOval((int) (cx - coreR), (int) (cy - coreR), (int) (coreR * 2), (int) (coreR * 2));
    }

    private Color blend(Color c1, Color c2, float ratio) {
        int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        return new Color(r, g, b);
    }
}
