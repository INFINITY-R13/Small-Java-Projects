package com.blackjack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a hand of cards held by a player or dealer.
 */
public class Hand {
    private final List<Card> cards;
    private int bet;
    private HandStatus status;
    private boolean fromSplit;

    public Hand() {
        this(0);
    }

    public Hand(int bet) {
        this.cards = new ArrayList<>();
        this.bet = bet;
        this.status = HandStatus.IN_PLAY;
        this.fromSplit = false;
    }

    public void addCard(Card card) {
        if (card != null) {
            cards.add(card);
            updateStatus();
        }
    }

    public Card removeCard(int index) {
        Card removed = cards.remove(index);
        updateStatus();
        return removed;
    }

    private void updateStatus() {
        if (isBust()) {
            status = HandStatus.BUSTED;
        } else if (isNaturalBlackjack()) {
            status = HandStatus.BLACKJACK;
        }
    }

    /**
     * Calculates the best Blackjack value for this hand.
     * Aces are dynamically evaluated as 11 or 1 to achieve the highest value <= 21.
     */
    public int calculateValue() {
        int total = 0;
        int acesAsEleven = 0;

        for (Card card : cards) {
            total += card.getValue();
            if (card.isAce()) {
                acesAsEleven++;
            }
        }

        while (total > 21 && acesAsEleven > 0) {
            total -= 10;
            acesAsEleven--;
        }

        return total;
    }

    /**
     * Checks if this hand has a "soft" total (contains an Ace counted as 11).
     */
    public boolean isSoft() {
        int total = 0;
        int acesAsEleven = 0;

        for (Card card : cards) {
            total += card.getValue();
            if (card.isAce()) {
                acesAsEleven++;
            }
        }

        while (total > 21 && acesAsEleven > 0) {
            total -= 10;
            acesAsEleven--;
        }

        return acesAsEleven > 0;
    }

    /**
     * Returns true if this is an initial 2-card 21 (natural Blackjack), not from a split.
     */
    public boolean isNaturalBlackjack() {
        return !fromSplit && cards.size() == 2 && calculateValue() == 21;
    }

    /**
     * Returns true if the hand total exceeds 21.
     */
    public boolean isBust() {
        return calculateValue() > 21;
    }

    /**
     * Returns true if the hand has exactly 21.
     */
    public boolean isTwentyOne() {
        return calculateValue() == 21;
    }

    /**
     * Eligible for split if hand has exactly 2 cards with matching rank value.
     */
    public boolean canSplit() {
        return cards.size() == 2 && cards.get(0).getValue() == cards.get(1).getValue();
    }

    /**
     * Eligible to double down if hand has exactly 2 cards.
     */
    public boolean canDoubleDown() {
        return cards.size() == 2 && status == HandStatus.IN_PLAY;
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public int cardCount() {
        return cards.size();
    }

    public Card getCard(int index) {
        return cards.get(index);
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public HandStatus getStatus() {
        return status;
    }

    public void setStatus(HandStatus status) {
        this.status = status;
    }

    public boolean isFromSplit() {
        return fromSplit;
    }

    public void setFromSplit(boolean fromSplit) {
        this.fromSplit = fromSplit;
    }

    public void clear() {
        cards.clear();
        bet = 0;
        status = HandStatus.IN_PLAY;
        fromSplit = false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            sb.append(cards.get(i).getShortDisplay());
            if (i < cards.size() - 1) sb.append(" ");
        }
        sb.append(" (Total: ").append(calculateValue());
        if (isSoft()) {
            sb.append(" soft");
        }
        sb.append(")");
        return sb.toString();
    }
}
