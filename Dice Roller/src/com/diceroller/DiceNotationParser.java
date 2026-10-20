package com.diceroller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and executes RPG dice notation strings like "2d6", "1d20+5", "3d8-2", or "d100".
 */
public final class DiceNotationParser {

    public static final int MAX_DICE_COUNT = 500;
    public static final int MAX_SIDES = 10000;
    public static final int MAX_MODIFIER = 10000;

    // Matches patterns like "2d6", "d20", "3d8+5", "4d10-2" (case-insensitive, optional spaces)
    private static final Pattern DICE_PATTERN = Pattern.compile(
        "^\\s*(\\d+)?\\s*[dD]\\s*(\\d+)(?:\\s*([+-])\\s*(\\d+))?\\s*$"
    );

    private DiceNotationParser() {
        // Utility class
    }

    /**
     * Checks if a string conforms to dice notation.
     *
     * @param notation the string to test
     * @return true if valid dice notation format
     */
    public static boolean isValid(String notation) {
        if (notation == null) {
            return false;
        }
        return DICE_PATTERN.matcher(notation.trim()).matches();
    }

    /**
     * Parses the dice notation expression and executes the rolls.
     *
     * @param notation dice expression, e.g., "2d6+3", "1d20", "d12-1"
     * @return DiceRollResult containing individual outcomes and the total
     * @throws IllegalArgumentException if the expression cannot be parsed or values are out of bounds
     */
    public static DiceRollResult roll(String notation) {
        if (notation == null || notation.trim().isEmpty()) {
            throw new IllegalArgumentException("Dice expression cannot be empty.");
        }

        String cleaned = notation.trim();
        Matcher matcher = DICE_PATTERN.matcher(cleaned);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                String.format("Invalid dice notation '%s'. Examples of valid expressions: '2d6', 'd20', '3d8+4', '1d100-5'.", notation)
            );
        }

        // Dice count (group 1, optional; defaults to 1)
        int count = 1;
        String countGroup = matcher.group(1);
        if (countGroup != null && !countGroup.isEmpty()) {
            count = Integer.parseInt(countGroup);
        }

        if (count < 1 || count > MAX_DICE_COUNT) {
            throw new IllegalArgumentException(
                String.format("Dice count must be between 1 and %d (received %d).", MAX_DICE_COUNT, count)
            );
        }

        // Sides (group 2)
        int sides = Integer.parseInt(matcher.group(2));
        if (sides < Die.MIN_SIDES || sides > MAX_SIDES) {
            throw new IllegalArgumentException(
                String.format("Dice sides must be between %d and %d (received %d).", Die.MIN_SIDES, MAX_SIDES, sides)
            );
        }

        // Modifier (group 3 sign, group 4 value)
        int modifier = 0;
        String sign = matcher.group(3);
        String modVal = matcher.group(4);
        if (sign != null && modVal != null) {
            int val = Integer.parseInt(modVal);
            if (val > MAX_MODIFIER) {
                throw new IllegalArgumentException(
                    String.format("Modifier cannot exceed %d (received %d).", MAX_MODIFIER, val)
                );
            }
            modifier = sign.equals("-") ? -val : val;
        }

        // Execute rolls
        Die die = new Die(sides);
        List<Integer> rolls = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            rolls.add(die.roll());
        }

        // Standardized expression representation (e.g., "2d6+3")
        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(count).append("d").append(sides);
        if (modifier > 0) {
            exprBuilder.append("+").append(modifier);
        } else if (modifier < 0) {
            exprBuilder.append("-").append(Math.abs(modifier));
        }

        return new DiceRollResult(exprBuilder.toString(), rolls, modifier, sides);
    }
}
