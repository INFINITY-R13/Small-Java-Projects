package com.diceroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the history and aggregate statistics of dice rolls during a session.
 */
public class RollHistory {

    private final List<DiceRollResult> history = new ArrayList<>();

    /**
     * Records a new roll result in the history.
     *
     * @param result the dice roll result to add
     */
    public synchronized void add(DiceRollResult result) {
        if (result != null) {
            history.add(result);
        }
    }

    /**
     * Returns an unmodifiable view of all recorded roll results.
     */
    public synchronized List<DiceRollResult> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    /**
     * Clears all roll history.
     */
    public synchronized void clear() {
        history.clear();
    }

    /**
     * Checks if history is empty.
     */
    public synchronized boolean isEmpty() {
        return history.isEmpty();
    }

    /**
     * Returns total number of roll operations performed.
     */
    public synchronized int getRollCount() {
        return history.size();
    }

    /**
     * Returns the aggregate number of individual dice thrown across all rolls.
     */
    public synchronized int getTotalDiceCount() {
        return history.stream().mapToInt(DiceRollResult::getDiceCount).sum();
    }

    /**
     * Returns the highest total produced in any single roll operation.
     */
    public synchronized int getHighestTotal() {
        return history.stream().mapToInt(DiceRollResult::getTotal).max().orElse(0);
    }

    /**
     * Returns the lowest total produced in any single roll operation.
     */
    public synchronized int getLowestTotal() {
        return history.stream().mapToInt(DiceRollResult::getTotal).min().orElse(0);
    }

    /**
     * Returns the grand total of all roll outcomes combined.
     */
    public synchronized long getGrandTotal() {
        return history.stream().mapToLong(DiceRollResult::getTotal).sum();
    }

    /**
     * Returns the average roll total across all rolls.
     */
    public synchronized double getAverageTotal() {
        return history.stream().mapToInt(DiceRollResult::getTotal).average().orElse(0.0);
    }

    /**
     * Prints a formatted table of the most recent rolls.
     *
     * @param limit maximum number of recent rolls to display
     */
    public synchronized void printRecent(int limit) {
        if (history.isEmpty()) {
            System.out.println("\n[!] No rolls have been recorded yet in this session.");
            return;
        }

        int start = Math.max(0, history.size() - limit);
        List<DiceRollResult> sub = history.subList(start, history.size());

        System.out.println("\n==================================================================");
        System.out.printf(" %-4s | %-8s | %-12s | %-24s | %-6s%n", "#", "Time", "Formula", "Rolls", "Total");
        System.out.println("------------------------------------------------------------------");

        for (int i = 0; i < sub.size(); i++) {
            DiceRollResult r = sub.get(i);
            int idx = start + i + 1;
            String rollsStr = r.getRolls().toString();
            if (rollsStr.length() > 24) {
                rollsStr = rollsStr.substring(0, 21) + "...";
            }
            System.out.printf(" %-4d | %-8s | %-12s | %-24s | %-6d%n",
                idx, r.getFormattedTimestamp(), r.getExpression(), rollsStr, r.getTotal());
        }
        System.out.println("==================================================================");
    }

    /**
     * Prints comprehensive aggregate statistics for the session.
     */
    public synchronized void printStatistics() {
        if (history.isEmpty()) {
            System.out.println("\n[!] No statistics available. Roll some dice first!");
            return;
        }

        System.out.println("\n==================================================");
        System.out.println("           📊 SESSION STATISTICS 📊");
        System.out.println("==================================================");
        System.out.printf("  Total Roll Commands : %d%n", getRollCount());
        System.out.printf("  Total Dice Rolled   : %d%n", getTotalDiceCount());
        System.out.printf("  Highest Roll Total  : %d%n", getHighestTotal());
        System.out.printf("  Lowest Roll Total   : %d%n", getLowestTotal());
        System.out.printf("  Grand Total Sum     : %d%n", getGrandTotal());
        System.out.printf("  Average Roll Total  : %.2f%n", getAverageTotal());
        System.out.println("==================================================");
    }
}
