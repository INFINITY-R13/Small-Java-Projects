package com.blackjack.game;

/**
 * Outcomes for a hand evaluated against the dealer.
 */
public enum RoundResult {
    NATURAL_BLACKJACK("Natural Blackjack! Pays 3:2", 1.5),
    WIN("Player Wins! Pays 1:1", 1.0),
    PUSH("Push (Tie). Bet returned.", 0.0),
    LOSS("Dealer Wins. Bet lost.", -1.0),
    BUST("Busted! Over 21.", -1.0),
    SURRENDER("Surrendered. Forfeited half bet.", -0.5);

    private final String message;
    private final double profitMultiplier;

    RoundResult(String message, double profitMultiplier) {
        this.message = message;
        this.profitMultiplier = profitMultiplier;
    }

    public String getMessage() {
        return message;
    }

    public double getProfitMultiplier() {
        return profitMultiplier;
    }
}
