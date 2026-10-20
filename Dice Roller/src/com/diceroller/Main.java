package com.diceroller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Entry point for the Java Dice Roller application.
 * Supports both CLI argument mode and an interactive terminal menu.
 */
public class Main {

    private static final RollHistory history = new RollHistory();

    public static void main(String[] args) {
        // If arguments were provided, execute them directly in CLI mode
        if (args != null && args.length > 0) {
            runCliMode(args);
            return;
        }

        // Otherwise, launch the interactive console application
        runInteractiveMode();
    }

    /**
     * Executes one or more dice expressions supplied as command line arguments.
     * Example: java -cp bin com.diceroller.Main 2d6+3 1d20
     */
    private static void runCliMode(String[] args) {
        System.out.println("🎲 JAVA DICE ROLLER (CLI Mode) 🎲\n");
        for (String arg : args) {
            try {
                DiceRollResult result = DiceNotationParser.roll(arg);
                System.out.println("----------------------------------------");
                System.out.println("Expression : " + result.getExpression());
                System.out.println("Rolls      : " + result.getRolls());
                if (result.getModifier() != 0) {
                    System.out.println("Modifier   : " + (result.getModifier() > 0 ? "+" : "") + result.getModifier());
                }
                System.out.println("Total      : " + result.getTotal());
                if (result.getDiceCount() > 1) {
                    System.out.printf("Min / Max  : %d / %d%n", result.getMin(), result.getMax());
                    System.out.printf("Average    : %.2f%n", result.getAverage());
                }
                if (result.getDiceCount() <= 10) {
                    System.out.println();
                    System.out.print(DiceAsciiArt.renderPolyhedral(result.getRolls(), result.getSides()));
                }
                System.out.println("----------------------------------------\n");
            } catch (IllegalArgumentException e) {
                System.err.printf("[!] Error parsing '%s': %s%n%n", arg, e.getMessage());
            }
        }
    }

    /**
     * Runs the interactive terminal menu loop.
     */
    private static void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        printWelcomeBanner();

        while (running) {
            printMainMenu();
            int choice = readInt(scanner, "Choose an option (1-6): ", 1, 6);

            switch (choice) {
                case 1 -> handleStandardD6Roll(scanner);
                case 2 -> handlePolyhedralRoll(scanner);
                case 3 -> handleDiceNotationRoll(scanner);
                case 4 -> handleViewHistory();
                case 5 -> handleClearHistory(scanner);
                case 6 -> {
                    System.out.println("\nThanks for rolling! May the odds be ever in your favor. 🎲✨\n");
                    running = false;
                }
                default -> System.out.println("[!] Invalid choice. Please choose from 1 to 6.");
            }

            if (running && choice != 6) {
                System.out.print("\nPress Enter to return to the main menu...");
                try {
                    scanner.nextLine();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static void printWelcomeBanner() {
        System.out.println("==================================================");
        System.out.println("             🎲 JAVA DICE ROLLER 🎲               ");
        System.out.println("     Feature-rich terminal dice rolling tool      ");
        System.out.println("==================================================");
    }

    private static void printMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println(" 1. Roll Standard 6-Sided Dice (with ASCII Art)");
        System.out.println(" 2. Roll Custom Polyhedral Dice (d4, d8, d10, d20, etc.)");
        System.out.println(" 3. Roll Dice Notation Expression (e.g., 2d6+3, 1d20)");
        System.out.println(" 4. View Roll History & Session Statistics");
        System.out.println(" 5. Clear Roll History");
        System.out.println(" 6. Exit");
        System.out.println("-----------------");
    }

    /**
     * Handles rolling standard 6-sided dice with ASCII visuals.
     */
    private static void handleStandardD6Roll(Scanner scanner) {
        System.out.println("\n--- Roll Standard 6-Sided Dice ---");
        int count = readInt(scanner, "How many dice would you like to roll? (1-20): ", 1, 20);

        Die d6 = new Die(6);
        List<Integer> rolls = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            rolls.add(d6.roll());
        }

        DiceRollResult result = new DiceRollResult(count + "d6", rolls, 0, 6);
        history.add(result);

        displayResultWithVisuals(result);
    }

    /**
     * Handles rolling polyhedral dice or user-specified custom sides.
     */
    private static void handlePolyhedralRoll(Scanner scanner) {
        System.out.println("\n--- Roll Custom Polyhedral Dice ---");
        System.out.println("Popular types: 4 (d4), 6 (d6), 8 (d8), 10 (d10), 12 (d12), 20 (d20), 100 (d100)");
        int sides = readInt(scanner, "Enter number of sides per die (2-1000): ", 2, 1000);
        int count = readInt(scanner, "How many dice to roll? (1-50): ", 1, 50);

        Die die = new Die(sides);
        List<Integer> rolls = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            rolls.add(die.roll());
        }

        DiceRollResult result = new DiceRollResult(count + "d" + sides, rolls, 0, sides);
        history.add(result);

        displayResultWithVisuals(result);
    }

    /**
     * Handles parsing and rolling custom dice notation expressions like 3d8+4.
     */
    private static void handleDiceNotationRoll(Scanner scanner) {
        System.out.println("\n--- Roll Dice Notation ---");
        System.out.println("Format: [count]d[sides][+/-modifier]");
        System.out.println("Examples: 'd20', '2d6', '3d8+5', '1d100-10'");

        while (true) {
            System.out.print("Enter dice expression: ");
            String input;
            try {
                input = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                return;
            }

            if (input.isEmpty()) {
                System.out.println("[!] Input cannot be empty. Please enter an expression.");
                continue;
            }

            try {
                DiceRollResult result = DiceNotationParser.roll(input);
                history.add(result);
                displayResultWithVisuals(result);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("[!] " + e.getMessage());
                System.out.println("Please try again or enter another expression.");
            }
        }
    }

    /**
     * Displays a roll result, statistics, and ASCII visual representation.
     */
    private static void displayResultWithVisuals(DiceRollResult result) {
        System.out.println("\nRolling...");
        System.out.println("==================================================");
        System.out.println("Result: " + result.getExpression());
        System.out.println("Rolls : " + result.getRolls());

        if (result.getModifier() != 0) {
            String sign = result.getModifier() > 0 ? "+" : "";
            System.out.printf("Sum of dice: %d | Modifier: %s%d%n",
                result.getDiceSum(), sign, result.getModifier());
        }

        System.out.println("TOTAL : " + result.getTotal());

        if (result.getDiceCount() > 1) {
            System.out.printf("Stats : Min = %d | Max = %d | Avg = %.2f%n",
                result.getMin(), result.getMax(), result.getAverage());
        }
        System.out.println("==================================================");

        // Render ASCII art when rolling 10 or fewer dice
        if (result.getDiceCount() <= 10) {
            System.out.println();
            System.out.print(DiceAsciiArt.renderPolyhedral(result.getRolls(), result.getSides()));
        }
    }

    /**
     * Displays recent rolls table and overall session statistics.
     */
    private static void handleViewHistory() {
        history.printStatistics();
        history.printRecent(10);
    }

    /**
     * Clears recorded roll history after confirmation.
     */
    private static void handleClearHistory(Scanner scanner) {
        if (history.isEmpty()) {
            System.out.println("\n[!] Roll history is already empty.");
            return;
        }

        System.out.print("\nAre you sure you want to clear session history? (y/n): ");
        String confirmation = "";
        try {
            confirmation = scanner.nextLine().trim().toLowerCase();
        } catch (NoSuchElementException ignored) {
        }

        if (confirmation.equals("y") || confirmation.equals("yes")) {
            history.clear();
            System.out.println("[✓] Roll history cleared successfully.");
        } else {
            System.out.println("Cancelled. History was preserved.");
        }
    }

    /**
     * Safely reads an integer within the specified range from the scanner.
     */
    private static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line;
            try {
                line = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                return min;
            }

            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("[!] Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a valid integer.");
            }
        }
    }
}
