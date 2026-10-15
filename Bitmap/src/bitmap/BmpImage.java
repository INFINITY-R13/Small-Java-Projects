package bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java implementation of a 24-bit uncompressed Windows Bitmap (BMP) encoder and decoder.
 * Supports reading/writing raw BMP binary files, rasterizing ASCII bitmaps into high-res images,
 * and converting image files into ASCII art.
 *
 * <p>Specification Reference:
 * <ul>
 *   <li>BITMAPFILEHEADER (14 bytes): Magic "BM" (0x4D42), file size, offset to pixel array (54).</li>
 *   <li>BITMAPINFOHEADER (40 bytes): 24 bits-per-pixel RGB, uncompressed (BI_RGB).</li>
 *   <li>Pixel Array: Bottom-up row order, BGR color byte sequence, 4-byte row padding.</li>
 * </ul>
 */
public class BmpImage {

    public static final int FILE_HEADER_SIZE = 14;
    public static final int INFO_HEADER_SIZE = 40;
    public static final int TOTAL_HEADER_SIZE = FILE_HEADER_SIZE + INFO_HEADER_SIZE; // 54 bytes

    private final int width;
    private final int height;
    // Internal pixel array stored in top-down [y][x] order as 0xRRGGBB
    private final int[][] pixels;

    /**
     * Creates a new blank BMP image with the given dimensions.
     *
     * @param width Image width in pixels
     * @param height Image height in pixels
     */
    public BmpImage(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Image dimensions must be positive: " + width + "x" + height);
        }
        this.width = width;
        this.height = height;
        this.pixels = new int[height][width];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Sets the pixel color at (x, y) where (0, 0) is top-left.
     *
     * @param x X coordinate (0 to width - 1)
     * @param y Y coordinate (0 to height - 1)
     * @param rgb 24-bit RGB packed integer (0xRRGGBB)
     */
    public void setPixel(int x, int y, int rgb) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            pixels[y][x] = rgb & 0xFFFFFF;
        }
    }

    /**
     * Gets the pixel color at (x, y).
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return 24-bit RGB packed integer (0xRRGGBB)
     */
    public int getPixel(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return pixels[y][x];
        }
        return 0;
    }

    /**
     * Fills the entire image with a background color.
     *
     * @param rgb Color to fill (0xRRGGBB)
     */
    public void fill(int rgb) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = rgb & 0xFFFFFF;
            }
        }
    }

    /**
     * Calculates row padding bytes required to align each row to a 4-byte boundary.
     *
     * @param width Width in pixels
     * @return Number of padding bytes (0 to 3)
     */
    public static int calculatePadding(int width) {
        return (4 - ((width * 3) % 4)) % 4;
    }

    /**
     * Calculates the row stride (row width in bytes including padding).
     *
     * @param width Width in pixels
     * @return Row stride in bytes
     */
    public static int calculateRowStride(int width) {
        return width * 3 + calculatePadding(width);
    }

    /**
     * Saves this image to a 24-bit uncompressed Windows BMP file.
     *
     * @param file Target file
     * @throws IOException If writing fails
     */
    public void save(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        int padding = calculatePadding(width);
        int rowStride = width * 3 + padding;
        int imageSize = rowStride * height;
        int fileSize = TOTAL_HEADER_SIZE + imageSize;

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            // --- BITMAPFILEHEADER (14 bytes) ---
            out.write('B');
            out.write('M');
            writeLittleEndian32(out, fileSize);
            writeLittleEndian16(out, 0); // Reserved 1
            writeLittleEndian16(out, 0); // Reserved 2
            writeLittleEndian32(out, TOTAL_HEADER_SIZE); // Offset to pixel data (54)

            // --- BITMAPINFOHEADER (40 bytes) ---
            writeLittleEndian32(out, INFO_HEADER_SIZE); // Header size (40)
            writeLittleEndian32(out, width);            // Image width
            writeLittleEndian32(out, height);           // Image height (positive = bottom-up)
            writeLittleEndian16(out, 1);                // Color planes (must be 1)
            writeLittleEndian16(out, 24);               // Bits per pixel (24-bit RGB)
            writeLittleEndian32(out, 0);                // Compression (0 = BI_RGB uncompressed)
            writeLittleEndian32(out, imageSize);        // Image size
            writeLittleEndian32(out, 2835);             // Horizontal resolution (72 DPI ~ 2835 ppm)
            writeLittleEndian32(out, 2835);             // Vertical resolution (72 DPI ~ 2835 ppm)
            writeLittleEndian32(out, 0);                // Colors in color palette (0 = default)
            writeLittleEndian32(out, 0);                // Important colors (0 = all)

            // --- PIXEL ARRAY (Bottom-up scanlines, BGR format) ---
            byte[] paddingBytes = new byte[padding];

            for (int y = height - 1; y >= 0; y--) { // Bottom-up
                for (int x = 0; x < width; x++) {
                    int rgb = pixels[y][x];
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    // BMP stores pixels in Blue, Green, Red order
                    out.write(b);
                    out.write(g);
                    out.write(r);
                }
                if (padding > 0) {
                    out.write(paddingBytes);
                }
            }
        }
    }

    /**
     * Loads a 24-bit uncompressed Windows BMP file from disk.
     *
     * @param file Source file
     * @return Decoded BmpImage
     * @throws IOException If file cannot be read or is invalid
     */
    public static BmpImage load(File file) throws IOException {
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            // --- BITMAPFILEHEADER ---
            int b0 = in.read();
            int b1 = in.read();
            if (b0 != 'B' || b1 != 'M') {
                throw new IOException("Not a valid BMP file (missing 'BM' magic header).");
            }

            readLittleEndian32(in); // File size
            readLittleEndian16(in); // Reserved 1
            readLittleEndian16(in); // Reserved 2
            int pixelOffset = readLittleEndian32(in);

            // --- BITMAPINFOHEADER ---
            int infoHeaderSize = readLittleEndian32(in);
            if (infoHeaderSize < 40) {
                throw new IOException("Unsupported BMP header size: " + infoHeaderSize);
            }

            int w = readLittleEndian32(in);
            int h = readLittleEndian32(in);
            int planes = readLittleEndian16(in);
            int bitsPerPixel = readLittleEndian16(in);
            int compression = readLittleEndian32(in);

            if (planes != 1) {
                throw new IOException("Unsupported planes count: " + planes);
            }
            if (bitsPerPixel != 24) {
                throw new IOException("Only 24-bit BMP images are supported (found " + bitsPerPixel + "-bit).");
            }
            if (compression != 0) {
                throw new IOException("Compressed BMP images are not supported (compression code: " + compression + ").");
            }

            boolean topDown = (h < 0);
            int actualHeight = Math.abs(h);
            int actualWidth = w;

            // Skip remaining header bytes up to pixelOffset
            int bytesToSkip = pixelOffset - (FILE_HEADER_SIZE + 20); // 14 + read 20 bytes of info header
            if (bytesToSkip > 0) {
                long skipped = in.skip(bytesToSkip);
                while (skipped < bytesToSkip) {
                    long extra = in.skip(bytesToSkip - skipped);
                    if (extra <= 0) break;
                    skipped += extra;
                }
            }

            BmpImage img = new BmpImage(actualWidth, actualHeight);
            int padding = calculatePadding(actualWidth);

            for (int row = 0; row < actualHeight; row++) {
                int y = topDown ? row : (actualHeight - 1 - row);
                for (int x = 0; x < actualWidth; x++) {
                    int b = in.read();
                    int g = in.read();
                    int r = in.read();
                    if (b < 0 || g < 0 || r < 0) {
                        throw new IOException("Unexpected end of BMP file data.");
                    }
                    int rgb = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
                    img.setPixel(x, y, rgb);
                }
                for (int p = 0; p < padding; p++) {
                    in.read(); // Read padding byte
                }
            }

            return img;
        }
    }

    /**
     * Rasterizes a rendered ASCII bitmap into a crisp, high-resolution BmpImage.
     *
     * @param lines Plain ASCII lines of the rendered bitmap
     * @param theme Color theme to colorize the pixels
     * @param scale Pixel block size per character (e.g. 10 creates 10x10 px blocks per char)
     * @param bgRgb Background color (e.g. 0x101015 for dark mode, 0xFFFFFF for white)
     * @return Generated BmpImage
     */
    public static BmpImage fromAsciiBitmap(List<String> lines, ColorTheme theme, int scale, int bgRgb) {
        if (scale < 1) scale = 1;
        int totalRows = lines.size();
        int totalCols = 0;
        for (String line : lines) {
            if (line.length() > totalCols) {
                totalCols = line.length();
            }
        }

        int imgWidth = totalCols * scale;
        int imgHeight = totalRows * scale;
        BmpImage bmp = new BmpImage(imgWidth, imgHeight);
        bmp.fill(bgRgb);

        for (int r = 0; r < totalRows; r++) {
            String line = lines.get(r);
            for (int c = 0; c < line.length(); c++) {
                char ch = line.charAt(c);
                if (ch != ' ') {
                    int fgColor = (theme == ColorTheme.MONOCHROME) ? 0xFFFFFF :
                                  theme.getRgb(r, totalRows, c, totalCols);

                    // Draw block of size scale x scale
                    int startX = c * scale;
                    int startY = r * scale;
                    for (int dy = 0; dy < scale; dy++) {
                        for (int dx = 0; dx < scale; dx++) {
                            bmp.setPixel(startX + dx, startY + dy, fgColor);
                        }
                    }
                }
            }
        }

        return bmp;
    }

    /**
     * Converts this image into a multi-line ASCII art representation.
     *
     * @param targetWidth Max columns of ASCII output (image is scaled proportionally)
     * @return List of ASCII strings
     */
    public List<String> toAsciiArt(int targetWidth) {
        if (targetWidth <= 0) targetWidth = 60;
        targetWidth = Math.min(targetWidth, width);

        // Aspect ratio correction: terminal characters are typically twice as tall as they are wide
        double fontAspect = 2.0;
        double scale = (double) targetWidth / width;
        int targetHeight = (int) Math.max(1, Math.round((height * scale) / fontAspect));

        String ramp = " .:-=+*#%@"; // Dark to bright
        List<String> asciiRows = new ArrayList<>();

        for (int ty = 0; ty < targetHeight; ty++) {
            StringBuilder sb = new StringBuilder();
            int srcYStart = (int) Math.round((double) ty / targetHeight * height);
            int srcYEnd = (int) Math.round((double) (ty + 1) / targetHeight * height);
            srcYStart = Math.min(srcYStart, height - 1);
            srcYEnd = Math.max(srcYStart + 1, Math.min(srcYEnd, height));

            for (int tx = 0; tx < targetWidth; tx++) {
                int srcXStart = (int) Math.round((double) tx / targetWidth * width);
                int srcXEnd = (int) Math.round((double) (tx + 1) / targetWidth * width);
                srcXStart = Math.min(srcXStart, width - 1);
                srcXEnd = Math.max(srcXStart + 1, Math.min(srcXEnd, width));

                // Compute average luminance in the box
                double totalLuma = 0;
                int count = 0;
                for (int y = srcYStart; y < srcYEnd; y++) {
                    for (int x = srcXStart; x < srcXEnd; x++) {
                        int rgb = getPixel(x, y);
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;
                        // ITU-R BT.601 standard luminance
                        double luma = 0.299 * r + 0.587 * g + 0.114 * b;
                        totalLuma += luma;
                        count++;
                    }
                }

                double avgLuma = count > 0 ? (totalLuma / count) : 0;
                int rampIndex = (int) Math.round((avgLuma / 255.0) * (ramp.length() - 1));
                rampIndex = Math.max(0, Math.min(ramp.length() - 1, rampIndex));
                sb.append(ramp.charAt(rampIndex));
            }
            asciiRows.add(sb.toString());
        }

        return asciiRows;
    }

    // --- Little Endian Binary Helper Methods ---

    private static void writeLittleEndian16(OutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
    }

    private static void writeLittleEndian32(OutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }

    private static int readLittleEndian16(InputStream in) throws IOException {
        int b0 = in.read();
        int b1 = in.read();
        if (b0 < 0 || b1 < 0) throw new IOException("Unexpected EOF while reading 16-bit integer");
        return (b0 & 0xFF) | ((b1 & 0xFF) << 8);
    }

    private static int readLittleEndian32(InputStream in) throws IOException {
        int b0 = in.read();
        int b1 = in.read();
        int b2 = in.read();
        int b3 = in.read();
        if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0) throw new IOException("Unexpected EOF while reading 32-bit integer");
        return (b0 & 0xFF) | ((b1 & 0xFF) << 8) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 24);
    }
}
