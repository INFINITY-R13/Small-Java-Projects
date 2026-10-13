package com.endless.ui.components;

import com.endless.model.MetadataExtractor;
import com.endless.model.Playlist;
import com.endless.model.Track;
import com.endless.ui.Theme;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * macOS Music styled sidebar for library navigation, source selection,
 * file imports, and library statistics.
 */
public class Sidebar extends JPanel {

    public interface CategoryListener {
        void onCategorySelected(String categoryKey);
    }

    private final Playlist playlist;
    private CategoryListener categoryListener = null;

    private String selectedCategory = "ALL";
    private int hoveredIndex = -1;

    private static class NavItem {
        final String key;
        final String title;
        final SFIcon.Type icon;

        NavItem(String key, String title, SFIcon.Type icon) {
            this.key = key;
            this.title = title;
            this.icon = icon;
        }
    }

    private final List<NavItem> navItems = new ArrayList<>();

    public Sidebar(Playlist playlist) {
        this.playlist = playlist;
        setPreferredSize(new Dimension(210, 600));
        setOpaque(false);
        setLayout(new BorderLayout());

        navItems.add(new NavItem("ALL", "All Tracks", SFIcon.Type.WAVEFORM));
        navItems.add(new NavItem("FAVORITES", "Favorites", SFIcon.Type.HEART));
        navItems.add(new NavItem("DEMO", "Ambient Demos", SFIcon.Type.INFINITY));

        setupInteractions();
        setupBottomActions();

        Theme.addListener(isDark -> repaint());

        playlist.addListener(new Playlist.PlaylistListener() {
            @Override
            public void onPlaylistChanged() {
                repaint();
            }

            @Override
            public void onCurrentTrackChanged(Track track, int index) {}

            @Override
            public void onShuffleChanged(boolean shuffle) {}

            @Override
            public void onRepeatChanged(Playlist.RepeatMode mode) {}
        });
    }

    public void setCategoryListener(CategoryListener listener) {
        this.categoryListener = listener;
    }

    private void setupInteractions() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int itemH = 36;
                int startY = 80;
                int y = e.getY() - startY;
                int idx = (y >= 0) ? y / itemH : -1;
                if (idx >= navItems.size()) idx = -1;

                if (idx != hoveredIndex) {
                    hoveredIndex = idx;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (hoveredIndex != -1) {
                    hoveredIndex = -1;
                    repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int itemH = 36;
                int startY = 80;
                int y = e.getY() - startY;
                int idx = (y >= 0) ? y / itemH : -1;

                if (idx >= 0 && idx < navItems.size()) {
                    selectedCategory = navItems.get(idx).key;
                    if (categoryListener != null) {
                        categoryListener.onCategorySelected(selectedCategory);
                    }
                    repaint();
                }
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private void setupBottomActions() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(12, 14, 16, 14));

        AppleButton addFilesBtn = new AppleButton("Add Tracks", SFIcon.Type.PLUS, AppleButton.Style.PILL);
        addFilesBtn.setMaximumSize(new Dimension(182, 32));
        addFilesBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addFilesBtn.addActionListener(e -> chooseFiles());

        AppleButton addFolderBtn = new AppleButton("Add Folder", SFIcon.Type.FOLDER, AppleButton.Style.SECONDARY);
        addFolderBtn.setMaximumSize(new Dimension(182, 32));
        addFolderBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addFolderBtn.addActionListener(e -> chooseFolder());

        bottomPanel.add(addFilesBtn);
        bottomPanel.add(Box.createVerticalStrut(8));
        bottomPanel.add(addFolderBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void chooseFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Import Audio Files into Endless");
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new FileNameExtensionFilter("Audio Files (*.mp3, *.wav, *.m4a, *.flac, *.aiff)",
                "mp3", "wav", "m4a", "flac", "aiff", "aif", "au"));

        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            new Thread(() -> {
                for (File f : files) {
                    Track t = MetadataExtractor.extract(f);
                    if (t != null) {
                        SwingUtilities.invokeLater(() -> playlist.addTrack(t));
                    }
                }
            }).start();
        }
    }

    private void chooseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Folder with Music");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            new Thread(() -> scanFolder(folder)).start();
        }
    }

    private void scanFolder(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                scanFolder(f);
            } else {
                String name = f.getName().toLowerCase();
                if (name.endsWith(".wav") || name.endsWith(".mp3") || name.endsWith(".m4a")
                        || name.endsWith(".flac") || name.endsWith(".aiff") || name.endsWith(".aif")) {
                    Track t = MetadataExtractor.extract(f);
                    if (t != null) {
                        SwingUtilities.invokeLater(() -> playlist.addTrack(t));
                    }
                }
            }
        }
    }

    private int getCountForKey(String key) {
        int count = 0;
        for (Track t : playlist.getTracks()) {
            if ("ALL".equals(key)) count++;
            else if ("FAVORITES".equals(key) && t.isFavorite()) count++;
            else if ("DEMO".equals(key) && t.isDemo()) count++;
        }
        return count;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityHints(g2);

        int w = getWidth();
        int h = getHeight();

        // Sidebar Background
        g2.setColor(Theme.sidebarBg());
        g2.fillRect(0, 0, w, h);

        // Right vertical divider
        g2.setColor(Theme.border());
        g2.drawLine(w - 1, 0, w - 1, h);

        // Section header
        g2.setFont(Theme.bold(10f));
        g2.setColor(Theme.textTertiary());
        g2.drawString("LIBRARY", 18, 64);

        // Navigation Items
        int itemH = 36;
        int startY = 80;

        for (int i = 0; i < navItems.size(); i++) {
            NavItem item = navItems.get(i);
            int iy = startY + (i * itemH);
            boolean isSelected = item.key.equals(selectedCategory);
            boolean isHovered = (i == hoveredIndex);

            // Capsule highlight
            if (isSelected) {
                g2.setColor(Theme.cardSelectedBg());
                g2.fill(new RoundRectangle2D.Float(10, iy, w - 20, itemH - 4, 8, 8));
            } else if (isHovered) {
                g2.setColor(Theme.cardHoverBg());
                g2.fill(new RoundRectangle2D.Float(10, iy, w - 20, itemH - 4, 8, 8));
            }

            // Icon
            Color itemColor = isSelected ? Theme.accent() : Theme.textSecondary();
            SFIcon.draw(g2, item.icon, 20, iy + 7, 16, itemColor);

            // Label
            g2.setFont(isSelected ? Theme.bold(12f) : Theme.regular(12f));
            g2.setColor(isSelected ? Theme.textPrimary() : Theme.textSecondary());
            g2.drawString(item.title, 46, iy + 20);

            // Count badge
            int count = getCountForKey(item.key);
            if (count > 0) {
                String cntStr = String.valueOf(count);
                g2.setFont(Theme.regular(11f));
                g2.setColor(Theme.textTertiary());
                int cw = g2.getFontMetrics().stringWidth(cntStr);
                g2.drawString(cntStr, w - 24 - cw, iy + 20);
            }
        }

        g2.dispose();
    }
}
