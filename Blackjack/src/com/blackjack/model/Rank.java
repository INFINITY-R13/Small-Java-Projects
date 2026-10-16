package com.blackjack.model;

/**
 * Standard playing card ranks with Blackjack scoring values.
 */
public enum Rank {
    TWO("2", "Two", 2),
    THREE("3", "Three", 3),
    FOUR("4", "Four", 4),
    FIVE("5", "Five", 5),
    SIX("6", "Six", 6),
    SEVEN("7", "Seven", 7),
    EIGHT("8", "Eight", 8),
    NINE("9", "Nine", 9),
    TEN("10", "Ten", 10),
    JACK("J", "Jack", 10),
    QUEEN("Q", "Queen", 10),
    KING("K", "King", 10),
    ACE("A", "Ace", 11);

    private final String symbol;
    private final String displayName;
    private final int value;

    Rank(String symbol, String displayName, int value) {
        this.symbol = symbol;
        this.displayName = displayName;
        this.value = value;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getValue() {
        return value;
    }

    public boolean isAce() {
        return this == ACE;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
