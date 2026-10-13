package com.endless.ui.components;

import com.endless.audio.AudioPlayer;
import com.endless.model.Playlist;
import com.endless.model.Track;
import com.endless.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Bottom control dock featuring playback controls, now-playing summary,
 * progress scrubber, volume slider, and visualizer shortcuts.
 */
public class ControlsPanel extends JPanel {

    private final AudioPlayer player;
    private final Playlist playlist;
    private final WaveformVisualizer visualizer;

    // UI elements
    private AppleButton shuffleBtn;
    private AppleButton prevBtn;
    private AppleButton playPauseBtn;
    private AppleButton nextBtn;
    private AppleButton repeatBtn;
    private AppleButton favoriteBtn;
    private ProgressScrubber scrubber;

    private AppleSlider volumeSlider;
    private AppleButton volumeIconBtn;
    private float previousVolume = 0.85f;

    private Track currentTrack = null;
    private JPanel leftPanel;

    public ControlsPanel(AudioPlayer player, Playlist playlist, WaveformVisualizer visualizer) {
        this.player = player;
        this.playlist = playlist;
        this.visualizer = visualizer;

        setPreferredSize(new Dimension(800, 92));
        setOpaque(false);
        setLayout(new BorderLayout());

        setupComponents();
        setupListeners();

        // Listen for Theme changes to update colors immediately
        Theme.addListener(isDark -> {
            repaint();
            if (leftPanel != null) leftPanel.repaint();
        });
    }

    private void setupComponents() {
        // --- Left: Now Playing Metadata ---
        leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityHints(g2);

                int h = getHeight();
                int artSize = 46;
                int ax = 16;
                int ay = (h - artSize) / 2;

                // Draw thumbnail
                if (currentTrack != null && currentTrack.getArtwork() != null) {
                    BufferedImage art = currentTrack.getArtwork();
                    Shape clip = new RoundRectangle2D.Float(ax, ay, artSize, artSize, 10, 10);
                    g2.setClip(clip);
                    g2.drawImage(art, ax, ay, artSize, artSize, null);
                    g2.setClip(null);
                } else {
                    g2.setColor(Theme.cardHoverBg());
                    g2.fill(new RoundRectangle2D.Float(ax, ay, artSize, artSize, 10, 10));
                    SFIcon.draw(g2, SFIcon.Type.WAVEFORM, ax + 11, ay + 11, 24, Theme.textTertiary());
                }

                // Border around thumbnail
                g2.setColor(Theme.border());
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(ax + 0.5f, ay + 0.5f, artSize - 1f, artSize - 1f, 10, 10));

                // Title and Artist
                int textX = ax + artSize + 12;
                String title = (currentTrack != null) ? currentTrack.getTitle() : "Endless";
                String artist = (currentTrack != null) ? currentTrack.getArtist() : "Select a track to play";

                // Title
                g2.setFont(Theme.bold(13f));
                g2.setColor(Theme.textPrimary());
                FontMetrics fm = g2.getFontMetrics();
                // Truncate if too long
                if (fm.stringWidth(title) > 130) {
                    title = title.substring(0, Math.min(title.length(), 16)) + "...";
                }
                g2.drawString(title, textX, ay + 18);

                // Artist
                g2.setFont(Theme.regular(11f));
                g2.setColor(Theme.textSecondary());
                FontMetrics fm2 = g2.getFontMetrics();
                if (fm2.stringWidth(artist) > 130) {
                    artist = artist.substring(0, Math.min(artist.length(), 18)) + "...";
                }
                g2.drawString(artist, textX, ay + 36);

                g2.dispose();
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(240, 92));
        leftPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 32));

        favoriteBtn = new AppleButton(SFIcon.Type.HEART, AppleButton.Style.ICON_ONLY);
        favoriteBtn.setPreferredSize(new Dimension(28, 28));
        favoriteBtn.setIconSize(16f);
        favoriteBtn.setToolTipText("Add to Favorites");
        favoriteBtn.addActionListener(e -> {
            if (currentTrack != null) {
                currentTrack.setFavorite(!currentTrack.isFavorite());
                favoriteBtn.setIconType(currentTrack.isFavorite() ? SFIcon.Type.HEART_FILLED : SFIcon.Type.HEART);
                favoriteBtn.setActive(currentTrack.isFavorite());
                repaint();
            }
        });
        leftPanel.add(favoriteBtn);

        // --- Center: Playback Controls & Scrubber ---
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Buttons row
        JPanel buttonsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        buttonsRow.setOpaque(false);

        shuffleBtn = new AppleButton(SFIcon.Type.SHUFFLE, AppleButton.Style.ICON_ONLY);
        shuffleBtn.setPreferredSize(new Dimension(32, 32));
        shuffleBtn.setIconSize(17f);
        shuffleBtn.setToolTipText("Shuffle (⌘S)");
        shuffleBtn.addActionListener(e -> playlist.toggleShuffle());

        prevBtn = new AppleButton(SFIcon.Type.PREVIOUS, AppleButton.Style.ICON_ONLY);
        prevBtn.setPreferredSize(new Dimension(34, 34));
        prevBtn.setIconSize(18f);
        prevBtn.setToolTipText("Previous Track (⌘←)");
        prevBtn.addActionListener(e -> {
            Track prev = playlist.previous();
            if (prev != null) player.play(prev);
        });

        playPauseBtn = new AppleButton(SFIcon.Type.PLAY, AppleButton.Style.PRIMARY);
        playPauseBtn.setPreferredSize(new Dimension(42, 42));
        playPauseBtn.setIconSize(20f);
        playPauseBtn.setToolTipText("Play / Pause (Space)");
        playPauseBtn.addActionListener(e -> {
            if (player.getState() == AudioPlayer.State.STOPPED) {
                Track t = playlist.getCurrentTrack();
                if (t == null && playlist.getTrackCount() > 0) {
                    playlist.setCurrentIndex(0);
                    t = playlist.getCurrentTrack();
                }
                if (t != null) player.play(t);
            } else {
                player.togglePlayPause();
            }
        });

        nextBtn = new AppleButton(SFIcon.Type.NEXT, AppleButton.Style.ICON_ONLY);
        nextBtn.setPreferredSize(new Dimension(34, 34));
        nextBtn.setIconSize(18f);
        nextBtn.setToolTipText("Next Track (⌘→)");
        nextBtn.addActionListener(e -> {
            Track next = playlist.next();
            if (next != null) player.play(next);
        });

        repeatBtn = new AppleButton(SFIcon.Type.REPEAT, AppleButton.Style.ICON_ONLY);
        repeatBtn.setPreferredSize(new Dimension(32, 32));
        repeatBtn.setIconSize(17f);
        repeatBtn.setToolTipText("Repeat (⌘R)");
        repeatBtn.addActionListener(e -> playlist.cycleRepeatMode());

        buttonsRow.add(shuffleBtn);
        buttonsRow.add(prevBtn);
        buttonsRow.add(playPauseBtn);
        buttonsRow.add(nextBtn);
        buttonsRow.add(repeatBtn);

        // Scrubber
        scrubber = new ProgressScrubber();
        scrubber.setSeekListener(targetMs -> player.seekTo(targetMs));

        centerPanel.add(buttonsRow);
        centerPanel.add(scrubber);

        // --- Right: Volume & Tools ---
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 30));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(240, 92));

        // Visualizer mode switcher button
        AppleButton visBtn = new AppleButton(SFIcon.Type.WAVEFORM, AppleButton.Style.ICON_ONLY);
        visBtn.setPreferredSize(new Dimension(28, 28));
        visBtn.setIconSize(16f);
        visBtn.setToolTipText("Visualizer Mode");
        visBtn.addActionListener(e -> {
            if (visualizer != null) visualizer.cycleMode();
        });

        // Volume Button
        volumeIconBtn = new AppleButton(SFIcon.Type.VOLUME_HIGH, AppleButton.Style.ICON_ONLY);
        volumeIconBtn.setPreferredSize(new Dimension(28, 28));
        volumeIconBtn.setIconSize(16f);
        volumeIconBtn.setToolTipText("Mute / Unmute");
        volumeIconBtn.addActionListener(e -> {
            if (player.getVolume() > 0) {
                previousVolume = player.getVolume();
                player.setVolume(0);
                updateVolume(0);
            } else {
                float restored = (previousVolume > 0) ? previousVolume : 0.85f;
                player.setVolume(restored);
                updateVolume(restored);
            }
        });

        // Custom Apple Volume Slider
        volumeSlider = new AppleSlider(player.getVolume());
        volumeSlider.setValueChangeListener(val -> {
            player.setVolume(val);
            updateVolumeIcon();
        });

        rightPanel.add(visBtn);
        rightPanel.add(volumeIconBtn);
        rightPanel.add(volumeSlider);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    public void updateVolume(float vol) {
        if (volumeSlider != null) {
            volumeSlider.setValue(vol);
        }
        updateVolumeIcon();
        repaint();
    }

    private void updateVolumeIcon() {
        float vol = player.getVolume();
        if (vol <= 0.001f) {
            volumeIconBtn.setIconType(SFIcon.Type.VOLUME_MUTE);
        } else if (vol < 0.33f) {
            volumeIconBtn.setIconType(SFIcon.Type.VOLUME_LOW);
        } else if (vol < 0.66f) {
            volumeIconBtn.setIconType(SFIcon.Type.VOLUME_MED);
        } else {
            volumeIconBtn.setIconType(SFIcon.Type.VOLUME_HIGH);
        }
        volumeIconBtn.repaint();
    }

    private void setupListeners() {
        player.addListener(new AudioPlayer.PlaybackListener() {
            @Override
            public void onPlaybackStateChanged(AudioPlayer.State state) {
                if (state == AudioPlayer.State.PLAYING) {
                    playPauseBtn.setIconType(SFIcon.Type.PAUSE);
                } else {
                    playPauseBtn.setIconType(SFIcon.Type.PLAY);
                }
                repaint();
            }

            @Override
            public void onProgress(long pos, long dur) {
                scrubber.setProgress(pos, dur);
            }

            @Override
            public void onTrackEnded() {
                // Advance to next song according to playlist
                SwingUtilities.invokeLater(() -> {
                    Track next = playlist.next();
                    if (next != null) {
                        player.play(next);
                    }
                });
            }
        });

        playlist.addListener(new Playlist.PlaylistListener() {
            @Override
            public void onPlaylistChanged() {
                repaint();
            }

            @Override
            public void onCurrentTrackChanged(Track track, int index) {
                setTrack(track);
            }

            @Override
            public void onShuffleChanged(boolean shuffle) {
                shuffleBtn.setActive(shuffle);
            }

            @Override
            public void onRepeatChanged(Playlist.RepeatMode mode) {
                switch (mode) {
                    case OFF:
                        repeatBtn.setIconType(SFIcon.Type.REPEAT);
                        repeatBtn.setActive(false);
                        break;
                    case ALL:
                        repeatBtn.setIconType(SFIcon.Type.REPEAT);
                        repeatBtn.setActive(true);
                        break;
                    case ONE:
                        repeatBtn.setIconType(SFIcon.Type.REPEAT_ONE);
                        repeatBtn.setActive(true);
                        break;
                }
            }
        });
    }

    public void setTrack(Track track) {
        this.currentTrack = track;
        if (track != null) {
            favoriteBtn.setIconType(track.isFavorite() ? SFIcon.Type.HEART_FILLED : SFIcon.Type.HEART);
            favoriteBtn.setActive(track.isFavorite());
            scrubber.setCustomAccent(track.getAccentColor());
            if (volumeSlider != null) {
                volumeSlider.setCustomAccent(track.getAccentColor());
            }
            playPauseBtn.setCustomAccentColor(track.getAccentColor());
            if (visualizer != null) {
                visualizer.setAccentColor(track.getAccentColor());
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityHints(g2);

        int w = getWidth();
        int h = getHeight();

        // Dock background with frosted surface feel
        g2.setColor(Theme.playerBarBg());
        g2.fillRect(0, 0, w, h);

        // Top border divider
        g2.setColor(Theme.border());
        g2.drawLine(0, 0, w, 0);

        g2.dispose();
        super.paintComponent(g);
    }
}
