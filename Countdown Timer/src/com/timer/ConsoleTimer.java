package com.timer;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Command-Line Interface for the Countdown Timer.
 * Provides interactive menus, real-time single-line terminal progress,
 * and completion alerts.
 */
public class ConsoleTimer {

    private final CountdownTimer timer = new CountdownTimer();
    private CountDownLatch latch;

    /**
     * Runs an interactive CLI session where users can choose from presets,
     * enter custom durations, or launch the GUI.
     */
    public void startInteractiveSession() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        printBanner();

        while (running) {
            System.out.println(TimeUtils.CYAN + "=== MAIN MENU ===" + TimeUtils.RESET);
            System.out.println(" 1. Custom Countdown  (e.g., 90s, 05:00, 1h 30m)");
            System.out.println(" 2. Pomodoro Focus    (25 minutes)");
            System.out.println(" 3. Short Break       (5 minutes)");
            System.out.println(" 4. Long Break        (15 minutes)");
            System.out.println(" 5. Quick Test        (10 seconds)");
            System.out.println(" 6. Launch GUI        (Desktop Window)");
            System.out.println(" 0. Exit");
            System.out.print(TimeUtils.BOLD + "\nEnter your choice (0-6): " + TimeUtils.RESET);

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter duration (e.g., 30, 02:30, 5m, 1h 15m): ");
                    String input = scanner.nextLine().trim();
                    try {
                        long seconds = TimeUtils.parseDuration(input);
                        runCountdown(seconds);
                    } catch (IllegalArgumentException e) {
                        System.out.println(TimeUtils.RED + "Error: " + e.getMessage() + TimeUtils.RESET + "\n");
                    }
                }
                case "2" -> runCountdown(25 * 60); // 25 min
                case "3" -> runCountdown(5 * 60);  // 5 min
                case "4" -> runCountdown(15 * 60); // 15 min
                case "5" -> runCountdown(10);      // 10 sec
                case "6" -> {
                    System.out.println(TimeUtils.GREEN + "Launching Graphical User Interface..." + TimeUtils.RESET);
                    GuiTimer.launch();
                }
                case "0" -> {
                    System.out.println("Goodbye! Have a productive day!");
                    running = false;
                }
                default -> System.out.println(TimeUtils.RED + "Invalid option. Please enter 0 to 6.\n" + TimeUtils.RESET);
            }
        }
    }

    /**
     * Executes a countdown for the specified number of seconds and blocks
     * until the countdown finishes or is interrupted.
     */
    public void runCountdown(long seconds) {
        if (seconds <= 0) {
            System.out.println(TimeUtils.RED + "Duration must be greater than 0." + TimeUtils.RESET);
            return;
        }

        latch = new CountDownLatch(1);

        System.out.println();
        System.out.println(TimeUtils.DIM + "Starting timer for " + TimeUtils.formatHHMMSS(seconds) + " (" + seconds + "s)..." + TimeUtils.RESET);
        System.out.println(TimeUtils.DIM + "Press Ctrl+C at any time to cancel." + TimeUtils.RESET);
        System.out.println();

        TimerListener listener = new TimerListener() {
            @Override
            public void onTick(long remaining, long total) {
                renderCliTick(remaining, total);
            }

            @Override
            public void onStateChange(TimerState oldState, TimerState newState) {
                // Not needed for simple CLI run
            }

            @Override
            public void onComplete() {
                renderCliComplete();
                if (latch != null) {
                    latch.countDown();
                }
            }
        };

        timer.addListener(listener);

        // Clean terminal cursor on shutdown hook
        Thread shutdownHook = new Thread(() -> {
            timer.stop();
            System.out.println("\n" + TimeUtils.YELLOW + "\nTimer cancelled." + TimeUtils.RESET);
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        try {
            timer.start(seconds);
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            } catch (IllegalStateException ignored) {
                // JVM already shutting down
            }
            timer.removeListener(listener);
        }
    }

    private void renderCliTick(long remaining, long total) {
        String timeStr = TimeUtils.formatHHMMSS(remaining);
        String progressBar = TimeUtils.renderProgressBar(remaining, total, 25);

        // Determine color based on time left
        double ratio = (double) remaining / (double) total;
        String color;
        if (ratio > 0.5) {
            color = TimeUtils.GREEN;
        } else if (ratio > 0.2) {
            color = TimeUtils.YELLOW;
        } else {
            color = TimeUtils.RED;
        }

        // Single-line carriage return update
        System.out.print(String.format(
                "\r  ⏱️  %s%s%s  %s%s%s  (%s remaining) ",
                TimeUtils.BRIGHT_CYAN + TimeUtils.BOLD,
                timeStr,
                TimeUtils.RESET,
                color,
                progressBar,
                TimeUtils.RESET,
                TimeUtils.formatAuto(remaining)
        ));
        System.out.flush();
    }

    private void renderCliComplete() {
        System.out.print("\r                                                                      \r");
        System.out.println(TimeUtils.BOLD + TimeUtils.GREEN);
        System.out.println("  ╔═════════════════════════════════════════════════════════╗");
        System.out.println("  ║                   🎉  TIME'S UP!  🎉                    ║");
        System.out.println("  ║              Countdown completed successfully!          ║");
        System.out.println("  ╚═════════════════════════════════════════════════════════╝" + TimeUtils.RESET);
        System.out.println();

        SoundUtils.playCompletionChime();
    }

    private void printBanner() {
        System.out.println(TimeUtils.CYAN + TimeUtils.BOLD);
        System.out.println("   ___             _      _                  _____ _                 ");
        System.out.println("  / __|___ _  _ _ _| |_ __| |_____ __ ___ _ |_   _(_)_ __  ___ _ _  ");
        System.out.println(" | (__/ _ \\ || | ' \\  _/ _` / _ \\ V  V / ' \\  | | | | '  \\/ -_) '_| ");
        System.out.println("  \\___\\___/\\_,_|_||_\\__\\__,_\\___/\\_/\\_/|_||_| |_| |_|_|_|_\\___|_|   ");
        System.out.println("                                              Java Edition v1.0      " + TimeUtils.RESET);
        System.out.println();
    }
}
