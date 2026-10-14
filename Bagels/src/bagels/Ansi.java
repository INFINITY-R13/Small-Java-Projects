package bagels;

/**
 * Utility for formatting terminal text with ANSI escape codes.
 * Gracefully degrades to plain text if ANSI is disabled or unsupported.
 */
public final class Ansi {

    private static boolean enabled = true;

    static {
        // Automatically disable colors if NO_COLOR env var is set
        if (System.getenv("NO_COLOR") != null) {
            enabled = false;
        }
    }

    private Ansi() {
        // Prevent instantiation
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String UNDERLINE = "\u001B[4m";

    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BOLD_GREEN = "\u001B[1;32m";
    public static final String BOLD_YELLOW = "\u001B[1;33m";
    public static final String BOLD_RED = "\u001B[1;31m";
    public static final String BOLD_CYAN = "\u001B[1;36m";
    public static final String BOLD_WHITE = "\u001B[1;37m";

    public static String color(String text, String code) {
        if (!enabled || text == null) {
            return text;
        }
        return code + text + RESET;
    }

    public static String bold(String text) {
        return color(text, BOLD);
    }

    public static String green(String text) {
        return color(text, GREEN);
    }

    public static String boldGreen(String text) {
        return color(text, BOLD_GREEN);
    }

    public static String yellow(String text) {
        return color(text, YELLOW);
    }

    public static String boldYellow(String text) {
        return color(text, BOLD_YELLOW);
    }

    public static String red(String text) {
        return color(text, RED);
    }

    public static String boldRed(String text) {
        return color(text, BOLD_RED);
    }

    public static String cyan(String text) {
        return color(text, CYAN);
    }

    public static String boldCyan(String text) {
        return color(text, BOLD_CYAN);
    }

    public static String magenta(String text) {
        return color(text, MAGENTA);
    }

    public static String dim(String text) {
        return color(text, DIM);
    }

    /**
     * Highlights clue words (Fermi -> Green, Pico -> Yellow, Bagels -> Red).
     */
    public static String formatClue(String clueString) {
        if (!enabled || clueString == null) {
            return clueString;
        }
        String[] tokens = clueString.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            String token = tokens[i];
            switch (token) {
                case "Fermi" -> sb.append(boldGreen(token));
                case "Pico" -> sb.append(boldYellow(token));
                case "Bagels" -> sb.append(boldRed(token));
                default -> sb.append(token);
            }
        }
        return sb.toString();
    }
}
