package com.endless.model;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Extracts metadata, album artwork, and audio duration from audio files,
 * with fallbacks to Apple Music-style generative aesthetic art.
 */
public class MetadataExtractor {

    public static Track extract(File file) {
        if (file == null || !file.exists()) return null;

        String name = file.getName();
        int dot = name.lastIndexOf('.');
        String ext = (dot > 0) ? name.substring(dot + 1).toLowerCase() : "";
        String baseName = (dot > 0) ? name.substring(0, dot) : name;

        String title = baseName;
        String artist = "Unknown Artist";
        String album = "Unknown Album";
        long durationMs = 0;
        BufferedImage artwork = null;

        // Clean filename guessing if structured like "Artist - Title"
        if (baseName.contains(" - ")) {
            String[] parts = baseName.split(" - ", 2);
            artist = parts[0].trim();
            title = parts[1].trim();
        }

        // Try ID3v2 extraction for mp3/m4a/wav
        try {
            ID3Data id3 = readID3(file);
            if (id3 != null) {
                if (id3.title != null && !id3.title.isEmpty()) title = id3.title;
                if (id3.artist != null && !id3.artist.isEmpty()) artist = id3.artist;
                if (id3.album != null && !id3.album.isEmpty()) album = id3.album;
                if (id3.artwork != null) artwork = id3.artwork;
            }
        } catch (Exception ignored) {
        }

        // Try calculating duration
        durationMs = estimateDuration(file, ext);

        // If no artwork, generate Apple Music style aesthetic gradient cover
        if (artwork == null) {
            artwork = generateAppleCover(title, artist);
        }

        return new Track(title, artist, album, durationMs, file, artwork);
    }

    public static long estimateDuration(File file, String ext) {
        // 1. Try AudioSystem
        try {
            AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
            Map<String, Object> props = aff.properties();
            if (props.containsKey("duration")) {
                return ((Long) props.get("duration")) / 1000;
            }
            if (aff.getFormat() != null && aff.getFrameLength() > 0) {
                float frameRate = aff.getFormat().getFrameRate();
                if (frameRate > 0) {
                    return (long) ((aff.getFrameLength() / frameRate) * 1000);
                }
            }
        } catch (Exception ignored) {
        }

        // 2. Try macOS afinfo
        if (System.getProperty("os.name", "").toLowerCase().contains("mac")) {
            try {
                ProcessBuilder pb = new ProcessBuilder("afinfo", "-b", file.getAbsolutePath());
                Process p = pb.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("estimated duration:")) {
                            String[] parts = line.split(":");
                            if (parts.length > 1) {
                                String secStr = parts[1].trim().split(" ")[0];
                                double sec = Double.parseDouble(secStr);
                                return (long) (sec * 1000);
                            }
                        }
                    }
                }
                p.waitFor();
            } catch (Exception ignored) {
            }
        }

        // Default estimate based on file length (assuming standard bitrates)
        long len = file.length();
        if (ext.equals("wav") || ext.equals("aif") || ext.equals("aiff")) {
            // ~176.4 KB per second for 44.1kHz 16-bit stereo
            return (long) ((len / 176400.0) * 1000);
        } else {
            // Compressed: average ~256kbps (32 KB per sec)
            return (long) ((len / 32000.0) * 1000);
        }
    }

    /**
     * Minimal high-speed ID3v2 reader for MP3 files.
     */
    private static class ID3Data {
        String title;
        String artist;
        String album;
        BufferedImage artwork;
    }

    private static ID3Data readID3(File file) {
        if (!file.getName().toLowerCase().endsWith(".mp3")) return null;

        try (FileInputStream fis = new FileInputStream(file);
             DataInputStream dis = new DataInputStream(new BufferedInputStream(fis))) {

            byte[] header = new byte[10];
            dis.readFully(header);
            if (header[0] != 'I' || header[1] != 'D' || header[2] != '3') {
                return null;
            }

            int majorVersion = header[3];
            int tagSize = ((header[6] & 0x7F) << 21) |
                          ((header[7] & 0x7F) << 14) |
                          ((header[8] & 0x7F) << 7)  |
                          (header[9] & 0x7F);

            byte[] tagData = new byte[tagSize];
            dis.readFully(tagData);

            ID3Data data = new ID3Data();
            int offset = 0;

            while (offset + 10 <= tagSize) {
                String frameID = new String(tagData, offset, 4, StandardCharsets.ISO_8859_1);
                if (frameID.charAt(0) == 0) break; // Padding reached

                int frameSize;
                if (majorVersion == 4) {
                    // Syncsafe integer
                    frameSize = ((tagData[offset + 4] & 0x7F) << 21) |
                                ((tagData[offset + 5] & 0x7F) << 14) |
                                ((tagData[offset + 6] & 0x7F) << 7)  |
                                (tagData[offset + 7] & 0x7F);
                } else {
                    // Standard 32-bit int
                    frameSize = ((tagData[offset + 4] & 0xFF) << 24) |
                                ((tagData[offset + 5] & 0xFF) << 16) |
                                ((tagData[offset + 6] & 0xFF) << 8)  |
                                (tagData[offset + 7] & 0xFF);
                }

                offset += 10;
                if (frameSize <= 0 || offset + frameSize > tagSize) break;

                if ("TIT2".equals(frameID)) {
                    data.title = decodeTextFrame(tagData, offset, frameSize);
                } else if ("TPE1".equals(frameID)) {
                    data.artist = decodeTextFrame(tagData, offset, frameSize);
                } else if ("TALB".equals(frameID)) {
                    data.album = decodeTextFrame(tagData, offset, frameSize);
                } else if ("APIC".equals(frameID) && data.artwork == null) {
                    data.artwork = decodeApicFrame(tagData, offset, frameSize);
                }

                offset += frameSize;
            }

            return data;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String decodeTextFrame(byte[] data, int offset, int length) {
        if (length <= 1) return "";
        int encoding = data[offset];
        try {
            switch (encoding) {
                case 1:
                    return new String(data, offset + 1, length - 1, StandardCharsets.UTF_16).trim();
                case 2:
                    return new String(data, offset + 1, length - 1, StandardCharsets.UTF_16BE).trim();
                case 3:
                    return new String(data, offset + 1, length - 1, StandardCharsets.UTF_8).trim();
                default:
                    return new String(data, offset + 1, length - 1, StandardCharsets.ISO_8859_1).trim();
            }
        } catch (Exception e) {
            return "";
        }
    }

    private static BufferedImage decodeApicFrame(byte[] data, int offset, int length) {
        try {
            int pos = offset + 1; // skip encoding byte
            // skip MIME type (null terminated)
            while (pos < offset + length && data[pos] != 0) pos++;
            pos++; // skip null
            pos++; // skip picture type byte
            // skip description (null terminated)
            while (pos < offset + length && data[pos] != 0) pos++;
            pos++; // skip null

            if (pos < offset + length) {
                int imgBytesLen = (offset + length) - pos;
                ByteArrayInputStream bais = new ByteArrayInputStream(data, pos, imgBytesLen);
                return ImageIO.read(bais);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Generates a sleek, Apple Music-style aesthetic dynamic cover artwork with ambient gradient mesh
     * and minimalist iconography.
     */
    public static BufferedImage generateAppleCover(String title, String artist) {
        int size = 512;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int hash = Math.abs((title + artist).hashCode());

        // Apple signature color palettes
        Color[][] palettes = {
            {new Color(250, 45, 72), new Color(175, 82, 222), new Color(25, 20, 45)},   // Magenta -> Violet -> Midnight
            {new Color(10, 132, 255), new Color(50, 173, 230), new Color(15, 28, 50)},  // Azure -> Cyan -> Deep Abyss
            {new Color(255, 149, 0), new Color(255, 45, 85), new Color(40, 15, 25)},    // Sunset Peach -> Crimson -> Onyx
            {new Color(48, 209, 88), new Color(10, 132, 255), new Color(12, 35, 30)},   // Neon Mint -> Royal Blue -> Emerald
            {new Color(191, 90, 242), new Color(255, 55, 95), new Color(30, 12, 40)},   // Orchid Purple -> Electric Coral -> Obsidian
            {new Color(100, 210, 255), new Color(94, 92, 230), new Color(20, 22, 45)}   // Sky Pastel -> Indigo -> Galaxy
        };

        Color[] p = palettes[hash % palettes.length];
        Color c1 = p[0];
        Color c2 = p[1];
        Color c3 = p[2];

        // Base gradient
        GradientPaint gp = new GradientPaint(0, 0, c3, size, size, c2);
        g2.setPaint(gp);
        g2.fillRect(0, 0, size, size);

        // Ambient glowing orbs (Apple mesh gradient look)
        float cx1 = (hash % 100) / 100f * size;
        float cy1 = ((hash / 100) % 100) / 100f * size;
        RadialGradientPaint rgp1 = new RadialGradientPaint(
            cx1, cy1, size * 0.75f,
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{c1, new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 80), new Color(0, 0, 0, 0)}
        );
        g2.setPaint(rgp1);
        g2.fillOval(-size / 4, -size / 4, size * 3 / 2, size * 3 / 2);

        // Secondary ambient glow
        RadialGradientPaint rgp2 = new RadialGradientPaint(
            size - cx1, size - cy1, size * 0.6f,
            new float[]{0.0f, 0.8f, 1.0f},
            new Color[]{c2, new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 60), new Color(0, 0, 0, 0)}
        );
        g2.setPaint(rgp2);
        g2.fillOval(0, 0, size, size);

        // Minimalist Apple geometric overlay: soft centered frosted circle & infinity / initial
        g2.setColor(new Color(255, 255, 255, 18));
        g2.fillOval(size / 4, size / 4, size / 2, size / 2);
        g2.setColor(new Color(255, 255, 255, 40));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(size / 4, size / 4, size / 2, size / 2);

        // Center typography (Initials)
        String initial = (!title.isEmpty()) ? title.substring(0, 1).toUpperCase() : "E";
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 120);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int tx = (size - fm.stringWidth(initial)) / 2;
        int ty = (size - fm.getHeight()) / 2 + fm.getAscent();

        // Subtle soft drop shadow for letter
        g2.setColor(new Color(0, 0, 0, 60));
        g2.drawString(initial, tx + 2, ty + 3);

        // Pristine white text with slight glow
        g2.setColor(new Color(255, 255, 255, 240));
        g2.drawString(initial, tx, ty);

        // Subtle bottom border highlight (Apple card gloss edge)
        g2.setColor(new Color(255, 255, 255, 30));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRect(0, 0, size - 1, size - 1);

        g2.dispose();
        return img;
    }
}
