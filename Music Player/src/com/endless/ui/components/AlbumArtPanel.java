package com.endless.ui.components;

import com.endless.model.Track;
import com.endless.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Displays album artwork with Apple-style rounded corners, 1px border,
 * and a dynamic soft ambient backlight glow matching the track's color.
 */
public class AlbumArtPanel extends JPanel {

    private Track currentTrack = null;
    private int cornerRadius = 18;

    public AlbumArtPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(220, 220));
        Theme.addListener(isDark -> repaint());
    }

    public void setTrack(Track track) {
        this.currentTrack = track;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityHints(g2);

        int w = getWidth();
        int h = getHeight();

        // Calculate square bounds centered with padding for shadow
        int pad = 12;
        int artSize = Math.min(w, h) - (pad * 2);
        int ax = (w - artSize) / 2;
        int ay = (h - artSize) / 2;

        Color accent = (currentTrack != null) ? currentTrack.getAccentColor() : Theme.accent();

        // 1. Ambient Backlight Glow (Apple Music diffused color aura)
        if (currentTrack != null) {
            for (int i = 8; i >= 1; i--) {
                int glowAlpha = Math.max(2, 28 - (i * 3));
                Color glowColor = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), glowAlpha);
                g2.setColor(glowColor);
                int expand = i * 2;
                g2.fill(new RoundRectangle2D.Float(
                        ax - expand,
                        ay - expand + (i * 2), // slightly shifted down for realistic lighting
                        artSize + expand * 2,
                        artSize + expand * 2,
                        cornerRadius + i * 2,
                        cornerRadius + i * 2
                ));
            }
        }

        // 2. Artwork image or placeholder
        Shape artClip = new RoundRectangle2D.Float(ax, ay, artSize, artSize, cornerRadius, cornerRadius);
        Graphics2D gArt = (Graphics2D) g2.create();
        gArt.setClip(artClip);

        BufferedImage img = (currentTrack != null) ? currentTrack.getArtwork() : null;
        if (img != null) {
            gArt.drawImage(img, ax, ay, artSize, artSize, null);
        } else {
            // Elegant placeholder card
            gArt.setColor(Theme.cardBg());
            gArt.fillRect(ax, ay, artSize, artSize);
            SFIcon.draw(gArt, SFIcon.Type.WAVEFORM, ax + artSize * 0.3f, ay + artSize * 0.3f, artSize * 0.4f, Theme.textTertiary());
        }
        gArt.dispose();

        // 3. Crisp 1px inner border
        g2.setColor(new Color(255, 255, 255, 25));
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Float(ax + 0.6f, ay + 0.6f, artSize - 1.2f, artSize - 1.2f, cornerRadius, cornerRadius));

        g2.dispose();
    }
}
