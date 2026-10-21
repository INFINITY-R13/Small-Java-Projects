package com.tictactoe.game;

import com.tictactoe.model.Player;

/**
 * Tracks and displays game session statistics (wins, losses, draws).
 */
public class GameScore {

    private int player1Wins;
    private int player2Wins;
    private int draws;

    public GameScore() {
        this.player1Wins = 0;
        this.player2Wins = 0;
        this.draws = 0;
    }

    public void recordPlayer1Win() {
        player1Wins++;
    }

    public void recordPlayer2Win() {
        player2Wins++;
    }

    public void recordDraw() {
        draws++;
    }

    public int getTotalGames() {
        return player1Wins + player2Wins + draws;
    }

    public void display(Player p1, Player p2) {
        System.out.println();
        System.out.println("  =====================================");
        System.out.println("               SCOREBOARD              ");
        System.out.println("  =====================================");
        System.out.printf("   %-20s (%c): %d wins%n", p1.getName(), p1.getSymbol(), player1Wins);
        System.out.printf("   %-20s (%c): %d wins%n", p2.getName(), p2.getSymbol(), player2Wins);
        System.out.printf("   %-25s: %d%n", "Draws / Ties", draws);
        System.out.printf("   %-25s: %d%n", "Total Matches Played", getTotalGames());
        System.out.println("  =====================================");
        System.out.println();
    }

    public void reset() {
        player1Wins = 0;
        player2Wins = 0;
        draws = 0;
    }
}
