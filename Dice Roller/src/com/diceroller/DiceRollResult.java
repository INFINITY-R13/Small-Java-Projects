package com.diceroller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the complete result of a dice roll operation.
 */
public class DiceRollResult {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String expression;
    private final List<Integer> rolls;
    private final int modifier;
    private final int diceSum;
    private final int total;
    private final int sides;
    private final LocalDateTime timestamp;

    public DiceRollResult(String expression, List<Integer> rolls, int modifier, int sides) {
        this.expression = expression;
        this.rolls = Collections.unmodifiableList(new ArrayList<>(rolls));
        this.modifier = modifier;
        this.sides = sides;
        this.diceSum = rolls.stream().mapToInt(Integer::intValue).sum();
        this.total = this.diceSum + modifier;
        this.timestamp = LocalDateTime.now();
    }

    public String getExpression() {
        return expression;
    }

    public List<Integer> getRolls() {
        return rolls;
    }

    public int getModifier() {
        return modifier;
    }

    public int getDiceSum() {
        return diceSum;
    }

    public int getTotal() {
        return total;
    }

    public int getSides() {
        return sides;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(TIME_FORMATTER);
    }

    public int getDiceCount() {
        return rolls.size();
    }

    public int getMin() {
        return rolls.stream().mapToInt(Integer::intValue).min().orElse(0);
    }

    public int getMax() {
        return rolls.stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    public double getAverage() {
        return rolls.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    /**
     * Produces a readable string summary of this roll.
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s] %s => ", getFormattedTimestamp(), expression));

        if (rolls.size() == 1 && modifier == 0) {
            sb.append(rolls.get(0));
        } else {
            sb.append(rolls);
            if (modifier > 0) {
                sb.append(" + ").append(modifier);
            } else if (modifier < 0) {
                sb.append(" - ").append(Math.abs(modifier));
            }
            sb.append(" = ").append(total);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getSummary();
    }
}
