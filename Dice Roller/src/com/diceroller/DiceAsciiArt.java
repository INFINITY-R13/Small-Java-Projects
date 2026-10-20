package com.diceroller;

import java.util.List;

/**
 * Utility for rendering dice visually in the terminal using ASCII / box-drawing art.
 */
public final class DiceAsciiArt {

    private static final int MAX_DICE_PER_ROW = 5;

    // Visual templates for standard 6-sided dice faces (1 through 6)
    private static final String[][] D6_FACES = {
        // Face 1
        {
            "┌───────┐",
            "│       │",
            "│   *   │",
            "│       │",
            "└───────┘"
        },
        // Face 2
        {
            "┌───────┐",
            "│ *     │",
            "│       │",
            "│     * │",
            "└───────┘"
        },
        // Face 3
        {
            "┌───────┐",
            "│ *     │",
            "│   *   │",
            "│     * │",
            "└───────┘"
        },
        // Face 4
        {
            "┌───────┐",
            "│ *   * │",
            "│       │",
            "│ *   * │",
            "└───────┘"
        },
        // Face 5
        {
            "┌───────┐",
            "│ *   * │",
            "│   *   │",
            "│ *   * │",
            "└───────┘"
        },
        // Face 6
        {
            "┌───────┐",
            "│ *   * │",
            "│ *   * │",
            "│ *   * │",
            "└───────┘"
        }
    };

    private DiceAsciiArt() {
        // Utility class
    }

    /**
     * Renders a list of standard 6-sided dice values side-by-side.
     * Wrapped across multiple rows if count exceeds MAX_DICE_PER_ROW.
     *
     * @param values list of rolled values (1 to 6)
     * @return formatted string containing the ASCII dice
     */
    public static String renderD6(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int total = values.size();

        for (int i = 0; i < total; i += MAX_DICE_PER_ROW) {
            int end = Math.min(i + MAX_DICE_PER_ROW, total);
            List<Integer> rowValues = values.subList(i, end);

            // Render each of the 5 lines of the dice boxes
            for (int line = 0; line < 5; line++) {
                for (int j = 0; j < rowValues.size(); j++) {
                    int val = rowValues.get(j);
                    if (val >= 1 && val <= 6) {
                        sb.append(D6_FACES[val - 1][line]);
                    } else {
                        sb.append(formatGenericFace(val, 6)[line]);
                    }
                    if (j < rowValues.size() - 1) {
                        sb.append("  ");
                    }
                }
                sb.append("\n");
            }

            if (end < total) {
                sb.append("\n"); // Blank line between rows
            }
        }

        return sb.toString();
    }

    /**
     * Renders polyhedral dice (e.g., d4, d8, d10, d12, d20, d100) as stylish dice cards.
     *
     * @param values list of rolled values
     * @param sides number of sides of each die
     * @return formatted ASCII string
     */
    public static String renderPolyhedral(List<Integer> values, int sides) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        // If it's a 6-sided die, use the pip rendering
        if (sides == 6) {
            return renderD6(values);
        }

        StringBuilder sb = new StringBuilder();
        int total = values.size();

        for (int i = 0; i < total; i += MAX_DICE_PER_ROW) {
            int end = Math.min(i + MAX_DICE_PER_ROW, total);
            List<Integer> rowValues = values.subList(i, end);

            for (int line = 0; line < 5; line++) {
                for (int j = 0; j < rowValues.size(); j++) {
                    int val = rowValues.get(j);
                    String[] face = formatGenericFace(val, sides);
                    sb.append(face[line]);
                    if (j < rowValues.size() - 1) {
                        sb.append("  ");
                    }
                }
                sb.append("\n");
            }

            if (end < total) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Generates a 5-line box for any die value and side count.
     */
    private static String[] formatGenericFace(int val, int sides) {
        String tag = "d" + sides;
        String valStr = String.valueOf(val);

        // Center tag inside 7 characters between border
        String topText = centerString(tag, 7);
        String midText = centerString("[" + valStr + "]", 7);

        return new String[] {
            "┌───────┐",
            "│" + topText + "│",
            "│" + midText + "│",
            "│       │",
            "└───────┘"
        };
    }

    private static String centerString(String s, int width) {
        if (s.length() >= width) {
            return s.substring(0, width);
        }
        int leftPadding = (width - s.length()) / 2;
        int rightPadding = width - s.length() - leftPadding;
        return " ".repeat(leftPadding) + s + " ".repeat(rightPadding);
    }
}
