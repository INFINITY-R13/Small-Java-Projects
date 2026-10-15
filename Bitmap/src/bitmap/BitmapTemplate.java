package bitmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages bitmap templates, including built-in presets and external file loaders.
 */
public class BitmapTemplate {

    private final String name;
    private final String description;
    private final List<String> lines;

    private static final Map<String, BitmapTemplate> PRESETS = new LinkedHashMap<>();

    public BitmapTemplate(String name, String description, List<String> lines) {
        this.name = name;
        this.description = description;
        this.lines = new ArrayList<>(lines);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public int getWidth() {
        int max = 0;
        for (String line : lines) {
            if (line.length() > max) {
                max = line.length();
            }
        }
        return max;
    }

    public int getHeight() {
        return lines.size();
    }

    public int countSolidPixels() {
        int count = 0;
        for (String line : lines) {
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) != ' ') {
                    count++;
                }
            }
        }
        return count;
    }

    static {
        // Preset 1: World Map (Al Sweigart classic)
        register(new BitmapTemplate("world", "Classic Global World Map (Sweigart)", Arrays.asList(
            "....................................................................",
            "   **************   *  *** **  *      ******************************",
            "  ********************* ** ** *  * ****************************** *",
            " **      *****************       ******************************",
            "          *************          **  * **** ** ************** *",
            "           *********            *******   **************** * *",
            "            ********           ***************************  *",
            "   *        * **** ***         *************** ******  ** *",
            "               ****  *         ***************   *** ***  *",
            "                 ******         *************    **   **  *",
            "                 ********        *************    *  ** ***",
            "                   ********         ********          * *** ****",
            "                   *********         ******  *        **** ** * **",
            "                   *********         ****** * *           *** *   *",
            "                     ******          ***** **             *****   *",
            "                     *****            **** *            ********",
            "                    *****             ****              *********",
            "                    ****              **                 *******   *",
            "                    ***                                       *    *",
            "                    **     *                    *",
            "...................................................................."
        )));

        // Preset 2: Coffee Cup
        register(new BitmapTemplate("coffee", "Steaming Cup of Java Espresso", Arrays.asList(
            "                    (   (",
            "                     )   )",
            "                    (   (",
            "                     )   )",
            "          ************************",
            "         **************************",
            "        ****************************",
            "        ****************************      ******",
            "        ****************************    **********",
            "        ****************************   ************",
            "        ****************************   ************",
            "        ****************************    **********",
            "         **************************       ******",
            "          ************************",
            "           **********************",
            "            ********************",
            "         **************************",
            "        ****************************"
        )));

        // Preset 3: Heart
        register(new BitmapTemplate("heart", "Vibrant Pixel Heart", Arrays.asList(
            "         *********               *********         ",
            "      ***************         ***************      ",
            "    *******************     *******************    ",
            "   *********************   *********************   ",
            "  *********************** ***********************  ",
            "  ***********************************************  ",
            "  ***********************************************  ",
            "  ***********************************************  ",
            "   *********************************************   ",
            "    *******************************************    ",
            "      ***************************************      ",
            "        ***********************************        ",
            "          *******************************          ",
            "            ***************************            ",
            "              ***********************              ",
            "                *******************                ",
            "                  ***************                  ",
            "                    ***********                    ",
            "                      *******                      ",
            "                        ***                        ",
            "                         *                         "
        )));

        // Preset 4: Skull
        register(new BitmapTemplate("skull", "Retro Pirate / Arcade Skull", Arrays.asList(
            "             ********************",
            "          **************************",
            "        ******************************",
            "       ********************************",
            "      **********************************",
            "     ************************************",
            "     ************************************",
            "     ******   ******      ******   ******",
            "     ****       ****      ****       ****",
            "     ****       ****      ****       ****",
            "     ******   ******      ******   ******",
            "     ************************************",
            "      **********************************",
            "       ********************************",
            "        ********  ***    ***  ********",
            "         *******  ***    ***  *******",
            "          ******  ***    ***  ******",
            "           *****              *****",
            "             ********************",
            "               ****************",
            "                 ************"
        )));

        // Preset 5: Alien (Space Invader)
        register(new BitmapTemplate("alien", "8-bit Retro Space Invader", Arrays.asList(
            "          **                  **",
            "            ****          ****",
            "          **********************",
            "        **************************",
            "       ******  ************  ******",
            "      ********************************",
            "      ********************************",
            "      ****  ********************  ****",
            "      ****  **                **  ****",
            "              ****        ****",
            "            ****            ****"
        )));

        // Preset 6: Cat
        register(new BitmapTemplate("cat", "Pixel Silhouette of a Cat", Arrays.asList(
            "     ***                ***",
            "    *****              *****",
            "   *******            *******",
            "   **************************",
            "  ****************************",
            "  ****************************",
            "  *****   ************   *****",
            "  ****     **********     ****",
            "  ****     **********     ****",
            "  *****   ************   *****",
            "  ****************************",
            "   **************************",
            "     **********************",
            "       ******************",
            "      ********************",
            "     **********************",
            "    ************************",
            "    ************************",
            "    ************************",
            "     ******          ******",
            "     ******          ******"
        )));

        // Preset 7: Rocket
        register(new BitmapTemplate("rocket", "Retro Space Exploration Rocket", Arrays.asList(
            "                 **",
            "                ****",
            "               ******",
            "              ********",
            "              ********",
            "             **********",
            "             **********",
            "             **********",
            "            ************",
            "            ************",
            "            ***  **  ***",
            "            ***  **  ***",
            "            ************",
            "            ************",
            "           **************",
            "          ****************",
            "         ***  ********** ***",
            "        ****  **********  ****",
            "       *****  **********  *****",
            "      ******  **********  ******",
            "              **********",
            "               ********",
            "                ******",
            "                *    *",
            "               ***  ***",
            "              ****  ****",
            "             *****  *****",
            "              ***    ***",
            "               *      *"
        )));
    }

    private static void register(BitmapTemplate template) {
        PRESETS.put(template.getName().toLowerCase(), template);
    }

    public static Map<String, BitmapTemplate> getPresets() {
        return Collections.unmodifiableMap(PRESETS);
    }

    public static BitmapTemplate getPreset(String name) {
        if (name == null) return PRESETS.get("world");
        return PRESETS.get(name.trim().toLowerCase());
    }

    /**
     * Loads a bitmap template from an external text file.
     *
     * @param file Text file to load
     * @return BitmapTemplate
     * @throws IOException If file reading fails
     */
    public static BitmapTemplate loadFromFile(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return new BitmapTemplate(file.getName(), "Custom template from " + file.getName(), lines);
    }
}
