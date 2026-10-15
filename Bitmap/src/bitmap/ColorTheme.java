package bitmap;

/**
 * Color theme engine providing TrueColor gradients and palettes for bitmap rendering.
 * Used for both terminal ANSI rendering and BMP image color mapping.
 */
public enum ColorTheme {

    RAINBOW("Rainbow", "Vibrant continuous spectrum gradient"),
    MATRIX("Matrix", "Cyberpunk hacker phosphor green glow"),
    CYBERPUNK("Cyberpunk", "Electric neon cyan, magenta, and violet"),
    SUNSET("Sunset", "Warm golden orange, coral, and deep purple"),
    OCEAN("Ocean", "Deep sapphire blue, teal, and aquamarine"),
    FIRE("Fire", "Blazing yellow, flame orange, and deep red"),
    GOLD("Gold", "Radiant metallic amber and polished gold"),
    MONOCHROME("Monochrome", "Clean classic high-contrast terminal text");

    private final String displayName;
    private final String description;

    ColorTheme(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Computes the 24-bit RGB integer (0xRRGGBB) for a given pixel coordinate.
     *
     * @param row Current row index (0-based)
     * @param totalRows Total number of rows in the bitmap
     * @param col Current column index (0-based)
     * @param totalCols Total number of columns in the bitmap
     * @return RGB integer (0xRRGGBB)
     */
    public int getRgb(int row, int totalRows, int col, int totalCols) {
        double rowFrac = totalRows > 1 ? (double) row / (totalRows - 1) : 0.0;
        double colFrac = totalCols > 1 ? (double) col / (totalCols - 1) : 0.0;
        double diagFrac = (rowFrac + colFrac) / 2.0;

        switch (this) {
            case RAINBOW: {
                // Hue shifts across columns and rows
                double hue = (diagFrac * 360.0) % 360.0;
                return Ansi.hslToRgb(hue, 0.95, 0.60);
            }
            case MATRIX: {
                // Variations of terminal phosphor green
                int darkGreen = 0x003B00;
                int brightGreen = 0x00FF66;
                int neonGreen = 0x66FF66;
                if (rowFrac < 0.2) {
                    return Ansi.lerpRgb(neonGreen, brightGreen, rowFrac / 0.2);
                } else {
                    return Ansi.lerpRgb(brightGreen, darkGreen, (rowFrac - 0.2) / 0.8);
                }
            }
            case CYBERPUNK: {
                // Neon Cyan (0x00F0FF) to Electric Pink/Magenta (0xFF007F) to Purple (0x8A2BE2)
                int cyan = 0x00F0FF;
                int pink = 0xFF007F;
                int purple = 0x8A2BE2;
                if (diagFrac < 0.5) {
                    return Ansi.lerpRgb(cyan, pink, diagFrac * 2.0);
                } else {
                    return Ansi.lerpRgb(pink, purple, (diagFrac - 0.5) * 2.0);
                }
            }
            case SUNSET: {
                // Golden Yellow (0xFFD700) to Crimson (0xFF4500) to Plum (0x4B0082)
                int gold = 0xFFD700;
                int crimson = 0xFF3300;
                int plum = 0x581845;
                if (rowFrac < 0.5) {
                    return Ansi.lerpRgb(gold, crimson, rowFrac * 2.0);
                } else {
                    return Ansi.lerpRgb(crimson, plum, (rowFrac - 0.5) * 2.0);
                }
            }
            case OCEAN: {
                // Aquamarine (0x00FFFF) to Deep Sky Blue (0x0099FF) to Navy (0x001F54)
                int aqua = 0x00FFFF;
                int sky = 0x0099FF;
                int navy = 0x0A2558;
                if (rowFrac < 0.5) {
                    return Ansi.lerpRgb(aqua, sky, rowFrac * 2.0);
                } else {
                    return Ansi.lerpRgb(sky, navy, (rowFrac - 0.5) * 2.0);
                }
            }
            case FIRE: {
                // Pale Yellow (0xFFFF66) to Orange (0xFF6600) to Deep Red (0x990000)
                int brightYellow = 0xFFFF66;
                int orange = 0xFF5500;
                int deepRed = 0x880000;
                if (rowFrac < 0.4) {
                    return Ansi.lerpRgb(brightYellow, orange, rowFrac / 0.4);
                } else {
                    return Ansi.lerpRgb(orange, deepRed, (rowFrac - 0.4) / 0.6);
                }
            }
            case GOLD: {
                // Brilliant Gold (0xFFF099) to Metallic Amber (0xD4AF37) to Deep Bronze (0x8B5A00)
                int brightGold = 0xFFF275;
                int goldMid = 0xD4AF37;
                int bronze = 0x7A4E00;
                if (diagFrac < 0.5) {
                    return Ansi.lerpRgb(brightGold, goldMid, diagFrac * 2.0);
                } else {
                    return Ansi.lerpRgb(goldMid, bronze, (diagFrac - 0.5) * 2.0);
                }
            }
            case MONOCHROME:
            default:
                return 0xFFFFFF; // Pure white
        }
    }

    /**
     * Returns an ANSI TrueColor escape code for this theme at the specified coordinate.
     *
     * @param row Current row index (0-based)
     * @param totalRows Total number of rows
     * @param col Current column index (0-based)
     * @param totalCols Total number of columns
     * @return ANSI escape sequence string
     */
    public String getAnsiEscape(int row, int totalRows, int col, int totalCols) {
        if (this == MONOCHROME) {
            return "";
        }
        int rgb = getRgb(row, totalRows, col, totalCols);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return Ansi.rgb(r, g, b);
    }

    /**
     * Resolves a theme name case-insensitively.
     *
     * @param name Theme name (e.g. "rainbow", "matrix", "sunset")
     * @return Matching ColorTheme or RAINBOW as default
     */
    public static ColorTheme fromString(String name) {
        if (name == null || name.isBlank()) {
            return RAINBOW;
        }
        String clean = name.trim().toUpperCase();
        for (ColorTheme theme : values()) {
            if (theme.name().equals(clean) || theme.displayName.equalsIgnoreCase(clean)) {
                return theme;
            }
        }
        return RAINBOW;
    }
}
