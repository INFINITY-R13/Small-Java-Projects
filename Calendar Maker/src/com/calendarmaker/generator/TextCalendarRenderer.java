package com.calendarmaker.generator;

import com.calendarmaker.model.CalendarEvent;
import com.calendarmaker.util.DateUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Plain ASCII monospaced text calendar renderer suitable for text files.
 */
public class TextCalendarRenderer implements CalendarRenderer {

    @Override
    public String getFormatName() {
        return "text";
    }

    @Override
    public String getFileExtension() {
        return "txt";
    }

    @Override
    public String renderMonth(YearMonth month, List<CalendarEvent> events, DayOfWeek weekStart) {
        StringBuilder sb = new StringBuilder();
        Map<LocalDate, List<CalendarEvent>> eventMap = groupEventsForYear(events, month.getYear());

        String title = DateUtils.formatMonthYear(month).toUpperCase();
        int cellWidth = 6;
        int totalWidth = (cellWidth * 7) + 8;

        sb.append("+").append("-".repeat(totalWidth - 2)).append("+\n");
        sb.append("|").append(DateUtils.center(title, totalWidth - 2)).append("|\n");
        sb.append(tableDivider(cellWidth)).append("\n");

        List<DayOfWeek> days = DateUtils.getWeekDays(weekStart);
        sb.append("|");
        for (DayOfWeek d : days) {
            sb.append(DateUtils.center(d.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), cellWidth)).append("|");
        }
        sb.append("\n");
        sb.append(tableDivider(cellWidth)).append("\n");

        LocalDate firstOfMonth = month.atDay(1);
        int daysInMonth = month.lengthOfMonth();
        int startOffset = (firstOfMonth.getDayOfWeek().getValue() - weekStart.getValue() + 7) % 7;

        int currentDay = 1;
        while (currentDay <= daysInMonth) {
            sb.append("|");
            for (int col = 0; col < 7; col++) {
                if (currentDay == 1 && col < startOffset) {
                    sb.append(" ".repeat(cellWidth)).append("|");
                } else if (currentDay > daysInMonth) {
                    sb.append(" ".repeat(cellWidth)).append("|");
                } else {
                    LocalDate date = month.atDay(currentDay);
                    String dayStr = String.valueOf(currentDay);
                    if (eventMap.containsKey(date)) {
                        dayStr += "*";
                    }
                    sb.append(DateUtils.center(dayStr, cellWidth)).append("|");
                    currentDay++;
                }
            }
            sb.append("\n");
            if (currentDay <= daysInMonth) {
                sb.append(tableDivider(cellWidth)).append("\n");
            }
        }
        sb.append("+").append("-".repeat(totalWidth - 2)).append("+\n");

        if (events != null && !events.isEmpty()) {
            sb.append("\n=== EVENTS & REMINDERS (").append(events.size()).append(") ===\n");
            for (CalendarEvent e : events) {
                LocalDate displayDate = e.isRecurringAnnual() ? LocalDate.of(month.getYear(), e.getDate().getMonth(), e.getDate().getDayOfMonth()) : e.getDate();
                sb.append(String.format(" * %s [%s] %s%s%s\n",
                        DateUtils.formatIso(displayDate),
                        e.getCategory().getDisplayName(),
                        e.getTitle(),
                        e.isRecurringAnnual() ? " (Annual)" : "",
                        e.getDescription().isEmpty() ? "" : " - " + e.getDescription()));
            }
        }

        return sb.toString();
    }

    @Override
    public String renderYear(int year, List<CalendarEvent> events, DayOfWeek weekStart) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append(DateUtils.center("CALENDAR " + year, 72)).append("\n");
        sb.append("========================================================================\n\n");

        for (int m = 1; m <= 12; m++) {
            YearMonth ym = YearMonth.of(year, m);
            List<CalendarEvent> monthEvents = filterEventsForMonth(events, ym);
            sb.append(renderMonth(ym, monthEvents, weekStart));
            sb.append("\n\n");
        }

        return sb.toString();
    }

    private List<CalendarEvent> filterEventsForMonth(List<CalendarEvent> events, YearMonth ym) {
        if (events == null) return Collections.emptyList();
        List<CalendarEvent> list = new ArrayList<>();
        for (CalendarEvent e : events) {
            if (e.isRecurringAnnual()) {
                if (e.getDate().getMonth() == ym.getMonth()) list.add(e);
            } else if (e.getDate().getYear() == ym.getYear() && e.getDate().getMonth() == ym.getMonth()) {
                list.add(e);
            }
        }
        return list;
    }

    private String tableDivider(int cellWidth) {
        StringBuilder sb = new StringBuilder("+");
        for (int i = 0; i < 7; i++) {
            sb.append("-".repeat(cellWidth)).append("+");
        }
        return sb.toString();
    }

    private Map<LocalDate, List<CalendarEvent>> groupEventsForYear(List<CalendarEvent> events, int targetYear) {
        Map<LocalDate, List<CalendarEvent>> map = new HashMap<>();
        if (events == null) return map;
        for (CalendarEvent e : events) {
            LocalDate effectiveDate;
            if (e.isRecurringAnnual()) {
                effectiveDate = LocalDate.of(targetYear, e.getDate().getMonth(), e.getDate().getDayOfMonth());
            } else {
                effectiveDate = e.getDate();
            }
            map.computeIfAbsent(effectiveDate, k -> new ArrayList<>()).add(e);
        }
        return map;
    }
}
