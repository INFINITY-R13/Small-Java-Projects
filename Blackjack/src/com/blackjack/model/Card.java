package com.blackjack.model;

import com.blackjack.ui.ConsoleColors;
import java.util.Objects;

/**
 * Represents a standard playing card with a suit and rank.
 */
public class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Rank rank, Suit suit) {
        this.rank = Objects.requireNonNull(rank, "Rank must not be null");
        this.suit = Objects.requireNonNull(suit, "Suit must not be null");
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public int getValue() {
        return rank.getValue();
    }

    public boolean isAce() {
        return rank.isAce();
    }

    /**
     * Compact string e.g. "A♠" or "10♥" with suit color.
     */
    public String getShortDisplay() {
        return ConsoleColors.colorize(suit.getColorCode(), rank.getSymbol() + suit.getSymbol());
    }

    /**
     * Uncolored compact string for length calculations e.g. "10♥" or "A♠".
     */
    public String getRawShortDisplay() {
        return rank.getSymbol() + suit.getSymbol();
    }

    /**
     * Full string e.g. "Ace of Spades (♠)"
     */
    public String getFullDisplay() {
        return rank.getDisplayName() + " of " + suit.toString();
    }

    @Override
    public String toString() {
        return getFullDisplay();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card card)) return false;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }
}
