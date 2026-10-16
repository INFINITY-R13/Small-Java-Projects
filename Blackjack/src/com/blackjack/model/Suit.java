package com.blackjack.model;

import com.blackjack.ui.ConsoleColors;

/**
 * Standard playing card suits.
 */
public enum Suit {
    HEARTS("♥", "Hearts", ConsoleColors.RED),
    DIAMONDS("♦", "Diamonds", ConsoleColors.RED),
    CLUBS("♣", "Clubs", ConsoleColors.CYAN),
    SPADES("♠", "Spades", ConsoleColors.CYAN);

    private final String symbol;
    private final String displayName;
    private final String colorCode;

    Suit(String symbol, String displayName, String colorCode) {
        this.symbol = symbol;
        this.displayName = displayName;
        this.colorCode = colorCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }

    @Override
    public String toString() {
        return ConsoleColors.colorize(colorCode, symbol + " " + displayName);
    }
}
