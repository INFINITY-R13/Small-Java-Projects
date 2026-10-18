package com.calendarmaker.generator;

import com.calendarmaker.model.CalendarEvent;
import com.calendarmaker.util.DateUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Renders GitHub Flavored Markdown (GFM) calendar tables and event lists.
 */
public class MarkdownCalendarRenderer implements CalendarRenderer {

    @Override
    public String getFormatName() {
        return "markdown";
    }

    @Override
    public String getFileExtension() {
        return "md";
    }

    @Override
    public String renderMonth(YearMonth month, List<CalendarEvent> events, DayOfWeek weekStart) {
        StringBuilder sb = new StringBuilder();
        String title = DateUtils.formatMonthYear(month);

        sb.append("# 📅 Calendar: ").append(title).append("\n\n");
        sb.append(renderMonthMarkdownTable(month, weekStart, groupEventsForYear(events, month.getYear())));

        if (events != null && !events.isEmpty()) {
            sb.append("\n### 📌 Events & Holidays\n\n");
            for (CalendarEvent e : events) {
                LocalDate displayDate = e.isRecurringAnnual() ? LocalDate.of(month.getYear(), e.getDate().getMonth(), e.getDate().getDayOfMonth()) : e.getDate();
                sb.append(String.format("- **%s** `%s` %s %s%s\n",
                        DateUtils.formatIso(displayDate),
                        e.getCategory().getDisplayName(),
                        e.getCategory().getIcon(),
                        e.getTitle(),
                        e.getDescription().isEmpty() ? "" : " - *" + e.getDescription() + "*"));
            }
        }
        return sb.toString();
    }

    @Override
    public String renderYear(int year, List<CalendarEvent> events, DayOfWeek weekStart) {
        StringBuilder sb = new StringBuilder();
        sb.append("# 🗓️ Calendar: ").append(year).append("\n\n");

        Map<LocalDate, List<CalendarEvent>> eventMap = groupEventsForYear(events, year);

        for (int m = 1; m <= 12; m++) {
            YearMonth ym = YearMonth.of(year, m);
            sb.append("## ").append(ym.getMonth().name()).append(" ").append(year).append("\n\n");
            sb.append(renderMonthMarkdownTable(ym, weekStart, eventMap));
            sb.append("\n");
        }

        if (events != null && !events.isEmpty()) {
            sb.append("## 📋 Annual Agenda & Holidays (").append(events.size()).append(")\n\n");
            sb.append("| Date | Category | Event | Details |\n");
            sb.append("|:---|:---|:---|:---|\n");
            for (CalendarEvent e : events) {
                LocalDate displayDate = e.isRecurringAnnual() ? LocalDate.of(year, e.getDate().getMonth(), e.getDate().getDayOfMonth()) : e.getDate();
                sb.append(String.format("| `%s` | %s %s | **%s** | %s |\n",
                        DateUtils.formatIso(displayDate),
                        e.getCategory().getIcon(),
                        e.getCategory().getDisplayName(),
                        e.getTitle(),
                        e.getDescription().isEmpty() ? "-" : e.getDescription()));
            }
        }

        return sb.toString();
    }

    private String renderMonthMarkdownTable(YearMonth ym, DayOfWeek weekStart, Map<LocalDate, List<CalendarEvent>> eventMap) {
        StringBuilder sb = new StringBuilder();
        List<DayOfWeek> days = DateUtils.getWeekDays(weekStart);

        // Header
        sb.append("| ");
        for (DayOfWeek d : days) {
            sb.append(d.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)).append(" | ");
        }
        sb.append("\n");

        // Separator
        sb.append("|");
        for (int i = 0; i < 7; i++) {
            sb.append(":---:|");
        }
        sb.append("\n");

        LocalDate firstOfMonth = ym.atDay(1);
        int daysInMonth = ym.lengthOfMonth();
        int startOffset = (firstOfMonth.getDayOfWeek().getValue() - weekStart.getValue() + 7) % 7;

        int currentDay = 1;
        while (currentDay <= daysInMonth) {
            sb.append("| ");
            for (int col = 0; col < 7; col++) {
                if (currentDay == 1 && col < startOffset) {
                    sb.append("  | ");
                } else if (currentDay > daysInMonth) {
                    sb.append("  | ");
                } else {
                    LocalDate date = ym.atDay(currentDay);
                    boolean hasEvent = eventMap.containsKey(date);
                    String dayStr = String.valueOf(currentDay);
                    if (hasEvent) {
                        dayStr = "**" + dayStr + "* **";
                    }
                    sb.append(dayStr).append(" | ");
                    currentDay++;
                }
            }
            sb.append("\n");
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
