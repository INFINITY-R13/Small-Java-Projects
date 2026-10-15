package bitmap;

/**
 * ANSI escape code constants and helper utilities for terminal text formatting,
 * 24-bit TrueColor RGB gradients, and color manipulation.
 */
public final class Ansi {

    private Ansi() {
        // Utility class
    }

    // Reset
    public static final String RESET = "\u001B[0m";

    // Text Styles
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";

    // Standard Foreground Colors
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Bright Foreground Colors
    public static final String BRIGHT_BLACK = "\u001B[90m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    /**
     * Constructs a 24-bit TrueColor RGB foreground escape sequence.
     *
     * @param r Red component (0-255)
     * @param g Green component (0-255)
     * @param b Blue component (0-255)
     * @return ANSI escape string
     */
    public static String rgb(int r, int g, int b) {
        r = clamp(r, 0, 255);
        g = clamp(g, 0, 255);
        b = clamp(b, 0, 255);
        return "\u001B[38;2;" + r + ";" + g + ";" + b + "m";
    }

    /**
     * Constructs a 24-bit TrueColor RGB background escape sequence.
     *
     * @param r Red component (0-255)
     * @param g Green component (0-255)
     * @param b Blue component (0-255)
     * @return ANSI escape string
     */
    public static String bgRgb(int r, int g, int b) {
        r = clamp(r, 0, 255);
        g = clamp(g, 0, 255);
        b = clamp(b, 0, 255);
        return "\u001B[48;2;" + r + ";" + g + ";" + b + "m";
    }

    /**
     * Linearly interpolates between two RGB colors.
     *
     * @param c1 Start RGB packed as 0xRRGGBB
     * @param c2 End RGB packed as 0xRRGGBB
     * @param fraction Progress fraction between 0.0 and 1.0
     * @return Interpolated RGB packed as 0xRRGGBB
     */
    public static int lerpRgb(int c1, int c2, double fraction) {
        fraction = Math.max(0.0, Math.min(1.0, fraction));
        int r1 = (c1 >> 16) & 0xFF;
        int g1 = (c1 >> 8) & 0xFF;
        int b1 = c1 & 0xFF;

        int r2 = (c2 >> 16) & 0xFF;
        int g2 = (c2 >> 8) & 0xFF;
        int b2 = c2 & 0xFF;

        int r = (int) Math.round(r1 + fraction * (r2 - r1));
        int g = (int) Math.round(g1 + fraction * (g2 - g1));
        int b = (int) Math.round(b1 + fraction * (b2 - b1));

        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Converts HSL (Hue, Saturation, Lightness) to 24-bit RGB packed integer.
     *
     * @param hue Hue in degrees [0, 360)
     * @param saturation Saturation [0, 1]
     * @param lightness Lightness [0, 1]
     * @return RGB integer 0xRRGGBB
     */
    public static int hslToRgb(double hue, double saturation, double lightness) {
        hue = ((hue % 360) + 360) % 360;
        double c = (1 - Math.abs(2 * lightness - 1)) * saturation;
        double x = c * (1 - Math.abs((hue / 60.0) % 2 - 1));
        double m = lightness - c / 2;

        double rPrime = 0;
        double gPrime = 0;
        double bPrime = 0;

        if (hue < 60) {
            rPrime = c;
            gPrime = x;
        } else if (hue < 120) {
            rPrime = x;
            gPrime = c;
        } else if (hue < 180) {
            gPrime = c;
            bPrime = x;
        } else if (hue < 240) {
            gPrime = x;
            bPrime = c;
        } else if (hue < 300) {
            rPrime = x;
            bPrime = c;
        } else {
            rPrime = c;
            bPrime = x;
        }

        int r = (int) Math.round((rPrime + m) * 255.0);
        int g = (int) Math.round((gPrime + m) * 255.0);
        int b = (int) Math.round((bPrime + m) * 255.0);

        return ((clamp(r, 0, 255) & 0xFF) << 16) |
               ((clamp(g, 0, 255) & 0xFF) << 8) |
                (clamp(b, 0, 255) & 0xFF);
    }

    /**
     * Strips all ANSI escape codes from a string.
     *
     * @param input Raw text with ANSI codes
     * @return Clean plain text
     */
    public static String stripAnsi(String input) {
        if (input == null) return "";
        return input.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    private static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }
}
