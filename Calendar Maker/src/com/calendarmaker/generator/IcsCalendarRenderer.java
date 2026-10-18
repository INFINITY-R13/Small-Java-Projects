package com.calendarmaker.generator;

import com.calendarmaker.model.CalendarEvent;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generates RFC 5545 compliant iCalendar (.ics) format compatible with
 * Google Calendar, Apple Calendar, and Microsoft Outlook.
 */
public class IcsCalendarRenderer implements CalendarRenderer {

    private static final DateTimeFormatter ICS_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter ICS_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    @Override
    public String getFormatName() {
        return "ics";
    }

    @Override
    public String getFileExtension() {
        return "ics";
    }

    @Override
    public String renderMonth(YearMonth month, List<CalendarEvent> events, DayOfWeek weekStart) {
        return renderIcs(events, "Calendar - " + month, month.getYear());
    }

    @Override
    public String renderYear(int year, List<CalendarEvent> events, DayOfWeek weekStart) {
        return renderIcs(events, "Calendar - " + year, year);
    }

    private String renderIcs(List<CalendarEvent> events, String calName, int year) {
        StringBuilder sb = new StringBuilder();
        String dtstamp = ZonedDateTime.now(ZoneOffset.UTC).format(ICS_TIMESTAMP);

        sb.append("BEGIN:VCALENDAR\r\n");
        sb.append("VERSION:2.0\r\n");
        sb.append("PRODID:-//CalendarMaker//EN\r\n");
        sb.append("CALSCALE:GREGORIAN\r\n");
        sb.append("METHOD:PUBLISH\r\n");
        sb.append("X-WR-CALNAME:").append(calName).append("\r\n");

        if (events != null) {
            for (CalendarEvent event : events) {
                LocalDate start = event.isRecurringAnnual() ? LocalDate.of(year, event.getDate().getMonth(), event.getDate().getDayOfMonth()) : event.getDate();
                LocalDate end = start.plusDays(1); // All-day events in ICS have non-inclusive end date

                sb.append("BEGIN:VEVENT\r\n");
                sb.append("UID:").append(event.getId()).append("@calendarmaker\r\n");
                sb.append("DTSTAMP:").append(dtstamp).append("\r\n");
                sb.append("DTSTART;VALUE=DATE:").append(start.format(ICS_DATE)).append("\r\n");
                sb.append("DTEND;VALUE=DATE:").append(end.format(ICS_DATE)).append("\r\n");
                sb.append("SUMMARY:").append(escapeIcsText(event.getTitle())).append("\r\n");
                if (!event.getDescription().isEmpty()) {
                    sb.append("DESCRIPTION:").append(escapeIcsText(event.getDescription())).append("\r\n");
                }
                sb.append("CATEGORIES:").append(event.getCategory().getDisplayName().toUpperCase()).append("\r\n");

                if (event.isRecurringAnnual()) {
                    sb.append("RRULE:FREQ=YEARLY\r\n");
                }

                sb.append("END:VEVENT\r\n");
            }
        }

        sb.append("END:VCALENDAR\r\n");
        return sb.toString();
    }

    private String escapeIcsText(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
