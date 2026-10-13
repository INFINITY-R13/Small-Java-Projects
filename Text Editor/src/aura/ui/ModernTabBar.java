package aura.ui;

import aura.editor.EditorTab;
import aura.theme.Theme;
import aura.theme.ThemeManager;
import aura.util.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modern aesthetic tab bar with custom tab buttons, close action, dirty dot, and new tab button.
 */
public class ModernTabBar extends JPanel {
    private final List<EditorTab> tabs = new ArrayList<>();
    private int selectedIndex = -1;
    private final JPanel tabsContainer;
    private final JScrollPane scrollPane;
    private final ModernButton btnNew;
    private final Consumer<Integer> onTabSelected;
    private final Consumer<Integer> onTabClosed;
    private final Runnable onNewTab;

    public ModernTabBar(Consumer<Integer> onTabSelected, Consumer<Integer> onTabClosed, Runnable onNewTab) {
        this.onTabSelected = onTabSelected;
        this.onTabClosed = onTabClosed;
        this.onNewTab = onNewTab;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 38));

        tabsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        tabsContainer.setOpaque(true);

        // Scroll pane for many tabs
        scrollPane = new JScrollPane(tabsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(true);

        btnNew = new ModernButton(Icons.createPlusIcon(ThemeManager.getTheme().fgMuted(), 14));
        btnNew.setToolTipText("New Tab (Ctrl/Cmd + N)");
        btnNew.setPreferredSize(new Dimension(28, 28));
        btnNew.addActionListener(e -> {
            if (onNewTab != null) onNewTab.run();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 5));
        rightPanel.setOpaque(false);
        rightPanel.add(btnNew);

        add(scrollPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        ThemeManager.addListener(this::applyTheme);
    }

    private void applyTheme(Theme theme) {
        setBackground(theme.bgHeader());
        tabsContainer.setBackground(theme.bgHeader());
        scrollPane.setBackground(theme.bgHeader());
        scrollPane.getViewport().setBackground(theme.bgHeader());
        btnNew.setIcon(Icons.createPlusIcon(theme.fgMuted(), 14));
        rebuildTabsUI();
    }

    public void addTab(EditorTab tab) {
        tabs.add(tab);
        tab.addModificationListener(this::rebuildTabsUI);
        selectedIndex = tabs.size() - 1;
        rebuildTabsUI();
    }

    public void removeTab(int index) {
        if (index >= 0 && index < tabs.size()) {
            tabs.remove(index);
            if (selectedIndex >= tabs.size()) {
                selectedIndex = tabs.size() - 1;
            }
            rebuildTabsUI();
        }
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < tabs.size()) {
            this.selectedIndex = index;
            rebuildTabsUI();
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public int getTabCount() {
        return tabs.size();
    }

    public EditorTab getTabAt(int index) {
        if (index >= 0 && index < tabs.size()) {
            return tabs.get(index);
        }
        return null;
    }

    private void rebuildTabsUI() {
        tabsContainer.removeAll();
        Theme theme = ThemeManager.getTheme();

        for (int i = 0; i < tabs.size(); i++) {
            final int index = i;
            EditorTab tab = tabs.get(i);
            boolean isSelected = (i == selectedIndex);

            JPanel tabItem = new TabItemPanel(tab, isSelected, theme,
                () -> {
                    selectedIndex = index;
                    rebuildTabsUI();
                    if (onTabSelected != null) onTabSelected.accept(index);
                },
                () -> {
                    if (onTabClosed != null) onTabClosed.accept(index);
                }
            );

            tabsContainer.add(tabItem);
        }

        tabsContainer.revalidate();
        tabsContainer.repaint();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Theme theme = ThemeManager.getTheme();
        g.setColor(theme.border());
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    private static class TabItemPanel extends JPanel {
        private final EditorTab tab;
        private final boolean selected;
        private boolean hovered = false;
        private boolean closeHovered = false;
        private final JLabel lblBadge;
        private final JLabel lblTitle;
        private final JLabel lblClose;

        public TabItemPanel(EditorTab tab, boolean selected, Theme theme, Runnable onClick, Runnable onClose) {
            this.tab = tab;
            this.selected = selected;

            setLayout(new BorderLayout(8, 0));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 8));

            // Language badge dot
            lblBadge = new JLabel("●");
            lblBadge.setFont(new Font("Dialog", Font.BOLD, 9));
            lblBadge.setForeground(tab.getFileType().getBadgeColor());

            // Tab title (file name)
            lblTitle = new JLabel(tab.getTitle());
            lblTitle.setFont(new Font("-apple-system", selected ? Font.BOLD : Font.PLAIN, 12));
            lblTitle.setForeground(selected ? theme.fgEditor() : theme.fgMuted());

            JPanel centerInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
            centerInfo.setOpaque(false);
            centerInfo.add(lblBadge);
            centerInfo.add(lblTitle);

            // Close / Dirty indicator
            lblClose = new JLabel(tab.isModified() ? "•" : "×");
            lblClose.setFont(new Font("-apple-system", Font.BOLD, tab.isModified() ? 16 : 14));
            lblClose.setForeground(tab.isModified() ? theme.accent() : theme.fgMuted());
            lblClose.setPreferredSize(new Dimension(16, 16));
            lblClose.setHorizontalAlignment(SwingConstants.CENTER);
            lblClose.setCursor(new Cursor(Cursor.HAND_CURSOR));

            lblClose.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    closeHovered = true;
                    lblClose.setText("×");
                    lblClose.setForeground(theme.accent());
                    lblClose.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    closeHovered = false;
                    lblClose.setText(tab.isModified() ? "•" : "×");
                    lblClose.setForeground(tab.isModified() ? theme.accent() : theme.fgMuted());
                    lblClose.repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    e.consume();
                    onClose.run();
                }
            });

            add(centerInfo, BorderLayout.CENTER);
            add(lblClose, BorderLayout.EAST);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    if (!selected) {
                        lblTitle.setForeground(theme.fgEditor());
                    }
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    if (!selected) {
                        lblTitle.setForeground(theme.fgMuted());
                    }
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    onClick.run();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Theme theme = ThemeManager.getTheme();

            int w = getWidth();
            int h = getHeight();

            if (selected) {
                // Active tab background matches the editor background
                g2d.setColor(theme.bgEditor());
                g2d.fillRoundRect(0, 1, w, h - 2, 7, 7);

                // Accent top bar
                g2d.setColor(theme.accent());
                g2d.fillRoundRect(2, 1, w - 4, 3, 2, 2);

                // Border
                g2d.setColor(theme.border());
                g2d.drawRoundRect(0, 1, w - 1, h - 3, 7, 7);
            } else if (hovered) {
                g2d.setColor(theme.bgHover());
                g2d.fillRoundRect(0, 2, w, h - 4, 6, 6);
                g2d.setColor(theme.border());
                g2d.drawRoundRect(0, 2, w - 1, h - 5, 6, 6);
            } else {
                g2d.setColor(theme.bgSurface());
                g2d.fillRoundRect(0, 2, w, h - 4, 6, 6);
                g2d.setColor(theme.border());
                g2d.drawRoundRect(0, 2, w - 1, h - 5, 6, 6);
            }

            g2d.dispose();
            super.paintComponent(g);
        }
    }
}
