package com.contactbook.ui;

import java.awt.*;

/**
 * Centralized colour, font, and size constants for the application UI.
 */
public final class UIConstants {

    private UIConstants() {}

    // ── Colours ──────────────────────────────────────────────────────────────

    public static final Color BG_MAIN      = new Color(0xF0F2F5);
    public static final Color BG_SIDEBAR   = new Color(0xE8EAED);
    public static final Color BG_CARD      = Color.WHITE;
    public static final Color ACCENT       = new Color(0x5C7CFA);
    public static final Color ACCENT_HOVER = new Color(0x4C6EF5);
    public static final Color TEXT_PRIMARY  = new Color(0x1A1A2E);
    public static final Color TEXT_SECONDARY = new Color(0x6B7280);
    public static final Color DIVIDER      = new Color(0xE5E7EB);
    public static final Color DANGER       = new Color(0xEF4444);
    public static final Color DANGER_HOVER = new Color(0xDC2626);
    public static final Color SUCCESS      = new Color(0x22C55E);
    public static final Color CARD_SELECTED_BG = new Color(0xEEF2FF);

    /** Pastel avatar background colours – one per letter bucket. */
    public static final Color[] AVATAR_COLORS = {
        new Color(0xFEE2E2), // red
        new Color(0xFEF3C7), // amber
        new Color(0xD1FAE5), // green
        new Color(0xDBEAFE), // blue
        new Color(0xEDE9FE), // purple
        new Color(0xFCE7F3), // pink
        new Color(0xCCFBF1), // teal
        new Color(0xFFEDD5), // orange
    };

    /** Matching text colours for each avatar background. */
    public static final Color[] AVATAR_TEXT_COLORS = {
        new Color(0xDC2626),
        new Color(0xD97706),
        new Color(0x059669),
        new Color(0x2563EB),
        new Color(0x7C3AED),
        new Color(0xDB2777),
        new Color(0x0D9488),
        new Color(0xEA580C),
    };

    // ── Fonts ────────────────────────────────────────────────────────────────

    public static final Font FONT_TITLE   = resolveFont("Inter", Font.BOLD,  20);
    public static final Font FONT_HEADING = resolveFont("Inter", Font.BOLD,  15);
    public static final Font FONT_BODY    = resolveFont("Inter", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = resolveFont("Inter", Font.PLAIN, 11);
    public static final Font FONT_CAPTION = resolveFont("Inter", Font.PLAIN, 12);

    // ── Dimensions ───────────────────────────────────────────────────────────

    public static final int SIDEBAR_WIDTH  = 320;
    public static final int CARD_HEIGHT    = 72;
    public static final int ARC            = 8;

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Resolves a font by trying Inter → Segoe UI → SansSerif in order.
     */
    private static Font resolveFont(String preferred, int style, int size) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.util.Set<String> families =
                java.util.Set.of(ge.getAvailableFontFamilyNames());
        if (families.contains(preferred)) return new Font(preferred, style, size);
        if (families.contains("Segoe UI")) return new Font("Segoe UI", style, size);
        return new Font(Font.SANS_SERIF, style, size);
    }

    /**
     * Returns the avatar colour index for the given name (0-based, cycling
     * through {@link #AVATAR_COLORS}).
     */
    public static int getAvatarColorIndex(String name) {
        if (name == null || name.isEmpty()) return 0;
        return Math.abs(name.charAt(0)) % AVATAR_COLORS.length;
    }
}
