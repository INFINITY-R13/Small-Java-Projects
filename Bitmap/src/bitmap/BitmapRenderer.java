package bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Core rendering engine for projecting user messages onto 2D bitmap templates,
 * supporting column-modulo wrapping, continuous streams, color gradients, and reveal animation.
 */
public class BitmapRenderer {

    public enum RenderMode {
        COLUMN_MODULO("Column-Aligned", "Cycles message characters aligned to column index (Sweigart classic)"),
        CONTINUOUS_STREAM("Continuous Stream", "Flows message sequentially across all solid pixels"),
        SOLID_GLYPH("Solid Glyph", "Fills bitmap with a uniform solid block character");

        private final String label;
        private final String description;

        RenderMode(String label, String description) {
            this.label = label;
            this.description = description;
        }

        public String getLabel() {
            return label;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class RenderOptions {
        public RenderMode mode = RenderMode.COLUMN_MODULO;
        public ColorTheme theme = ColorTheme.RAINBOW;
        public boolean invert = false;
        public boolean enableColor = true;
        public char solidGlyph = '#';

        public RenderOptions() {}

        public RenderOptions(RenderMode mode, ColorTheme theme, boolean invert, boolean enableColor) {
            this.mode = mode;
            this.theme = theme;
            this.invert = invert;
            this.enableColor = enableColor;
        }
    }

    public static class RenderResult {
        private final List<String> coloredLines;
        private final List<String> plainLines;
        private final int width;
        private final int height;
        private final int solidPixels;
        private final double messageCycles;

        public RenderResult(List<String> coloredLines, List<String> plainLines, int width, int height,
                            int solidPixels, double messageCycles) {
            this.coloredLines = coloredLines;
            this.plainLines = plainLines;
            this.width = width;
            this.height = height;
            this.solidPixels = solidPixels;
            this.messageCycles = messageCycles;
        }

        public List<String> getColoredLines() {
            return coloredLines;
        }

        public List<String> getPlainLines() {
            return plainLines;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getSolidPixels() {
            return solidPixels;
        }

        public double getMessageCycles() {
            return messageCycles;
        }
    }

    /**
     * Renders a bitmap template using the provided message and options.
     *
     * @param template The bitmap template
     * @param message The user message to fill into the bitmap
     * @param options Configuration options for rendering
     * @return RenderResult containing rendered lines and stats
     */
    public static RenderResult render(BitmapTemplate template, String message, RenderOptions options) {
        if (options == null) {
            options = new RenderOptions();
        }
        if (message == null || message.isEmpty()) {
            message = "JAVA";
        }

        List<String> sourceLines = template.getLines();
        int totalRows = sourceLines.size();
        int totalCols = template.getWidth();

        List<String> coloredLines = new ArrayList<>();
        List<String> plainLines = new ArrayList<>();

        int streamIndex = 0;
        int renderedSolidCount = 0;

        for (int r = 0; r < totalRows; r++) {
            String line = sourceLines.get(r);
            StringBuilder coloredSb = new StringBuilder();
            StringBuilder plainSb = new StringBuilder();

            for (int c = 0; c < line.length(); c++) {
                char ch = line.charAt(c);
                boolean isSolid = (ch != ' ');

                // If inverted, empty spaces become solid and solids become spaces
                if (options.invert) {
                    isSolid = !isSolid;
                }

                if (!isSolid) {
                    coloredSb.append(' ');
                    plainSb.append(' ');
                } else {
                    char displayChar;
                    if (options.mode == RenderMode.SOLID_GLYPH) {
                        displayChar = options.solidGlyph;
                    } else if (options.mode == RenderMode.CONTINUOUS_STREAM) {
                        displayChar = message.charAt(streamIndex % message.length());
                        streamIndex++;
                    } else { // COLUMN_MODULO
                        displayChar = message.charAt(c % message.length());
                    }

                    if (options.enableColor && options.theme != ColorTheme.MONOCHROME) {
                        String colorCode = options.theme.getAnsiEscape(r, totalRows, c, totalCols);
                        coloredSb.append(colorCode).append(displayChar);
                    } else {
                        coloredSb.append(displayChar);
                    }
                    plainSb.append(displayChar);
                    renderedSolidCount++;
                }
            }

            if (options.enableColor && options.theme != ColorTheme.MONOCHROME) {
                coloredSb.append(Ansi.RESET);
            }

            coloredLines.add(coloredSb.toString());
            plainLines.add(plainSb.toString());
        }

        double cycles = message.length() > 0 ? (double) renderedSolidCount / message.length() : 0.0;
        return new RenderResult(coloredLines, plainLines, totalCols, totalRows, renderedSolidCount, cycles);
    }

    /**
     * Prints the rendered lines to standard output with an animated line delay.
     *
     * @param lines The lines to print
     * @param delayMs Delay in milliseconds per line
     */
    public static void printWithAnimation(List<String> lines, int delayMs) {
        for (String line : lines) {
            System.out.println(line);
            if (delayMs > 0) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
