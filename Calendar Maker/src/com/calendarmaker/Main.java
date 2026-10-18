package com.calendarmaker;

import com.calendarmaker.cli.CommandLineParser;
import com.calendarmaker.cli.InteractiveMenu;
import com.calendarmaker.generator.*;
import com.calendarmaker.model.CalendarEvent;
import com.calendarmaker.model.EventCategory;
import com.calendarmaker.model.EventManager;
import com.calendarmaker.util.AnsiColor;
import com.calendarmaker.util.DateUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Entry point for the Calendar Maker application.
 */
public class Main {

    public static void main(String[] args) {
        CommandLineParser parser = CommandLineParser.parse(args);

        if (parser.isShowHelp()) {
            CommandLineParser.printHelp();
            return;
        }

        if (parser.isShowVersion()) {
            CommandLineParser.printVersion();
            return;
        }

        if (parser.isDisableColor()) {
            AnsiColor.setEnabled(false);
        }

        EventManager eventManager = new EventManager();
        eventManager.setIncludeHolidays(parser.isIncludeHolidays());

        // CLI Quick Event Addition
        if (parser.getAddEventStr() != null) {
            handleAddEventCli(parser.getAddEventStr(), eventManager);
            return;
        }

        // CLI List Events
        if (parser.isDoListEvents()) {
            handleListEventsCli(parser.getListEventsFilter(), eventManager);
            return;
        }

        // Interactive Mode
        if (parser.isInteractive()) {
            InteractiveMenu menu = new InteractiveMenu(eventManager);
            menu.start();
            return;
        }

        // Non-interactive generation mode
        CalendarRenderer renderer = getRenderer(parser.getFormat());
        String output;

        if (parser.isFullYear() || parser.getMonth() == null) {
            int year = parser.getYear();
            List<CalendarEvent> events = eventManager.getEventsForYear(year);
            output = renderer.renderYear(year, events, parser.getWeekStart());
        } else {
            YearMonth ym = YearMonth.of(parser.getYear(), parser.getMonth());
            List<CalendarEvent> events = eventManager.getEventsForMonth(ym);
            output = renderer.renderMonth(ym, events, parser.getWeekStart());
        }

        if (parser.getOutputFile() != null && !parser.getOutputFile().isEmpty()) {
            try {
                Path outPath = Paths.get(parser.getOutputFile());
                Path parent = outPath.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.writeString(outPath, output);
                System.out.println(AnsiColor.colorize("Calendar saved to: " + outPath.toAbsolutePath(), AnsiColor.BRIGHT_GREEN));
            } catch (IOException e) {
                System.err.println("Error writing to output file: " + e.getMessage());
                System.exit(1);
            }
        } else {
            System.out.print(output);
        }
    }

    private static CalendarRenderer getRenderer(String format) {
        switch (format.toLowerCase()) {
            case "html":
                return new HtmlCalendarRenderer();
            case "markdown":
            case "md":
                return new MarkdownCalendarRenderer();
            case "text":
            case "txt":
                return new TextCalendarRenderer();
            case "ics":
            case "ical":
                return new IcsCalendarRenderer();
            case "csv":
                return new CsvCalendarRenderer();
            case "console":
            default:
                return new ConsoleCalendarRenderer();
        }
    }

    private static void handleAddEventCli(String eventSpec, EventManager eventManager) {
        // Format: YYYY-MM-DD|Title|[Category]|[Description]|[Annual]
        String[] parts = eventSpec.split("\\|");
        if (parts.length < 2) {
            System.err.println("Error: Event spec format must be 'YYYY-MM-DD|Title|[Category]|[Description]|[Annual]'");
            System.exit(1);
        }

        try {
            LocalDate date = LocalDate.parse(parts[0].trim());
            String title = parts[1].trim();
            EventCategory category = (parts.length > 2 && !parts[2].trim().isEmpty())
                    ? EventCategory.fromString(parts[2].trim())
                    : EventCategory.OTHER;
            String desc = (parts.length > 3) ? parts[3].trim() : "";
            boolean recurring = (parts.length > 4) && Boolean.parseBoolean(parts[4].trim());

            CalendarEvent event = new CalendarEvent(title, date, category, desc, recurring);
            eventManager.addEvent(event);
            eventManager.saveToFile(Paths.get(EventManager.DEFAULT_STORAGE_FILE));

            System.out.println(AnsiColor.colorize("✓ Event added: " + event, AnsiColor.BRIGHT_GREEN));
        } catch (Exception e) {
            System.err.println("Error adding event: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void handleListEventsCli(String filter, EventManager eventManager) {
        List<CalendarEvent> events;
        if (filter != null && !filter.isEmpty()) {
            if (filter.length() == 4) { // Year
                int year = Integer.parseInt(filter);
                events = eventManager.getEventsForYear(year);
                System.out.println(AnsiColor.colorize("=== Events for Year " + year + " (" + events.size() + ") ===", AnsiColor.BOLD + AnsiColor.CYAN));
            } else { // Year-Month
                YearMonth ym = YearMonth.parse(filter);
                events = eventManager.getEventsForMonth(ym);
                System.out.println(AnsiColor.colorize("=== Events for " + DateUtils.formatMonthYear(ym) + " (" + events.size() + ") ===", AnsiColor.BOLD + AnsiColor.CYAN));
            }
        } else {
            events = eventManager.getEventsForYear(LocalDate.now().getYear());
            System.out.println(AnsiColor.colorize("=== Events for " + LocalDate.now().getYear() + " (" + events.size() + ") ===", AnsiColor.BOLD + AnsiColor.CYAN));
        }

        for (CalendarEvent e : events) {
            System.out.printf(" • %s [%s %s] %s%s %s\n",
                    DateUtils.formatIso(e.getDate()),
                    e.getCategory().getIcon(),
                    e.getCategory().getDisplayName(),
                    e.getTitle(),
                    e.isRecurringAnnual() ? " (Annual)" : "",
                    e.getDescription().isEmpty() ? "" : "- " + e.getDescription());
        }
    }
}
