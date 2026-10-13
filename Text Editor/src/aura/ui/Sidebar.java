package aura.ui;

import aura.theme.Theme;
import aura.theme.ThemeManager;
import aura.util.FileType;
import aura.util.Icons;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;

/**
 * Collapsible sidebar providing file tree browsing and workspace exploration.
 */
public class Sidebar extends JPanel {
    private File currentDirectory;
    private final JTree fileTree;
    private final JPanel headerPanel;
    private final JLabel lblTitle;
    private final JLabel lblEmpty;
    private final ModernButton btnOpenFolder;
    private final ModernButton btnRefresh;
    private final JPanel emptyPanel;
    private final JScrollPane treeScrollPane;
    private final Consumer<File> fileOpenConsumer;

    public Sidebar(Consumer<File> fileOpenConsumer, Runnable onOpenFolderAction) {
        this.fileOpenConsumer = fileOpenConsumer;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(240, 0));
        setMinimumSize(new Dimension(180, 0));

        // 1. Header
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 36));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 8));

        lblTitle = new JLabel("EXPLORER");
        lblTitle.setFont(new Font("-apple-system", Font.BOLD, 11));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        headerActions.setOpaque(false);

        btnOpenFolder = new ModernButton(Icons.createFolderIcon(ThemeManager.getTheme().fgMuted(), 13));
        btnOpenFolder.setToolTipText("Open Folder");
        btnOpenFolder.setPreferredSize(new Dimension(26, 26));
        btnOpenFolder.addActionListener(e -> onOpenFolderAction.run());

        btnRefresh = new ModernButton("↺");
        btnRefresh.setToolTipText("Refresh Explorer");
        btnRefresh.setPreferredSize(new Dimension(26, 26));
        btnRefresh.addActionListener(e -> refresh());

        headerActions.add(btnOpenFolder);
        headerActions.add(btnRefresh);
        headerPanel.add(headerActions, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Tree
        fileTree = new JTree();
        fileTree.setOpaque(true);
        fileTree.setRowHeight(26);
        fileTree.setRootVisible(false);
        fileTree.setShowsRootHandles(true);
        fileTree.setCellRenderer(new ModernTreeCellRenderer());

        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 || e.getClickCount() == 2) {
                    TreePath path = fileTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        if (node.getUserObject() instanceof FileNode fileNode && !fileNode.file.isDirectory()) {
                            fileOpenConsumer.accept(fileNode.file);
                        }
                    }
                }
            }
        });

        treeScrollPane = new JScrollPane(fileTree);
        treeScrollPane.setBorder(BorderFactory.createEmptyBorder());
        treeScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        treeScrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        // 3. Empty State
        emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setOpaque(false);
        emptyPanel.setBorder(BorderFactory.createEmptyBorder(40, 16, 20, 16));

        lblEmpty = new JLabel("No folder opened");
        lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblEmpty.setFont(new Font("-apple-system", Font.PLAIN, 12));

        ModernButton btnOpen = new ModernButton("Open Folder");
        btnOpen.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOpen.setPrimary(true);
        btnOpen.setMaximumSize(new Dimension(140, 30));
        btnOpen.addActionListener(e -> onOpenFolderAction.run());

        emptyPanel.add(lblEmpty);
        emptyPanel.add(Box.createVerticalStrut(14));
        emptyPanel.add(btnOpen);

        add(emptyPanel, BorderLayout.CENTER);

        ThemeManager.addListener(this::applyTheme);
    }

    public void setDirectory(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;
        this.currentDirectory = dir;

        remove(emptyPanel);
        add(treeScrollPane, BorderLayout.CENTER);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileNode(dir));
        populateTree(root, dir, 0);

        DefaultTreeModel model = new DefaultTreeModel(root);
        fileTree.setModel(model);

        revalidate();
        repaint();
    }

    public void refresh() {
        if (currentDirectory != null) {
            setDirectory(currentDirectory);
        }
    }

    private void populateTree(DefaultMutableTreeNode parentNode, File folder, int depth) {
        if (depth > 5) return; // avoid infinite recursion
        File[] files = folder.listFiles((f, name) -> !name.startsWith("."));
        if (files == null) return;

        Arrays.sort(files, Comparator.comparing(File::isFile).thenComparing(File::getName));

        for (File f : files) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(new FileNode(f));
            parentNode.add(child);
            if (f.isDirectory()) {
                populateTree(child, f, depth + 1);
            }
        }
    }

    private void applyTheme(Theme theme) {
        setBackground(theme.bgSidebar());
        headerPanel.setBackground(theme.bgSidebar());
        lblTitle.setForeground(theme.fgMuted());
        lblEmpty.setForeground(theme.fgMuted());
        btnOpenFolder.setIcon(Icons.createFolderIcon(theme.fgMuted(), 13));

        treeScrollPane.setBackground(theme.bgSidebar());
        treeScrollPane.getViewport().setBackground(theme.bgSidebar());
        fileTree.setBackground(theme.bgSidebar());
        fileTree.repaint();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Theme theme = ThemeManager.getTheme();
        g.setColor(theme.border());
        g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
    }

    private record FileNode(File file) {
        @Override
        public String toString() {
            return file.getName();
        }
    }

    /**
     * Completely custom tree cell renderer without DefaultTreeCellRenderer's hardcoded white boxes.
     */
    private static class ModernTreeCellRenderer extends JPanel implements TreeCellRenderer {
        private final JLabel iconLabel;
        private final JLabel textLabel;
        private boolean isSelected = false;

        public ModernTreeCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 6, 2));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 6));

            iconLabel = new JLabel();
            textLabel = new JLabel();
            textLabel.setFont(new Font("-apple-system", Font.PLAIN, 12));

            add(iconLabel);
            add(textLabel);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                      boolean exp, boolean leaf, int row, boolean hasFocus) {
            this.isSelected = sel;
            Theme theme = ThemeManager.getTheme();

            if (value instanceof DefaultMutableTreeNode node && node.getUserObject() instanceof FileNode fn) {
                File f = fn.file;
                textLabel.setText(f.getName());
                if (f.isDirectory()) {
                    iconLabel.setIcon(Icons.createFolderIcon(theme.accent(), 13));
                    textLabel.setForeground(sel ? theme.accent() : theme.fgHeader());
                } else {
                    FileType ft = FileType.fromFileName(f.getName());
                    iconLabel.setIcon(Icons.createFileIcon(ft.getBadgeColor(), 13));
                    textLabel.setForeground(sel ? theme.accent() : theme.fgEditor());
                }
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isSelected) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Theme theme = ThemeManager.getTheme();
                g2d.setColor(theme.bgActive());
                g2d.fillRoundRect(0, 1, getWidth(), getHeight() - 2, 6, 6);
                g2d.dispose();
            }
            super.paintComponent(g);
        }
    }
}
