package com.calendarmaker.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Date calculation and formatting utilities.
 */
public final class DateUtils {

    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter PRETTY_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
    public static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    private DateUtils() {}

    /**
     * Formats a date as YYYY-MM-DD.
     */
    public static String formatIso(LocalDate date) {
        return date.format(ISO_FORMATTER);
    }

    /**
     * Formats a date nicely (e.g., "October 18, 2026").
     */
    public static String formatPretty(LocalDate date) {
        return date.format(PRETTY_FORMATTER);
    }

    /**
     * Formats YearMonth as "Month YYYY" (e.g., "October 2026").
     */
    public static String formatMonthYear(YearMonth ym) {
        return ym.format(MONTH_YEAR_FORMATTER);
    }

    /**
     * Computes the N-th occurrence of a day of week in a given month/year.
     * If n == -1, finds the LAST occurrence of that day of week in the month.
     * Example: 4th Thursday in November, or last Monday in May.
     */
    public static LocalDate getNthDayOfWeek(int year, Month month, DayOfWeek targetDay, int n) {
        YearMonth ym = YearMonth.of(year, month);
        if (n == -1) {
            // Find last occurrence
            LocalDate lastDay = ym.atEndOfMonth();
            int diff = (lastDay.getDayOfWeek().getValue() - targetDay.getValue() + 7) % 7;
            return lastDay.minusDays(diff);
        }

        LocalDate firstDay = ym.atDay(1);
        int diff = (targetDay.getValue() - firstDay.getDayOfWeek().getValue() + 7) % 7;
        LocalDate firstOccurrence = firstDay.plusDays(diff);
        return firstOccurrence.plusWeeks(n - 1);
    }

    /**
     * Returns ordered list of 7 days of the week starting from startOfWeek.
     */
    public static List<DayOfWeek> getWeekDays(DayOfWeek startOfWeek) {
        List<DayOfWeek> days = new ArrayList<>(7);
        DayOfWeek current = startOfWeek;
        for (int i = 0; i < 7; i++) {
            days.add(current);
            current = current.plus(1);
        }
        return days;
    }

    /**
     * Returns standard 3-letter abbreviation for DayOfWeek (e.g. "Sun", "Mon").
     */
    public static String getDayAbbr(DayOfWeek day) {
        return day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }

    /**
     * Centers text within specified width, considering ANSI codes.
     */
    public static String center(String text, int width) {
        int visibleLen = AnsiColor.visibleLength(text);
        if (visibleLen >= width) {
            return text;
        }
        int totalPad = width - visibleLen;
        int leftPad = totalPad / 2;
        int rightPad = totalPad - leftPad;
        return " ".repeat(leftPad) + text + " ".repeat(rightPad);
    }

    /**
     * Left pads text to fixed width considering visual length.
     */
    public static String padLeft(String text, int width) {
        int visibleLen = AnsiColor.visibleLength(text);
        if (visibleLen >= width) {
            return text;
        }
        return " ".repeat(width - visibleLen) + text;
    }

    /**
     * Right pads text to fixed width considering visual length.
     */
    public static String padRight(String text, int width) {
        int visibleLen = AnsiColor.visibleLength(text);
        if (visibleLen >= width) {
            return text;
        }
        return text + " ".repeat(width - visibleLen);
    }
}
