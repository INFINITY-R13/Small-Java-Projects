package com.blackjack.game;

import com.blackjack.model.Hand;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a human Blackjack player, managing chip stack and active hands.
 */
public class Player {
    private final String name;
    private int chips;
    private final List<Hand> hands;
    private int insuranceBet;

    public Player(String name, int startingChips) {
        this.name = (name == null || name.trim().isEmpty()) ? "Player" : name.trim();
        this.chips = Math.max(0, startingChips);
        this.hands = new ArrayList<>();
        this.insuranceBet = 0;
    }

    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }

    public void addChips(int amount) {
        if (amount > 0) {
            chips += amount;
        }
    }

    public boolean deductChips(int amount) {
        if (amount > 0 && chips >= amount) {
            chips -= amount;
            return true;
        }
        return false;
    }

    public boolean hasSufficientChips(int amount) {
        return amount > 0 && chips >= amount;
    }

    public List<Hand> getHands() {
        return Collections.unmodifiableList(hands);
    }

    public void addHand(Hand hand) {
        hands.add(hand);
    }

    public Hand getHand(int index) {
        return hands.get(index);
    }

    public int getHandCount() {
        return hands.size();
    }

    public void clearHands() {
        hands.clear();
        insuranceBet = 0;
    }

    public int getInsuranceBet() {
        return insuranceBet;
    }

    public void setInsuranceBet(int insuranceBet) {
        this.insuranceBet = insuranceBet;
    }
}
