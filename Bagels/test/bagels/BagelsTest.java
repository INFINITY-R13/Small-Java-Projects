package bagels;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Self-contained unit test suite for the Bagels game.
 * Can be run directly without external testing frameworks.
 */
public class BagelsTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("Running Bagels Test Suite...\n");

        testSecretGeneration();
        testSecretGenerationValidation();
        testClassicClueScenarios();
        testCluesAlphabeticalOrder();
        testDuplicateDigitsInGuessHandling();
        testMultiDigitClues();
        testGuessValidation();
        testDifficulty();
        testGameStats();
        testAnsiClueFormatting();

        System.out.println("\n" + "=".repeat(45));
        System.out.printf("Test Results: %d run, %d passed, %d failed.%n",
            testsRun, testsPassed, testsFailed);
        System.out.println("=".repeat(45));

        if (testsFailed > 0) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED SUCCESSFULLY! ✓");
        }
    }

    private static void testSecretGeneration() {
        // Test various lengths
        for (int len : new int[]{1, 3, 4, 5, 10}) {
            String secret = BagelsGame.generateSecretNumber(len);
            assertEquals(len, secret.length(), "Secret length must match requested length");

            Set<Character> uniqueDigits = new HashSet<>();
            for (char c : secret.toCharArray()) {
                assertTrue(Character.isDigit(c), "Character must be a digit");
                uniqueDigits.add(c);
            }
            assertEquals(len, uniqueDigits.size(), "All digits in secret must be unique");
        }

        // Test determinism with seeded Random
        Random r1 = new Random(12345);
        Random r2 = new Random(12345);
        String s1 = BagelsGame.generateSecretNumber(3, r1);
        String s2 = BagelsGame.generateSecretNumber(3, r2);
        assertEquals(s1, s2, "Same random seed must produce identical secret numbers");
    }

    private static void testSecretGenerationValidation() {
        assertThrows(() -> BagelsGame.generateSecretNumber(0),
            IllegalArgumentException.class, "0 digits should throw");
        assertThrows(() -> BagelsGame.generateSecretNumber(-1),
            IllegalArgumentException.class, "Negative digits should throw");
        assertThrows(() -> BagelsGame.generateSecretNumber(11),
            IllegalArgumentException.class, "More than 10 digits should throw");
        assertThrows(() -> BagelsGame.generateSecretNumber(3, null),
            IllegalArgumentException.class, "Null random generator should throw");
    }

    private static void testClassicClueScenarios() {
        String secret = "248";

        // From classic Al Sweigart Bagels specification:
        // Guess 843 -> 4 is Fermi, 8 is Pico -> "Fermi Pico"
        assertEquals("Fermi Pico", BagelsGame.getClues("843", secret), "Guess 843 on 248");

        // Guess 135 -> Bagels
        assertEquals("Bagels", BagelsGame.getClues("135", secret), "Guess 135 on 248");

        // Guess 482 -> All 3 are Pico
        assertEquals("Pico Pico Pico", BagelsGame.getClues("482", secret), "Guess 482 on 248");

        // Exact match
        assertEquals("Fermi Fermi Fermi", BagelsGame.getClues("248", secret), "Guess 248 on 248");
        assertTrue(BagelsGame.isWinningGuess("248", secret), "248 is winning guess");
        assertFalse(BagelsGame.isWinningGuess("842", secret), "842 is not winning guess");
    }

    private static void testCluesAlphabeticalOrder() {
        // "Fermi" (starts with F) must always precede "Pico" (starts with P)
        String secret = "123";
        // Guess "329" -> 2 is Fermi, 3 is Pico -> must be "Fermi Pico"
        assertEquals("Fermi Pico", BagelsGame.getClues("329", secret), "Fermi before Pico");

        // Guess "132" -> 1 is Fermi, 3 & 2 are Pico -> "Fermi Pico Pico"
        assertEquals("Fermi Pico Pico", BagelsGame.getClues("132", secret), "Fermi Pico Pico order");
    }

    private static void testDuplicateDigitsInGuessHandling() {
        String secret = "123";

        // If player enters repeated digit "111", only the one at index 0 is Fermi, others shouldn't generate extra Fermi or Pico
        assertEquals("Fermi", BagelsGame.getClues("111", secret), "111 on 123 yields single Fermi");

        // "222" -> guess[1] matches secret[1] ('2') exactly, so it yields Fermi (the other two '2's find no unused '2')
        assertEquals("Fermi", BagelsGame.getClues("222", secret), "222 on 123 yields single Fermi");

        // "242" -> pos 0 has '2' (secret has '2' at pos 1) -> Pico. pos 2 has '2', but secret '2' already matched -> single Pico
        assertEquals("Pico", BagelsGame.getClues("242", secret), "242 on 123 yields single Pico");

        // "112" -> '1' at pos 0 is Fermi, '2' at pos 2 is Pico, duplicate '1' gives nothing
        assertEquals("Fermi Pico", BagelsGame.getClues("112", secret), "112 on 123");
    }

    private static void testMultiDigitClues() {
        // 4 digits
        String secret4 = "1234";
        assertEquals("Fermi Fermi Pico Pico", BagelsGame.getClues("1243", secret4), "4-digit mixed");
        assertEquals("Bagels", BagelsGame.getClues("5678", secret4), "4-digit Bagels");

        // 5 digits
        String secret5 = "56789";
        assertEquals("Fermi Fermi Fermi Fermi Fermi", BagelsGame.getClues("56789", secret5), "5-digit exact");
        assertEquals("Pico Pico Pico Pico Pico", BagelsGame.getClues("67895", secret5), "5-digit rotated");
    }

    private static void testGuessValidation() {
        // Valid 3-digit guesses
        assertTrue(BagelsGame.validateGuess("123", 3).isValid(), "123 is valid");
        assertTrue(BagelsGame.validateGuess("048", 3).isValid(), "048 is valid (leading zero allowed)");

        // Null or blank
        assertFalse(BagelsGame.validateGuess(null, 3).isValid(), "Null is invalid");
        assertFalse(BagelsGame.validateGuess("   ", 3).isValid(), "Blank is invalid");

        // Incorrect length
        assertFalse(BagelsGame.validateGuess("12", 3).isValid(), "2 digits invalid for 3-digit game");
        assertFalse(BagelsGame.validateGuess("1234", 3).isValid(), "4 digits invalid for 3-digit game");

        // Non-digits
        assertFalse(BagelsGame.validateGuess("12a", 3).isValid(), "Letters invalid");
        assertFalse(BagelsGame.validateGuess("1 2", 3).isValid(), "Spaces invalid");
        assertFalse(BagelsGame.validateGuess("-12", 3).isValid(), "Minus sign invalid");

        // Duplicate digits
        assertFalse(BagelsGame.validateGuess("112", 3).isValid(), "Repeated digits invalid");
        assertFalse(BagelsGame.validateGuess("999", 3).isValid(), "All same digits invalid");
    }

    private static void testDifficulty() {
        assertEquals(3, Difficulty.EASY.digits(), "Easy digits");
        assertEquals(12, Difficulty.EASY.maxGuesses(), "Easy guesses");

        assertEquals(3, Difficulty.NORMAL.digits(), "Normal digits");
        assertEquals(10, Difficulty.NORMAL.maxGuesses(), "Normal guesses");

        assertEquals(4, Difficulty.HARD.digits(), "Hard digits");
        assertEquals(10, Difficulty.HARD.maxGuesses(), "Hard guesses");

        assertEquals(5, Difficulty.EXPERT.digits(), "Expert digits");
        assertEquals(12, Difficulty.EXPERT.maxGuesses(), "Expert guesses");

        Difficulty custom = Difficulty.custom(6, 15);
        assertEquals(6, custom.digits(), "Custom digits");
        assertEquals(15, custom.maxGuesses(), "Custom guesses");

        assertThrows(() -> Difficulty.custom(0, 10), IllegalArgumentException.class, "0 digits invalid");
        assertThrows(() -> Difficulty.custom(11, 10), IllegalArgumentException.class, "11 digits invalid");
        assertThrows(() -> Difficulty.custom(3, 0), IllegalArgumentException.class, "0 max guesses invalid");
    }

    private static void testGameStats() {
        GameStats stats = new GameStats();
        assertEquals(0, stats.getGamesPlayed(), "Initial played");
        assertEquals(0, stats.getGamesWon(), "Initial won");
        assertEquals(0, stats.getGamesLost(), "Initial lost");
        assertEquals(0.0, stats.getWinPercentage(), "Initial win percentage");

        // Win round in 4 guesses
        stats.recordGame(true, 4);
        assertEquals(1, stats.getGamesPlayed(), "Played after win");
        assertEquals(1, stats.getGamesWon(), "Won count");
        assertEquals(1, stats.getCurrentStreak(), "Current streak");
        assertEquals(1, stats.getBestStreak(), "Best streak");
        assertEquals(4, stats.getFewestGuessesToWin(), "Fewest guesses");
        assertEquals(100.0, stats.getWinPercentage(), "Win percentage");

        // Win round in 2 guesses
        stats.recordGame(true, 2);
        assertEquals(2, stats.getGamesPlayed(), "Played 2");
        assertEquals(2, stats.getGamesWon(), "Won 2");
        assertEquals(2, stats.getCurrentStreak(), "Current streak 2");
        assertEquals(2, stats.getBestStreak(), "Best streak 2");
        assertEquals(2, stats.getFewestGuessesToWin(), "Fewest updated to 2");
        assertEquals(3.0, stats.getAverageGuessesPerWin(), "Average guesses: (4+2)/2 = 3.0");

        // Loss
        stats.recordGame(false, 10);
        assertEquals(3, stats.getGamesPlayed(), "Played 3");
        assertEquals(2, stats.getGamesWon(), "Won still 2");
        assertEquals(1, stats.getGamesLost(), "Lost 1");
        assertEquals(0, stats.getCurrentStreak(), "Current streak reset to 0");
        assertEquals(2, stats.getBestStreak(), "Best streak preserved as 2");
        assertEquals(66.666, stats.getWinPercentage(), 0.1, "Win % is ~66.7%");

        // Reset
        stats.reset();
        assertEquals(0, stats.getGamesPlayed(), "Reset games played");
        assertEquals(0, stats.getGamesWon(), "Reset games won");
        assertEquals(0, stats.getCurrentStreak(), "Reset streak");
    }

    private static void testAnsiClueFormatting() {
        Ansi.setEnabled(false);
        assertEquals("Fermi Pico", Ansi.formatClue("Fermi Pico"), "Plain formatting when disabled");

        Ansi.setEnabled(true);
        String formatted = Ansi.formatClue("Fermi Pico Bagels");
        assertTrue(formatted.contains("Fermi"), "Contains Fermi");
        assertTrue(formatted.contains("Pico"), "Contains Pico");
        assertTrue(formatted.contains("Bagels"), "Contains Bagels");
    }

    // Helper assertions
    private static void assertTrue(boolean condition, String message) {
        testsRun++;
        if (!condition) {
            testsFailed++;
            System.err.printf("  FAIL: %s%n", message);
        } else {
            testsPassed++;
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        testsRun++;
        if (expected == null && actual == null) {
            testsPassed++;
            return;
        }
        if (expected != null && expected.equals(actual)) {
            testsPassed++;
        } else {
            testsFailed++;
            System.err.printf("  FAIL: %s (Expected: <%s>, Actual: <%s>)%n", message, expected, actual);
        }
    }

    private static void assertEquals(double expected, double actual, double delta, String message) {
        testsRun++;
        if (Math.abs(expected - actual) <= delta) {
            testsPassed++;
        } else {
            testsFailed++;
            System.err.printf("  FAIL: %s (Expected: <%f>, Actual: <%f>, Delta: <%f>)%n",
                message, expected, actual, delta);
        }
    }

    private static void assertThrows(Runnable runnable, Class<? extends Throwable> expectedException, String message) {
        testsRun++;
        try {
            runnable.run();
            testsFailed++;
            System.err.printf("  FAIL: Expected exception %s but nothing was thrown. %s%n",
                expectedException.getSimpleName(), message);
        } catch (Throwable t) {
            if (expectedException.isInstance(t)) {
                testsPassed++;
            } else {
                testsFailed++;
                System.err.printf("  FAIL: Expected %s but caught %s: %s%n",
                    expectedException.getSimpleName(), t.getClass().getSimpleName(), message);
            }
        }
    }
}
