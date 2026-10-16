package com.blackjack.game;

import com.blackjack.ui.ConsoleColors;

/**
 * Tracks performance statistics over the course of a Blackjack session.
 */
public class GameStats {
    private final int startingChips;
    private int roundsPlayed;
    private int handsWon;
    private int handsLost;
    private int handsPushed;
    private int blackjacksHit;
    private int peakChips;
    private int currentChips;

    public GameStats(int startingChips) {
        this.startingChips = startingChips;
        this.currentChips = startingChips;
        this.peakChips = startingChips;
    }

    public void updateBalance(int currentChips) {
        this.currentChips = currentChips;
        if (currentChips > peakChips) {
            peakChips = currentChips;
        }
    }

    public void recordRound() {
        roundsPlayed++;
    }

    public void recordWin(boolean isBlackjack) {
        handsWon++;
        if (isBlackjack) {
            blackjacksHit++;
        }
    }

    public void recordLoss() {
        handsLost++;
    }

    public void recordPush() {
        handsPushed++;
    }

    public double getWinRate() {
        int totalDecisive = handsWon + handsLost;
        if (totalDecisive == 0) return 0.0;
        return ((double) handsWon / totalDecisive) * 100.0;
    }

    public int getNetProfit() {
        return currentChips - startingChips;
    }

    public String generateSummary() {
        int net = getNetProfit();
        String netColor = net >= 0 ? ConsoleColors.GREEN : ConsoleColors.RED;
        String sign = net > 0 ? "+" : "";

        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.colorize(ConsoleColors.BOLD_CYAN, "════════════════════ SESSION STATISTICS ════════════════════\n"));
        sb.append(String.format("  Rounds Played     : %d\n", roundsPlayed));
        sb.append(String.format("  Hands Won         : %s (Blackjacks: %d)\n",
                ConsoleColors.colorize(ConsoleColors.GREEN, String.valueOf(handsWon)), blackjacksHit));
        sb.append(String.format("  Hands Lost        : %s\n",
                ConsoleColors.colorize(ConsoleColors.RED, String.valueOf(handsLost))));
        sb.append(String.format("  Hands Pushed (Tie): %s\n",
                ConsoleColors.colorize(ConsoleColors.YELLOW, String.valueOf(handsPushed))));
        sb.append(String.format("  Win Rate (ex. tie): %.1f%%\n", getWinRate()));
        sb.append(String.format("  Starting Chips    : $%,d\n", startingChips));
        sb.append(String.format("  Peak Chips        : $%,d\n", peakChips));
        sb.append(String.format("  Ending Chips      : $%,d\n", currentChips));
        sb.append(String.format("  Net Profit / Loss : %s\n",
                ConsoleColors.colorize(netColor, String.format("%s$%,d", sign, net))));
        sb.append(ConsoleColors.colorize(ConsoleColors.BOLD_CYAN, "════════════════════════════════════════════════════════════\n"));
        return sb.toString();
    }

    public int getStartingChips() {
        return startingChips;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public int getHandsWon() {
        return handsWon;
    }

    public int getHandsLost() {
        return handsLost;
    }

    public int getHandsPushed() {
        return handsPushed;
    }

    public int getBlackjacksHit() {
        return blackjacksHit;
    }

    public int getPeakChips() {
        return peakChips;
    }
}
