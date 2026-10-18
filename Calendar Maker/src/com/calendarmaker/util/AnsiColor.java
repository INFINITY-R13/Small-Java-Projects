package com.calendarmaker.util;

import java.util.regex.Pattern;

/**
 * ANSI Color utility for styling terminal output.
 */
public final class AnsiColor {

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";

    // Standard colors
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Bright colors
    public static final String BRIGHT_BLACK = "\u001B[90m"; // Gray
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    // Backgrounds
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_CYAN = "\u001B[46m";
    public static final String BG_GRAY = "\u001B[100m";

    private static final Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[[;\\d]*m");
    private static boolean enabled = true;

    private AnsiColor() {}

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    /**
     * Colorize a text string with ANSI code if colors are enabled.
     */
    public static String colorize(String text, String ansiCode) {
        if (!enabled || text == null || text.isEmpty()) {
            return text;
        }
        return ansiCode + text + RESET;
    }

    /**
     * Remove all ANSI escape codes from string.
     */
    public static String stripAnsi(String text) {
        if (text == null) {
            return "";
        }
        return ANSI_PATTERN.matcher(text).replaceAll("");
    }

    /**
     * Returns visual length of text ignoring ANSI escape sequences.
     */
    public static int visibleLength(String text) {
        return stripAnsi(text).length();
    }
}
