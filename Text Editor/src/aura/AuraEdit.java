package aura;

import aura.editor.CodeEditorPane;
import aura.editor.EditorTab;
import aura.theme.Theme;
import aura.theme.ThemeManager;
import aura.ui.*;
import aura.util.FileType;
import aura.util.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * AuraEdit - Aesthetic, lightweight, modern code & text editor in Java.
 */
public class AuraEdit extends JFrame {
    private final ModernTabBar tabBar;
    private final JPanel editorContainer;
    private final Sidebar sidebar;
    private final StatusBar statusBar;
    private final FindReplaceBar findReplaceBar;
    private final JComboBox<String> cmbThemes;
    private final ModernButton btnNew;
    private final ModernButton btnOpen;
    private final ModernButton btnSave;
    private final ModernButton btnFind;
    private final ModernButton btnSidebar;
    private final ModernButton btnPreview;
    private final JLabel lblBrand;
    private final JPanel headerPanel;
    private final JPanel topContainer;
    private final JPanel rootPanel;
    private final JSplitPane mainSplitPane;

    private final List<EditorTab> tabs = new ArrayList<>();
    private int untitledCounter = 1;

    public AuraEdit() {
        super("AuraEdit");

        // Window properties
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClose();
            }
        });
        setSize(1120, 740);
        setMinimumSize(new Dimension(640, 420));
        setLocationRelativeTo(null);

        // 1. Status Bar
        statusBar = new StatusBar();

        // 2. Tab Bar
        tabBar = new ModernTabBar(
            this::selectTab,
            this::closeTab,
            this::newDocument
        );

        // 3. Find & Replace Overlay
        findReplaceBar = new FindReplaceBar();
        findReplaceBar.setVisible(false);

        // 4. Editor Container
        editorContainer = new JPanel(new BorderLayout());
        editorContainer.setOpaque(false);

        JPanel centerWithFind = new JPanel(new BorderLayout());
        centerWithFind.setOpaque(false);
        centerWithFind.add(findReplaceBar, BorderLayout.NORTH);
        centerWithFind.add(editorContainer, BorderLayout.CENTER);

        // 5. Sidebar
        sidebar = new Sidebar(this::openFile, this::chooseAndOpenFolder);

        // 6. Split Pane (Sidebar + Editor)
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, centerWithFind);
        mainSplitPane.setDividerSize(1);
        mainSplitPane.setDividerLocation(220);
        mainSplitPane.setBorder(BorderFactory.createEmptyBorder());
        mainSplitPane.setContinuousLayout(true);

        // 7. Header Toolbar
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 44));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        // Brand Label
        lblBrand = new JLabel("✦ AuraEdit");
        lblBrand.setFont(new Font("-apple-system", Font.BOLD, 15));

        // Toolbar Buttons
        JPanel toolbarActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 8));
        toolbarActions.setOpaque(false);

        btnNew = new ModernButton(Icons.createNewFileIcon(ThemeManager.getTheme().fgHeader(), 14));
        btnNew.setToolTipText("New File (Ctrl/Cmd + N)");
        btnNew.addActionListener(e -> newDocument());

        btnOpen = new ModernButton(Icons.createFileIcon(ThemeManager.getTheme().fgHeader(), 14));
        btnOpen.setToolTipText("Open File (Ctrl/Cmd + O)");
        btnOpen.addActionListener(e -> chooseAndOpenFile());

        btnSave = new ModernButton(Icons.createSaveIcon(ThemeManager.getTheme().fgHeader(), 14));
        btnSave.setToolTipText("Save File (Ctrl/Cmd + S)");
        btnSave.addActionListener(e -> saveCurrentDocument());

        btnFind = new ModernButton(Icons.createSearchIcon(ThemeManager.getTheme().fgHeader(), 14));
        btnFind.setToolTipText("Find & Replace (Ctrl/Cmd + F)");
        btnFind.addActionListener(e -> toggleFindBar(false));

        btnSidebar = new ModernButton(Icons.createSidebarIcon(ThemeManager.getTheme().fgHeader(), 14));
        btnSidebar.setToolTipText("Toggle Sidebar (Ctrl/Cmd + B)");
        btnSidebar.addActionListener(e -> toggleSidebar());

        btnPreview = new ModernButton(Icons.createEyeIcon(ThemeManager.getTheme().fgHeader(), 14));
        btnPreview.setToolTipText("Toggle Markdown Preview");
        btnPreview.addActionListener(e -> toggleMarkdownPreview());

        // Theme Switcher Dropdown
        String[] themeNames = ThemeManager.getThemes().stream().map(Theme::name).toArray(String[]::new);
        cmbThemes = new JComboBox<>(themeNames);
        cmbThemes.setFont(new Font("-apple-system", Font.PLAIN, 12));
        cmbThemes.setFocusable(false);
        cmbThemes.setPreferredSize(new Dimension(145, 26));
        cmbThemes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Theme t = ThemeManager.getTheme();
                setBackground(isSelected ? t.bgHover() : t.bgSurface());
                setForeground(isSelected ? t.accent() : t.fgEditor());
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        cmbThemes.addActionListener(e -> {
            int idx = cmbThemes.getSelectedIndex();
            if (idx >= 0 && idx < ThemeManager.getThemes().size()) {
                ThemeManager.setTheme(ThemeManager.getThemes().get(idx));
            }
        });

        toolbarActions.add(btnNew);
        toolbarActions.add(btnOpen);
        toolbarActions.add(btnSave);
        toolbarActions.add(btnFind);
        toolbarActions.add(btnPreview);
        toolbarActions.add(btnSidebar);
        toolbarActions.add(Box.createHorizontalStrut(6));
        toolbarActions.add(cmbThemes);

        headerPanel.add(lblBrand, BorderLayout.WEST);
        headerPanel.add(toolbarActions, BorderLayout.EAST);

        // Overall layout
        topContainer = new JPanel(new BorderLayout());
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(tabBar, BorderLayout.SOUTH);

        rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(topContainer, BorderLayout.NORTH);
        rootPanel.add(mainSplitPane, BorderLayout.CENTER);
        rootPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(rootPanel);

        // Theme listener
        ThemeManager.addListener(this::applyTheme);

        // Drag and Drop
        setupDragAndDrop();

        // Keyboard Shortcuts
        setupGlobalKeyBindings();

        // Load Welcome / Initial Document
        loadWelcomeDocument();
    }

    private void applyTheme(Theme theme) {
        setBackground(theme.bgEditor());
        getContentPane().setBackground(theme.bgEditor());
        if (rootPanel != null) rootPanel.setBackground(theme.bgEditor());
        if (topContainer != null) topContainer.setBackground(theme.bgHeader());
        if (headerPanel != null) headerPanel.setBackground(theme.bgHeader());
        if (lblBrand != null) lblBrand.setForeground(theme.accent());
        if (mainSplitPane != null) mainSplitPane.setBackground(theme.border());

        if (btnNew != null) btnNew.setIcon(Icons.createNewFileIcon(theme.fgHeader(), 14));
        if (btnOpen != null) btnOpen.setIcon(Icons.createFileIcon(theme.fgHeader(), 14));
        if (btnSave != null) btnSave.setIcon(Icons.createSaveIcon(theme.fgHeader(), 14));
        if (btnFind != null) btnFind.setIcon(Icons.createSearchIcon(theme.fgHeader(), 14));
        if (btnSidebar != null) btnSidebar.setIcon(Icons.createSidebarIcon(theme.fgHeader(), 14));
        if (btnPreview != null) btnPreview.setIcon(Icons.createEyeIcon(theme.fgHeader(), 14));

        if (cmbThemes != null) {
            cmbThemes.setBackground(theme.bgSurface());
            cmbThemes.setForeground(theme.fgEditor());
        }

        repaint();
    }

    private void setupGlobalKeyBindings() {
        JComponent root = (JComponent) getContentPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();
        int mask = InputEvent.CTRL_DOWN_MASK;
        try {
            if (!GraphicsEnvironment.isHeadless()) {
                mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
            }
        } catch (Throwable ignored) {}

        // New Tab: Cmd/Ctrl + N
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, mask), "actNew");
        am.put("actNew", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { newDocument(); } });

        // Open File: Cmd/Ctrl + O
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, mask), "actOpen");
        am.put("actOpen", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { chooseAndOpenFile(); } });

        // Open Folder: Cmd/Ctrl + Shift + O
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, mask | InputEvent.SHIFT_DOWN_MASK), "actOpenFolder");
        am.put("actOpenFolder", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { chooseAndOpenFolder(); } });

        // Save: Cmd/Ctrl + S
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask), "actSave");
        am.put("actSave", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { saveCurrentDocument(); } });

        // Save As: Cmd/Ctrl + Shift + S
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask | InputEvent.SHIFT_DOWN_MASK), "actSaveAs");
        am.put("actSaveAs", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { saveAsCurrentDocument(); } });

        // Close Tab: Cmd/Ctrl + W
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, mask), "actClose");
        am.put("actClose", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { closeCurrentTab(); } });

        // Find: Cmd/Ctrl + F
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, mask), "actFind");
        am.put("actFind", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { toggleFindBar(false); } });

        // Replace: Cmd/Ctrl + H
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, mask), "actReplace");
        am.put("actReplace", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { toggleFindBar(true); } });

        // Toggle Sidebar: Cmd/Ctrl + B
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, mask), "actSidebar");
        am.put("actSidebar", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { toggleSidebar(); } });

        // Next Theme: Cmd/Ctrl + T
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, mask), "actTheme");
        am.put("actTheme", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { cycleNextTheme(); } });
    }

    private void cycleNextTheme() {
        int nextIdx = (cmbThemes.getSelectedIndex() + 1) % cmbThemes.getItemCount();
        cmbThemes.setSelectedIndex(nextIdx);
    }

    private void setupDragAndDrop() {
        new DropTarget(this, DnDConstants.ACTION_COPY, new java.awt.dnd.DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked")
                    List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File f : droppedFiles) {
                        if (f.isDirectory()) {
                            sidebar.setDirectory(f);
                        } else {
                            openFile(f);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void newDocument() {
        String title = "Untitled-" + (untitledCounter++) + ".txt";
        EditorTab tab = new EditorTab(title, null);
        tabs.add(tab);
        tabBar.addTab(tab);
        selectTab(tabs.size() - 1);
        statusBar.setStatusMessage("Created " + title);
    }

    public void openFile(File f) {
        if (f == null || !f.exists()) return;

        // Check if already open
        for (int i = 0; i < tabs.size(); i++) {
            if (f.equals(tabs.get(i).getFile())) {
                selectTab(i);
                tabBar.setSelectedIndex(i);
                return;
            }
        }

        // If single untitled unmodified tab, reuse it
        if (tabs.size() == 1 && tabs.get(0).getFile() == null && !tabs.get(0).isModified()
            && tabs.get(0).getEditorPane().getText().isEmpty()) {
            tabs.get(0).loadFile(f);
            selectTab(0);
            tabBar.setSelectedIndex(0);
            return;
        }

        EditorTab tab = new EditorTab(f.getName(), f);
        tabs.add(tab);
        tabBar.addTab(tab);
        selectTab(tabs.size() - 1);
        statusBar.setStatusMessage("Opened " + f.getName());
    }

    public void chooseAndOpenFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            openFile(chooser.getSelectedFile());
        }
    }

    public void chooseAndOpenFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            sidebar.setDirectory(chooser.getSelectedFile());
            statusBar.setStatusMessage("Opened folder " + chooser.getSelectedFile().getName());
        }
    }

    public void selectTab(int index) {
        if (index < 0 || index >= tabs.size()) return;

        editorContainer.removeAll();
        EditorTab tab = tabs.get(index);
        tab.setStatusBar(statusBar);
        editorContainer.add(tab, BorderLayout.CENTER);
        editorContainer.revalidate();
        editorContainer.repaint();

        tabBar.setSelectedIndex(index);
        setTitle("AuraEdit — " + tab.getTitle() + (tab.isModified() ? " *" : ""));
        btnPreview.setVisible(tab.getFileType() == FileType.MARKDOWN);

        tab.getEditorPane().requestFocusInWindow();
    }

    public void closeCurrentTab() {
        int idx = tabBar.getSelectedIndex();
        if (idx >= 0) closeTab(idx);
    }

    public void closeTab(int index) {
        if (index < 0 || index >= tabs.size()) return;
        EditorTab tab = tabs.get(index);

        if (tab.isModified()) {
            int opt = JOptionPane.showConfirmDialog(
                this,
                "Save changes to \"" + tab.getTitle() + "\" before closing?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (opt == JOptionPane.CANCEL_OPTION) return;
            if (opt == JOptionPane.YES_OPTION) {
                if (!saveDocument(tab)) return;
            }
        }

        tabs.remove(index);
        tabBar.removeTab(index);

        if (tabs.isEmpty()) {
            newDocument();
        } else {
            int newIdx = Math.min(index, tabs.size() - 1);
            selectTab(newIdx);
        }
    }

    public boolean saveCurrentDocument() {
        int idx = tabBar.getSelectedIndex();
        if (idx >= 0 && idx < tabs.size()) {
            return saveDocument(tabs.get(idx));
        }
        return false;
    }

    public boolean saveDocument(EditorTab tab) {
        if (tab.getFile() == null) {
            return saveAsDocument(tab);
        } else {
            boolean success = tab.save();
            setTitle("AuraEdit — " + tab.getTitle());
            return success;
        }
    }

    public boolean saveAsCurrentDocument() {
        int idx = tabBar.getSelectedIndex();
        if (idx >= 0 && idx < tabs.size()) {
            return saveAsDocument(tabs.get(idx));
        }
        return false;
    }

    public boolean saveAsDocument(EditorTab tab) {
        JFileChooser chooser = new JFileChooser();
        if (tab.getFile() != null) {
            chooser.setSelectedFile(tab.getFile());
        } else {
            chooser.setSelectedFile(new File(tab.getTitle()));
        }

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            boolean success = tab.saveAs(chooser.getSelectedFile());
            setTitle("AuraEdit — " + tab.getTitle());
            return success;
        }
        return false;
    }

    private void toggleFindBar(boolean replaceMode) {
        int idx = tabBar.getSelectedIndex();
        if (idx >= 0 && idx < tabs.size()) {
            CodeEditorPane pane = tabs.get(idx).getEditorPane();
            findReplaceBar.showForEditor(pane, replaceMode);
        }
    }

    private void toggleSidebar() {
        sidebar.setVisible(!sidebar.isVisible());
        mainSplitPane.resetToPreferredSizes();
    }

    private void toggleMarkdownPreview() {
        int idx = tabBar.getSelectedIndex();
        if (idx >= 0 && idx < tabs.size()) {
            tabs.get(idx).toggleMarkdownPreview();
        }
    }

    private void handleWindowClose() {
        for (EditorTab tab : tabs) {
            if (tab.isModified()) {
                int opt = JOptionPane.showConfirmDialog(
                    this,
                    "There are unsaved changes in open documents. Exit anyway?",
                    "Exit AuraEdit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (opt != JOptionPane.YES_OPTION) {
                    return;
                }
                break;
            }
        }
        dispose();
        System.exit(0);
    }

    private void loadWelcomeDocument() {
        File welcome = new File("demo/Welcome.md");
        if (welcome.exists()) {
            openFile(welcome);
        } else {
            newDocument();
        }

        File demoDir = new File("demo");
        if (demoDir.exists() && demoDir.isDirectory()) {
            sidebar.setDirectory(demoDir);
        }
    }

    public static void main(String[] args) {
        // High-DPI and Anti-Aliasing flags
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "AuraEdit");
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");

        try {
            // Set platform look and feel as baseline
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            AuraEdit app = new AuraEdit();
            app.setVisible(true);
        });
    }
}
