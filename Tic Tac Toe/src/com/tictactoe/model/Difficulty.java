package com.tictactoe.model;

/**
 * Difficulty levels available for the AI opponent.
 */
public enum Difficulty {
    EASY("Easy (Random moves)"),
    MEDIUM("Medium (Tactical: attacks and blocks)"),
    HARD("Hard / Unbeatable (Minimax algorithm)");

    private final String description;

    Difficulty(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
