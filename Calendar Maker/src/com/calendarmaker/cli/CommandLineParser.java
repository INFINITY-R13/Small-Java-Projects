package com.calendarmaker.cli;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Parses and validates command-line arguments.
 */
public class CommandLineParser {

    private int year = LocalDate.now().getYear();
    private Integer month = null; // null means month not explicitly set (or full year)
    private String format = "console";
    private String outputFile = null;
    private DayOfWeek weekStart = DayOfWeek.SUNDAY;
    private boolean includeHolidays = true;
    private boolean fullYear = false;
    private boolean interactive = false;
    private boolean showHelp = false;
    private boolean showVersion = false;
    private boolean disableColor = false;
    private String addEventStr = null;
    private String listEventsFilter = null;
    private boolean doListEvents = false;

    public static CommandLineParser parse(String[] args) {
        CommandLineParser parser = new CommandLineParser();

        if (args == null || args.length == 0) {
            parser.interactive = true;
            return parser;
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg) {
                case "-h":
                case "--help":
                    parser.showHelp = true;
                    return parser;

                case "-v":
                case "--version":
                    parser.showVersion = true;
                    return parser;

                case "-i":
                case "--interactive":
                    parser.interactive = true;
                    break;

                case "--no-color":
                    parser.disableColor = true;
                    break;

                case "--no-holidays":
                    parser.includeHolidays = false;
                    break;

                case "--full-year":
                    parser.fullYear = true;
                    break;

                case "-y":
                case "--year":
                    if (i + 1 < args.length) {
                        try {
                            parser.year = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Warning: Invalid year. Using current year.");
                        }
                    }
                    break;

                case "-m":
                case "--month":
                    if (i + 1 < args.length) {
                        try {
                            int m = Integer.parseInt(args[++i]);
                            if (m >= 1 && m <= 12) {
                                parser.month = m;
                            } else {
                                System.err.println("Warning: Month must be between 1 and 12.");
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Warning: Invalid month.");
                        }
                    }
                    break;

                case "-f":
                case "--format":
                    if (i + 1 < args.length) {
                        parser.format = args[++i].toLowerCase();
                    }
                    break;

                case "-o":
                case "--output":
                    if (i + 1 < args.length) {
                        parser.outputFile = args[++i];
                    }
                    break;

                case "-w":
                case "--week-start":
                    if (i + 1 < args.length) {
                        String ws = args[++i].toLowerCase();
                        if (ws.startsWith("m")) {
                            parser.weekStart = DayOfWeek.MONDAY;
                        } else {
                            parser.weekStart = DayOfWeek.SUNDAY;
                        }
                    }
                    break;

                case "--add-event":
                    if (i + 1 < args.length) {
                        parser.addEventStr = args[++i];
                    }
                    break;

                case "--list-events":
                    parser.doListEvents = true;
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        parser.listEventsFilter = args[++i];
                    }
                    break;

                default:
                    System.err.println("Unknown option: " + arg + ". Run with --help for usage.");
                    break;
            }
        }

        return parser;
    }

    public static void printHelp() {
        System.out.println("===============================================================================");
        System.out.println("                         CALENDAR MAKER - CLI USAGE                            ");
        System.out.println("===============================================================================");
        System.out.println("Usage:");
        System.out.println("  java -jar CalendarMaker.jar [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -i, --interactive              Start interactive terminal menu (default if no args)");
        System.out.println("  -y, --year <YYYY>              Specify calendar year (default: current year)");
        System.out.println("  -m, --month <1-12>             Specify calendar month (default: current month)");
        System.out.println("  --full-year                    Generate full 12-month calendar");
        System.out.println("  -f, --format <format>          Output format: console, html, markdown, text, ics, csv");
        System.out.println("                                 (default: console)");
        System.out.println("  -o, --output <file>            Save output to file instead of printing to console");
        System.out.println("  -w, --week-start <sun|mon>     First day of week (default: sun)");
        System.out.println("  --no-holidays                  Exclude built-in Indian national and gazetted holidays");
        System.out.println("  --no-color                     Disable ANSI color codes in terminal");
        System.out.println();
        System.out.println("Event Management via CLI:");
        System.out.println("  --add-event \"YYYY-MM-DD|Title|[Category]|[Description]|[Annual(true/false)]\"");
        System.out.println("                                 Add a new event directly from command line");
        System.out.println("  --list-events [YYYY or YYYY-MM] List saved events and Indian holidays");
        System.out.println();
        System.out.println("General:");
        System.out.println("  -h, --help                     Display this help screen");
        System.out.println("  -v, --version                  Display version info");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar CalendarMaker.jar --month 10 --year 2026");
        System.out.println("  java -jar CalendarMaker.jar --year 2026 --full-year --format html -o calendar2026.html");
        System.out.println("  java -jar CalendarMaker.jar --year 2026 --format ics -o events2026.ics");
        System.out.println("  java -jar CalendarMaker.jar --add-event \"2026-06-13|Raghuraj's Birthday|Birthday|Raghuraj's Birthday|true\"");
        System.out.println("===============================================================================");
    }

    public static void printVersion() {
        System.out.println("Calendar Maker v1.0.0 (Java Edition)");
    }

    public int getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public String getFormat() {
        return format;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public DayOfWeek getWeekStart() {
        return weekStart;
    }

    public boolean isIncludeHolidays() {
        return includeHolidays;
    }

    public boolean isFullYear() {
        return fullYear;
    }

    public boolean isInteractive() {
        return interactive;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isShowVersion() {
        return showVersion;
    }

    public boolean isDisableColor() {
        return disableColor;
    }

    public String getAddEventStr() {
        return addEventStr;
    }

    public String getListEventsFilter() {
        return listEventsFilter;
    }

    public boolean isDoListEvents() {
        return doListEvents;
    }
}