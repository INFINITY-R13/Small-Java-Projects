package bagels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Core game engine for the Bagels deduction game.
 * Contains purely functional and decoupled game logic:
 * secret number generation, clue calculation, and guess validation.
 */
public class BagelsGame {

    public static final String CLUE_FERMI = "Fermi";
    public static final String CLUE_PICO = "Pico";
    public static final String CLUE_BAGELS = "Bagels";

    /**
     * Result of validating a player's guess.
     */
    public record ValidationResult(boolean isValid, String errorMessage) {
        public static ValidationResult ok() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
    }

    /**
     * Generates a secret number containing unique random digits (0-9).
     *
     * @param numDigits number of unique digits (1 to 10)
     * @return secret number string
     */
    public static String generateSecretNumber(int numDigits) {
        return generateSecretNumber(numDigits, new Random());
    }

    /**
     * Generates a secret number using a specific Random generator.
     *
     * @param numDigits number of unique digits (1 to 10)
     * @param random the Random instance to use
     * @return secret number string
     */
    public static String generateSecretNumber(int numDigits, Random random) {
        if (numDigits < 1 || numDigits > 10) {
            throw new IllegalArgumentException("Number of digits must be between 1 and 10.");
        }
        if (random == null) {
            throw new IllegalArgumentException("Random generator cannot be null.");
        }

        List<Character> digits = new ArrayList<>(10);
        for (char c = '0'; c <= '9'; c++) {
            digits.add(c);
        }
        Collections.shuffle(digits, random);

        StringBuilder sb = new StringBuilder(numDigits);
        for (int i = 0; i < numDigits; i++) {
            sb.append(digits.get(i));
        }
        return sb.toString();
    }

    /**
     * Checks if a guess matches the secret number exactly.
     */
    public static boolean isWinningGuess(String guess, String secret) {
        return secret != null && secret.equals(guess);
    }

    /**
     * Computes the clues for a given guess against the secret number.
     *
     * Clue rules:
     * - "Fermi": One digit is correct and in the right position.
     * - "Pico": One digit is correct but in the wrong position.
     * - "Bagels": None of the digits are correct.
     *
     * Clues are sorted alphabetically so their ordering reveals no position information.
     *
     * @param guess the player's guess
     * @param secret the secret number
     * @return clue string (e.g., "Fermi Pico", "Fermi", "Bagels")
     */
    public static String getClues(String guess, String secret) {
        if (guess == null || secret == null) {
            throw new IllegalArgumentException("Guess and secret must not be null.");
        }
        if (guess.length() != secret.length()) {
            throw new IllegalArgumentException("Guess length must match secret length.");
        }

        int length = secret.length();
        boolean[] secretMatched = new boolean[length];
        boolean[] guessMatched = new boolean[length];
        List<String> clues = new ArrayList<>();

        // First pass: identify exact matches (Fermi)
        for (int i = 0; i < length; i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                clues.add(CLUE_FERMI);
                secretMatched[i] = true;
                guessMatched[i] = true;
            }
        }

        // Second pass: identify digits in wrong position (Pico)
        for (int i = 0; i < length; i++) {
            if (!guessMatched[i]) {
                char gChar = guess.charAt(i);
                for (int j = 0; j < length; j++) {
                    if (!secretMatched[j] && secret.charAt(j) == gChar) {
                        clues.add(CLUE_PICO);
                        secretMatched[j] = true;
                        break;
                    }
                }
            }
        }

        if (clues.isEmpty()) {
            return CLUE_BAGELS;
        }

        Collections.sort(clues);
        return String.join(" ", clues);
    }

    /**
     * Validates whether a player's input conforms to Bagels guess rules:
     * - Exactly expected length
     * - Contains only digits 0-9
     * - Contains no duplicate digits
     */
    public static ValidationResult validateGuess(String guess, int expectedDigits) {
        if (guess == null || guess.isBlank()) {
            return ValidationResult.error(
                String.format("Please enter a %d-digit number.", expectedDigits)
            );
        }

        guess = guess.trim();

        if (guess.length() != expectedDigits) {
            return ValidationResult.error(
                String.format("Guess must be exactly %d digits long (you entered %d).",
                    expectedDigits, guess.length())
            );
        }

        Set<Character> seen = new HashSet<>();
        for (int i = 0; i < guess.length(); i++) {
            char c = guess.charAt(i);
            if (!Character.isDigit(c)) {
                return ValidationResult.error("Guess must contain digits (0-9) only.");
            }
            if (!seen.add(c)) {
                return ValidationResult.error(
                    String.format("Digits must be unique. Repeated digit: '%c'.", c)
                );
            }
        }

        return ValidationResult.ok();
    }

    /**
     * Returns the formatted game rules and clue guide.
     */
    public static String getRulesText() {
        return """
            =============================================================
                                HOW TO PLAY BAGELS
            =============================================================
             Bagels is a deductive logic game. The computer picks a secret
             number with unique, non-repeating digits. Your mission is to
             deduce the secret number within the maximum number of guesses!

             After each guess, you receive clues:
               * Fermi  -> One digit is correct AND in the right position.
               * Pico   -> One digit is correct BUT in the wrong position.
               * Bagels -> None of your digits are correct.

             Example:
               If secret is 248 and your guess is 843:
               Clues: "Fermi Pico"
               (4 is Fermi because it's in pos 2; 8 is Pico because it's
                in pos 1 instead of pos 3; 3 is not in the secret.)

             NOTE: Clues are sorted alphabetically, so the order of clues
                   does NOT indicate which digit corresponds to which clue!
            =============================================================
            """;
    }
}
