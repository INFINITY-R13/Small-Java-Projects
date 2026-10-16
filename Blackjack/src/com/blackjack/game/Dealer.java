package com.blackjack.game;

import com.blackjack.model.Card;
import com.blackjack.model.Hand;

/**
 * Represents the casino Dealer in Blackjack.
 * The dealer follows strict casino house rules:
 * - One card is face-up, one is face-down (hole card) initially.
 * - Hits on totals of 16 or less.
 * - Stands on all 17s (hard or soft 17).
 */
public class Dealer {
    private final Hand hand;

    public Dealer() {
        this.hand = new Hand();
    }

    public void addCard(Card card) {
        hand.addCard(card);
    }

    public Hand getHand() {
        return hand;
    }

    public Card getUpCard() {
        if (hand.cardCount() > 0) {
            return hand.getCard(0);
        }
        return null;
    }

    public Card getHoleCard() {
        if (hand.cardCount() > 1) {
            return hand.getCard(1);
        }
        return null;
    }

    /**
     * Standard dealer strategy: hits if score < 17.
     */
    public boolean shouldHit() {
        return hand.calculateValue() < 17;
    }

    public boolean hasAceUp() {
        Card up = getUpCard();
        return up != null && up.isAce();
    }

    public boolean hasTenUp() {
        Card up = getUpCard();
        return up != null && up.getValue() == 10;
    }

    public boolean isBlackjack() {
        return hand.isNaturalBlackjack();
    }

    public boolean isBust() {
        return hand.isBust();
    }

    public int getValue() {
        return hand.calculateValue();
    }

    public void clearHand() {
        hand.clear();
    }
}
