package com.tictactoe.game;

import com.tictactoe.model.Board;
import com.tictactoe.model.Player;

import java.util.Scanner;

/**
 * Coordinates game rounds, player turns, winning condition checks,
 * and rematch loops.
 */
public class TicTacToeGame {

    private final Player player1;
    private final Player player2;
    private final Board board;
    private final GameScore score;
    private final Scanner scanner;

    public TicTacToeGame(Player player1, Player player2, Scanner scanner) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new Board();
        this.score = new GameScore();
        this.scanner = scanner;
    }

    /**
     * Runs the game session until the players choose to stop.
     */
    public void start() {
        int round = 1;
        boolean keepPlaying = true;

        while (keepPlaying) {
            System.out.println();
            System.out.println("---------------------------------------------");
            System.out.printf("               ROUND %d START                %n", round);
            System.out.printf("        %s (%c) vs %s (%c)%n",
                    player1.getName(), player1.getSymbol(),
                    player2.getName(), player2.getSymbol());
            System.out.println("---------------------------------------------");

            board.reset();

            // Alternate starting player each round
            Player current = (round % 2 == 1) ? player1 : player2;
            playMatch(current);

            score.display(player1, player2);

            keepPlaying = askPlayAgain();
            if (keepPlaying) {
                round++;
            }
        }

        System.out.println("Thanks for playing this match session!");
    }

    /**
     * Executes a single game of Tic Tac Toe until win or draw.
     */
    private void playMatch(Player current) {
        while (true) {
            board.printBoard();
            System.out.printf("Turn: %s (%c)%n", current.getName(), current.getSymbol());

            int move = current.getMove(board);
            board.makeMove(move, current.getSymbol());

            // Check for win
            if (board.hasWon(current.getSymbol())) {
                board.printBoard();
                System.out.println("*********************************************");
                System.out.printf("  WINNER! Congratulations %s (%c)!%n", current.getName(), current.getSymbol());
                System.out.println("*********************************************");

                if (current == player1) {
                    score.recordPlayer1Win();
                } else {
                    score.recordPlayer2Win();
                }
                break;
            }

            // Check for draw
            if (board.isFull()) {
                board.printBoard();
                System.out.println("*********************************************");
                System.out.println("  IT'S A DRAW! Well played by both players.");
                System.out.println("*********************************************");
                score.recordDraw();
                break;
            }

            // Switch player
            current = (current == player1) ? player2 : player1;
        }
    }

    /**
     * Asks user if they wish to play another round.
     */
    private boolean askPlayAgain() {
        while (true) {
            System.out.print("Play another round? (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("Please enter 'y' for yes or 'n' for no.");
        }
    }
}
