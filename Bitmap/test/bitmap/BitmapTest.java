package bitmap;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Self-contained unit test suite for the Bitmap program.
 * Tests core template loading, message wrapping algorithms, BMP binary encoding/decoding,
 * row padding calculations, font rasterization, and color gradients.
 */
public class BitmapTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("Running Bitmap Program Test Suite...\n");

        testPresetsLoadCorrectly();
        testColumnModuloRendering();
        testContinuousStreamRendering();
        testInvertRendering();
        testEmptyAndDefaultMessageHandling();
        testColorThemeCalculations();
        testTextToBitmapBanner();
        testBmpPaddingCalculations();
        testBmpSaveAndLoadRoundtrip();
        testBmpFromAsciiBitmap();
        testBmpToAsciiArt();

        System.out.println("\n" + "=".repeat(45));
        System.out.printf("Test Results: %d run, %d passed, %d failed.%n",
            testsRun, testsPassed, testsFailed);
        System.out.println("=".repeat(45));

        if (testsFailed > 0) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED SUCCESSFULLY! ✓");
        }
    }

    private static void assertTrue(String testName, boolean condition, String message) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.printf("  ✓ PASS: %s%n", testName);
        } else {
            testsFailed++;
            System.err.printf("  ✗ FAIL: %s -> %s%n", testName, message);
        }
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        testsRun++;
        boolean match = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        if (match) {
            testsPassed++;
            System.out.printf("  ✓ PASS: %s%n", testName);
        } else {
            testsFailed++;
            System.err.printf("  ✗ FAIL: %s -> expected [%s] but got [%s]%n", testName, expected, actual);
        }
    }

    private static void testPresetsLoadCorrectly() {
        Map<String, BitmapTemplate> presets = BitmapTemplate.getPresets();
        assertTrue("Presets map should not be empty", !presets.isEmpty(), "Presets map is empty");

        String[] expectedNames = {"world", "coffee", "heart", "skull", "alien", "cat", "rocket"};
        for (String name : expectedNames) {
            BitmapTemplate t = BitmapTemplate.getPreset(name);
            assertTrue("Preset '" + name + "' should exist", t != null, "Missing preset: " + name);
            if (t != null) {
                assertTrue("Preset '" + name + "' width > 0", t.getWidth() > 0, "Width is 0");
                assertTrue("Preset '" + name + "' height > 0", t.getHeight() > 0, "Height is 0");
                assertTrue("Preset '" + name + "' solid pixels > 0", t.countSolidPixels() > 0, "Zero solid pixels");
            }
        }
    }

    private static void testColumnModuloRendering() {
        // Simple 3-line template
        List<String> lines = Arrays.asList(
            " *** ", // cols: 1, 2, 3 are solid
            "  *  "  // col: 2 is solid
        );
        BitmapTemplate template = new BitmapTemplate("test", "test", lines);
        String message = "ABC"; // message length 3

        BitmapRenderer.RenderOptions options = new BitmapRenderer.RenderOptions();
        options.mode = BitmapRenderer.RenderMode.COLUMN_MODULO;
        options.enableColor = false;

        BitmapRenderer.RenderResult result = BitmapRenderer.render(template, message, options);
        List<String> plain = result.getPlainLines();

        // Col indices: 0=' ', 1='B', 2='C', 3='A' (3%3=0), 4=' '
        assertEquals("Row 0 column modulo matching", " BCA ", plain.get(0));
        // Col 2: 2%3=2 -> 'C'
        assertEquals("Row 1 column modulo matching", "  C  ", plain.get(1));
        assertEquals("Solid pixel count match", 4, result.getSolidPixels());
    }

    private static void testContinuousStreamRendering() {
        List<String> lines = Arrays.asList(
            " * * ", // 2 solid pixels
            "  *  "  // 1 solid pixel
        );
        BitmapTemplate template = new BitmapTemplate("stream_test", "stream_test", lines);
        String message = "XYZ";

        BitmapRenderer.RenderOptions options = new BitmapRenderer.RenderOptions();
        options.mode = BitmapRenderer.RenderMode.CONTINUOUS_STREAM;
        options.enableColor = false;

        BitmapRenderer.RenderResult result = BitmapRenderer.render(template, message, options);
        List<String> plain = result.getPlainLines();

        // Solid pixels in order should receive 'X', 'Y', 'Z'
        assertEquals("Row 0 stream match", " X Y ", plain.get(0));
        assertEquals("Row 1 stream match", "  Z  ", plain.get(1));
    }

    private static void testInvertRendering() {
        List<String> lines = Arrays.asList(
            " * "
        );
        BitmapTemplate template = new BitmapTemplate("invert_test", "invert_test", lines);
        String message = "AB";

        BitmapRenderer.RenderOptions options = new BitmapRenderer.RenderOptions();
        options.mode = BitmapRenderer.RenderMode.COLUMN_MODULO;
        options.invert = true;
        options.enableColor = false;

        BitmapRenderer.RenderResult result = BitmapRenderer.render(template, message, options);
        List<String> plain = result.getPlainLines();

        // Original: col 0=' ', col 1='*', col 2=' '
        // Inverted: col 0=solid('A'), col 1=' ', col 2=solid('A')
        assertEquals("Invert mode row match", "A A", plain.get(0));
    }

    private static void testEmptyAndDefaultMessageHandling() {
        BitmapTemplate template = new BitmapTemplate("t", "t", Arrays.asList("***"));
        BitmapRenderer.RenderOptions options = new BitmapRenderer.RenderOptions();
        options.enableColor = false;

        // Null message should fallback gracefully to "JAVA"
        BitmapRenderer.RenderResult resNull = BitmapRenderer.render(template, null, options);
        assertEquals("Fallback to JAVA on null", "JAV", resNull.getPlainLines().get(0));

        // Empty message should fallback to "JAVA"
        BitmapRenderer.RenderResult resEmpty = BitmapRenderer.render(template, "", options);
        assertEquals("Fallback to JAVA on empty", "JAV", resEmpty.getPlainLines().get(0));
    }

    private static void testColorThemeCalculations() {
        for (ColorTheme theme : ColorTheme.values()) {
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 5; c++) {
                    int rgb = theme.getRgb(r, 5, c, 5);
                    assertTrue(theme.name() + " RGB >= 0", rgb >= 0, "RGB negative: " + rgb);
                    assertTrue(theme.name() + " RGB <= 0xFFFFFF", rgb <= 0xFFFFFF, "RGB exceeds 24-bit: " + rgb);
                }
            }
        }
        assertEquals("Theme fromString case insensitivity", ColorTheme.CYBERPUNK, ColorTheme.fromString("cyberpunk"));
        assertEquals("Theme fromString default", ColorTheme.RAINBOW, ColorTheme.fromString("nonexistent"));
    }

    private static void testTextToBitmapBanner() {
        List<String> rows = TextToBitmap.generate("HI");
        assertEquals("Text banner has 7 rows", 7, rows.size());
        for (String row : rows) {
            assertTrue("Banner row is not empty", !row.isEmpty(), "Empty row in banner");
        }
    }

    private static void testBmpPaddingCalculations() {
        // Standard BMP 4-byte alignment rule:
        // width * 3 bytes per row.
        // padding = (4 - (width*3 % 4)) % 4.
        assertEquals("Padding for width 1 (3 bytes -> pad 1)", 1, BmpImage.calculatePadding(1));
        assertEquals("Padding for width 2 (6 bytes -> pad 2)", 2, BmpImage.calculatePadding(2));
        assertEquals("Padding for width 3 (9 bytes -> pad 3)", 3, BmpImage.calculatePadding(3));
        assertEquals("Padding for width 4 (12 bytes -> pad 0)", 0, BmpImage.calculatePadding(4));
        assertEquals("Padding for width 5 (15 bytes -> pad 1)", 1, BmpImage.calculatePadding(5));
        assertEquals("Padding for width 7 (21 bytes -> pad 3)", 3, BmpImage.calculatePadding(7));

        assertEquals("Stride for width 1", 4, BmpImage.calculateRowStride(1));
        assertEquals("Stride for width 4", 12, BmpImage.calculateRowStride(4));
    }

    private static void testBmpSaveAndLoadRoundtrip() {
        try {
            int w = 5;
            int h = 7;
            BmpImage img = new BmpImage(w, h);
            img.fill(0x112233);

            // Set distinct colors on corners and center
            img.setPixel(0, 0, 0xFF0000); // Top-left Red
            img.setPixel(w - 1, 0, 0x00FF00); // Top-right Green
            img.setPixel(0, h - 1, 0x0000FF); // Bottom-left Blue
            img.setPixel(w - 1, h - 1, 0xFFFF00); // Bottom-right Yellow
            img.setPixel(2, 3, 0xFF00FF); // Center Magenta

            File tempFile = File.createTempFile("test_bmp_", ".bmp");
            tempFile.deleteOnExit();

            img.save(tempFile);
            assertTrue("BMP file was written to disk", tempFile.exists() && tempFile.length() > 54, "BMP file missing or empty");

            BmpImage loaded = BmpImage.load(tempFile);
            assertEquals("Loaded width matches", w, loaded.getWidth());
            assertEquals("Loaded height matches", h, loaded.getHeight());

            assertEquals("Pixel (0,0) Red", 0xFF0000, loaded.getPixel(0, 0));
            assertEquals("Pixel (w-1,0) Green", 0x00FF00, loaded.getPixel(w - 1, 0));
            assertEquals("Pixel (0,h-1) Blue", 0x0000FF, loaded.getPixel(0, h - 1));
            assertEquals("Pixel (w-1,h-1) Yellow", 0xFFFF00, loaded.getPixel(w - 1, h - 1));
            assertEquals("Pixel (2,3) Center Magenta", 0xFF00FF, loaded.getPixel(2, 3));
            assertEquals("Background pixel check", 0x112233, loaded.getPixel(1, 1));

            tempFile.delete();
        } catch (IOException e) {
            assertTrue("BMP roundtrip failed with exception", false, e.getMessage());
        }
    }

    private static void testBmpFromAsciiBitmap() {
        List<String> lines = Arrays.asList(
            " ** ",
            " ** "
        );
        int scale = 4;
        BmpImage bmp = BmpImage.fromAsciiBitmap(lines, ColorTheme.MONOCHROME, scale, 0x000000);
        assertEquals("Scaled width is 4 * 4 = 16", 16, bmp.getWidth());
        assertEquals("Scaled height is 2 * 4 = 8", 8, bmp.getHeight());
        // Background at (0, 0) should be 0x000000
        assertEquals("Background color at (0,0)", 0x000000, bmp.getPixel(0, 0));
        // Solid pixel at (scale, 0) should be 0xFFFFFF (monochrome fg)
        assertEquals("Foreground color at (4,0)", 0xFFFFFF, bmp.getPixel(4, 0));
    }

    private static void testBmpToAsciiArt() {
        BmpImage bmp = new BmpImage(10, 10);
        bmp.fill(0x000000); // All dark
        List<String> ascii = bmp.toAsciiArt(5);
        assertTrue("ASCII output rows > 0", ascii.size() > 0, "No rows produced");
        // Darkest pixel should map to ' ' or '.'
        char firstChar = ascii.get(0).charAt(0);
        assertEquals("Dark pixel ramp char", ' ', firstChar);
    }
}
