package com.endless.ui.components;

import com.endless.audio.AudioPlayer;
import com.endless.model.Playlist;
import com.endless.model.Track;
import com.endless.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Apple Music styled library and playlist track table with animated equalizer,
 * thumbnail art, hover highlights, and context actions.
 */
public class PlaylistView extends JPanel {

    private final AudioPlayer player;
    private final Playlist playlist;

    private String filterQuery = "";
    private String activeCategory = "ALL"; // ALL, FAVORITES, DEMO
    private final List<Track> filteredTracks = new ArrayList<>();

    private int hoveredRow = -1;
    private int selectedRow = -1;

    // Mini animated equalizer state
    private final Timer eqTimer;
    private float eqPhase = 0f;

    public PlaylistView(AudioPlayer player, Playlist playlist) {
        this.player = player;
        this.playlist = playlist;

        setOpaque(false);
        setLayout(new BorderLayout());

        updateFilteredList();

        eqTimer = new Timer(50, e -> {
            if (player.isPlaying()) {
                eqPhase += 0.35f;
                repaint();
            }
        });
        eqTimer.start();

        setupMouseInteractions();
        setupListeners();

        Theme.addListener(isDark -> repaint());
    }

    public void setFilterQuery(String query) {
        this.filterQuery = (query != null) ? query.toLowerCase().trim() : "";
        updateFilteredList();
        repaint();
    }

    public void setActiveCategory(String category) {
        this.activeCategory = category;
        updateFilteredList();
        repaint();
    }

    public void updateFilteredList() {
        filteredTracks.clear();
        for (Track t : playlist.getTracks()) {
            // Category check
            if ("FAVORITES".equals(activeCategory) && !t.isFavorite()) {
                continue;
            }
            if ("DEMO".equals(activeCategory) && !t.isDemo()) {
                continue;
            }

            // Search query check
            if (!filterQuery.isEmpty()) {
                boolean matchTitle = t.getTitle().toLowerCase().contains(filterQuery);
                boolean matchArtist = t.getArtist().toLowerCase().contains(filterQuery);
                boolean matchAlbum = t.getAlbum().toLowerCase().contains(filterQuery);
                if (!matchTitle && !matchArtist && !matchAlbum) {
                    continue;
                }
            }

            filteredTracks.add(t);
        }
    }

    private void setupMouseInteractions() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int rowH = 54;
                int headerH = 32;
                int y = e.getY() - headerH;
                int row = (y >= 0) ? y / rowH : -1;
                if (row >= filteredTracks.size()) row = -1;

                if (row != hoveredRow) {
                    hoveredRow = row;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (hoveredRow != -1) {
                    hoveredRow = -1;
                    repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int rowH = 54;
                int headerH = 32;
                int y = e.getY() - headerH;
                int row = (y >= 0) ? y / rowH : -1;

                if (row >= 0 && row < filteredTracks.size()) {
                    selectedRow = row;
                    Track target = filteredTracks.get(row);

                    // Check if clicked favorite heart column (near right edge)
                    int heartX = getWidth() - 36;
                    if (e.getX() >= heartX - 10 && e.getX() <= heartX + 20) {
                        target.setFavorite(!target.isFavorite());
                        repaint();
                        return;
                    }

                    // Double click to play
                    if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                        int fullIndex = playlist.getTracks().indexOf(target);
                        if (fullIndex >= 0) {
                            playlist.setCurrentIndex(fullIndex);
                            player.play(target);
                        }
                    }

                    // Right click context menu
                    if (SwingUtilities.isRightMouseButton(e)) {
                        showContextMenu(e.getX(), e.getY(), target);
                    }

                    repaint();
                }
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private void showContextMenu(int x, int y, Track track) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem playItem = new JMenuItem("Play Now");
        playItem.addActionListener(e -> {
            int fullIndex = playlist.getTracks().indexOf(track);
            if (fullIndex >= 0) {
                playlist.setCurrentIndex(fullIndex);
                player.play(track);
            }
        });

        JMenuItem favItem = new JMenuItem(track.isFavorite() ? "Remove from Favorites" : "Add to Favorites");
        favItem.addActionListener(e -> {
            track.setFavorite(!track.isFavorite());
            repaint();
        });

        JMenuItem removeItem = new JMenuItem("Remove from Library");
        removeItem.addActionListener(e -> {
            int fullIndex = playlist.getTracks().indexOf(track);
            if (fullIndex >= 0) {
                playlist.removeTrack(fullIndex);
            }
        });

        JMenuItem revealItem = new JMenuItem("Reveal in Finder");
        revealItem.addActionListener(e -> {
            if (track.getFile() != null && track.getFile().exists()) {
                try {
                    Desktop.getDesktop().open(track.getFile().getParentFile());
                } catch (Exception ignored) {
                }
            }
        });

        menu.add(playItem);
        menu.add(favItem);
        menu.addSeparator();
        menu.add(revealItem);
        menu.add(removeItem);

        menu.show(this, x, y);
    }

    private void setupListeners() {
        playlist.addListener(new Playlist.PlaylistListener() {
            @Override
            public void onPlaylistChanged() {
                updateFilteredList();
                repaint();
            }

            @Override
            public void onCurrentTrackChanged(Track track, int index) {
                repaint();
            }

            @Override
            public void onShuffleChanged(boolean shuffle) {}

            @Override
            public void onRepeatChanged(Playlist.RepeatMode mode) {}
        });

        player.addListener(new AudioPlayer.PlaybackListener() {
            @Override
            public void onPlaybackStateChanged(AudioPlayer.State state) {
                repaint();
            }

            @Override
            public void onProgress(long pos, long dur) {}

            @Override
            public void onTrackEnded() {}
        });
    }

    @Override
    public Dimension getPreferredSize() {
        int rowH = 54;
        int headerH = 32;
        int count = filteredTracks.size();
        return new Dimension(500, headerH + (count * rowH) + 40);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityHints(g2);

        int w = getWidth();
        int headerH = 32;
        int rowH = 54;

        // 1. Table Header
        g2.setColor(Theme.textTertiary());
        g2.setFont(Theme.bold(10f));
        g2.drawString("#", 20, 20);
        g2.drawString("TITLE", 60, 20);
        g2.drawString("ALBUM", (int) (w * 0.60f), 20);
        g2.drawString("TIME", w - 76, 20);

        // Header bottom divider
        g2.setColor(Theme.borderSubtle());
        g2.drawLine(12, headerH - 1, w - 12, headerH - 1);

        // 2. Empty State
        if (filteredTracks.isEmpty()) {
            g2.setFont(Theme.medium(14f));
            g2.setColor(Theme.textSecondary());
            String emptyMsg = filterQuery.isEmpty() ? "No tracks in this view" : "No results for \"" + filterQuery + "\"";
            FontMetrics fm = g2.getFontMetrics();
            int msgW = fm.stringWidth(emptyMsg);
            g2.drawString(emptyMsg, (w - msgW) / 2, headerH + 80);

            g2.setFont(Theme.regular(12f));
            g2.setColor(Theme.textTertiary());
            String subMsg = "Drag & drop audio files here or click '+ Add Music'";
            int subW = g2.getFontMetrics().stringWidth(subMsg);
            g2.drawString(subMsg, (w - subW) / 2, headerH + 104);
            g2.dispose();
            return;
        }

        // 3. Rows
        Track currentTrack = playlist.getCurrentTrack();

        for (int i = 0; i < filteredTracks.size(); i++) {
            Track t = filteredTracks.get(i);
            int ry = headerH + (i * rowH);
            boolean isCurrent = (currentTrack != null && currentTrack.getId().equals(t.getId()));
            boolean isHovered = (i == hoveredRow);
            boolean isSelected = (i == selectedRow);

            // Row Highlight
            if (isHovered || isSelected || isCurrent) {
                Color trkAccent = t.getAccentColor();
                Color rowBg = isCurrent
                        ? (Theme.isDarkMode()
                                ? new Color(trkAccent.getRed(), trkAccent.getGreen(), trkAccent.getBlue(), 28)
                                : new Color(trkAccent.getRed(), trkAccent.getGreen(), trkAccent.getBlue(), 20))
                        : (isHovered ? Theme.cardHoverBg() : Theme.cardSelectedBg());
                g2.setColor(rowBg);
                g2.fill(new RoundRectangle2D.Float(8, ry + 2, w - 16, rowH - 4, 10, 10));
            }

            // Index or Animated Equalizer
            if (isCurrent && player.isPlaying()) {
                // Draw 3-bar mini animated equalizer
                float bw = 2.5f;
                float bx = 18f;
                Color eqColor = t.getAccentColor();
                g2.setColor(eqColor);
                float h1 = (float) (4 + 8 * Math.abs(Math.sin(eqPhase)));
                float h2 = (float) (4 + 10 * Math.abs(Math.sin(eqPhase + 1.2)));
                float h3 = (float) (4 + 7 * Math.abs(Math.sin(eqPhase + 2.4)));

                float baseEqY = ry + (rowH / 2f) + 6;
                g2.fill(new RoundRectangle2D.Float(bx, baseEqY - h1, bw, h1, bw, bw));
                g2.fill(new RoundRectangle2D.Float(bx + 4.5f, baseEqY - h2, bw, h2, bw, bw));
                g2.fill(new RoundRectangle2D.Float(bx + 9.0f, baseEqY - h3, bw, h3, bw, bw));
            } else {
                g2.setFont(Theme.regular(12f));
                g2.setColor(isCurrent ? t.getAccentColor() : Theme.textTertiary());
                g2.drawString(String.valueOf(i + 1), 18, ry + (rowH / 2) + 4);
            }

            // Thumbnail Artwork (36x36)
            int artSize = 36;
            int ax = 56;
            int ay = ry + (rowH - artSize) / 2;
            BufferedImage art = t.getArtwork();
            if (art != null) {
                Shape clip = new RoundRectangle2D.Float(ax, ay, artSize, artSize, 8, 8);
                Graphics2D gArt = (Graphics2D) g2.create();
                gArt.setClip(clip);
                gArt.drawImage(art, ax, ay, artSize, artSize, null);
                gArt.dispose();
            } else {
                g2.setColor(Theme.cardHoverBg());
                g2.fill(new RoundRectangle2D.Float(ax, ay, artSize, artSize, 8, 8));
            }

            // Title & Artist
            int textX = ax + artSize + 12;
            int titleY = ry + (rowH / 2) - 2;
            int artistY = ry + (rowH / 2) + 14;

            g2.setFont(Theme.bold(13f));
            g2.setColor(isCurrent ? t.getAccentColor() : Theme.textPrimary());
            String title = t.getTitle();
            int maxTitleW = (int) (w * 0.60f) - textX - 10;
            if (g2.getFontMetrics().stringWidth(title) > maxTitleW) {
                title = title.substring(0, Math.min(title.length(), 24)) + "...";
            }
            g2.drawString(title, textX, titleY);

            g2.setFont(Theme.regular(11f));
            g2.setColor(Theme.textSecondary());
            String artist = t.getArtist();
            if (g2.getFontMetrics().stringWidth(artist) > maxTitleW) {
                artist = artist.substring(0, Math.min(artist.length(), 26)) + "...";
            }
            g2.drawString(artist, textX, artistY);

            // Album
            int albumX = (int) (w * 0.60f);
            g2.setFont(Theme.regular(12f));
            g2.setColor(Theme.textSecondary());
            String album = t.getAlbum();
            int maxAlbumW = w - albumX - 100;
            if (g2.getFontMetrics().stringWidth(album) > maxAlbumW) {
                album = album.substring(0, Math.min(album.length(), 20)) + "...";
            }
            g2.drawString(album, albumX, ry + (rowH / 2) + 4);

            // Duration
            String dur = t.getFormattedDuration();
            g2.setColor(Theme.textTertiary());
            g2.drawString(dur, w - 76, ry + (rowH / 2) + 4);

            // Favorite Heart icon
            int heartX = w - 36;
            int heartY = ry + (rowH - 14) / 2;
            if (t.isFavorite()) {
                SFIcon.draw(g2, SFIcon.Type.HEART_FILLED, heartX, heartY, 14, Theme.accent());
            } else if (isHovered) {
                SFIcon.draw(g2, SFIcon.Type.HEART, heartX, heartY, 14, Theme.textTertiary());
            }
        }

        g2.dispose();
    }
}
