package com.tictactoe;

import com.tictactoe.game.TicTacToeGame;
import com.tictactoe.model.AIPlayer;
import com.tictactoe.model.Board;
import com.tictactoe.model.Difficulty;
import com.tictactoe.model.HumanPlayer;
import com.tictactoe.model.Player;

import java.util.Scanner;

/**
 * Main entry point for the Tic Tac Toe application.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    startTwoPlayerGame();
                    break;
                case "2":
                    startSinglePlayerGame();
                    break;
                case "3":
                    startAIVsAIGame();
                    break;
                case "4":
                    displayRules();
                    break;
                case "5":
                    System.out.println("\nThank you for playing Tic Tac Toe! Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please choose an option from 1 to 5.");
                    break;
            }
        }

        scanner.close();
    }

    private static void printBanner() {
        System.out.println("=================================================");
        System.out.println("            TIC TAC TOE - JAVA EDITION           ");
        System.out.println("=================================================");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("MAIN MENU:");
        System.out.println("  1. Two Player (Pass & Play)");
        System.out.println("  2. Single Player (vs AI Computer)");
        System.out.println("  3. AI vs AI (Watch Demo Match)");
        System.out.println("  4. How to Play & Rules");
        System.out.println("  5. Exit");
        System.out.print("Choose an option (1-5): ");
    }

    private static void startTwoPlayerGame() {
        System.out.println("\n--- TWO PLAYER MODE ---");

        System.out.print("Enter name for Player 1 (X) [Default: Player 1]: ");
        String name1 = scanner.nextLine().trim();
        if (name1.isEmpty()) {
            name1 = "Player 1";
        }

        System.out.print("Enter name for Player 2 (O) [Default: Player 2]: ");
        String name2 = scanner.nextLine().trim();
        if (name2.isEmpty()) {
            name2 = "Player 2";
        }

        Player p1 = new HumanPlayer(name1, Board.PLAYER_X, scanner);
        Player p2 = new HumanPlayer(name2, Board.PLAYER_O, scanner);

        TicTacToeGame game = new TicTacToeGame(p1, p2, scanner);
        game.start();
    }

    private static void startSinglePlayerGame() {
        System.out.println("\n--- SINGLE PLAYER MODE (VS COMPUTER) ---");

        System.out.print("Enter your name [Default: Player]: ");
        String playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) {
            playerName = "Player";
        }

        Difficulty difficulty = selectDifficulty();

        System.out.println("\nWho plays first?");
        System.out.println("  1. You play first (X)");
        System.out.println("  2. Computer plays first (X)");
        System.out.print("Choose (1 or 2) [Default: 1]: ");
        String orderChoice = scanner.nextLine().trim();

        Player p1;
        Player p2;

        if ("2".equals(orderChoice)) {
            p1 = new AIPlayer("Computer", Board.PLAYER_X, difficulty);
            p2 = new HumanPlayer(playerName, Board.PLAYER_O, scanner);
        } else {
            p1 = new HumanPlayer(playerName, Board.PLAYER_X, scanner);
            p2 = new AIPlayer("Computer", Board.PLAYER_O, difficulty);
        }

        TicTacToeGame game = new TicTacToeGame(p1, p2, scanner);
        game.start();
    }

    private static void startAIVsAIGame() {
        System.out.println("\n--- AI VS AI DEMO MODE ---");
        System.out.println("Select difficulty for Computer 1 (X):");
        Difficulty diff1 = selectDifficulty();

        System.out.println("Select difficulty for Computer 2 (O):");
        Difficulty diff2 = selectDifficulty();

        Player bot1 = new AIPlayer("AlphaBot", Board.PLAYER_X, diff1);
        Player bot2 = new AIPlayer("OmegaBot", Board.PLAYER_O, diff2);

        TicTacToeGame game = new TicTacToeGame(bot1, bot2, scanner);
        game.start();
    }

    private static Difficulty selectDifficulty() {
        while (true) {
            System.out.println("\nSelect AI Difficulty:");
            System.out.println("  1. Easy (Random moves)");
            System.out.println("  2. Medium (Tactical: blocks and takes wins)");
            System.out.println("  3. Hard (Unbeatable Minimax)");
            System.out.print("Choose (1-3) [Default: 3]: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty() || input.equals("3")) {
                return Difficulty.HARD;
            } else if (input.equals("1")) {
                return Difficulty.EASY;
            } else if (input.equals("2")) {
                return Difficulty.MEDIUM;
            } else {
                System.out.println("Invalid selection. Please choose 1, 2, or 3.");
            }
        }
    }

    private static void displayRules() {
        System.out.println("\n=================================================");
        System.out.println("                  HOW TO PLAY                    ");
        System.out.println("=================================================");
        System.out.println("1. Tic Tac Toe is played on a 3x3 grid.");
        System.out.println("2. Players take turns placing their mark ('X' or 'O').");
        System.out.println("3. Use the numbers 1 through 9 to pick your cell:");
        System.out.println();
        System.out.println("     1 | 2 | 3");
        System.out.println("    ---+---+---");
        System.out.println("     4 | 5 | 6");
        System.out.println("    ---+---+---");
        System.out.println("     7 | 8 | 9");
        System.out.println();
        System.out.println("4. The first player to get 3 marks in a row");
        System.out.println("   (horizontal, vertical, or diagonal) wins!");
        System.out.println("5. If all 9 squares are filled and no player has");
        System.out.println("   3 in a row, the match is declared a draw.");
        System.out.println("=================================================");
    }
}
