package bagels;

/**
 * Represents difficulty settings for the Bagels game,
 * specifying the number of digits to guess and the maximum guesses allowed.
 */
public record Difficulty(String name, int digits, int maxGuesses, String description) {

    public static final Difficulty EASY = new Difficulty(
        "Easy", 3, 12, "3 digits, 12 guesses (Great for beginners)"
    );

    public static final Difficulty NORMAL = new Difficulty(
        "Normal", 3, 10, "3 digits, 10 guesses (Classic Bagels rules)"
    );

    public static final Difficulty HARD = new Difficulty(
        "Hard", 4, 10, "4 digits, 10 guesses (A serious challenge)"
    );

    public static final Difficulty EXPERT = new Difficulty(
        "Expert", 5, 12, "5 digits, 12 guesses (Master deduction)"
    );

    public Difficulty {
        if (digits < 1 || digits > 10) {
            throw new IllegalArgumentException("Number of digits must be between 1 and 10 (inclusive).");
        }
        if (maxGuesses < 1) {
            throw new IllegalArgumentException("Max guesses must be at least 1.");
        }
        if (name == null || name.isBlank()) {
            name = "Custom";
        }
        if (description == null || description.isBlank()) {
            description = String.format("%d digits, %d guesses", digits, maxGuesses);
        }
    }

    public static Difficulty custom(int digits, int maxGuesses) {
        return new Difficulty(
            "Custom",
            digits,
            maxGuesses,
            String.format("%d digits, %d guesses (Custom configuration)", digits, maxGuesses)
        );
    }

    @Override
    public String toString() {
        return String.format("%s (%d digits, %d guesses)", name, digits, maxGuesses);
    }
}
