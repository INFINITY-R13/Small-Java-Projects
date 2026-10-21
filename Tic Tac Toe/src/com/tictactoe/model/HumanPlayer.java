package com.tictactoe.model;

import java.util.Scanner;

/**
 * Represents a human player entering moves through the console.
 */
public class HumanPlayer implements Player {

    private final String name;
    private final char symbol;
    private final Scanner scanner;

    public HumanPlayer(String name, char symbol, Scanner scanner) {
        this.name = name;
        this.symbol = symbol;
        this.scanner = scanner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public char getSymbol() {
        return symbol;
    }

    @Override
    public int getMove(Board board) {
        while (true) {
            System.out.printf("[%s (%c)] Enter your move (1-9): ", name, symbol);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            int position;
            try {
                position = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(">> Invalid input! Please enter a number between 1 and 9.");
                continue;
            }

            if (position < 1 || position > 9) {
                System.out.println(">> Out of bounds! Please enter a number between 1 and 9.");
                continue;
            }

            if (!board.isValidMove(position)) {
                System.out.printf(">> Position %d is already occupied! Choose another cell.%n", position);
                continue;
            }

            return position;
        }
    }
}
