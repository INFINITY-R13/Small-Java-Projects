package com.tictactoe.model;

/**
 * Interface representing a Tic Tac Toe player (human or AI).
 */
public interface Player {

    /**
     * Gets the display name of the player.
     *
     * @return player name
     */
    String getName();

    /**
     * Gets the symbol representing the player ('X' or 'O').
     *
     * @return player symbol
     */
    char getSymbol();

    /**
     * Determines the next move on the given board.
     *
     * @param board current state of the board
     * @return grid position from 1 to 9
     */
    int getMove(Board board);
}
