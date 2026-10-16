package com.blackjack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents a deck or multi-deck shoe of cards used in Blackjack.
 */
public class Deck {
    public static final int DEFAULT_DECK_COUNT = 4;
    private static final double CUT_CARD_PENETRATION = 0.25; // Reshuffle when 25% cards left

    private final int numberOfDecks;
    private final List<Card> cards;
    private final Random random;

    public Deck() {
        this(DEFAULT_DECK_COUNT);
    }

    public Deck(int numberOfDecks) {
        if (numberOfDecks < 1) {
            throw new IllegalArgumentException("Number of decks must be at least 1");
        }
        this.numberOfDecks = numberOfDecks;
        this.cards = new ArrayList<>(numberOfDecks * 52);
        this.random = new Random();
        resetAndShuffle();
    }

    /**
     * Rebuilds the shoe with all 52 cards per deck and shuffles thoroughly.
     */
    public void resetAndShuffle() {
        cards.clear();
        for (int d = 0; d < numberOfDecks; d++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    cards.add(new Card(rank, suit));
                }
            }
        }
        Collections.shuffle(cards, random);
    }

    /**
     * Deals a card from the top of the deck.
     */
    public Card dealCard() {
        if (cards.isEmpty()) {
            resetAndShuffle();
        }
        return cards.remove(cards.size() - 1);
    }

    /**
     * Checks if the cut card has been reached, signaling a reshuffle is recommended.
     */
    public boolean isCutCardReached() {
        int totalOriginal = numberOfDecks * 52;
        return cards.size() <= (totalOriginal * CUT_CARD_PENETRATION);
    }

    public int remainingCards() {
        return cards.size();
    }

    public int totalCards() {
        return numberOfDecks * 52;
    }

    public int getNumberOfDecks() {
        return numberOfDecks;
    }
}
