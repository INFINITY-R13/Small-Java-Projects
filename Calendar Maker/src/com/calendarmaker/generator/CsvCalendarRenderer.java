package com.calendarmaker.generator;

import com.calendarmaker.model.CalendarEvent;
import com.calendarmaker.util.DateUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Exports calendar events in CSV format suitable for Excel and Google Sheets.
 */
public class CsvCalendarRenderer implements CalendarRenderer {

    @Override
    public String getFormatName() {
        return "csv";
    }

    @Override
    public String getFileExtension() {
        return "csv";
    }

    @Override
    public String renderMonth(YearMonth month, List<CalendarEvent> events, DayOfWeek weekStart) {
        return renderCsv(events, month.getYear());
    }

    @Override
    public String renderYear(int year, List<CalendarEvent> events, DayOfWeek weekStart) {
        return renderCsv(events, year);
    }

    private String renderCsv(List<CalendarEvent> events, int year) {
        StringBuilder sb = new StringBuilder();
        sb.append("Date,DayOfWeek,Title,Category,AnnualRecurring,Description\n");

        if (events != null) {
            for (CalendarEvent e : events) {
                LocalDate displayDate = e.isRecurringAnnual() ? LocalDate.of(year, e.getDate().getMonth(), e.getDate().getDayOfMonth()) : e.getDate();
                sb.append(escapeCsv(DateUtils.formatIso(displayDate))).append(",");
                sb.append(escapeCsv(displayDate.getDayOfWeek().name())).append(",");
                sb.append(escapeCsv(e.getTitle())).append(",");
                sb.append(escapeCsv(e.getCategory().getDisplayName())).append(",");
                sb.append(e.isRecurringAnnual() ? "Yes" : "No").append(",");
                sb.append(escapeCsv(e.getDescription())).append("\n");
            }
        }

        return sb.toString();
    }

    private String escapeCsv(String field) {
        if (field == null) return "\"\"";
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
