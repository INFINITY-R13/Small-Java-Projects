package com.calendarmaker.cli;

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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Interactive terminal menu and calendar browser.
 */
public class InteractiveMenu {

    private final EventManager eventManager;
    private final Scanner scanner;
    private DayOfWeek weekStart = DayOfWeek.SUNDAY;

    public InteractiveMenu(EventManager eventManager) {
        this.eventManager = eventManager;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = prompt("Select an option (1-6): ").trim();

            switch (choice) {
                case "1":
                    browseMonthInteractive(YearMonth.now());
                    break;
                case "2":
                    viewFullYearInteractive();
                    break;
                case "3":
                    manageEventsSubmenu();
                    break;
                case "4":
                    exportCalendarWizard();
                    break;
                case "5":
                    settingsSubmenu();
                    break;
                case "6":
                case "q":
                case "exit":
                    saveEventsSafe();
                    System.out.println(AnsiColor.colorize("\nThank you for using Calendar Maker! Goodbye. 👋\n", AnsiColor.BRIGHT_GREEN));
                    running = false;
                    break;
                default:
                    System.out.println(AnsiColor.colorize("Invalid selection. Please choose an option from 1 to 6.", AnsiColor.RED));
                    break;
            }
        }
    }

    private void printBanner() {
        String banner =
                "  ____      _                _             __  __       _\n" +
                " / ___|__ _| | ___ _ __   __| | __ _ _ __ |  \\/  | __ _| | _____ _ __\n" +
                "| |   / _` | |/ _ \\ '_ \\ / _` |/ _` | '__|| |\\/| |/ _` | |/ / _ \\ '__|\n" +
                "| |__| (_| | |  __/ | | | (_| | (_| | |   | |  | | (_| |   <  __/ |\n" +
                " \\____\\__,_|_|\\___|_| |_|\\__,_|\\__,_|_|   |_|  |_|\\__,_|_|\\_\\___|_|\n";

        System.out.println(AnsiColor.colorize(banner, AnsiColor.BRIGHT_CYAN + AnsiColor.BOLD));
        System.out.println(AnsiColor.colorize("       ✨ Java Calendar Maker & Export Suite (Indian Edition) ✨\n", AnsiColor.BRIGHT_YELLOW));
    }

    private void printMainMenu() {
        System.out.println(AnsiColor.colorize("\n================ MAIN MENU ================", AnsiColor.BOLD + AnsiColor.BLUE));
        System.out.println("  1. 📅 View Month Calendar (Interactive Browser)");
        System.out.println("  2. 🗓️ View Full Year Calendar");
        System.out.println("  3. 📝 Manage Events, Holidays & Reminders");
        System.out.println("  4. 💾 Generate & Export Calendar (HTML, MD, ICS, CSV, TXT)");
        System.out.println("  5. ⚙️ Settings (Week Start, Holidays, Colors)");
        System.out.println("  6. 🚪 Exit");
        System.out.println(AnsiColor.colorize("===========================================", AnsiColor.BLUE));
    }

    /**
     * Interactive month calendar viewer with Next, Prev, Today navigation.
     */
    private void browseMonthInteractive(YearMonth initialMonth) {
        YearMonth current = initialMonth;
        ConsoleCalendarRenderer renderer = new ConsoleCalendarRenderer();

        while (true) {
            List<CalendarEvent> events = eventManager.getEventsForMonth(current);
            System.out.println("\n" + renderer.renderMonth(current, events, weekStart));

            System.out.println(AnsiColor.colorize("Navigation: [N]ext Month | [P]revious Month | [T]oday | [J]ump to Month | [E]xport | [M]enu", AnsiColor.CYAN));
            String cmd = prompt("Action: ").trim().toLowerCase();

            if (cmd.equals("n")) {
                current = current.plusMonths(1);
            } else if (cmd.equals("p")) {
                current = current.minusMonths(1);
            } else if (cmd.equals("t")) {
                current = YearMonth.now();
            } else if (cmd.equals("j")) {
                current = promptYearMonth("Enter target month (YYYY-MM): ", current);
            } else if (cmd.equals("e")) {
                exportSingleMonthDirect(current, events);
            } else if (cmd.equals("m") || cmd.equals("q") || cmd.isEmpty()) {
                break;
            }
        }
    }

    private void viewFullYearInteractive() {
        int defaultYear = LocalDate.now().getYear();
        String yearInput = prompt("Enter year to view [" + defaultYear + "]: ").trim();
        int year = defaultYear;
        if (!yearInput.isEmpty()) {
            try {
                year = Integer.parseInt(yearInput);
            } catch (NumberFormatException e) {
                System.out.println(AnsiColor.colorize("Invalid year number. Using " + defaultYear, AnsiColor.YELLOW));
            }
        }

        ConsoleCalendarRenderer renderer = new ConsoleCalendarRenderer();
        List<CalendarEvent> events = eventManager.getEventsForYear(year);
        System.out.println(renderer.renderYear(year, events, weekStart));

        String exportOpt = prompt("Export this year's calendar? (y/n) [n]: ").trim().toLowerCase();
        if (exportOpt.equals("y") || exportOpt.equals("yes")) {
            exportFullYearDirect(year, events);
        }
    }

    private void manageEventsSubmenu() {
        boolean inEvents = true;
        while (inEvents) {
            System.out.println(AnsiColor.colorize("\n--- MANAGE EVENTS & REMINDERS ---", AnsiColor.BOLD + AnsiColor.YELLOW));
            System.out.println("  1. ➕ Add New Event");
            System.out.println("  2. 🔍 View Events for Specific Date");
            System.out.println("  3. 📋 View Events for a Month");
            System.out.println("  4. 📑 List All Custom Events (" + eventManager.getCustomEvents().size() + ")");
            System.out.println("  5. 🗑️ Delete Custom Event");
            System.out.println("  6. 🔙 Back to Main Menu");

            String choice = prompt("Select an option (1-6): ").trim();
            switch (choice) {
                case "1":
                    addNewEventPrompt();
                    break;
                case "2":
                    viewEventsForDatePrompt();
                    break;
                case "3":
                    viewEventsForMonthPrompt();
                    break;
                case "4":
                    listAllCustomEventsPrompt();
                    break;
                case "5":
                    deleteEventPrompt();
                    break;
                case "6":
                case "q":
                    inEvents = false;
                    break;
                default:
                    System.out.println(AnsiColor.colorize("Invalid option.", AnsiColor.RED));
                    break;
            }
        }
    }

    private void addNewEventPrompt() {
        System.out.println(AnsiColor.colorize("\n--- Add New Event ---", AnsiColor.BOLD));
        LocalDate date = promptDate("Enter date (YYYY-MM-DD): ");
        if (date == null) return;

        String title = prompt("Event Title: ").trim();
        if (title.isEmpty()) {
            System.out.println(AnsiColor.colorize("Title cannot be empty.", AnsiColor.RED));
            return;
        }

        System.out.println("Select Category:");
        EventCategory[] categories = EventCategory.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("  %d. %s %s\n", i + 1, categories[i].getIcon(), categories[i].getDisplayName());
        }
        String catIdxStr = prompt("Category number [7 for Other]: ").trim();
        EventCategory category = EventCategory.OTHER;
        try {
            int catIdx = Integer.parseInt(catIdxStr) - 1;
            if (catIdx >= 0 && catIdx < categories.length) {
                category = categories[catIdx];
            }
        } catch (NumberFormatException ignored) {}

        String description = prompt("Description (optional): ").trim();
        String recurStr = prompt("Annual recurring event (e.g. Birthday/Anniversary)? (y/n) [n]: ").trim().toLowerCase();
        boolean recurring = recurStr.equals("y") || recurStr.equals("yes");

        CalendarEvent event = new CalendarEvent(title, date, category, description, recurring);
        eventManager.addEvent(event);
        saveEventsSafe();

        System.out.println(AnsiColor.colorize("✓ Event added successfully! ID: " + event.getId(), AnsiColor.BRIGHT_GREEN));
    }

    private void viewEventsForDatePrompt() {
        LocalDate date = promptDate("Enter date to check (YYYY-MM-DD): ");
        if (date == null) return;

        List<CalendarEvent> events = eventManager.getEventsForDate(date);
        System.out.println(AnsiColor.colorize("\nEvents on " + DateUtils.formatPretty(date) + " (" + events.size() + "):", AnsiColor.BOLD));
        if (events.isEmpty()) {
            System.out.println("  (No events scheduled)");
        } else {
            for (CalendarEvent e : events) {
                System.out.printf("  • [%s %s] %s%s %s\n",
                        e.getCategory().getIcon(),
                        e.getCategory().getDisplayName(),
                        e.getTitle(),
                        e.isRecurringAnnual() ? " (Annual)" : "",
                        e.getDescription().isEmpty() ? "" : "- " + e.getDescription());
            }
        }
    }

    private void viewEventsForMonthPrompt() {
        YearMonth ym = promptYearMonth("Enter Month (YYYY-MM): ", YearMonth.now());
        List<CalendarEvent> events = eventManager.getEventsForMonth(ym);

        System.out.println(AnsiColor.colorize("\nEvents in " + DateUtils.formatMonthYear(ym) + " (" + events.size() + "):", AnsiColor.BOLD));
        if (events.isEmpty()) {
            System.out.println("  (No events found)");
        } else {
            for (CalendarEvent e : events) {
                System.out.printf("  • %s [%s] %s%s %s\n",
                        DateUtils.formatIso(e.getDate()),
                        e.getCategory().getDisplayName(),
                        e.getTitle(),
                        e.isRecurringAnnual() ? " (Annual)" : "",
                        e.getDescription().isEmpty() ? "" : "- " + e.getDescription());
            }
        }
    }

    private void listAllCustomEventsPrompt() {
        List<CalendarEvent> customs = eventManager.getCustomEvents();
        System.out.println(AnsiColor.colorize("\n--- ALL CUSTOM EVENTS (" + customs.size() + ") ---", AnsiColor.BOLD));
        if (customs.isEmpty()) {
            System.out.println("  (No custom events saved yet. Use option 1 to add some!)");
            return;
        }

        for (int i = 0; i < customs.size(); i++) {
            CalendarEvent e = customs.get(i);
            System.out.printf("  #%d [%s] %s | %s %s: %s%s %s\n",
                    i + 1,
                    e.getId(),
                    DateUtils.formatIso(e.getDate()),
                    e.getCategory().getIcon(),
                    e.getCategory().getDisplayName(),
                    e.getTitle(),
                    e.isRecurringAnnual() ? " (Annual)" : "",
                    e.getDescription().isEmpty() ? "" : " - " + e.getDescription());
        }
    }

    private void deleteEventPrompt() {
        List<CalendarEvent> customs = eventManager.getCustomEvents();
        if (customs.isEmpty()) {
            System.out.println(AnsiColor.colorize("No custom events to delete.", AnsiColor.YELLOW));
            return;
        }

        listAllCustomEventsPrompt();
        String target = prompt("\nEnter Event ID or #number to delete (or press Enter to cancel): ").trim();
        if (target.isEmpty()) return;

        boolean removed = false;
        try {
            int num = Integer.parseInt(target);
            if (num >= 1 && num <= customs.size()) {
                CalendarEvent toRemove = customs.get(num - 1);
                removed = eventManager.removeEvent(toRemove.getId());
            }
        } catch (NumberFormatException ignored) {}

        if (!removed) {
            removed = eventManager.removeEvent(target);
        }

        if (removed) {
            saveEventsSafe();
            System.out.println(AnsiColor.colorize("✓ Event deleted successfully.", AnsiColor.BRIGHT_GREEN));
        } else {
            System.out.println(AnsiColor.colorize("Event not found.", AnsiColor.RED));
        }
    }

    private void exportCalendarWizard() {
        System.out.println(AnsiColor.colorize("\n--- EXPORT CALENDAR WIZARD ---", AnsiColor.BOLD + AnsiColor.YELLOW));
        System.out.println("Select Scope:");
        System.out.println("  1. Single Month");
        System.out.println("  2. Full Year (12 Months)");
        String scope = prompt("Choice (1-2) [1]: ").trim();
        boolean isYear = "2".equals(scope);

        int year = LocalDate.now().getYear();
        YearMonth ym = YearMonth.now();

        if (isYear) {
            String yInput = prompt("Enter Year [" + year + "]: ").trim();
            if (!yInput.isEmpty()) {
                try { year = Integer.parseInt(yInput); } catch (NumberFormatException ignored) {}
            }
        } else {
            ym = promptYearMonth("Enter Month (YYYY-MM) [" + ym + "]: ", ym);
            year = ym.getYear();
        }

        System.out.println("\nSelect Format:");
        System.out.println("  1. 🌐 Modern HTML (Printable with CSS styling)");
        System.out.println("  2. 📝 Markdown (.md tables)");
        System.out.println("  3. 📅 iCalendar (.ics for Google / Apple Calendar / Outlook)");
        System.out.println("  4. 📊 CSV Spreadsheet (.csv)");
        System.out.println("  5. 📄 Plain Text (.txt ASCII format)");
        String fmtChoice = prompt("Format (1-5) [1]: ").trim();

        CalendarRenderer renderer;
        switch (fmtChoice) {
            case "2": renderer = new MarkdownCalendarRenderer(); break;
            case "3": renderer = new IcsCalendarRenderer(); break;
            case "4": renderer = new CsvCalendarRenderer(); break;
            case "5": renderer = new TextCalendarRenderer(); break;
            default: renderer = new HtmlCalendarRenderer(); break;
        }

        String defaultFileName = isYear
                ? String.format("calendar_%d.%s", year, renderer.getFileExtension())
                : String.format("calendar_%d_%02d.%s", ym.getYear(), ym.getMonthValue(), renderer.getFileExtension());

        String filename = prompt("Output file name [" + defaultFileName + "]: ").trim();
        if (filename.isEmpty()) {
            filename = defaultFileName;
        }

        List<CalendarEvent> events = isYear ? eventManager.getEventsForYear(year) : eventManager.getEventsForMonth(ym);
        String rendered = isYear ? renderer.renderYear(year, events, weekStart) : renderer.renderMonth(ym, events, weekStart);

        try {
            Path path = Paths.get(filename);
            Files.writeString(path, rendered);
            System.out.println(AnsiColor.colorize("\n✓ Calendar successfully exported to: " + path.toAbsolutePath(), AnsiColor.BRIGHT_GREEN));
        } catch (IOException e) {
            System.out.println(AnsiColor.colorize("Error saving file: " + e.getMessage(), AnsiColor.RED));
        }
    }

    private void exportSingleMonthDirect(YearMonth ym, List<CalendarEvent> events) {
        String defaultName = String.format("calendar_%d_%02d.html", ym.getYear(), ym.getMonthValue());
        String filename = prompt("Export filename [" + defaultName + "]: ").trim();
        if (filename.isEmpty()) filename = defaultName;

        CalendarRenderer renderer;
        if (filename.endsWith(".md")) renderer = new MarkdownCalendarRenderer();
        else if (filename.endsWith(".ics")) renderer = new IcsCalendarRenderer();
        else if (filename.endsWith(".csv")) renderer = new CsvCalendarRenderer();
        else if (filename.endsWith(".txt")) renderer = new TextCalendarRenderer();
        else renderer = new HtmlCalendarRenderer();

        try {
            Path path = Paths.get(filename);
            Files.writeString(path, renderer.renderMonth(ym, events, weekStart));
            System.out.println(AnsiColor.colorize("✓ Saved to " + path.toAbsolutePath(), AnsiColor.BRIGHT_GREEN));
        } catch (IOException e) {
            System.out.println(AnsiColor.colorize("Error: " + e.getMessage(), AnsiColor.RED));
        }
    }

    private void exportFullYearDirect(int year, List<CalendarEvent> events) {
        String defaultName = "calendar_" + year + ".html";
        String filename = prompt("Export filename [" + defaultName + "]: ").trim();
        if (filename.isEmpty()) filename = defaultName;

        CalendarRenderer renderer;
        if (filename.endsWith(".md")) renderer = new MarkdownCalendarRenderer();
        else if (filename.endsWith(".ics")) renderer = new IcsCalendarRenderer();
        else if (filename.endsWith(".csv")) renderer = new CsvCalendarRenderer();
        else if (filename.endsWith(".txt")) renderer = new TextCalendarRenderer();
        else renderer = new HtmlCalendarRenderer();

        try {
            Path path = Paths.get(filename);
            Files.writeString(path, renderer.renderYear(year, events, weekStart));
            System.out.println(AnsiColor.colorize("✓ Saved to " + path.toAbsolutePath(), AnsiColor.BRIGHT_GREEN));
        } catch (IOException e) {
            System.out.println(AnsiColor.colorize("Error: " + e.getMessage(), AnsiColor.RED));
        }
    }

    private void settingsSubmenu() {
        boolean inSettings = true;
        while (inSettings) {
            System.out.println(AnsiColor.colorize("\n--- SETTINGS ---", AnsiColor.BOLD + AnsiColor.YELLOW));
            System.out.printf("  1. First day of week: %s (Toggle to %s)\n",
                    weekStart == DayOfWeek.SUNDAY ? "Sunday" : "Monday",
                    weekStart == DayOfWeek.SUNDAY ? "Monday" : "Sunday");
            System.out.printf("  2. Built-in Indian Holidays: %s (Toggle)\n",
                    eventManager.isIncludeHolidays() ? "Enabled [ON]" : "Disabled [OFF]");
            System.out.printf("  3. ANSI Color Styling: %s (Toggle)\n",
                    AnsiColor.isEnabled() ? "Enabled [ON]" : "Disabled [OFF]");
            System.out.println("  4. 🔙 Back to Main Menu");

            String choice = prompt("Select an option (1-4): ").trim();
            switch (choice) {
                case "1":
                    weekStart = (weekStart == DayOfWeek.SUNDAY) ? DayOfWeek.MONDAY : DayOfWeek.SUNDAY;
                    System.out.println(AnsiColor.colorize("First day of week set to: " + weekStart, AnsiColor.GREEN));
                    break;
                case "2":
                    eventManager.setIncludeHolidays(!eventManager.isIncludeHolidays());
                    System.out.println(AnsiColor.colorize("Holidays are now: " + (eventManager.isIncludeHolidays() ? "ON" : "OFF"), AnsiColor.GREEN));
                    break;
                case "3":
                    AnsiColor.setEnabled(!AnsiColor.isEnabled());
                    System.out.println("ANSI colors are now: " + (AnsiColor.isEnabled() ? "ON" : "OFF"));
                    break;
                case "4":
                case "q":
                    inSettings = false;
                    break;
                default:
                    System.out.println(AnsiColor.colorize("Invalid option.", AnsiColor.RED));
                    break;
            }
        }
    }

    private void saveEventsSafe() {
        try {
            eventManager.saveToFile(Paths.get(EventManager.DEFAULT_STORAGE_FILE));
        } catch (IOException e) {
            System.err.println("Could not save events.json: " + e.getMessage());
        }
    }

    private String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private LocalDate promptDate(String message) {
        while (true) {
            String input = prompt(message).trim();
            if (input.equalsIgnoreCase("q") || input.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(input);
            } catch (Exception e) {
                System.out.println(AnsiColor.colorize("Invalid date format. Use YYYY-MM-DD (e.g. 2026-10-25) or 'q' to cancel.", AnsiColor.RED));
            }
        }
    }

    private YearMonth promptYearMonth(String message, YearMonth defaultVal) {
        while (true) {
            String input = prompt(message).trim();
            if (input.isEmpty()) return defaultVal;
            try {
                return YearMonth.parse(input);
            } catch (Exception e) {
                System.out.println(AnsiColor.colorize("Invalid month format. Please use YYYY-MM (e.g. 2026-10).", AnsiColor.RED));
            }
        }
    }
}
