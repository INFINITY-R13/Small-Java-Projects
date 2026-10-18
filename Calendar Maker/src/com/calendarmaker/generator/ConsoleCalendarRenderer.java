package com.calendarmaker.generator;

import com.calendarmaker.model.CalendarEvent;
import com.calendarmaker.util.AnsiColor;
import com.calendarmaker.util.DateUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Terminal renderer supporting Unicode box-drawing, ANSI colors, and event indicators.
 */
public class ConsoleCalendarRenderer implements CalendarRenderer {

    @Override
    public String getFormatName() {
        return "console";
    }

    @Override
    public String getFileExtension() {
        return "txt";
    }

    @Override
    public String renderMonth(YearMonth month, List<CalendarEvent> events, DayOfWeek weekStart) {
        StringBuilder sb = new StringBuilder();
        LocalDate today = LocalDate.now();

        // Map events by date for fast lookup
        Map<LocalDate, List<CalendarEvent>> eventMap = groupEventsForYear(events, month.getYear());

        String title = DateUtils.formatMonthYear(month).toUpperCase();
        int cellWidth = 7;
        int totalWidth = (cellWidth * 7) + 8; // 57 chars

        // Top border
        sb.append(boxTop(totalWidth)).append("\n");
        sb.append("│").append(DateUtils.center(AnsiColor.colorize(title, AnsiColor.BOLD + AnsiColor.BRIGHT_CYAN), totalWidth - 2)).append("│\n");
        sb.append(boxDivider(cellWidth)).append("\n");

        // Weekday headers
        List<DayOfWeek> days = DateUtils.getWeekDays(weekStart);
        sb.append("│");
        for (DayOfWeek dow : days) {
            String dayName = dow.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String styledName = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY)
                    ? AnsiColor.colorize(dayName, AnsiColor.BRIGHT_MAGENTA)
                    : AnsiColor.colorize(dayName, AnsiColor.BOLD);
            sb.append(DateUtils.center(styledName, cellWidth)).append("│");
        }
        sb.append("\n");
        sb.append(boxCrossDivider(cellWidth)).append("\n");

        // Days grid
        LocalDate firstOfMonth = month.atDay(1);
        int daysInMonth = month.lengthOfMonth();

        int startOffset = (firstOfMonth.getDayOfWeek().getValue() - weekStart.getValue() + 7) % 7;

        int currentDay = 1;
        while (currentDay <= daysInMonth) {
            sb.append("│");
            for (int col = 0; col < 7; col++) {
                if (currentDay == 1 && col < startOffset) {
                    sb.append(" ".repeat(cellWidth)).append("│");
                } else if (currentDay > daysInMonth) {
                    sb.append(" ".repeat(cellWidth)).append("│");
                } else {
                    LocalDate date = month.atDay(currentDay);
                    List<CalendarEvent> dayEvents = eventMap.getOrDefault(date, Collections.emptyList());

                    String dayStr = String.valueOf(currentDay);
                    if (!dayEvents.isEmpty()) {
                        dayStr += "*";
                    }

                    // Styling
                    if (date.equals(today)) {
                        dayStr = AnsiColor.colorize(dayStr, AnsiColor.BOLD + AnsiColor.BRIGHT_YELLOW + AnsiColor.UNDERLINE);
                    } else if (!dayEvents.isEmpty()) {
                        dayStr = AnsiColor.colorize(dayStr, dayEvents.get(0).getCategory().getAnsiColor() + AnsiColor.BOLD);
                    } else if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        dayStr = AnsiColor.colorize(dayStr, AnsiColor.BRIGHT_BLACK);
                    }

                    sb.append(DateUtils.center(dayStr, cellWidth)).append("│");
                    currentDay++;
                }
            }
            sb.append("\n");
            if (currentDay <= daysInMonth) {
                sb.append(boxRowDivider(cellWidth)).append("\n");
            }
        }
        sb.append(boxBottom(cellWidth)).append("\n");

        // Event listing section
        if (events != null && !events.isEmpty()) {
            sb.append("\n");
            sb.append(AnsiColor.colorize("── EVENTS & REMINDERS (" + events.size() + ") ──", AnsiColor.BOLD + AnsiColor.BRIGHT_YELLOW)).append("\n");
            for (CalendarEvent event : events) {
                LocalDate displayDate = event.isRecurringAnnual() ? LocalDate.of(month.getYear(), event.getDate().getMonth(), event.getDate().getDayOfMonth()) : event.getDate();
                String catBadge = AnsiColor.colorize("[" + event.getCategory().getIcon() + " " + event.getCategory().getDisplayName() + "]", event.getCategory().getAnsiColor());
                String dateStr = AnsiColor.colorize(DateUtils.formatIso(displayDate), AnsiColor.DIM);
                String titleStr = AnsiColor.colorize(event.getTitle(), AnsiColor.BOLD);
                String recur = event.isRecurringAnnual() ? AnsiColor.colorize("(Annual)", AnsiColor.BRIGHT_BLACK) : "";
                String desc = event.getDescription().isEmpty() ? "" : AnsiColor.colorize(" - " + event.getDescription(), AnsiColor.DIM);

                sb.append(String.format(" • %s %s: %s %s%s\n", dateStr, catBadge, titleStr, recur, desc).trim()).append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String renderYear(int year, List<CalendarEvent> events, DayOfWeek weekStart) {
        StringBuilder sb = new StringBuilder();
        String yearHeader = "=== " + year + " CALENDAR ===";
        sb.append("\n").append(AnsiColor.colorize(DateUtils.center(yearHeader, 72), AnsiColor.BOLD + AnsiColor.BRIGHT_CYAN)).append("\n\n");

        Map<LocalDate, List<CalendarEvent>> eventMap = groupEventsForYear(events, year);
        List<DayOfWeek> days = DateUtils.getWeekDays(weekStart);

        // Render in 3 columns of 4 rows of months
        for (int row = 0; row < 4; row++) {
            int m1 = row * 3 + 1;
            int m2 = row * 3 + 2;
            int m3 = row * 3 + 3;

            YearMonth ym1 = YearMonth.of(year, m1);
            YearMonth ym2 = YearMonth.of(year, m2);
            YearMonth ym3 = YearMonth.of(year, m3);

            // Month titles
            String title1 = DateUtils.center(ym1.getMonth().name(), 22);
            String title2 = DateUtils.center(ym2.getMonth().name(), 22);
            String title3 = DateUtils.center(ym3.getMonth().name(), 22);
            sb.append(AnsiColor.colorize(title1, AnsiColor.BOLD + AnsiColor.BRIGHT_YELLOW)).append("  ")
              .append(AnsiColor.colorize(title2, AnsiColor.BOLD + AnsiColor.BRIGHT_YELLOW)).append("  ")
              .append(AnsiColor.colorize(title3, AnsiColor.BOLD + AnsiColor.BRIGHT_YELLOW)).append("\n");

            // Weekday headers
            String dowHeader = buildMiniDowHeader(days);
            sb.append(dowHeader).append("  ").append(dowHeader).append("  ").append(dowHeader).append("\n");

            // Days grids (up to 6 weeks per month)
            List<String> lines1 = buildMiniMonthLines(ym1, weekStart, eventMap);
            List<String> lines2 = buildMiniMonthLines(ym2, weekStart, eventMap);
            List<String> lines3 = buildMiniMonthLines(ym3, weekStart, eventMap);

            int maxLines = Math.max(lines1.size(), Math.max(lines2.size(), lines3.size()));
            for (int lineIdx = 0; lineIdx < maxLines; lineIdx++) {
                String l1 = lineIdx < lines1.size() ? lines1.get(lineIdx) : " ".repeat(22);
                String l2 = lineIdx < lines2.size() ? lines2.get(lineIdx) : " ".repeat(22);
                String l3 = lineIdx < lines3.size() ? lines3.get(lineIdx) : " ".repeat(22);
                sb.append(l1).append("  ").append(l2).append("  ").append(l3).append("\n");
            }
            sb.append("\n");
        }

        // Year events overview
        if (events != null && !events.isEmpty()) {
            sb.append(AnsiColor.colorize("── " + year + " EVENTS & HOLIDAYS AGENDA (" + events.size() + ") ──", AnsiColor.BOLD + AnsiColor.BRIGHT_YELLOW)).append("\n");
            for (CalendarEvent e : events) {
                LocalDate displayDate = e.isRecurringAnnual() ? LocalDate.of(year, e.getDate().getMonth(), e.getDate().getDayOfMonth()) : e.getDate();
                String catBadge = AnsiColor.colorize("[" + e.getCategory().getDisplayName() + "]", e.getCategory().getAnsiColor());
                sb.append(String.format(" • %s %s %s%s\n",
                        DateUtils.formatIso(displayDate),
                        catBadge,
                        e.getTitle(),
                        e.getDescription().isEmpty() ? "" : " (" + e.getDescription() + ")"));
            }
        }

        return sb.toString();
    }

    private String buildMiniDowHeader(List<DayOfWeek> days) {
        StringBuilder sb = new StringBuilder();
        for (DayOfWeek d : days) {
            String shortName = d.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).substring(0, 2);
            sb.append(DateUtils.padLeft(shortName, 3));
        }
        sb.append(" ");
        return sb.toString();
    }

    private List<String> buildMiniMonthLines(YearMonth ym, DayOfWeek weekStart, Map<LocalDate, List<CalendarEvent>> eventMap) {
        List<String> lines = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int daysInMonth = ym.lengthOfMonth();
        LocalDate first = ym.atDay(1);
        int offset = (first.getDayOfWeek().getValue() - weekStart.getValue() + 7) % 7;

        StringBuilder currentLine = new StringBuilder();
        for (int i = 0; i < offset; i++) {
            currentLine.append("   ");
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = ym.atDay(day);
            String dayStr = String.valueOf(day);
            boolean hasEvent = eventMap.containsKey(date);

            if (date.equals(today)) {
                dayStr = AnsiColor.colorize(DateUtils.padLeft(dayStr, 3), AnsiColor.BOLD + AnsiColor.BRIGHT_YELLOW + AnsiColor.UNDERLINE);
            } else if (hasEvent) {
                dayStr = AnsiColor.colorize(DateUtils.padLeft(dayStr, 3), AnsiColor.BOLD + AnsiColor.BRIGHT_RED);
            } else {
                dayStr = DateUtils.padLeft(dayStr, 3);
            }
            currentLine.append(dayStr);

            if ((offset + day) % 7 == 0 || day == daysInMonth) {
                // End of line pad
                if (day == daysInMonth && (offset + day) % 7 != 0) {
                    int rem = 7 - ((offset + day) % 7);
                    currentLine.append("   ".repeat(rem));
                }
                currentLine.append(" ");
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
        }

        return lines;
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

    private String boxTop(int totalWidth) {
        return "┌" + "─".repeat(totalWidth - 2) + "┐";
    }

    private String boxDivider(int cellWidth) {
        StringBuilder sb = new StringBuilder("├");
        for (int i = 0; i < 7; i++) {
            sb.append("─".repeat(cellWidth));
            sb.append(i == 6 ? "┤" : "┬");
        }
        return sb.toString();
    }

    private String boxCrossDivider(int cellWidth) {
        StringBuilder sb = new StringBuilder("├");
        for (int i = 0; i < 7; i++) {
            sb.append("─".repeat(cellWidth));
            sb.append(i == 6 ? "┤" : "┼");
        }
        return sb.toString();
    }

    private String boxRowDivider(int cellWidth) {
        StringBuilder sb = new StringBuilder("├");
        for (int i = 0; i < 7; i++) {
            sb.append("─".repeat(cellWidth));
            sb.append(i == 6 ? "┤" : "┼");
        }
        return sb.toString();
    }

    private String boxBottom(int cellWidth) {
        StringBuilder sb = new StringBuilder("└");
        for (int i = 0; i < 7; i++) {
            sb.append("─".repeat(cellWidth));
            sb.append(i == 6 ? "┘" : "┴");
        }
        return sb.toString();
    }
}
