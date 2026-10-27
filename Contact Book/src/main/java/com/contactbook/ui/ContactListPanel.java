package com.contactbook.ui;

import com.contactbook.model.Contact;
import com.contactbook.service.ContactService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Left sidebar panel containing the app header, search bar, contact list and
 * the "Add Contact" button.
 */
public class ContactListPanel extends JPanel {

    private final ContactService service;

    private final JLabel        countLabel;
    private final SearchPanel   searchPanel;
    private final JPanel        listContainer;
    private final JPanel        emptyState;

    private final List<ContactCard> cards = new ArrayList<>();

    private Consumer<Contact> onContactSelected;
    private Runnable          onAddContact;

    private Contact selectedContact;

    public ContactListPanel(ContactService service) {
        this.service = service;
        setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        setMinimumSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH, Integer.MAX_VALUE));
        setBackground(UIConstants.BG_SIDEBAR);
        setLayout(new BorderLayout());

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 16, 12, 16));

        JLabel title = new JLabel("📒 Contact Book");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.TEXT_PRIMARY);

        countLabel = new JLabel("0");
        countLabel.setFont(UIConstants.FONT_SMALL);
        countLabel.setForeground(Color.WHITE);
        countLabel.setBackground(UIConstants.ACCENT);
        countLabel.setOpaque(true);
        countLabel.setBorder(new EmptyBorder(2, 7, 2, 7));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Wrap count label in a rounded panel
        JPanel countBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        countBadge.setOpaque(false);
        countBadge.add(countLabel);

        header.add(title,      BorderLayout.WEST);
        header.add(countBadge, BorderLayout.EAST);

        // ── Search ────────────────────────────────────────────────────────────
        searchPanel = new SearchPanel();
        searchPanel.setBorder(new EmptyBorder(0, 12, 10, 12));
        searchPanel.setSearchCallback(this::filterContacts);

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.add(header);
        topSection.add(searchPanel);

        // ── Contact list ──────────────────────────────────────────────────────
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(UIConstants.BG_SIDEBAR);

        // Empty state
        emptyState = buildEmptyState();

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        // ── Add button ────────────────────────────────────────────────────────
        JButton addBtn = createAddButton();

        add(topSection,  BorderLayout.NORTH);
        add(scrollPane,  BorderLayout.CENTER);
        add(addBtn,      BorderLayout.SOUTH);

        refresh(null);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void setOnContactSelected(Consumer<Contact> listener) { this.onContactSelected = listener; }
    public void setOnAddContact(Runnable listener)               { this.onAddContact      = listener; }

    /**
     * Re-renders the contact list, preserving the currently selected contact if
     * it still exists. Pass {@code null} to keep the current selection or use
     * {@link #refresh(Contact)} to force a specific selection.
     */
    public void refresh(Contact select) {
        List<Contact> all = service.getAllContacts();
        String query = searchPanel.getQuery();
        List<Contact> displayed = query.isBlank() ? all : service.searchContacts(query);

        // Determine which contact to select after refresh
        Contact toSelect = select;
        if (toSelect == null) toSelect = selectedContact;
        // Ensure the target is still in the displayed list
        final Contact finalSelect = toSelect;
        boolean inList = finalSelect != null && displayed.stream()
                .anyMatch(c -> c.getId().equals(finalSelect.getId()));
        if (!inList) toSelect = displayed.isEmpty() ? null : displayed.get(0);

        renderList(displayed, toSelect);
        updateCount(all.size());
    }

    public Contact getSelectedContact() { return selectedContact; }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void filterContacts(String query) {
        List<Contact> results = service.searchContacts(query);
        renderList(results, selectedContact);
        updateCount(service.getAllContacts().size());
    }

    private void renderList(List<Contact> contacts, Contact toSelect) {
        listContainer.removeAll();
        cards.clear();

        if (contacts.isEmpty()) {
            listContainer.add(emptyState);
        } else {
            for (Contact c : contacts) {
                ContactCard card = new ContactCard(c, () -> selectContact(c));
                cards.add(card);
                listContainer.add(card);
            }
            // Apply selection highlight
            Contact finalSelect = toSelect;
            if (finalSelect != null) {
                cards.stream()
                        .filter(cd -> cd.getContact().getId().equals(finalSelect.getId()))
                        .findFirst()
                        .ifPresent(cd -> {
                            cd.setSelected(true);
                            selectedContact = cd.getContact();
                        });
            } else {
                selectedContact = null;
            }
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    private void selectContact(Contact c) {
        selectedContact = c;
        cards.forEach(card -> card.setSelected(card.getContact().getId().equals(c.getId())));
        if (onContactSelected != null) onContactSelected.accept(c);
    }

    private void updateCount(int n) {
        countLabel.setText(String.valueOf(n));
    }

    // ── Sub-components ────────────────────────────────────────────────────────

    private JPanel buildEmptyState() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(60, 20, 0, 20));

        JLabel icon = new JLabel("📭");
        icon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("<html><center>No contacts yet.<br>Hit <b>Add Contact</b> to get started!</center></html>");
        msg.setFont(UIConstants.FONT_CAPTION);
        msg.setForeground(UIConstants.TEXT_SECONDARY);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(icon);
        panel.add(Box.createVerticalStrut(12));
        panel.add(msg);
        return panel;
    }

    private JButton createAddButton() {
        JButton btn = new JButton("＋  Add Contact") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? UIConstants.ACCENT_HOVER : UIConstants.ACCENT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.ARC, UIConstants.ARC);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UIConstants.FONT_HEADING);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 48));
        btn.setBorder(new EmptyBorder(0, 0, 0, 0));
        btn.addActionListener(e -> { if (onAddContact != null) onAddContact.run(); });
        return btn;
    }
}
