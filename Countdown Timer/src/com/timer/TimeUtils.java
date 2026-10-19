package com.timer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions for parsing time input, formatting durations,
 * and generating visual progress bars for console/GUI display.
 */
public final class TimeUtils {

    // Regex for unit-based durations: "1h 30m 15s", "25m", "90s", "2h"
    private static final Pattern UNIT_PATTERN = Pattern.compile(
            "^(?:(\\d+)\\s*(?:h|hr|hours?))?\\s*(?:(\\d+)\\s*(?:m|min|minutes?))?\\s*(?:(\\d+)\\s*(?:s|sec|seconds?))?$",
            Pattern.CASE_INSENSITIVE
    );

    // ANSI Escape Codes for CLI styling
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String CYAN = "\u001B[36m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    private TimeUtils() {
        // Utility class
    }

    /**
     * Parses user input into total seconds.
     * Supported formats:
     * - Pure number: "90" -> 90 seconds
     * - Colon formats: "25:00" -> 1500 seconds, "01:30:00" -> 5400 seconds
     * - Unit strings: "1h 20m 30s", "15m", "45s", "2 hours"
     *
     * @param input Raw string entered by the user
     * @return Duration in seconds (always >= 1)
     * @throws IllegalArgumentException if the format is invalid or duration <= 0
     */
    public static long parseDuration(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Duration cannot be empty.");
        }

        String cleaned = input.trim();

        // 1. Check if it's purely a number of seconds
        if (cleaned.matches("^\\d+$")) {
            long sec = Long.parseLong(cleaned);
            if (sec <= 0) {
                throw new IllegalArgumentException("Duration must be greater than 0 seconds.");
            }
            return sec;
        }

        // 2. Colon-separated format: MM:SS or HH:MM:SS
        if (cleaned.contains(":")) {
            String[] parts = cleaned.split(":");
            try {
                if (parts.length == 2) {
                    long minutes = Long.parseLong(parts[0].trim());
                    long seconds = Long.parseLong(parts[1].trim());
                    if (seconds >= 60 || seconds < 0 || minutes < 0) {
                        throw new IllegalArgumentException("Invalid time: seconds must be 0-59.");
                    }
                    long total = minutes * 60 + seconds;
                    if (total <= 0) throw new IllegalArgumentException("Duration must be > 0.");
                    return total;
                } else if (parts.length == 3) {
                    long hours = Long.parseLong(parts[0].trim());
                    long minutes = Long.parseLong(parts[1].trim());
                    long seconds = Long.parseLong(parts[2].trim());
                    if (minutes >= 60 || minutes < 0 || seconds >= 60 || seconds < 0 || hours < 0) {
                        throw new IllegalArgumentException("Invalid time: minutes and seconds must be 0-59.");
                    }
                    long total = hours * 3600 + minutes * 60 + seconds;
                    if (total <= 0) throw new IllegalArgumentException("Duration must be > 0.");
                    return total;
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numbers in time format (use MM:SS or HH:MM:SS).");
            }
        }

        // 3. Unit-based string: e.g. "1h 30m 10s", "10m", "45s"
        Matcher matcher = UNIT_PATTERN.matcher(cleaned);
        if (matcher.matches()) {
            String hStr = matcher.group(1);
            String mStr = matcher.group(2);
            String sStr = matcher.group(3);

            if (hStr == null && mStr == null && sStr == null) {
                throw new IllegalArgumentException("Could not parse time format: \"" + input + "\"");
            }

            long hours = (hStr != null) ? Long.parseLong(hStr) : 0;
            long minutes = (mStr != null) ? Long.parseLong(mStr) : 0;
            long seconds = (sStr != null) ? Long.parseLong(sStr) : 0;

            long total = hours * 3600 + minutes * 60 + seconds;
            if (total <= 0) {
                throw new IllegalArgumentException("Total duration must be greater than 0.");
            }
            return total;
        }

        throw new IllegalArgumentException(
                "Invalid duration format: \"" + input + "\". Examples of valid inputs: '90', '25:00', '01:30:00', '5m', '1h 30m'."
        );
    }

    /**
     * Formats seconds into HH:MM:SS.
     */
    public static String formatHHMMSS(long totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Formats seconds dynamically:
     * - Returns MM:SS if duration is under an hour
     * - Returns HH:MM:SS if duration is one hour or more
     */
    public static String formatAuto(long totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    /**
     * Builds an ASCII / Unicode progress bar.
     *
     * @param remainingSeconds Seconds remaining in countdown.
     * @param totalSeconds     Original total seconds.
     * @param width            Number of character slots for the bar.
     * @return Formatted bar string, e.g. "[██████████░░░░░░░░░░]  50%"
     */
    public static String renderProgressBar(long remainingSeconds, long totalSeconds, int width) {
        if (totalSeconds <= 0) totalSeconds = 1;
        if (remainingSeconds < 0) remainingSeconds = 0;
        if (remainingSeconds > totalSeconds) remainingSeconds = totalSeconds;

        double progressRatio = 1.0 - ((double) remainingSeconds / (double) totalSeconds);
        int filled = (int) Math.round(progressRatio * width);
        if (filled > width) filled = width;
        int empty = width - filled;

        StringBuilder bar = new StringBuilder();
        bar.append("[");
        for (int i = 0; i < filled; i++) {
            bar.append("█");
        }
        for (int i = 0; i < empty; i++) {
            bar.append("░");
        }
        bar.append("]");

        int percentage = (int) Math.round(progressRatio * 100.0);
        return String.format("%s %3d%%", bar.toString(), percentage);
    }
}
