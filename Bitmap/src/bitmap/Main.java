package bitmap;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main application entry point for the Java Bitmap Program.
 * Supports both an interactive console menu and comprehensive CLI flag options.
 */
public class Main {

    private static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        if (args.length > 0) {
            runCli(args);
        } else {
            runInteractive();
        }
    }

    // ==========================================
    // CLI EXECUTION MODE
    // ==========================================

    private static void runCli(String[] args) {
        String presetName = null;
        String message = "JAVA IS AWESOME";
        String themeStr = "rainbow";
        String filePath = null;
        String textBanner = null;
        String exportBmpPath = null;
        String importBmpPath = null;
        boolean invert = false;
        boolean enableColor = true;
        int animateDelayMs = 0;
        int bmpScale = 8;
        BitmapRenderer.RenderMode renderMode = BitmapRenderer.RenderMode.COLUMN_MODULO;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-h":
                case "--help":
                    printHelp();
                    return;
                case "-v":
                case "--version":
                    System.out.println("Bitmap Program v" + VERSION + " (Java)");
                    return;
                case "-l":
                case "--list":
                    printPresetsAndThemes();
                    return;
                case "-p":
                case "--preset":
                    if (++i < args.length) presetName = args[i];
                    break;
                case "-m":
                case "--message":
                    if (++i < args.length) message = args[i];
                    break;
                case "-c":
                case "--color":
                case "--theme":
                    if (++i < args.length) themeStr = args[i];
                    break;
                case "-f":
                case "--file":
                    if (++i < args.length) filePath = args[i];
                    break;
                case "-t":
                case "--text":
                    if (++i < args.length) textBanner = args[i];
                    break;
                case "-e":
                case "--export":
                    if (++i < args.length) exportBmpPath = args[i];
                    break;
                case "-i":
                case "--import-bmp":
                    if (++i < args.length) importBmpPath = args[i];
                    break;
                case "-a":
                case "--animate":
                    animateDelayMs = 40;
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        try {
                            animateDelayMs = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException ignored) {}
                    }
                    break;
                case "--invert":
                    invert = true;
                    break;
                case "--no-color":
                    enableColor = false;
                    break;
                case "--scale":
                    if (++i < args.length) {
                        try {
                            bmpScale = Integer.parseInt(args[i]);
                        } catch (NumberFormatException ignored) {}
                    }
                    break;
                case "--mode":
                    if (++i < args.length) {
                        String m = args[i].toLowerCase();
                        if (m.contains("stream")) {
                            renderMode = BitmapRenderer.RenderMode.CONTINUOUS_STREAM;
                        } else if (m.contains("solid")) {
                            renderMode = BitmapRenderer.RenderMode.SOLID_GLYPH;
                        } else {
                            renderMode = BitmapRenderer.RenderMode.COLUMN_MODULO;
                        }
                    }
                    break;
                default:
                    System.err.println("Unknown option: " + arg + " (use --help for options)");
                    return;
            }
        }

        // Handle importing existing BMP image
        if (importBmpPath != null) {
            handleImportBmp(importBmpPath, 70);
            return;
        }

        // Determine template source
        BitmapTemplate template;
        try {
            if (filePath != null) {
                File file = new File(filePath);
                if (!file.exists()) {
                    System.err.println("Error: File not found: " + filePath);
                    return;
                }
                template = BitmapTemplate.loadFromFile(file);
            } else if (textBanner != null) {
                List<String> bannerLines = TextToBitmap.generate(textBanner);
                template = new BitmapTemplate("text-" + textBanner, "Dynamic banner for '" + textBanner + "'", bannerLines);
            } else {
                if (presetName == null) presetName = "world";
                template = BitmapTemplate.getPreset(presetName);
                if (template == null) {
                    System.err.println("Unknown preset: '" + presetName + "'. Using 'world'.");
                    template = BitmapTemplate.getPreset("world");
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading template: " + e.getMessage());
            return;
        }

        ColorTheme theme = ColorTheme.fromString(themeStr);
        BitmapRenderer.RenderOptions options = new BitmapRenderer.RenderOptions(renderMode, theme, invert, enableColor);
        BitmapRenderer.RenderResult result = BitmapRenderer.render(template, message, options);

        if (animateDelayMs > 0) {
            BitmapRenderer.printWithAnimation(result.getColoredLines(), animateDelayMs);
        } else {
            for (String line : result.getColoredLines()) {
                System.out.println(line);
            }
        }

        // Handle BMP Export if requested
        if (exportBmpPath != null) {
            exportToBmp(result.getPlainLines(), theme, bmpScale, exportBmpPath);
        }
    }

    // ==========================================
    // INTERACTIVE EXECUTION MODE
    // ==========================================

    private static void runInteractive() {
        Scanner scanner = new Scanner(System.in);
        printBanner();

        while (true) {
            System.out.println(Ansi.BOLD + "Choose a Bitmap Source:" + Ansi.RESET);
            System.out.println("  1) 🌍 Classic World Map (Sweigart)");
            System.out.println("  2) ☕ Steaming Java Coffee Cup");
            System.out.println("  3) ❤️ Pixel Heart");
            System.out.println("  4) 💀 Retro Arcade Skull");
            System.out.println("  5) 👾 8-Bit Space Invader Alien");
            System.out.println("  6) 🐱 Pixel Silhouette Cat");
            System.out.println("  7) 🚀 Space Exploration Rocket");
            System.out.println("  8) 🔤 Dynamic Word Banner (e.g. your name)");
            System.out.println("  9) 📂 Load Custom Template File (.txt)");
            System.out.println(" 10) 🖼️  Convert BMP Image to ASCII (.bmp)");
            System.out.println("  0) 🚪 Exit Program");
            System.out.print("\nEnter your choice [0-10]: ");

            String choiceStr = scanner.nextLine().trim();
            if (choiceStr.equals("0") || choiceStr.equalsIgnoreCase("q") || choiceStr.equalsIgnoreCase("exit")) {
                System.out.println("\nGoodbye! Happy Java coding! ☕");
                break;
            }

            if (choiceStr.equals("10")) {
                System.out.print("Enter path to 24-bit .bmp image: ");
                String path = scanner.nextLine().trim();
                handleImportBmp(path, 75);
                System.out.println("\nPress [Enter] to continue...");
                scanner.nextLine();
                continue;
            }

            BitmapTemplate template = null;
            if (choiceStr.equals("8")) {
                System.out.print("Enter word or short phrase for banner: ");
                String word = scanner.nextLine().trim();
                if (word.isEmpty()) word = "JAVA";
                template = new BitmapTemplate("banner", "Banner: " + word, TextToBitmap.generate(word));
            } else if (choiceStr.equals("9")) {
                System.out.print("Enter path to template text file: ");
                String path = scanner.nextLine().trim();
                File f = new File(path);
                if (!f.exists()) {
                    System.out.println(Ansi.RED + "Error: File does not exist!" + Ansi.RESET + "\n");
                    continue;
                }
                try {
                    template = BitmapTemplate.loadFromFile(f);
                } catch (IOException e) {
                    System.out.println(Ansi.RED + "Error reading file: " + e.getMessage() + Ansi.RESET + "\n");
                    continue;
                }
            } else {
                String[] presetKeys = {"world", "coffee", "heart", "skull", "alien", "cat", "rocket"};
                try {
                    int idx = Integer.parseInt(choiceStr) - 1;
                    if (idx >= 0 && idx < presetKeys.length) {
                        template = BitmapTemplate.getPreset(presetKeys[idx]);
                    }
                } catch (NumberFormatException ignored) {}
                if (template == null) {
                    System.out.println(Ansi.RED + "Invalid choice! Defaulting to World Map." + Ansi.RESET);
                    template = BitmapTemplate.getPreset("world");
                }
            }

            System.out.print("\nEnter custom message to fill bitmap (default: 'JAVA'): ");
            String msg = scanner.nextLine().trim();
            if (msg.isEmpty()) {
                msg = "JAVA";
            }

            System.out.println("\n" + Ansi.BOLD + "Choose Color Theme:" + Ansi.RESET);
            ColorTheme[] themes = ColorTheme.values();
            for (int i = 0; i < themes.length; i++) {
                System.out.printf("  %d) %-12s - %s%n", (i + 1), themes[i].getDisplayName(), themes[i].getDescription());
            }
            System.out.print("Select theme [1-" + themes.length + "] (default 1): ");
            String themeChoice = scanner.nextLine().trim();
            ColorTheme selectedTheme = ColorTheme.RAINBOW;
            try {
                int tIdx = Integer.parseInt(themeChoice) - 1;
                if (tIdx >= 0 && tIdx < themes.length) {
                    selectedTheme = themes[tIdx];
                }
            } catch (NumberFormatException ignored) {}

            System.out.println("\n" + Ansi.BOLD + "Choose Wrapping Mode:" + Ansi.RESET);
            System.out.println("  1) Column-Aligned (Classic Al Sweigart - vertical pattern lines)");
            System.out.println("  2) Continuous Flow (Sequential reading flow across entire shape)");
            System.out.print("Select mode [1-2] (default 1): ");
            String modeChoice = scanner.nextLine().trim();
            BitmapRenderer.RenderMode mode = "2".equals(modeChoice)
                ? BitmapRenderer.RenderMode.CONTINUOUS_STREAM
                : BitmapRenderer.RenderMode.COLUMN_MODULO;

            System.out.print("\nAnimate line reveal? (y/N): ");
            String animChoice = scanner.nextLine().trim();
            int delay = animChoice.equalsIgnoreCase("y") ? 35 : 0;

            System.out.println("\n" + "=".repeat(template.getWidth() > 30 ? template.getWidth() : 60));
            BitmapRenderer.RenderOptions options = new BitmapRenderer.RenderOptions(mode, selectedTheme, false, true);
            BitmapRenderer.RenderResult result = BitmapRenderer.render(template, msg, options);

            if (delay > 0) {
                BitmapRenderer.printWithAnimation(result.getColoredLines(), delay);
            } else {
                for (String line : result.getColoredLines()) {
                    System.out.println(line);
                }
            }
            System.out.println("=".repeat(template.getWidth() > 30 ? template.getWidth() : 60));
            System.out.printf(Ansi.DIM + "Stats: %d solid pixels | %d x %d dims | Message repeated %.1fx%n" + Ansi.RESET,
                result.getSolidPixels(), result.getWidth(), result.getHeight(), result.getMessageCycles());

            System.out.print("\nExport as 24-bit .bmp image file? (y/N): ");
            String expChoice = scanner.nextLine().trim();
            if (expChoice.equalsIgnoreCase("y")) {
                System.out.print("Enter output filename (e.g. output.bmp): ");
                String outName = scanner.nextLine().trim();
                if (outName.isEmpty()) outName = "bitmap_output.bmp";
                if (!outName.toLowerCase().endsWith(".bmp")) outName += ".bmp";
                exportToBmp(result.getPlainLines(), selectedTheme, 8, outName);
            }

            System.out.println("\nPress [Enter] to create another bitmap, or type 'exit' to quit...");
            String nextAction = scanner.nextLine().trim();
            if (nextAction.equalsIgnoreCase("exit") || nextAction.equalsIgnoreCase("q")) {
                System.out.println("Goodbye! ☕");
                break;
            }
            System.out.println();
        }
        scanner.close();
    }

    // ==========================================
    // HELPER ACTIONS
    // ==========================================

    private static void exportToBmp(List<String> plainLines, ColorTheme theme, int scale, String outputPath) {
        try {
            File outFile = new File(outputPath);
            BmpImage bmp = BmpImage.fromAsciiBitmap(plainLines, theme, scale, 0x12121A);
            bmp.save(outFile);
            System.out.println(Ansi.GREEN + "✓ Exported high-res 24-bit BMP image (" +
                bmp.getWidth() + "x" + bmp.getHeight() + " px) to: " + outFile.getAbsolutePath() + Ansi.RESET);
        } catch (IOException e) {
            System.err.println(Ansi.RED + "Failed to export BMP: " + e.getMessage() + Ansi.RESET);
        }
    }

    private static void handleImportBmp(String path, int targetWidth) {
        File f = new File(path);
        if (!f.exists()) {
            System.err.println(Ansi.RED + "Error: BMP image file does not exist: " + path + Ansi.RESET);
            return;
        }
        try {
            BmpImage bmp = BmpImage.load(f);
            System.out.printf(Ansi.CYAN + "Loaded BMP: %dx%d px (24-bit uncompressed)%n" + Ansi.RESET,
                bmp.getWidth(), bmp.getHeight());
            System.out.println("--- ASCII Art Conversion ---");
            List<String> ascii = bmp.toAsciiArt(targetWidth);
            for (String line : ascii) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println(Ansi.RED + "Failed to load BMP file: " + e.getMessage() + Ansi.RESET);
        }
    }

    private static void printBanner() {
        System.out.println(Ansi.rgb(0, 240, 255) + Ansi.BOLD);
        System.out.println("  ____  _ _                          ____                                      ");
        System.out.println(" | __ )(_) |_ _ __ ___   __ _ _ __  |  _ \\ _ __ ___   __ _ _ __ __ _ _ __ ___  ");
        System.out.println(" |  _ \\| | __| '_ ` _ \\ / _` | '_ \\ | |_) | '__/ _ \\ / _` | '__/ _` | '_ ` _ \\ ");
        System.out.println(" | |_) | | |_| | | | | | (_| | |_) ||  __/| | | (_) | (_| | | | (_| | | | | | |");
        System.out.println(" |____/|_|\\__|_| |_| |_|\\__,_| .__/ |_|   |_|  \\___/ \\__, |_|  \\__,_|_| |_| |_|");
        System.out.println("                             |_|                     |___/                     ");
        System.out.println(Ansi.RESET + Ansi.DIM + "                 Pure Java Bitmap Art & 24-bit BMP Generator (v" + VERSION + ")" + Ansi.RESET);
        System.out.println();
    }

    private static void printHelp() {
        System.out.println(Ansi.BOLD + "Bitmap Program in Java (v" + VERSION + ")" + Ansi.RESET);
        System.out.println("Usage: java -cp bin bitmap.Main [OPTIONS]\n");
        System.out.println(Ansi.BOLD + "Options:" + Ansi.RESET);
        System.out.println("  -p, --preset <name>       Choose built-in preset (world, coffee, heart, skull, alien, cat, rocket)");
        System.out.println("  -m, --message <text>      Custom message to fill bitmap shape with");
        System.out.println("  -c, --color <theme>       Color theme (rainbow, matrix, cyberpunk, sunset, ocean, fire, gold, mono)");
        System.out.println("  -t, --text <word>         Generate dynamic bitmap banner from text (e.g. 'JAVA')");
        System.out.println("  -f, --file <path>         Load custom template from text file");
        System.out.println("  -e, --export <file.bmp>   Export rendered bitmap to 24-bit uncompressed Windows BMP");
        System.out.println("  -i, --import-bmp <path>   Load an existing BMP file and convert to ASCII");
        System.out.println("  -a, --animate [ms]        Animate line-by-line reveal (default: 40ms)");
        System.out.println("      --mode <mode>         Wrapping mode: 'column' (classic Sweigart) or 'stream' (continuous)");
        System.out.println("      --invert              Invert solid and empty characters");
        System.out.println("      --scale <pixels>      Pixel block size per character for BMP export (default: 8)");
        System.out.println("      --no-color            Disable ANSI TrueColor escape sequences");
        System.out.println("  -l, --list                List available presets and color themes");
        System.out.println("  -v, --version             Show version");
        System.out.println("  -h, --help                Show this help manual\n");
        System.out.println(Ansi.BOLD + "Examples:" + Ansi.RESET);
        System.out.println("  java -cp bin bitmap.Main --preset world --message \"HELLO WORLD\" --color rainbow");
        System.out.println("  java -cp bin bitmap.Main --preset coffee --message \"JAVA ESPRESSO\" --color fire");
        System.out.println("  java -cp bin bitmap.Main --preset heart --message \"I LOVE JAVA\" --color sunset --export heart.bmp");
        System.out.println("  java -cp bin bitmap.Main --text \"ROCKET\" --message \"*\" --color cyberpunk");
    }

    private static void printPresetsAndThemes() {
        System.out.println(Ansi.BOLD + "Built-in Bitmap Presets:" + Ansi.RESET);
        for (Map.Entry<String, BitmapTemplate> entry : BitmapTemplate.getPresets().entrySet()) {
            BitmapTemplate t = entry.getValue();
            System.out.printf("  • %-10s : %s (%dx%d, %d pixels)%n",
                entry.getKey(), t.getDescription(), t.getWidth(), t.getHeight(), t.countSolidPixels());
        }

        System.out.println("\n" + Ansi.BOLD + "Color Themes:" + Ansi.RESET);
        for (ColorTheme theme : ColorTheme.values()) {
            System.out.printf("  • %-12s : %s%n", theme.name().toLowerCase(), theme.getDescription());
        }
    }
}
