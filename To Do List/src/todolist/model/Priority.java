package todolist.model;

import java.awt.Color;

/**
 * Priority levels for tasks with associated display styling.
 */
public enum Priority {
    LOW("Low", new Color(46, 125, 50), new Color(232, 245, 233)),
    MEDIUM("Medium", new Color(230, 81, 0), new Color(255, 243, 224)),
    HIGH("High", new Color(198, 40, 40), new Color(255, 235, 238));

    private final String displayName;
    private final Color foregroundColor;
    private final Color backgroundColor;

    Priority(String displayName, Color foregroundColor, Color backgroundColor) {
        this.displayName = displayName;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Priority fromString(String text) {
        if (text == null) {
            return MEDIUM;
        }
        for (Priority p : values()) {
            if (p.name().equalsIgnoreCase(text.trim()) || p.displayName.equalsIgnoreCase(text.trim())) {
                return p;
            }
        }
        return MEDIUM;
    }
}
