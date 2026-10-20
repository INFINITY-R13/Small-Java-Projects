package com.diceroller;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a single die with a configurable number of sides.
 * By default, represents a standard 6-sided die.
 */
public class Die {

    public static final int DEFAULT_SIDES = 6;
    public static final int MIN_SIDES = 2;
    public static final int MAX_SIDES = 10000;

    private final int sides;
    private int lastRoll;

    /**
     * Creates a standard 6-sided die.
     */
    public Die() {
        this(DEFAULT_SIDES);
    }

    /**
     * Creates a die with the specified number of sides.
     *
     * @param sides number of sides on the die (must be between 2 and 10,000)
     * @throws IllegalArgumentException if sides is less than 2 or exceeds MAX_SIDES
     */
    public Die(int sides) {
        if (sides < MIN_SIDES || sides > MAX_SIDES) {
            throw new IllegalArgumentException(
                String.format("Die must have between %d and %d sides (received %d).", MIN_SIDES, MAX_SIDES, sides)
            );
        }
        this.sides = sides;
        this.lastRoll = 0;
    }

    /**
     * Rolls the die and returns a random integer between 1 and sides (inclusive).
     *
     * @return the value rolled
     */
    public int roll() {
        this.lastRoll = ThreadLocalRandom.current().nextInt(1, sides + 1);
        return this.lastRoll;
    }

    /**
     * Returns the number of sides on this die.
     *
     * @return sides count
     */
    public int getSides() {
        return sides;
    }

    /**
     * Returns the outcome of the most recent roll, or 0 if never rolled.
     *
     * @return last rolled value
     */
    public int getLastRoll() {
        return lastRoll;
    }

    @Override
    public String toString() {
        return "d" + sides + (lastRoll > 0 ? " [last: " + lastRoll + "]" : "");
    }
}
