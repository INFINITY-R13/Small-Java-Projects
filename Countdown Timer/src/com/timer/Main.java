package com.timer;

import java.awt.GraphicsEnvironment;

/**
 * Application Entry Point for Countdown Timer.
 * Supports running via CLI, Swing Desktop GUI, or direct command arguments.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            // Interactive CLI session by default
            new ConsoleTimer().startInteractiveSession();
            return;
        }

        String firstArg = args[0].trim().toLowerCase();

        switch (firstArg) {
            case "--help", "-h", "help" -> printUsage();
            case "--gui", "-g", "gui" -> {
                if (GraphicsEnvironment.isHeadless()) {
                    System.err.println("Error: Graphical interface cannot be launched in a headless environment.");
                    System.exit(1);
                }
                GuiTimer.launch();
            }
            case "--cli", "-c", "cli" -> new ConsoleTimer().startInteractiveSession();
            case "--pomodoro", "-p", "pomodoro" -> new ConsoleTimer().runCountdown(25 * 60);
            case "--short-break" -> new ConsoleTimer().runCountdown(5 * 60);
            case "--long-break" -> new ConsoleTimer().runCountdown(15 * 60);
            default -> {
                // If it looks like a duration, parse and run directly
                // Join arguments to support spaced formats like "1h 30m"
                String durationInput = String.join(" ", args);
                try {
                    long seconds = TimeUtils.parseDuration(durationInput);
                    new ConsoleTimer().runCountdown(seconds);
                } catch (IllegalArgumentException e) {
                    System.err.println(TimeUtils.RED + "Error: " + e.getMessage() + TimeUtils.RESET);
                    System.err.println("Run with --help to see supported commands and formats.");
                    System.exit(1);
                }
            }
        }
    }

    private static void printUsage() {
        System.out.println(TimeUtils.BOLD + "Countdown Timer (Java)" + TimeUtils.RESET);
        System.out.println("Usage:");
        System.out.println("  java com.timer.Main                 Start interactive CLI menu");
        System.out.println("  java com.timer.Main --gui           Launch graphical desktop UI");
        System.out.println("  java com.timer.Main --pomodoro      Start 25-minute Pomodoro timer");
        System.out.println("  java com.timer.Main <duration>      Start countdown directly");
        System.out.println();
        System.out.println("Supported duration formats:");
        System.out.println("  90               90 seconds");
        System.out.println("  45s              45 seconds");
        System.out.println("  05:00            5 minutes (MM:SS)");
        System.out.println("  01:30:00         1 hour, 30 minutes (HH:MM:SS)");
        System.out.println("  25m              25 minutes");
        System.out.println("  1h 15m 30s       1 hour, 15 minutes, 30 seconds");
        System.out.println();
        System.out.println("Flags:");
        System.out.println("  --gui, -g        Launch Graphical User Interface (Swing)");
        System.out.println("  --cli, -c        Launch Interactive Terminal Menu");
        System.out.println("  --pomodoro, -p   Quick 25-minute focus session");
        System.out.println("  --short-break    Quick 5-minute break");
        System.out.println("  --long-break     Quick 15-minute break");
        System.out.println("  --help, -h       Show this help message");
    }
}
