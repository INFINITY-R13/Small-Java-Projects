package com.diceroller;

import java.util.List;

/**
 * Self-contained smoke and regression test suite for Dice Roller components.
 * Can be run independently without requiring external dependencies (like JUnit).
 */
public class DiceRollerTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("🧪 Running Dice Roller Test Suite...\n");

        testDieCreationAndRoll();
        testDieInvalidSides();
        testDiceNotationValid();
        testDiceNotationInvalid();
        testRollResultCalculations();
        testRollHistory();
        testAsciiArtRendering();

        System.out.println("\n----------------------------------------");
        System.out.printf("Test Results: %d Passed, %d Failed%n", testsPassed, testsFailed);
        System.out.println("----------------------------------------");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.printf("  [PASS] %s%n", testName);
            testsPassed++;
        } else {
            System.err.printf("  [FAIL] %s%n", testName);
            testsFailed++;
        }
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            System.out.printf("  [PASS] %s%n", testName);
            testsPassed++;
        } else {
            System.err.printf("  [FAIL] %s - Expected: %s, Actual: %s%n", testName, expected, actual);
            testsFailed++;
        }
    }

    private static void testDieCreationAndRoll() {
        Die d6 = new Die();
        assertEquals("Default die has 6 sides", 6, d6.getSides());
        for (int i = 0; i < 50; i++) {
            int roll = d6.roll();
            assertTrue("Roll within range 1..6", roll >= 1 && roll <= 6);
            assertEquals("Last roll matches returned value", roll, d6.getLastRoll());
        }

        Die d20 = new Die(20);
        assertEquals("Die(20) has 20 sides", 20, d20.getSides());
        for (int i = 0; i < 50; i++) {
            int roll = d20.roll();
            assertTrue("Roll within range 1..20", roll >= 1 && roll <= 20);
        }
    }

    private static void testDieInvalidSides() {
        boolean caught1 = false;
        try {
            new Die(1);
        } catch (IllegalArgumentException e) {
            caught1 = true;
        }
        assertTrue("Reject die with < 2 sides", caught1);

        boolean caught2 = false;
        try {
            new Die(20000);
        } catch (IllegalArgumentException e) {
            caught2 = true;
        }
        assertTrue("Reject die with sides exceeding MAX_SIDES", caught2);
    }

    private static void testDiceNotationValid() {
        assertTrue("Valid notation d6", DiceNotationParser.isValid("d6"));
        assertTrue("Valid notation 2d6", DiceNotationParser.isValid("2d6"));
        assertTrue("Valid notation 3d8+4", DiceNotationParser.isValid("3d8+4"));
        assertTrue("Valid notation 1d20-2", DiceNotationParser.isValid("1d20-2"));
        assertTrue("Valid notation with spaces ' 2d10 + 3 '", DiceNotationParser.isValid(" 2d10 + 3 "));

        DiceRollResult r1 = DiceNotationParser.roll("3d6+2");
        assertEquals("Parsed 3 dice", 3, r1.getDiceCount());
        assertEquals("Modifier +2", 2, r1.getModifier());
        assertEquals("Sides are 6", 6, r1.getSides());
        assertEquals("Total equals sum + modifier", r1.getDiceSum() + 2, r1.getTotal());

        DiceRollResult r2 = DiceNotationParser.roll("2d8-3");
        assertEquals("Parsed 2 dice", 2, r2.getDiceCount());
        assertEquals("Modifier -3", -3, r2.getModifier());
        assertEquals("Sides are 8", 8, r2.getSides());
        assertEquals("Total equals sum - 3", r2.getDiceSum() - 3, r2.getTotal());
    }

    private static void testDiceNotationInvalid() {
        String[] invalidInputs = { "", "abc", "2d", "d", "0d6", "2d1", "2d0", "99999d6" };
        for (String invalid : invalidInputs) {
            boolean caught = false;
            try {
                DiceNotationParser.roll(invalid);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTrue("Reject invalid notation: '" + invalid + "'", caught);
        }
    }

    private static void testRollResultCalculations() {
        List<Integer> rolls = List.of(2, 4, 6);
        DiceRollResult result = new DiceRollResult("3d6+1", rolls, 1, 6);

        assertEquals("Dice sum is 12", 12, result.getDiceSum());
        assertEquals("Total is 13", 13, result.getTotal());
        assertEquals("Min is 2", 2, result.getMin());
        assertEquals("Max is 6", 6, result.getMax());
        assertEquals("Average is 4.0", 4.0, result.getAverage());
    }

    private static void testRollHistory() {
        RollHistory history = new RollHistory();
        assertTrue("Initially empty history", history.isEmpty());

        DiceRollResult r1 = new DiceRollResult("2d6", List.of(3, 4), 0, 6);
        DiceRollResult r2 = new DiceRollResult("1d20", List.of(18), 0, 20);

        history.add(r1);
        history.add(r2);

        assertEquals("Roll count is 2", 2, history.getRollCount());
        assertEquals("Total dice count is 3", 3, history.getTotalDiceCount());
        assertEquals("Highest total is 18", 18, history.getHighestTotal());
        assertEquals("Lowest total is 7", 7, history.getLowestTotal());
        assertEquals("Grand total is 25", 25L, history.getGrandTotal());

        history.clear();
        assertTrue("History is empty after clear", history.isEmpty());
    }

    private static void testAsciiArtRendering() {
        String d6Art = DiceAsciiArt.renderD6(List.of(1, 2, 3, 4, 5, 6));
        assertTrue("ASCII art rendered non-empty for d6", d6Art != null && !d6Art.isEmpty());

        String polyArt = DiceAsciiArt.renderPolyhedral(List.of(15, 20), 20);
        assertTrue("ASCII art rendered non-empty for polyhedral", polyArt != null && !polyArt.isEmpty());
    }
}
