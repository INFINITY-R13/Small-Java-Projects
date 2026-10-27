package com.contactbook.ui;

import com.contactbook.model.Contact;
import com.contactbook.service.ContactService;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Application main window.
 *
 * <p>Layout: left sidebar ({@link ContactListPanel}) + right detail panel
 * ({@link ContactDetailPanel}) using a simple {@link BorderLayout}.
 */
public class MainFrame extends JFrame {

    private final ContactService      service;
    private final ContactListPanel    listPanel;
    private final ContactDetailPanel  detailPanel;

    public MainFrame(ContactService service) {
        super("Contact Book");
        this.service = service;

        // ── Look and Feel ─────────────────────────────────────────────────────
        // FlatLightLaf is already installed by Main; configure extra UIManager props
        UIManager.put("Component.arc",            UIConstants.ARC);
        UIManager.put("Button.arc",               UIConstants.ARC);
        UIManager.put("TextComponent.arc",        UIConstants.ARC);
        UIManager.put("ScrollBar.thumbArc",       999);
        UIManager.put("ScrollBar.width",          8);
        UIManager.put("ScrollBar.trackInsets",    new Insets(2, 2, 2, 2));
        UIManager.put("TabbedPane.selectedBackground", UIConstants.CARD_SELECTED_BG);
        UIManager.put("Panel.background",         UIConstants.BG_MAIN);

        // ── Frame config ──────────────────────────────────────────────────────
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setBackground(UIConstants.BG_MAIN);

        // ── Sub-panels ────────────────────────────────────────────────────────
        listPanel   = new ContactListPanel(service);
        detailPanel = new ContactDetailPanel();

        // Wire events
        listPanel.setOnContactSelected(contact -> {
            detailPanel.showContact(contact);
        });

        listPanel.setOnAddContact(this::showAddDialog);

        detailPanel.setOnEdit(contact -> showEditDialog(contact));
        detailPanel.setOnDelete(contact -> confirmDelete(contact));

        // ── Layout ────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.BG_MAIN);
        root.add(listPanel,   BorderLayout.WEST);
        root.add(detailPanel, BorderLayout.CENTER);
        setContentPane(root);

        // ── Keyboard shortcuts ────────────────────────────────────────────────
        registerShortcuts();

        // ── Initial render ────────────────────────────────────────────────────
        listPanel.refresh(null);
        detailPanel.showContact(null);
    }

    // ── Keyboard shortcuts ────────────────────────────────────────────────────

    private void registerShortcuts() {
        KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        getRootPane().registerKeyboardAction(e -> showAddDialog(), ctrlN, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    // ── CRUD dialogs ──────────────────────────────────────────────────────────

    private void showAddDialog() {
        new ContactFormDialog(this, contact -> {
            service.addContact(contact);
            listPanel.refresh(contact);
            detailPanel.showContact(contact);
        }).setVisible(true);
    }

    private void showEditDialog(Contact contact) {
        if (contact == null) return;
        new ContactFormDialog(this, contact, updated -> {
            service.updateContact(updated);
            listPanel.refresh(updated);
            detailPanel.showContact(updated);
        }).setVisible(true);
    }

    private void confirmDelete(Contact contact) {
        if (contact == null) return;

        int result = JOptionPane.showConfirmDialog(
                this,
                "<html>Are you sure you want to delete <b>" + contact.getName() + "</b>?<br>"
                        + "This action cannot be undone.</html>",
                "Delete Contact",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            service.deleteContact(contact.getId());
            listPanel.refresh(null);
            detailPanel.showContact(null);
        }
    }
}
