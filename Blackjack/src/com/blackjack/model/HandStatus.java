package com.blackjack.model;

/**
 * Lifecycle status of a hand during play.
 */
public enum HandStatus {
    IN_PLAY("In Play"),
    STOOD("Stood"),
    BUSTED("Busted"),
    BLACKJACK("Blackjack!"),
    SURRENDERED("Surrendered"),
    DOUBLED("Doubled Down");

    private final String description;

    HandStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
