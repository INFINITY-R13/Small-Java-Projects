package com.calendarmaker.generator;

import com.calendarmaker.model.CalendarEvent;
import com.calendarmaker.util.DateUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Generates modern, responsive, printer-friendly HTML calendars.
 */
public class HtmlCalendarRenderer implements CalendarRenderer {

    @Override
    public String getFormatName() {
        return "html";
    }

    @Override
    public String getFileExtension() {
        return "html";
    }

    @Override
    public String renderMonth(YearMonth month, List<CalendarEvent> events, DayOfWeek weekStart) {
        StringBuilder sb = new StringBuilder();
        Map<LocalDate, List<CalendarEvent>> eventMap = groupEventsForYear(events, month.getYear());

        String title = DateUtils.formatMonthYear(month);

        sb.append(htmlHeader(title));
        sb.append("<div class=\"container\">\n");
        sb.append("  <header class=\"cal-header\">\n");
        sb.append("    <h1>").append(escapeHtml(title)).append("</h1>\n");
        sb.append("    <div class=\"actions no-print\">\n");
        sb.append("      <button onclick=\"window.print()\" class=\"btn\">🖨️ Print Calendar</button>\n");
        sb.append("      <button onclick=\"toggleTheme()\" class=\"btn\" id=\"themeBtn\">🌓 Dark Mode</button>\n");
        sb.append("    </div>\n");
        sb.append("  </header>\n");

        sb.append(renderMonthGrid(month, weekStart, eventMap));

        // Agenda / Events List
        if (events != null && !events.isEmpty()) {
            sb.append(renderEventList(events, "Events & Holidays in " + title, month.getYear()));
        }

        sb.append("</div>\n");
        sb.append(htmlFooter());
        return sb.toString();
    }

    @Override
    public String renderYear(int year, List<CalendarEvent> events, DayOfWeek weekStart) {
        StringBuilder sb = new StringBuilder();
        Map<LocalDate, List<CalendarEvent>> eventMap = groupEventsForYear(events, year);

        String title = "Calendar " + year;
        sb.append(htmlHeader(title));
        sb.append("<div class=\"container year-container\">\n");
        sb.append("  <header class=\"cal-header\">\n");
        sb.append("    <h1>").append(year).append(" Calendar</h1>\n");
        sb.append("    <div class=\"actions no-print\">\n");
        sb.append("      <button onclick=\"window.print()\" class=\"btn\">🖨️ Print Year Calendar</button>\n");
        sb.append("      <button onclick=\"toggleTheme()\" class=\"btn\" id=\"themeBtn\">🌓 Dark Mode</button>\n");
        sb.append("    </div>\n");
        sb.append("  </header>\n");

        sb.append("  <div class=\"year-grid\">\n");
        for (int m = 1; m <= 12; m++) {
            YearMonth ym = YearMonth.of(year, m);
            sb.append("    <div class=\"mini-month-card\">\n");
            sb.append("      <h2>").append(ym.getMonth().name()).append("</h2>\n");
            sb.append(renderMonthGrid(ym, weekStart, eventMap));
            sb.append("    </div>\n");
        }
        sb.append("  </div>\n");

        if (events != null && !events.isEmpty()) {
            sb.append(renderEventList(events, year + " Annual Agenda & Holidays", year));
        }

        sb.append("</div>\n");
        sb.append(htmlFooter());
        return sb.toString();
    }

    private String renderMonthGrid(YearMonth ym, DayOfWeek weekStart, Map<LocalDate, List<CalendarEvent>> eventMap) {
        StringBuilder sb = new StringBuilder();
        LocalDate today = LocalDate.now();
        List<DayOfWeek> days = DateUtils.getWeekDays(weekStart);

        sb.append("  <div class=\"calendar-grid\">\n");

        // Headers
        for (DayOfWeek dow : days) {
            String shortName = dow.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            sb.append("    <div class=\"cal-day-header\">").append(shortName).append("</div>\n");
        }

        LocalDate firstOfMonth = ym.atDay(1);
        int daysInMonth = ym.lengthOfMonth();
        int startOffset = (firstOfMonth.getDayOfWeek().getValue() - weekStart.getValue() + 7) % 7;

        // Empty cells before start of month
        for (int i = 0; i < startOffset; i++) {
            sb.append("    <div class=\"cal-day empty-cell\"></div>\n");
        }

        // Days of month
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = ym.atDay(day);
            List<CalendarEvent> dayEvents = eventMap.getOrDefault(date, Collections.emptyList());

            boolean isToday = date.equals(today);
            boolean isWeekend = (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);

            StringBuilder cellClass = new StringBuilder("cal-day");
            if (isToday) cellClass.append(" today");
            if (isWeekend) cellClass.append(" weekend");
            if (!dayEvents.isEmpty()) cellClass.append(" has-events");

            sb.append("    <div class=\"").append(cellClass).append("\">\n");
            sb.append("      <div class=\"day-number\">").append(day).append("</div>\n");

            if (!dayEvents.isEmpty()) {
                sb.append("      <div class=\"events-container\">\n");
                for (CalendarEvent e : dayEvents) {
                    sb.append("        <div class=\"event-chip\" style=\"--chip-color: ")
                      .append(e.getCategory().getHexColor()).append(";\" title=\"")
                      .append(escapeHtml(e.getTitle()))
                      .append(e.getDescription().isEmpty() ? "" : ": " + escapeHtml(e.getDescription()))
                      .append("\">\n");
                    sb.append("          <span class=\"chip-dot\"></span>\n");
                    sb.append("          <span class=\"chip-title\">").append(escapeHtml(e.getTitle())).append("</span>\n");
                    sb.append("        </div>\n");
                }
                sb.append("      </div>\n");
            }

            sb.append("    </div>\n");
        }

        // Trailing empty cells
        int totalCells = startOffset + daysInMonth;
        int remaining = (7 - (totalCells % 7)) % 7;
        for (int i = 0; i < remaining; i++) {
            sb.append("    <div class=\"cal-day empty-cell\"></div>\n");
        }

        sb.append("  </div>\n");
        return sb.toString();
    }

    private String renderEventList(List<CalendarEvent> events, String sectionTitle, int year) {
        StringBuilder sb = new StringBuilder();
        sb.append("  <section class=\"events-section\">\n");
        sb.append("    <h2>").append(escapeHtml(sectionTitle)).append("</h2>\n");
        sb.append("    <div class=\"events-table-wrapper\">\n");
        sb.append("      <table class=\"events-table\">\n");
        sb.append("        <thead>\n");
        sb.append("          <tr>\n");
        sb.append("            <th>Date</th>\n");
        sb.append("            <th>Category</th>\n");
        sb.append("            <th>Event</th>\n");
        sb.append("            <th>Details</th>\n");
        sb.append("          </tr>\n");
        sb.append("        </thead>\n");
        sb.append("        <tbody>\n");

        for (CalendarEvent e : events) {
            LocalDate displayDate = e.isRecurringAnnual() ? LocalDate.of(year, e.getDate().getMonth(), e.getDate().getDayOfMonth()) : e.getDate();
            sb.append("          <tr>\n");
            sb.append("            <td class=\"nowrap\"><strong>").append(DateUtils.formatIso(displayDate)).append("</strong></td>\n");
            sb.append("            <td><span class=\"badge\" style=\"background-color: ")
              .append(e.getCategory().getHexColor()).append("20; color: ")
              .append(e.getCategory().getHexColor()).append(";\">")
              .append(e.getCategory().getIcon()).append(" ")
              .append(e.getCategory().getDisplayName()).append("</span></td>\n");
            sb.append("            <td><strong>").append(escapeHtml(e.getTitle())).append("</strong>");
            if (e.isRecurringAnnual()) {
                sb.append(" <span class=\"subtext\">(Annual)</span>");
            }
            sb.append("</td>\n");
            sb.append("            <td>").append(escapeHtml(e.getDescription())).append("</td>\n");
            sb.append("          </tr>\n");
        }

        sb.append("        </tbody>\n");
        sb.append("      </table>\n");
        sb.append("    </div>\n");
        sb.append("  </section>\n");
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

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String htmlHeader(String pageTitle) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\" data-theme=\"light\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>" + escapeHtml(pageTitle) + " - Calendar Maker</title>\n" +
                "  <style>\n" +
                "    :root {\n" +
                "      --bg-color: #f7fafc;\n" +
                "      --card-bg: #ffffff;\n" +
                "      --text-main: #2d3748;\n" +
                "      --text-muted: #718096;\n" +
                "      --border-color: #e2e8f0;\n" +
                "      --primary-color: #3182ce;\n" +
                "      --primary-light: #ebf8ff;\n" +
                "      --weekend-bg: #fafafa;\n" +
                "      --today-bg: #fffaf0;\n" +
                "      --today-border: #dd6b20;\n" +
                "    }\n" +
                "    [data-theme=\"dark\"] {\n" +
                "      --bg-color: #1a202c;\n" +
                "      --card-bg: #2d3748;\n" +
                "      --text-main: #f7fafc;\n" +
                "      --text-muted: #a0aec0;\n" +
                "      --border-color: #4a5568;\n" +
                "      --primary-color: #63b3ed;\n" +
                "      --primary-light: #2c5282;\n" +
                "      --weekend-bg: #283141;\n" +
                "      --today-bg: #3c3222;\n" +
                "      --today-border: #f6ad55;\n" +
                "    }\n" +
                "    * { box-sizing: border-box; margin: 0; padding: 0; }\n" +
                "    body {\n" +
                "      font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif;\n" +
                "      background-color: var(--bg-color);\n" +
                "      color: var(--text-main);\n" +
                "      line-height: 1.5;\n" +
                "      padding: 24px;\n" +
                "      transition: background-color 0.2s, color 0.2s;\n" +
                "    }\n" +
                "    .container { max-width: 1100px; margin: 0 auto; }\n" +
                "    .year-container { max-width: 1400px; }\n" +
                "    .cal-header {\n" +
                "      display: flex; justify-content: space-between; align-items: center;\n" +
                "      margin-bottom: 24px; padding-bottom: 16px; border-bottom: 2px solid var(--border-color);\n" +
                "    }\n" +
                "    .cal-header h1 { font-size: 2rem; font-weight: 700; }\n" +
                "    .actions { display: flex; gap: 10px; }\n" +
                "    .btn {\n" +
                "      background: var(--card-bg); border: 1px solid var(--border-color);\n" +
                "      color: var(--text-main); padding: 8px 16px; border-radius: 6px;\n" +
                "      cursor: pointer; font-size: 0.95rem; font-weight: 600; transition: all 0.2s;\n" +
                "    }\n" +
                "    .btn:hover { background: var(--primary-light); border-color: var(--primary-color); }\n" +
                "    .calendar-grid {\n" +
                "      display: grid; grid-template-columns: repeat(7, 1fr);\n" +
                "      background: var(--border-color); gap: 1px; border: 1px solid var(--border-color);\n" +
                "      border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.08);\n" +
                "    }\n" +
                "    .cal-day-header {\n" +
                "      background: var(--card-bg); padding: 12px 8px; text-align: center;\n" +
                "      font-weight: 700; font-size: 0.9rem; color: var(--text-muted); text-transform: uppercase;\n" +
                "    }\n" +
                "    .cal-day {\n" +
                "      background: var(--card-bg); min-height: 105px; padding: 8px;\n" +
                "      display: flex; flex-direction: column; transition: background 0.15s;\n" +
                "    }\n" +
                "    .cal-day:hover { background-color: var(--primary-light); }\n" +
                "    .cal-day.weekend { background: var(--weekend-bg); }\n" +
                "    .cal-day.today {\n" +
                "      background: var(--today-bg);\n" +
                "      outline: 2px solid var(--today-border);\n" +
                "      outline-offset: -2px;\n" +
                "    }\n" +
                "    .empty-cell { background: var(--border-color); opacity: 0.3; }\n" +
                "    .day-number { font-weight: 700; font-size: 0.95rem; margin-bottom: 4px; }\n" +
                "    .today .day-number { color: var(--today-border); }\n" +
                "    .events-container { display: flex; flex-direction: column; gap: 4px; margin-top: auto; }\n" +
                "    .event-chip {\n" +
                "      display: flex; align-items: center; gap: 6px; font-size: 0.75rem;\n" +
                "      padding: 2px 6px; border-radius: 4px; background: rgba(0,0,0,0.05);\n" +
                "      border-left: 3px solid var(--chip-color);\n" +
                "      white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\n" +
                "    }\n" +
                "    .chip-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--chip-color); flex-shrink: 0; }\n" +
                "    .chip-title { overflow: hidden; text-overflow: ellipsis; }\n" +
                "    .year-grid {\n" +
                "      display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));\n" +
                "      gap: 20px; margin-bottom: 30px;\n" +
                "    }\n" +
                "    .mini-month-card {\n" +
                "      background: var(--card-bg); border-radius: 8px; padding: 12px;\n" +
                "      box-shadow: 0 2px 4px rgba(0,0,0,0.05); border: 1px solid var(--border-color);\n" +
                "    }\n" +
                "    .mini-month-card h2 { font-size: 1.1rem; margin-bottom: 8px; text-align: center; }\n" +
                "    .mini-month-card .cal-day { min-height: 48px; padding: 4px; font-size: 0.8rem; }\n" +
                "    .mini-month-card .day-number { font-size: 0.8rem; }\n" +
                "    .mini-month-card .event-chip { font-size: 0.65rem; padding: 1px 3px; }\n" +
                "    .events-section {\n" +
                "      margin-top: 36px; background: var(--card-bg); border-radius: 8px;\n" +
                "      padding: 20px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); border: 1px solid var(--border-color);\n" +
                "    }\n" +
                "    .events-section h2 { font-size: 1.3rem; margin-bottom: 16px; }\n" +
                "    .events-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 0.9rem; }\n" +
                "    .events-table th, .events-table td { padding: 10px 14px; border-bottom: 1px solid var(--border-color); }\n" +
                "    .events-table th { background: var(--weekend-bg); color: var(--text-muted); }\n" +
                "    .badge { display: inline-flex; align-items: center; padding: 2px 8px; border-radius: 12px; font-weight: 600; font-size: 0.8rem; }\n" +
                "    .subtext { font-size: 0.8rem; color: var(--text-muted); }\n" +
                "    .nowrap { white-space: nowrap; }\n" +
                "    @media print {\n" +
                "      body { background: #fff !important; color: #000 !important; padding: 0; }\n" +
                "      .no-print { display: none !important; }\n" +
                "      .container, .year-container { max-width: 100% !important; margin: 0; }\n" +
                "      .calendar-grid { box-shadow: none; }\n" +
                "      .mini-month-card { page-break-inside: avoid; break-inside: avoid; }\n" +
                "      * { -webkit-print-color-adjust: exact !important; print-color-adjust: exact !important; }\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n";
    }

    private String htmlFooter() {
        return "<script>\n" +
                "  function toggleTheme() {\n" +
                "    const html = document.documentElement;\n" +
                "    const current = html.getAttribute('data-theme');\n" +
                "    const next = current === 'dark' ? 'light' : 'dark';\n" +
                "    html.setAttribute('data-theme', next);\n" +
                "    document.getElementById('themeBtn').textContent = next === 'dark' ? '☀️ Light Mode' : '🌓 Dark Mode';\n" +
                "  }\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>\n";
    }
}
