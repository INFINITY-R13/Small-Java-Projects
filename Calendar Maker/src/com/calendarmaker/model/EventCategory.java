package com.calendarmaker.model;

import com.calendarmaker.util.AnsiColor;

/**
 * Event categories with visual attributes (colors, icons, hex codes).
 */
public enum EventCategory {
    HOLIDAY("Holiday", "🎉", AnsiColor.BRIGHT_RED, "#e53e3e"),
    BIRTHDAY("Birthday", "🎂", AnsiColor.BRIGHT_MAGENTA, "#d53f8c"),
    MEETING("Meeting", "💼", AnsiColor.BRIGHT_BLUE, "#3182ce"),
    WORK("Work", "🏢", AnsiColor.CYAN, "#2b6cb0"),
    PERSONAL("Personal", "👤", AnsiColor.BRIGHT_GREEN, "#38a169"),
    REMINDER("Reminder", "⏰", AnsiColor.BRIGHT_YELLOW, "#d69e2e"),
    OTHER("Other", "📌", AnsiColor.WHITE, "#718096");

    private final String displayName;
    private final String icon;
    private final String ansiColor;
    private final String hexColor;

    EventCategory(String displayName, String icon, String ansiColor, String hexColor) {
        this.displayName = displayName;
        this.icon = icon;
        this.ansiColor = ansiColor;
        this.hexColor = hexColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    public String getAnsiColor() {
        return ansiColor;
    }

    public String getHexColor() {
        return hexColor;
    }

    /**
     * Parses a string into an EventCategory, with fallback to OTHER.
     */
    public static EventCategory fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return OTHER;
        }
        String clean = value.trim().toUpperCase();
        for (EventCategory cat : values()) {
            if (cat.name().equalsIgnoreCase(clean) || cat.displayName.equalsIgnoreCase(clean)) {
                return cat;
            }
        }
        return OTHER;
    }
}
