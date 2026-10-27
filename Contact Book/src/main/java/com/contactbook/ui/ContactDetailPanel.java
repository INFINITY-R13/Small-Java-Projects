package com.contactbook.ui;

import com.contactbook.model.Contact;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Desktop;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Right-hand panel that displays the details of the currently selected contact.
 * Shows an empty-state view when no contact is selected.
 */
public class ContactDetailPanel extends JPanel {

    private static final DateTimeFormatter BDAY_FMT =
            DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

    private Contact current;

    private Consumer<Contact> onEdit;
    private Consumer<Contact> onDelete;

    // Lazy-built card panel (replaces empty state)
    private JPanel detailCard;
    private JPanel emptyState;

    public ContactDetailPanel() {
        setBackground(UIConstants.BG_MAIN);
        setLayout(new BorderLayout());

        emptyState = buildEmptyState();
        add(emptyState, BorderLayout.CENTER);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void setOnEdit(Consumer<Contact> listener)   { this.onEdit   = listener; }
    public void setOnDelete(Consumer<Contact> listener) { this.onDelete = listener; }

    /**
     * Shows the details for the given contact, or the empty state if null.
     */
    public void showContact(Contact contact) {
        this.current = contact;
        removeAll();

        if (contact == null) {
            add(emptyState, BorderLayout.CENTER);
        } else {
            detailCard = buildDetailCard(contact);
            add(detailCard, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
    }

    // ── Building the detail card ──────────────────────────────────────────────

    private JPanel buildDetailCard(Contact c) {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UIConstants.BG_MAIN);

        // ── Central white card ────────────────────────────────────────────────
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());

        // ── Avatar area ───────────────────────────────────────────────────────
        JPanel avatarSection = new JPanel();
        avatarSection.setOpaque(false);
        avatarSection.setLayout(new BoxLayout(avatarSection, BoxLayout.Y_AXIS));
        avatarSection.setBorder(new EmptyBorder(32, 0, 20, 0));

        JPanel avatarPanel = new AvatarCircle(c, 90);
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarPanel.setMaximumSize(new Dimension(90, 90));
        avatarPanel.setPreferredSize(new Dimension(90, 90));

        JLabel nameLabel = new JLabel(c.getName() != null ? c.getName() : "");
        nameLabel.setFont(UIConstants.FONT_TITLE);
        nameLabel.setForeground(UIConstants.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String emailStr = c.getEmail() != null ? c.getEmail() : "";
        JLabel emailLabel = new JLabel(emailStr);
        emailLabel.setFont(UIConstants.FONT_CAPTION);
        emailLabel.setForeground(UIConstants.TEXT_SECONDARY);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        avatarSection.add(avatarPanel);
        avatarSection.add(Box.createVerticalStrut(14));
        avatarSection.add(nameLabel);
        if (!emailStr.isBlank()) {
            avatarSection.add(Box.createVerticalStrut(4));
            avatarSection.add(emailLabel);
        }

        // ── Info rows ─────────────────────────────────────────────────────────
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(0, 36, 24, 36));

        // Divider
        infoPanel.add(buildDivider());

        if (c.getPhone() != null && !c.getPhone().isBlank()) {
            infoPanel.add(buildInfoRow("📱", "Phone", c.getPhone(), null));
        }
        if (c.getEmail() != null && !c.getEmail().isBlank()) {
            infoPanel.add(buildInfoRow("📧", "Email", c.getEmail(), null));
        }
        if (c.getBirthDate() != null) {
            String bdStr = c.getBirthDate().format(BDAY_FMT);
            int age = Period.between(c.getBirthDate(), LocalDate.now()).getYears();
            String ageStr = " (" + age + " years old)";
            String bdLabel = bdStr + ageStr;
            if (c.isBirthdayToday()) bdLabel = "🎂 " + bdLabel;
            infoPanel.add(buildInfoRow("🎂", "Birthday", bdLabel, null));
        }
        if (c.getInstagram() != null && !c.getInstagram().isBlank()) {
            String handle   = c.getInstagram();
            String display  = c.getDisplayInstagram();
            String url = "https://instagram.com/" + handle;
            infoPanel.add(buildInfoRow("📸", "Instagram", display, url));
        }

        // ── Action buttons ────────────────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(0, 0, 28, 0));

        JButton editBtn   = createActionButton("✏  Edit",   UIConstants.ACCENT, UIConstants.ACCENT_HOVER, Color.WHITE);
        JButton deleteBtn = createActionButton("🗑  Delete", UIConstants.DANGER, UIConstants.DANGER_HOVER, Color.WHITE);

        editBtn.addActionListener(e -> { if (onEdit   != null) onEdit.accept(current);   });
        deleteBtn.addActionListener(e -> { if (onDelete != null) onDelete.accept(current); });

        btnRow.add(editBtn);
        btnRow.add(deleteBtn);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(avatarSection);
        body.add(infoPanel);
        body.add(btnRow);

        card.add(body, BorderLayout.CENTER);

        // Place card in root with some margin
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.insets = new Insets(40, 60, 40, 60);
        root.add(card, gbc);
        return root;
    }

    // ── Sub-component builders ────────────────────────────────────────────────

    private JPanel buildDivider() {
        JPanel div = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(UIConstants.DIVIDER);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        div.setOpaque(false);
        div.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setBorder(new EmptyBorder(0, 0, 16, 0));
        return div;
    }

    private JPanel buildInfoRow(String icon, String label, String value, String linkUrl) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(0, 0, 14, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        iconLabel.setPreferredSize(new Dimension(28, 28));

        JPanel textCol = new JPanel();
        textCol.setOpaque(false);
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(UIConstants.TEXT_SECONDARY);

        if (linkUrl != null) {
            JLabel valLabel = new JLabel("<html><a href='" + linkUrl + "'>" + value + "</a></html>");
            valLabel.setFont(UIConstants.FONT_BODY);
            valLabel.setForeground(UIConstants.ACCENT);
            valLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            String finalUrl = linkUrl;
            valLabel.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    try {
                        if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI(finalUrl));
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            });
            textCol.add(lbl);
            textCol.add(valLabel);
        } else {
            JLabel valLabel = new JLabel(value);
            valLabel.setFont(UIConstants.FONT_BODY);
            valLabel.setForeground(UIConstants.TEXT_PRIMARY);
            textCol.add(lbl);
            textCol.add(valLabel);
        }

        row.add(iconLabel, BorderLayout.WEST);
        row.add(textCol,   BorderLayout.CENTER);
        return row;
    }

    private JButton createActionButton(String text, Color bg, Color hoverBg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color color = getModel().isRollover() ? hoverBg : bg;
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.ARC, UIConstants.ARC);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UIConstants.FONT_BODY);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        return btn;
    }

    private JPanel buildEmptyState() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.BG_MAIN);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("👆");
        icon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("Select a contact to view details");
        msg.setFont(UIConstants.FONT_BODY);
        msg.setForeground(UIConstants.TEXT_SECONDARY);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(icon);
        inner.add(Box.createVerticalStrut(12));
        inner.add(msg);

        panel.add(inner);
        return panel;
    }

    // ── Inner class: avatar circle panel ────────────────────────────────────

    private static class AvatarCircle extends JPanel {
        private final Contact contact;
        private final int size;

        AvatarCircle(Contact contact, int size) {
            this.contact = contact;
            this.size    = size;
            setOpaque(false);
            setPreferredSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int idx = UIConstants.getAvatarColorIndex(contact.getName());
            g2.setColor(UIConstants.AVATAR_COLORS[idx]);
            g2.fill(new Ellipse2D.Float(0, 0, size, size));

            String initials = contact.getInitials();
            g2.setFont(UIConstants.FONT_TITLE.deriveFont(Font.BOLD, size * 0.35f));
            g2.setColor(UIConstants.AVATAR_TEXT_COLORS[idx]);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (size - fm.stringWidth(initials)) / 2;
            int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(initials, tx, ty);
            g2.dispose();
        }
    }
}
