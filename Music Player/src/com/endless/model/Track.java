package com.endless.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

/**
 * Represents a musical track in the Endless library with its metadata, artwork, and playback properties.
 */
public class Track {

    private final String id;
    private String title;
    private String artist;
    private String album;
    private long durationMs;
    private File file;
    private BufferedImage artwork;
    private Color accentColor;
    private boolean favorite;
    private boolean isDemo;

    public Track(String title, String artist, String album, long durationMs, File file, BufferedImage artwork) {
        this.id = UUID.randomUUID().toString();
        this.title = (title != null && !title.trim().isEmpty()) ? title : "Unknown Title";
        this.artist = (artist != null && !artist.trim().isEmpty()) ? artist : "Unknown Artist";
        this.album = (album != null && !album.trim().isEmpty()) ? album : "Unknown Album";
        this.durationMs = durationMs;
        this.file = file;
        this.artwork = artwork;
        this.favorite = false;
        this.isDemo = false;
        this.accentColor = extractDominantColor(artwork);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public BufferedImage getArtwork() {
        return artwork;
    }

    public void setArtwork(BufferedImage artwork) {
        this.artwork = artwork;
        this.accentColor = extractDominantColor(artwork);
    }

    public Color getAccentColor() {
        return accentColor != null ? accentColor : new Color(250, 45, 72);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isDemo() {
        return isDemo;
    }

    public void setDemo(boolean demo) {
        this.isDemo = demo;
    }

    public String getFormattedDuration() {
        return formatTime(durationMs);
    }

    public static String formatTime(long ms) {
        if (ms < 0) ms = 0;
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private Color extractDominantColor(BufferedImage img) {
        if (img == null) {
            return new Color(250, 45, 72); // Default Apple pink
        }
        try {
            long r = 0, g = 0, b = 0;
            int count = 0;
            int step = Math.max(1, img.getWidth() / 20);
            for (int y = 0; y < img.getHeight(); y += step) {
                for (int x = 0; x < img.getWidth(); x += step) {
                    int rgb = img.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    // Avoid overly dark or washed out gray pixels
                    int max = Math.max(red, Math.max(green, blue));
                    int min = Math.min(red, Math.min(green, blue));
                    if (max > 40 && (max - min) > 15) {
                        r += red;
                        g += green;
                        b += blue;
                        count++;
                    }
                }
            }
            if (count > 0) {
                return new Color((int) (r / count), (int) (g / count), (int) (b / count));
            }
        } catch (Exception ignored) {
        }
        return new Color(250, 45, 72);
    }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}
