package com.blackjack.ui;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/**
 * Handles safe user input from the console with validation and error messaging.
 */
public class InputHandler {
    private final Scanner scanner;

    public InputHandler() {
        this(new Scanner(System.in));
    }

    public InputHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Reads a non-empty string from the user.
     */
    public String readString(String prompt, String defaultValue) {
        while (true) {
            System.out.print(prompt);
            try {
                if (!scanner.hasNextLine()) {
                    return defaultValue;
                }
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    if (defaultValue != null) {
                        return defaultValue;
                    }
                    System.out.println(ConsoleColors.colorize(ConsoleColors.RED, "Input cannot be empty. Please try again."));
                    continue;
                }
                return line;
            } catch (NoSuchElementException e) {
                return defaultValue;
            }
        }
    }

    /**
     * Prompts for an integer within the specified [min, max] range.
     */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                if (!scanner.hasNextLine()) {
                    return min;
                }
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    System.out.println(ConsoleColors.colorize(ConsoleColors.RED, "Please enter a number."));
                    continue;
                }
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println(ConsoleColors.colorize(ConsoleColors.RED,
                            String.format("Please enter a number between %,d and %,d.", min, max)));
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.colorize(ConsoleColors.RED, "Invalid input. Please enter a valid number."));
            } catch (NoSuchElementException e) {
                return min;
            }
        }
    }

    /**
     * Prompts for a choice from allowed character keys (case-insensitive).
     */
    public char readChoice(String prompt, Set<Character> validChoices) {
        while (true) {
            System.out.print(prompt);
            try {
                if (!scanner.hasNextLine()) {
                    return validChoices.iterator().next();
                }
                String line = scanner.nextLine().trim().toUpperCase();
                if (line.isEmpty()) {
                    System.out.println(ConsoleColors.colorize(ConsoleColors.RED, "Please select an option."));
                    continue;
                }
                char choice = line.charAt(0);
                if (validChoices.contains(choice)) {
                    return choice;
                }
                System.out.println(ConsoleColors.colorize(ConsoleColors.RED, "Invalid choice. Valid options: " + validChoices));
            } catch (NoSuchElementException e) {
                return validChoices.iterator().next();
            }
        }
    }

    /**
     * Prompts for a Yes/No question.
     */
    public boolean readYesNo(String prompt, boolean defaultYes) {
        String defaultHint = defaultYes ? "[Y/n]" : "[y/N]";
        while (true) {
            System.out.print(prompt + " " + defaultHint + ": ");
            try {
                if (!scanner.hasNextLine()) {
                    return defaultYes;
                }
                String line = scanner.nextLine().trim().toUpperCase();
                if (line.isEmpty()) {
                    return defaultYes;
                }
                if (line.startsWith("Y")) {
                    return true;
                }
                if (line.startsWith("N")) {
                    return false;
                }
                System.out.println(ConsoleColors.colorize(ConsoleColors.RED, "Please enter 'y' for yes or 'n' for no."));
            } catch (NoSuchElementException e) {
                return defaultYes;
            }
        }
    }
}
