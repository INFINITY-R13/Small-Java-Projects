package com.endless.ui;

import com.endless.audio.AudioPlayer;
import com.endless.audio.TrackSynthesizer;
import com.endless.model.MetadataExtractor;
import com.endless.model.Playlist;
import com.endless.model.Track;
import com.endless.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.List;

/**
 * Main application window for Endless.
 * Implements Apple Human Interface Guidelines with rounded window chrome,
 * frosted materials, keyboard shortcuts, and Finder drag-and-drop.
 */
public class MainWindow extends JFrame {

    private final AudioPlayer player;
    private final Playlist playlist;

    // Components
    private TitleBar titleBar;
    private Sidebar sidebar;
    private AlbumArtPanel albumArtPanel;
    private WaveformVisualizer visualizer;
    private ControlsPanel controlsPanel;
    private SearchBar searchBar;
    private PlaylistView playlistView;
    private JLabel heroBadgeLabel;
    private JLabel heroTitleLabel;
    private JLabel heroArtistLabel;
    private JLabel heroAlbumLabel;

    public MainWindow(AudioPlayer player, Playlist playlist) {
        this.player = player;
        this.playlist = playlist;

        setTitle("Endless");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1020, 720);
        setMinimumSize(new Dimension(860, 600));
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // Allows antialiased rounded window corners

        // Window shape
        setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
            }
        });

        initUI();
        setupShortcuts();
        setupDragAndDrop();
        setupThemeListener();
        setupPlaylistSync();
        setupAudioPlayerSync();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityHints(g2);
                int w = getWidth();
                int h = getHeight();

                // Window background with rounded corners
                g2.setColor(Theme.bg());
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 16, 16));

                // 1px Subtle outer border
                g2.setColor(Theme.border());
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0.6f, 0.6f, w - 1.2f, h - 1.2f, 16, 16));

                g2.dispose();
                super.paintComponent(g);
            }
        };
        rootPanel.setOpaque(false);
        rootPanel.setLayout(new BorderLayout());

        // Top: Title Bar
        titleBar = new TitleBar(this, player);
        rootPanel.add(titleBar, BorderLayout.NORTH);

        // Center Area: Sidebar + Content
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setOpaque(false);

        // Left: Sidebar
        sidebar = new Sidebar(playlist);
        sidebar.setCategoryListener(category -> playlistView.setActiveCategory(category));
        bodyPanel.add(sidebar, BorderLayout.WEST);

        // Right: Content Panel (Hero + Search + Playlist Table)
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        // Hero Section (Now Playing Showcase)
        JPanel heroPanel = buildHeroSection();
        contentPanel.add(heroPanel, BorderLayout.NORTH);

        // Main Library Section: Search Bar + Table
        JPanel libraryPanel = new JPanel(new BorderLayout());
        libraryPanel.setOpaque(false);
        libraryPanel.setBorder(new EmptyBorder(8, 16, 8, 16));

        // Search Bar Row
        JPanel searchRow = new JPanel(new BorderLayout());
        searchRow.setOpaque(false);
        searchRow.setBorder(new EmptyBorder(0, 4, 10, 4));

        searchBar = new SearchBar();
        searchBar.setSearchListener(query -> playlistView.setFilterQuery(query));
        searchRow.add(searchBar, BorderLayout.WEST);

        libraryPanel.add(searchRow, BorderLayout.NORTH);

        // Track List
        playlistView = new PlaylistView(player, playlist);
        JScrollPane scrollPane = new JScrollPane(playlistView);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        libraryPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(libraryPanel, BorderLayout.CENTER);

        bodyPanel.add(contentPanel, BorderLayout.CENTER);
        rootPanel.add(bodyPanel, BorderLayout.CENTER);

        // Bottom: Dock Controls Panel
        controlsPanel = new ControlsPanel(player, playlist, visualizer);
        rootPanel.add(controlsPanel, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private JPanel buildHeroSection() {
        JPanel hero = new JPanel(new BorderLayout(24, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityHints(g2);
                int w = getWidth();
                int h = getHeight();

                // Subtle card surface background
                g2.setColor(Theme.cardBg());
                g2.fill(new RoundRectangle2D.Float(16, 12, w - 32, h - 16, 16, 16));

                // 1px Border
                g2.setColor(Theme.border());
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(16.5f, 12.5f, w - 33, h - 17, 16, 16));

                g2.dispose();
                super.paintComponent(g);
            }
        };
        hero.setOpaque(false);
        hero.setPreferredSize(new Dimension(800, 210));
        hero.setBorder(new EmptyBorder(16, 28, 16, 28));

        // Hero Left: Album Art
        albumArtPanel = new AlbumArtPanel();
        albumArtPanel.setPreferredSize(new Dimension(175, 175));
        hero.add(albumArtPanel, BorderLayout.WEST);

        // Hero Center: Title, Artist, Album, and Live Visualizer
        JPanel infoAndVisualizer = new JPanel();
        infoAndVisualizer.setOpaque(false);
        infoAndVisualizer.setLayout(new BoxLayout(infoAndVisualizer, BoxLayout.Y_AXIS));

        infoAndVisualizer.add(Box.createVerticalStrut(10));

        // Status Badge Label
        heroBadgeLabel = new JLabel("● NOW PLAYING");
        heroBadgeLabel.setFont(Theme.bold(10f));
        updateHeroBadge();
        infoAndVisualizer.add(heroBadgeLabel);

        infoAndVisualizer.add(Box.createVerticalStrut(4));

        heroTitleLabel = new JLabel("Endless");
        heroTitleLabel.setFont(Theme.bold(22f));
        heroTitleLabel.setForeground(Theme.textPrimary());
        infoAndVisualizer.add(heroTitleLabel);

        infoAndVisualizer.add(Box.createVerticalStrut(2));

        heroArtistLabel = new JLabel("Select a song to start listening");
        heroArtistLabel.setFont(Theme.medium(14f));
        heroArtistLabel.setForeground(Theme.textSecondary());
        infoAndVisualizer.add(heroArtistLabel);

        heroAlbumLabel = new JLabel("");
        heroAlbumLabel.setFont(Theme.regular(12f));
        heroAlbumLabel.setForeground(Theme.textTertiary());
        infoAndVisualizer.add(heroAlbumLabel);

        infoAndVisualizer.add(Box.createVerticalStrut(12));

        // Live Animated Visualizer
        visualizer = new WaveformVisualizer(player);
        visualizer.setPreferredSize(new Dimension(380, 52));
        visualizer.setMaximumSize(new Dimension(800, 52));
        visualizer.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoAndVisualizer.add(visualizer);

        hero.add(infoAndVisualizer, BorderLayout.CENTER);

        return hero;
    }

    private void updateHeroBadge() {
        if (heroBadgeLabel == null) return;
        AudioPlayer.State state = (player != null) ? player.getState() : AudioPlayer.State.STOPPED;
        Track current = (playlist != null) ? playlist.getCurrentTrack() : null;
        Color accent = (current != null) ? current.getAccentColor() : Theme.accent();

        if (state == AudioPlayer.State.PLAYING) {
            heroBadgeLabel.setText("● NOW PLAYING");
            heroBadgeLabel.setForeground(Theme.isDarkMode() ? accent : accent.darker());
        } else if (state == AudioPlayer.State.PAUSED) {
            heroBadgeLabel.setText("❚❚ PAUSED");
            heroBadgeLabel.setForeground(Theme.isDarkMode() ? new Color(255, 185, 75) : new Color(180, 105, 0));
        } else {
            heroBadgeLabel.setText("LIBRARY");
            heroBadgeLabel.setForeground(Theme.textTertiary());
        }
    }

    private void setupPlaylistSync() {
        playlist.addListener(new Playlist.PlaylistListener() {
            @Override
            public void onPlaylistChanged() {
                updateHeroTrack(playlist.getCurrentTrack());
            }

            @Override
            public void onCurrentTrackChanged(Track track, int index) {
                updateHeroTrack(track);
            }

            @Override
            public void onShuffleChanged(boolean shuffle) {}

            @Override
            public void onRepeatChanged(Playlist.RepeatMode mode) {}
        });
    }

    private void setupAudioPlayerSync() {
        if (player != null) {
            player.addListener(new AudioPlayer.PlaybackListener() {
                @Override
                public void onPlaybackStateChanged(AudioPlayer.State state) {
                    SwingUtilities.invokeLater(() -> {
                        updateHeroBadge();
                        if (titleBar != null) titleBar.repaint();
                    });
                }

                @Override
                public void onProgress(long pos, long dur) {}

                @Override
                public void onTrackEnded() {
                    SwingUtilities.invokeLater(() -> {
                        updateHeroBadge();
                        if (titleBar != null) titleBar.repaint();
                    });
                }
            });
        }
    }

    private void updateHeroTrack(Track track) {
        if (track != null) {
            heroTitleLabel.setText(track.getTitle());
            heroArtistLabel.setText(track.getArtist());
            heroAlbumLabel.setText(track.getAlbum());
            albumArtPanel.setTrack(track);
            controlsPanel.setTrack(track);
            visualizer.setAccentColor(track.getAccentColor());
        }
        updateHeroBadge();
        repaint();
    }

    private void setupThemeListener() {
        Theme.addListener(isDark -> {
            heroTitleLabel.setForeground(Theme.textPrimary());
            heroArtistLabel.setForeground(Theme.textSecondary());
            heroAlbumLabel.setForeground(Theme.textTertiary());
            updateHeroBadge();
            repaint();
        });
    }

    private void setupShortcuts() {
        JRootPane root = getRootPane();

        // Spacebar: Play / Pause
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "playPause");
        root.getActionMap().put("playPause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        // Left arrow: Seek backward 5 seconds
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "seekBack");
        root.getActionMap().put("seekBack", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.seekTo(player.getPositionMs() - 5000);
            }
        });

        // Right arrow: Seek forward 5 seconds
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "seekFwd");
        root.getActionMap().put("seekFwd", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.seekTo(player.getPositionMs() + 5000);
            }
        });

        // Up arrow: Volume +5% (fully synchronized with slider and icon)
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "volUp");
        root.getActionMap().put("volUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float newVol = Math.min(1.0f, player.getVolume() + 0.05f);
                player.setVolume(newVol);
                controlsPanel.updateVolume(newVol);
            }
        });

        // Down arrow: Volume -5% (fully synchronized with slider and icon)
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "volDown");
        root.getActionMap().put("volDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float newVol = Math.max(0.0f, player.getVolume() - 0.05f);
                player.setVolume(newVol);
                controlsPanel.updateVolume(newVol);
            }
        });

        // Cmd + Right: Next song
        int meta = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, meta), "nextSong");
        root.getActionMap().put("nextSong", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Track next = playlist.next();
                if (next != null) player.play(next);
            }
        });

        // Cmd + Left: Previous song
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, meta), "prevSong");
        root.getActionMap().put("prevSong", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Track prev = playlist.previous();
                if (prev != null) player.play(prev);
            }
        });

        // Cmd + F: Focus search
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, meta), "search");
        root.getActionMap().put("search", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBar.focusSearch();
            }
        });

        // Cmd + L: Toggle Appearance
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_L, meta), "toggleTheme");
        root.getActionMap().put("toggleTheme", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Theme.toggleDarkMode();
            }
        });

        // Cmd + S: Toggle Shuffle
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, meta), "shuffle");
        root.getActionMap().put("shuffle", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playlist.toggleShuffle();
            }
        });

        // Cmd + R: Cycle Repeat
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, meta), "repeat");
        root.getActionMap().put("repeat", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playlist.cycleRepeatMode();
            }
        });
    }

    private void setupDragAndDrop() {
        new DropTarget(this, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        @SuppressWarnings("unchecked")
                        List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        new Thread(() -> {
                            for (File file : droppedFiles) {
                                if (file.isDirectory()) {
                                    scanAndAdd(file);
                                } else {
                                    Track t = MetadataExtractor.extract(file);
                                    if (t != null) {
                                        SwingUtilities.invokeLater(() -> playlist.addTrack(t));
                                    }
                                }
                            }
                        }).start();
                        dtde.dropComplete(true);
                    } else {
                        dtde.rejectDrop();
                    }
                } catch (Exception e) {
                    dtde.rejectDrop();
                }
            }
        });
    }

    private void scanAndAdd(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                scanAndAdd(f);
            } else {
                String n = f.getName().toLowerCase();
                if (n.endsWith(".mp3") || n.endsWith(".wav") || n.endsWith(".m4a") || n.endsWith(".flac") || n.endsWith(".aiff")) {
                    Track t = MetadataExtractor.extract(f);
                    if (t != null) {
                        SwingUtilities.invokeLater(() -> playlist.addTrack(t));
                    }
                }
            }
        }
    }
}
