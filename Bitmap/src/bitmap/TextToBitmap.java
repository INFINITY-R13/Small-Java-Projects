package bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts alphanumeric text into a 2D bitmap character matrix using a 5x7 bitmap font.
 * This allows dynamic generation of banner bitmaps from arbitrary phrases.
 */
public class TextToBitmap {

    public static final int FONT_HEIGHT = 7;
    public static final int FONT_WIDTH = 5;

    private static final Map<Character, String[]> FONT = new HashMap<>();

    static {
        // 5x7 font definitions (* = solid pixel, space = blank)
        FONT.put('A', new String[] {
            " *** ",
            "*   *",
            "*   *",
            "*****",
            "*   *",
            "*   *",
            "*   *"
        });
        FONT.put('B', new String[] {
            "**** ",
            "*   *",
            "*   *",
            "**** ",
            "*   *",
            "*   *",
            "**** "
        });
        FONT.put('C', new String[] {
            " ****",
            "*    ",
            "*    ",
            "*    ",
            "*    ",
            "*    ",
            " ****"
        });
        FONT.put('D', new String[] {
            "**** ",
            "*   *",
            "*   *",
            "*   *",
            "*   *",
            "*   *",
            "**** "
        });
        FONT.put('E', new String[] {
            "*****",
            "*    ",
            "*    ",
            "**** ",
            "*    ",
            "*    ",
            "*****"
        });
        FONT.put('F', new String[] {
            "*****",
            "*    ",
            "*    ",
            "**** ",
            "*    ",
            "*    ",
            "*    "
        });
        FONT.put('G', new String[] {
            " ****",
            "*    ",
            "*    ",
            "* ***",
            "*   *",
            "*   *",
            " *** "
        });
        FONT.put('H', new String[] {
            "*   *",
            "*   *",
            "*   *",
            "*****",
            "*   *",
            "*   *",
            "*   *"
        });
        FONT.put('I', new String[] {
            "*****",
            "  *  ",
            "  *  ",
            "  *  ",
            "  *  ",
            "  *  ",
            "*****"
        });
        FONT.put('J', new String[] {
            "  ***",
            "    *",
            "    *",
            "    *",
            "*   *",
            "*   *",
            " *** "
        });
        FONT.put('K', new String[] {
            "*   *",
            "*  * ",
            "* *  ",
            "**   ",
            "* *  ",
            "*  * ",
            "*   *"
        });
        FONT.put('L', new String[] {
            "*    ",
            "*    ",
            "*    ",
            "*    ",
            "*    ",
            "*    ",
            "*****"
        });
        FONT.put('M', new String[] {
            "*   *",
            "** **",
            "* * *",
            "* * *",
            "*   *",
            "*   *",
            "*   *"
        });
        FONT.put('N', new String[] {
            "*   *",
            "**  *",
            "* * *",
            "* * *",
            "*  **",
            "*   *",
            "*   *"
        });
        FONT.put('O', new String[] {
            " *** ",
            "*   *",
            "*   *",
            "*   *",
            "*   *",
            "*   *",
            " *** "
        });
        FONT.put('P', new String[] {
            "**** ",
            "*   *",
            "*   *",
            "**** ",
            "*    ",
            "*    ",
            "*    "
        });
        FONT.put('Q', new String[] {
            " *** ",
            "*   *",
            "*   *",
            "*   *",
            "* * *",
            "*  * ",
            " ** *"
        });
        FONT.put('R', new String[] {
            "**** ",
            "*   *",
            "*   *",
            "**** ",
            "* *  ",
            "*  * ",
            "*   *"
        });
        FONT.put('S', new String[] {
            " ****",
            "*    ",
            "*    ",
            " *** ",
            "    *",
            "    *",
            "**** "
        });
        FONT.put('T', new String[] {
            "*****",
            "  *  ",
            "  *  ",
            "  *  ",
            "  *  ",
            "  *  ",
            "  *  "
        });
        FONT.put('U', new String[] {
            "*   *",
            "*   *",
            "*   *",
            "*   *",
            "*   *",
            "*   *",
            " *** "
        });
        FONT.put('V', new String[] {
            "*   *",
            "*   *",
            "*   *",
            "*   *",
            "*   *",
            " * * ",
            "  *  "
        });
        FONT.put('W', new String[] {
            "*   *",
            "*   *",
            "*   *",
            "* * *",
            "* * *",
            "** **",
            "*   *"
        });
        FONT.put('X', new String[] {
            "*   *",
            "*   *",
            " * * ",
            "  *  ",
            " * * ",
            "*   *",
            "*   *"
        });
        FONT.put('Y', new String[] {
            "*   *",
            "*   *",
            " * * ",
            "  *  ",
            "  *  ",
            "  *  ",
            "  *  "
        });
        FONT.put('Z', new String[] {
            "*****",
            "    *",
            "   * ",
            "  *  ",
            " *   ",
            "*    ",
            "*****"
        });

        // Digits
        FONT.put('0', new String[] {
            " *** ",
            "*  **",
            "* * *",
            "* * *",
            "**  *",
            "*   *",
            " *** "
        });
        FONT.put('1', new String[] {
            "  *  ",
            " **  ",
            "  *  ",
            "  *  ",
            "  *  ",
            "  *  ",
            " *** "
        });
        FONT.put('2', new String[] {
            " *** ",
            "*   *",
            "    *",
            "   * ",
            "  *  ",
            " *   ",
            "*****"
        });
        FONT.put('3', new String[] {
            "**** ",
            "    *",
            "    *",
            " *** ",
            "    *",
            "    *",
            "**** "
        });
        FONT.put('4', new String[] {
            "*   *",
            "*   *",
            "*   *",
            "*****",
            "    *",
            "    *",
            "    *"
        });
        FONT.put('5', new String[] {
            "*****",
            "*    ",
            "*    ",
            "**** ",
            "    *",
            "    *",
            "**** "
        });
        FONT.put('6', new String[] {
            " *** ",
            "*    ",
            "*    ",
            "**** ",
            "*   *",
            "*   *",
            " *** "
        });
        FONT.put('7', new String[] {
            "*****",
            "    *",
            "   * ",
            "  *  ",
            " *   ",
            " *   ",
            " *   "
        });
        FONT.put('8', new String[] {
            " *** ",
            "*   *",
            "*   *",
            " *** ",
            "*   *",
            "*   *",
            " *** "
        });
        FONT.put('9', new String[] {
            " *** ",
            "*   *",
            "*   *",
            " ****",
            "    *",
            "    *",
            " *** "
        });

        // Punctuation and Symbols
        FONT.put(' ', new String[] {
            "   ",
            "   ",
            "   ",
            "   ",
            "   ",
            "   ",
            "   "
        });
        FONT.put('!', new String[] {
            " * ",
            " * ",
            " * ",
            " * ",
            " * ",
            "   ",
            " * "
        });
        FONT.put('?', new String[] {
            "*** ",
            "   *",
            "  * ",
            " *  ",
            " *  ",
            "    ",
            " *  "
        });
        FONT.put('.', new String[] {
            "  ",
            "  ",
            "  ",
            "  ",
            "  ",
            "**",
            "**"
        });
        FONT.put(':', new String[] {
            "  ",
            "**",
            "**",
            "  ",
            "**",
            "**",
            "  "
        });
        FONT.put('-', new String[] {
            "     ",
            "     ",
            "     ",
            "*****",
            "     ",
            "     ",
            "     "
        });
        FONT.put('+', new String[] {
            "     ",
            "  *  ",
            "  *  ",
            "*****",
            "  *  ",
            "  *  ",
            "     "
        });
        FONT.put('<', new String[] {
            "   * ",
            "  *  ",
            " *   ",
            "*    ",
            " *   ",
            "  *  ",
            "   * "
        });
        FONT.put('>', new String[] {
            " *   ",
            "  *  ",
            "   * ",
            "    *",
            "   * ",
            "  *  ",
            " *   "
        });
        FONT.put('#', new String[] {
            " * * ",
            "*****",
            " * * ",
            "*****",
            " * * ",
            " * * ",
            " * * "
        });
        FONT.put('♥', new String[] {
            " * * ",
            "*****",
            "*****",
            "*****",
            " *** ",
            "  *  ",
            "     "
        });
    }

    /**
     * Converts a text string into a list of bitmap rows.
     *
     * @param text The input phrase to vectorize into bitmap characters
     * @return List of 7 strings representing the banner rows
     */
    public static List<String> generate(String text) {
        if (text == null || text.isEmpty()) {
            text = "JAVA";
        }
        text = text.toUpperCase();

        List<StringBuilder> rows = new ArrayList<>();
        for (int i = 0; i < FONT_HEIGHT; i++) {
            rows.add(new StringBuilder());
        }

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String[] glyph = FONT.get(c);
            if (glyph == null) {
                glyph = FONT.get('?');
            }

            for (int r = 0; r < FONT_HEIGHT; r++) {
                rows.get(r).append(glyph[r]);
                // Spacing between characters
                rows.get(r).append("  ");
            }
        }

        List<String> result = new ArrayList<>();
        for (StringBuilder sb : rows) {
            result.add(sb.toString());
        }
        return result;
    }
}
