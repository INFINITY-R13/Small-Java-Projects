package com.endless.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Theme manages the Apple design tokens, colors, fonts, and dark/light mode state.
 */
public class Theme {

    public interface ThemeChangeListener {
        void onThemeChanged(boolean isDark);
    }

    private static boolean darkMode = true;
    private static final List<ThemeChangeListener> listeners = new ArrayList<>();

    // Apple Brand Accent
    public static final Color ACCENT_PINK = new Color(250, 45, 72);       // Apple Music signature
    public static final Color ACCENT_PURPLE = new Color(175, 82, 222);    // Ambient accent
    public static final Color ACCENT_BLUE = new Color(10, 132, 255);      // SF Blue
    public static final Color ACCENT_CYAN = new Color(50, 173, 230);      // High-frequency glow

    // Traffic light colors
    public static final Color TRAFFIC_RED = new Color(255, 95, 86);
    public static final Color TRAFFIC_YELLOW = new Color(255, 189, 46);
    public static final Color TRAFFIC_GREEN = new Color(39, 201, 63);

    // Dynamic Getters based on Dark/Light mode
    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean dark) {
        if (darkMode != dark) {
            darkMode = dark;
            notifyListeners();
        }
    }

    public static void toggleDarkMode() {
        setDarkMode(!darkMode);
    }

    public static void addListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        List<ThemeChangeListener> copy = new ArrayList<>(listeners);
        for (ThemeChangeListener l : copy) {
            l.onThemeChanged(darkMode);
        }
    }

    // Colors
    public static Color bg() {
        return darkMode ? new Color(18, 18, 20) : new Color(246, 246, 248);
    }

    public static Color sidebarBg() {
        return darkMode ? new Color(24, 24, 27) : new Color(238, 238, 242);
    }

    public static Color playerBarBg() {
        return darkMode ? new Color(28, 28, 32) : new Color(255, 255, 255);
    }

    public static Color cardBg() {
        return darkMode ? new Color(32, 32, 37) : new Color(255, 255, 255);
    }

    public static Color cardHoverBg() {
        return darkMode ? new Color(42, 42, 48) : new Color(242, 242, 246);
    }

    public static Color cardSelectedBg() {
        return darkMode ? new Color(50, 50, 58) : new Color(232, 232, 238);
    }

    public static Color border() {
        return darkMode ? new Color(255, 255, 255, 22) : new Color(0, 0, 0, 18);
    }

    public static Color borderSubtle() {
        return darkMode ? new Color(255, 255, 255, 12) : new Color(0, 0, 0, 10);
    }

    public static Color textPrimary() {
        return darkMode ? new Color(245, 245, 247) : new Color(28, 28, 30);
    }

    public static Color textSecondary() {
        return darkMode ? new Color(152, 152, 157) : new Color(110, 110, 115);
    }

    public static Color textTertiary() {
        return darkMode ? new Color(99, 99, 102) : new Color(142, 142, 147);
    }

    public static Color accent() {
        return ACCENT_PINK;
    }

    public static Color scrubberBackground() {
        return darkMode ? new Color(255, 255, 255, 40) : new Color(0, 0, 0, 30);
    }

    public static Color scrubberProgress() {
        return darkMode ? Color.WHITE : ACCENT_PINK;
    }

    public static Color searchBarBg() {
        return darkMode ? new Color(36, 36, 42) : new Color(228, 228, 234);
    }

    // Typography
    private static String systemFontName = null;

    public static String getSystemFontFamily() {
        if (systemFontName == null) {
            String[] preferred = {".AppleSystemUIFont", "SF Pro Display", "SF Pro Text", "Helvetica Neue", "Arial", "SansSerif"};
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] available = ge.getAvailableFontFamilyNames();
            for (String pref : preferred) {
                for (String avail : available) {
                    if (pref.equalsIgnoreCase(avail)) {
                        systemFontName = avail;
                        return systemFontName;
                    }
                }
            }
            systemFontName = Font.SANS_SERIF;
        }
        return systemFontName;
    }

    public static Font font(float size, int style) {
        return new Font(getSystemFontFamily(), style, (int) size).deriveFont(size);
    }

    public static Font regular(float size) {
        return font(size, Font.PLAIN);
    }

    public static Font medium(float size) {
        return font(size, Font.PLAIN); // Standard Java doesn't have medium constant, can use bold or plain with size
    }

    public static Font bold(float size) {
        return font(size, Font.BOLD);
    }

    // Standard Graphics2D Rendering Hints Setup
    public static void applyQualityHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }
}
