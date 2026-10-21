package com.tictactoe.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the 3x3 Tic Tac Toe game board.
 * Cells are indexed 1 through 9 matching standard keypad layout:
 *  1 | 2 | 3
 * ---+---+---
 *  4 | 5 | 6
 * ---+---+---
 *  7 | 8 | 9
 */
public class Board {

    public static final char EMPTY = ' ';
    public static final char PLAYER_X = 'X';
    public static final char PLAYER_O = 'O';

    private final char[] grid;

    private static final int[][] WINNING_LINES = {
        // Rows
        {0, 1, 2},
        {3, 4, 5},
        {6, 7, 8},
        // Columns
        {0, 3, 6},
        {1, 4, 7},
        {2, 5, 8},
        // Diagonals
        {0, 4, 8},
        {2, 4, 6}
    };

    public Board() {
        this.grid = new char[9];
        reset();
    }

    /**
     * Resets all cells to empty.
     */
    public void reset() {
        Arrays.fill(grid, EMPTY);
    }

    /**
     * Checks if a given position (1-9) is valid and empty.
     *
     * @param position 1 to 9
     * @return true if valid move
     */
    public boolean isValidMove(int position) {
        return position >= 1 && position <= 9 && grid[position - 1] == EMPTY;
    }

    /**
     * Places the player's symbol at the specified position.
     *
     * @param position 1 to 9
     * @param symbol 'X' or 'O'
     * @return true if move was applied, false if invalid
     */
    public boolean makeMove(int position, char symbol) {
        if (!isValidMove(position)) {
            return false;
        }
        grid[position - 1] = symbol;
        return true;
    }

    /**
     * Undoes a move at the specified position (reverts to EMPTY).
     *
     * @param position 1 to 9
     */
    public void undoMove(int position) {
        if (position >= 1 && position <= 9) {
            grid[position - 1] = EMPTY;
        }
    }

    /**
     * Returns the symbol at position (1-9).
     */
    public char getCell(int position) {
        if (position >= 1 && position <= 9) {
            return grid[position - 1];
        }
        return EMPTY;
    }

    /**
     * Returns a list of all currently available cell positions (1-9).
     */
    public List<Integer> getAvailableMoves() {
        List<Integer> moves = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (grid[i] == EMPTY) {
                moves.add(i + 1);
            }
        }
        return moves;
    }

    /**
     * Checks if the given symbol has won the game.
     *
     * @param symbol 'X' or 'O'
     * @return true if symbol has a winning combination
     */
    public boolean hasWon(char symbol) {
        for (int[] line : WINNING_LINES) {
            if (grid[line[0]] == symbol &&
                grid[line[1]] == symbol &&
                grid[line[2]] == symbol) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the board has no empty cells left.
     */
    public boolean isFull() {
        for (char c : grid) {
            if (c == EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the game has ended (either player won or board is full).
     */
    public boolean isGameOver() {
        return hasWon(PLAYER_X) || hasWon(PLAYER_O) || isFull();
    }

    /**
     * Returns the winning line indices (0-8) if someone won, or null if no winner.
     */
    public int[] getWinningLine(char symbol) {
        for (int[] line : WINNING_LINES) {
            if (grid[line[0]] == symbol &&
                grid[line[1]] == symbol &&
                grid[line[2]] == symbol) {
                return line;
            }
        }
        return null;
    }

    /**
     * Prints the current board state side-by-side with a position reference guide.
     */
    public void printBoard() {
        System.out.println();
        System.out.println("     Current Board             Position Guide");
        System.out.printf("       %c | %c | %c                 1 | 2 | 3%n", cellDisplay(0), cellDisplay(1), cellDisplay(2));
        System.out.println("      ---+---+---               ---+---+---");
        System.out.printf("       %c | %c | %c                 4 | 5 | 6%n", cellDisplay(3), cellDisplay(4), cellDisplay(5));
        System.out.println("      ---+---+---               ---+---+---");
        System.out.printf("       %c | %c | %c                 7 | 8 | 9%n", cellDisplay(6), cellDisplay(7), cellDisplay(8));
        System.out.println();
    }

    private char cellDisplay(int index) {
        return grid[index] == EMPTY ? ' ' : grid[index];
    }
}
