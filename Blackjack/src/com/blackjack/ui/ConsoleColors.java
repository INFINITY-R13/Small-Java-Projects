package com.blackjack.ui;

/**
 * ANSI escape codes for coloring console output.
 */
public final class ConsoleColors {
    private ConsoleColors() {}

    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Bold
    public static final String BOLD = "\u001B[1m";
    public static final String BOLD_RED = "\u001B[1;31m";
    public static final String BOLD_GREEN = "\u001B[1;32m";
    public static final String BOLD_YELLOW = "\u001B[1;33m";
    public static final String BOLD_BLUE = "\u001B[1;34m";
    public static final String BOLD_PURPLE = "\u001B[1;35m";
    public static final String BOLD_CYAN = "\u001B[1;36m";
    public static final String BOLD_WHITE = "\u001B[1;37m";

    // Backgrounds
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_WHITE = "\u001B[47m";
    public static final String BG_DARK_GRAY = "\u001B[100m";

    private static boolean enabled = true;

    public static void setEnabled(boolean isEnabled) {
        enabled = isEnabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static String colorize(String color, String text) {
        if (!enabled) {
            return text;
        }
        return color + text + RESET;
    }
}
