package aura.util;

import java.awt.Color;

/**
 * File type detector and styling metadata.
 */
public enum FileType {
    JAVA("Java", "java", new Color(0xe7, 0x6f, 0x51), true),
    PYTHON("Python", "py", new Color(0x35, 0x72, 0xa5), true),
    JAVASCRIPT("JavaScript", "js", new Color(0xf7, 0xdf, 0x1e), true),
    TYPESCRIPT("TypeScript", "ts", new Color(0x31, 0x78, 0xc6), true),
    JSON("JSON", "json", new Color(0xfa, 0xb3, 0x87), true),
    MARKDOWN("Markdown", "md", new Color(0x89, 0xb4, 0xfa), true),
    HTML("HTML", "html", new Color(0xe3, 0x4f, 0x26), true),
    CSS("CSS", "css", new Color(0x56, 0x3d, 0x7c), true),
    SQL("SQL", "sql", new Color(0xe3, 0x8c, 0x00), true),
    SHELL("Shell", "sh", new Color(0x89, 0xe0, 0x51), true),
    CPP("C/C++", "cpp", new Color(0xf3, 0x4b, 0x7d), true),
    XML("XML", "xml", new Color(0x00, 0x60, 0xac), true),
    PLAINTEXT("Plain Text", "txt", new Color(0x94, 0x9c, 0xbb), false);

    private final String displayName;
    private final String defaultExtension;
    private final Color badgeColor;
    private final boolean supportsHighlighting;

    FileType(String displayName, String defaultExtension, Color badgeColor, boolean supportsHighlighting) {
        this.displayName = displayName;
        this.defaultExtension = defaultExtension;
        this.badgeColor = badgeColor;
        this.supportsHighlighting = supportsHighlighting;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultExtension() {
        return defaultExtension;
    }

    public Color getBadgeColor() {
        return badgeColor;
    }

    public boolean supportsHighlighting() {
        return supportsHighlighting;
    }

    public static FileType fromFileName(String filename) {
        if (filename == null) return PLAINTEXT;
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0 || lastDot == filename.length() - 1) return PLAINTEXT;
        String ext = filename.substring(lastDot + 1).toLowerCase();

        return switch (ext) {
            case "java" -> JAVA;
            case "py", "pyw" -> PYTHON;
            case "js", "jsx", "mjs" -> JAVASCRIPT;
            case "ts", "tsx" -> TYPESCRIPT;
            case "json" -> JSON;
            case "md", "markdown" -> MARKDOWN;
            case "html", "htm" -> HTML;
            case "css", "scss", "sass" -> CSS;
            case "sql" -> SQL;
            case "sh", "bash", "zsh" -> SHELL;
            case "c", "cpp", "h", "hpp", "cc" -> CPP;
            case "xml", "svg" -> XML;
            default -> PLAINTEXT;
        };
    }
}
