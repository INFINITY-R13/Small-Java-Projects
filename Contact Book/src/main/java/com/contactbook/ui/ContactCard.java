package com.contactbook.ui;

import com.contactbook.model.Contact;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

/**
 * A card component rendered for each contact in the sidebar list.
 *
 * <p>Layout: [Avatar 48px] [Name + phone] [🎂 badge]
 */
public class ContactCard extends JPanel {

    private final Contact contact;
    private boolean selected = false;
    private boolean hovered  = false;

    private final Runnable onClick;

    public ContactCard(Contact contact, Runnable onClick) {
        this.contact = contact;
        this.onClick  = onClick;

        setOpaque(false);
        setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, UIConstants.CARD_HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.CARD_HEIGHT));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e)  { hovered = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)   { hovered = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e)  { if (onClick != null) onClick.run(); }
            @Override public void mousePressed(MouseEvent e)  { if (onClick != null) onClick.run(); }
        });
    }

    public Contact getContact() { return contact; }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public boolean isSelected() { return selected; }

    // ── Painting ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth(), h = getHeight();

        // Background
        if (selected) {
            g2.setColor(UIConstants.CARD_SELECTED_BG);
            g2.fillRect(0, 0, w, h);
            // Left accent bar
            g2.setColor(UIConstants.ACCENT);
            g2.fillRect(0, 0, 3, h);
        } else if (hovered) {
            g2.setColor(new Color(0xF5F5F5));
            g2.fillRect(0, 0, w, h);
        }

        // ── Avatar ────────────────────────────────────────────────────────────
        int avatarSize = 48;
        int avatarX    = 14;
        int avatarY    = (h - avatarSize) / 2;

        int idx = UIConstants.getAvatarColorIndex(contact.getName());
        g2.setColor(UIConstants.AVATAR_COLORS[idx]);
        g2.fill(new Ellipse2D.Float(avatarX, avatarY, avatarSize, avatarSize));

        // Initials
        String initials = contact.getInitials();
        Font initialsFont = UIConstants.FONT_HEADING.deriveFont(Font.BOLD, 16f);
        g2.setFont(initialsFont);
        g2.setColor(UIConstants.AVATAR_TEXT_COLORS[idx]);
        FontMetrics fm = g2.getFontMetrics();
        int tx = avatarX + (avatarSize - fm.stringWidth(initials)) / 2;
        int ty = avatarY + (avatarSize - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(initials, tx, ty);

        // ── Text ──────────────────────────────────────────────────────────────
        int textX = avatarX + avatarSize + 12;
        int textW = w - textX - 36; // leave room for birthday badge

        // Name
        g2.setFont(UIConstants.FONT_HEADING);
        g2.setColor(selected ? UIConstants.ACCENT : UIConstants.TEXT_PRIMARY);
        String name = truncate(contact.getName(), g2.getFontMetrics(), textW);
        g2.drawString(name, textX, h / 2 - 2);

        // Phone (or email as fallback)
        String sub = contact.getPhone();
        if (sub == null || sub.isBlank()) sub = contact.getEmail();
        if (sub == null) sub = "";

        g2.setFont(UIConstants.FONT_SMALL);
        g2.setColor(UIConstants.TEXT_SECONDARY);
        String subStr = truncate(sub, g2.getFontMetrics(), textW);
        g2.drawString(subStr, textX, h / 2 + 13);

        // ── Birthday badge ────────────────────────────────────────────────────
        if (contact.isBirthdayToday()) {
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            // Fallback – drawString with emoji works on most JDKs
            g2.drawString("🎂", w - 30, h / 2 + 6);
        }

        g2.dispose();
    }

    // ── Util ─────────────────────────────────────────────────────────────────

    private String truncate(String text, FontMetrics fm, int maxWidth) {
        if (text == null || text.isBlank()) return "";
        if (fm.stringWidth(text) <= maxWidth) return text;
        String ellipsis = "…";
        while (text.length() > 0 && fm.stringWidth(text + ellipsis) > maxWidth) {
            text = text.substring(0, text.length() - 1);
        }
        return text + ellipsis;
    }
}
