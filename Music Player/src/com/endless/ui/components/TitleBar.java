package com.endless.ui.components;

import com.endless.audio.AudioPlayer;
import com.endless.model.Track;
import com.endless.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * macOS styled custom title bar with authentic traffic lights, window drag support,
 * state indicators, and theme toggle.
 */
public class TitleBar extends JPanel {

    private final JFrame frame;
    private final AudioPlayer player;

    private Point dragStartPoint = null;
    private boolean hoveredTrafficLight = false;
    private boolean maxState = false;
    private Rectangle normalBounds = null;
    private AppleButton themeBtn;

    public TitleBar(JFrame frame, AudioPlayer player) {
        this.frame = frame;
        this.player = player;
        setPreferredSize(new Dimension(800, 42));
        setOpaque(false);
        setLayout(new BorderLayout());

        setupDragAndMaximize();
        buildRightActions();

        if (player != null) {
            player.addListener(new AudioPlayer.PlaybackListener() {
                @Override
                public void onPlaybackStateChanged(AudioPlayer.State state) {
                    repaint();
                }

                @Override
                public void onProgress(long pos, long dur) {}

                @Override
                public void onTrackEnded() {
                    repaint();
                }
            });
        }

        // Theme synchronization
        Theme.addListener(isDark -> {
            if (themeBtn != null) {
                themeBtn.setIconType(isDark ? SFIcon.Type.SUN : SFIcon.Type.MOON);
            }
            repaint();
        });
    }

    private void setupDragAndMaximize() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragStartPoint = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragStartPoint = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStartPoint != null) {
                    Point curr = e.getLocationOnScreen();
                    frame.setLocation(curr.x - dragStartPoint.x, curr.y - dragStartPoint.y);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    toggleMaximize();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                boolean inLights = (e.getX() >= 12 && e.getX() <= 74 && e.getY() >= 14 && e.getY() <= 28);
                if (inLights != hoveredTrafficLight) {
                    hoveredTrafficLight = inLights;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (hoveredTrafficLight) {
                    hoveredTrafficLight = false;
                    repaint();
                }
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);

        // Click handlers for traffic lights
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (y >= 13 && y <= 29) {
                    // Red (Close): x around 14..26
                    if (x >= 14 && x <= 26) {
                        System.exit(0);
                    }
                    // Yellow (Minimize): x around 34..46
                    else if (x >= 34 && x <= 46) {
                        frame.setState(Frame.ICONIFIED);
                    }
                    // Green (Maximize / Zoom): x around 54..66
                    else if (x >= 54 && x <= 66) {
                        toggleMaximize();
                    }
                }
            }
        });
    }

    public void toggleMaximize() {
        if (!maxState) {
            normalBounds = frame.getBounds();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle maxBound = ge.getMaximumWindowBounds();
            frame.setBounds(maxBound);
            maxState = true;
        } else {
            if (normalBounds != null) {
                frame.setBounds(normalBounds);
            }
            maxState = false;
        }
        frame.revalidate();
    }

    private void buildRightActions() {
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        rightPanel.setOpaque(false);

        // Theme toggle button
        themeBtn = new AppleButton(Theme.isDarkMode() ? SFIcon.Type.SUN : SFIcon.Type.MOON, AppleButton.Style.ICON_ONLY);
        themeBtn.setPreferredSize(new Dimension(28, 28));
        themeBtn.setIconSize(15f);
        themeBtn.setToolTipText("Toggle Appearance (⌘L)");
        themeBtn.addActionListener(e -> {
            Theme.toggleDarkMode();
            themeBtn.setIconType(Theme.isDarkMode() ? SFIcon.Type.SUN : SFIcon.Type.MOON);
            frame.repaint();
        });

        rightPanel.add(themeBtn);
        add(rightPanel, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityHints(g2);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(Theme.sidebarBg());
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 16, 16));
        g2.fillRect(0, h - 8, w, 8); // Square bottom

        // Traffic Lights
        int r = 12;
        int y = 15;

        // Red Close
        g2.setColor(Theme.TRAFFIC_RED);
        g2.fill(new Ellipse2D.Float(14, y, r, r));
        g2.setColor(new Color(0, 0, 0, 30));
        g2.draw(new Ellipse2D.Float(14, y, r, r));

        // Yellow Minimize
        g2.setColor(Theme.TRAFFIC_YELLOW);
        g2.fill(new Ellipse2D.Float(34, y, r, r));
        g2.setColor(new Color(0, 0, 0, 30));
        g2.draw(new Ellipse2D.Float(34, y, r, r));

        // Green Maximize
        g2.setColor(Theme.TRAFFIC_GREEN);
        g2.fill(new Ellipse2D.Float(54, y, r, r));
        g2.setColor(new Color(0, 0, 0, 30));
        g2.draw(new Ellipse2D.Float(54, y, r, r));

        // Draw hover symbols inside traffic lights if mouse is over them
        if (hoveredTrafficLight) {
            g2.setColor(new Color(60, 20, 20, 180));
            g2.setStroke(new BasicStroke(1.2f));
            // 'x' in red
            g2.drawLine(18, y + 4, 22, y + 8);
            g2.drawLine(22, y + 4, 18, y + 8);

            // '-' in yellow
            g2.setColor(new Color(90, 60, 10, 180));
            g2.drawLine(37, y + 6, 43, y + 6);

            // '+' or arrows in green
            g2.setColor(new Color(15, 70, 20, 180));
            g2.drawLine(57, y + 6, 63, y + 6);
            g2.drawLine(60, y + 3, 60, y + 9);
        }

        // Center branding: "Endless" with subtle glowing status pill
        g2.setFont(Theme.bold(13f));
        FontMetrics fm = g2.getFontMetrics();
        String title = "Endless";
        int titleW = fm.stringWidth(title);
        int cx = (w - titleW) / 2;
        int cy = (h - fm.getHeight()) / 2 + fm.getAscent();

        // Infinity mark next to title
        SFIcon.draw(g2, SFIcon.Type.INFINITY, cx - 20, (h - 14) / 2f, 14f, Theme.accent());

        g2.setColor(Theme.textPrimary());
        g2.drawString(title, cx, cy);

        // Status pill indicator if playing or paused
        if (player != null) {
            boolean isPlaying = player.isPlaying();
            boolean isPaused = (player.getState() == AudioPlayer.State.PAUSED);
            if (isPlaying || isPaused) {
                String statusText = isPlaying ? "PLAYING" : "PAUSED";
                g2.setFont(Theme.bold(9f));
                FontMetrics pfm = g2.getFontMetrics();
                int textW = pfm.stringWidth(statusText);
                int pillW = textW + 24; // room for dot, spacing and text
                int pillH = 18;
                int pillX = cx + titleW + 12;
                int pillY = (h - pillH) / 2;

                Color baseAccent = isPlaying ? Theme.accent() : new Color(255, 179, 64);
                Track current = player.getCurrentTrack();
                if (current != null && isPlaying) {
                    baseAccent = current.getAccentColor();
                }

                // Pill background
                int bgAlpha = Theme.isDarkMode() ? 36 : 24;
                g2.setColor(new Color(baseAccent.getRed(), baseAccent.getGreen(), baseAccent.getBlue(), bgAlpha));
                g2.fill(new RoundRectangle2D.Float(pillX, pillY, pillW, pillH, pillH, pillH));

                // Pill border
                int borderAlpha = Theme.isDarkMode() ? 75 : 85;
                g2.setColor(new Color(baseAccent.getRed(), baseAccent.getGreen(), baseAccent.getBlue(), borderAlpha));
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(pillX + 0.5f, pillY + 0.5f, pillW - 1f, pillH - 1f, pillH, pillH));

                // Status dot
                g2.setColor(baseAccent);
                g2.fillOval(pillX + 6, pillY + (pillH - 5) / 2, 5, 5);

                // Status text (high-contrast in both Dark and Light modes)
                Color textColor;
                if (Theme.isDarkMode()) {
                    textColor = isPlaying ? baseAccent : new Color(255, 190, 80);
                } else {
                    textColor = isPlaying ? baseAccent.darker() : new Color(175, 100, 0);
                }
                g2.setColor(textColor);
                int textY = pillY + (pillH - pfm.getHeight()) / 2 + pfm.getAscent();
                g2.drawString(statusText, pillX + 16, textY);
            }
        }

        // Subtle bottom border
        g2.setColor(Theme.borderSubtle());
        g2.drawLine(0, h - 1, w, h - 1);

        g2.dispose();
    }
}
