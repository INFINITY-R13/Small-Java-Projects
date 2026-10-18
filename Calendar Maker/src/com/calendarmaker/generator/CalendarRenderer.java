package com.calendarmaker.generator;

import com.calendarmaker.model.CalendarEvent;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.List;

/**
 * Interface for calendar output renderers.
 */
public interface CalendarRenderer {

    /**
     * Renders a single month calendar view.
     */
    String renderMonth(YearMonth month, List<CalendarEvent> events, DayOfWeek weekStart);

    /**
     * Renders a full year calendar view.
     */
    String renderYear(int year, List<CalendarEvent> events, DayOfWeek weekStart);

    /**
     * Short identifier for format (e.g. "console", "html", "markdown", "text", "ics", "csv").
     */
    String getFormatName();

    /**
     * Default file extension (e.g. "html", "md", "txt", "ics", "csv").
     */
    String getFileExtension();
}
