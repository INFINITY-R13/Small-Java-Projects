public enum Move {
    ROCK("Rock", "🪨"),
    PAPER("Paper", "📄"),
    SCISSORS("Scissors", "✂️");

    private final String name;
    private final String icon;

    Move(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return icon + " " + name;
    }

    /**
     * Determines whether this move beats another move.
     *
     * @param other the move to compare against
     * @return true if this move beats other, false otherwise
     */
    public boolean beats(Move other) {
        if (other == null) {
            return false;
        }
        return (this == ROCK && other == SCISSORS)
                || (this == PAPER && other == ROCK)
                || (this == SCISSORS && other == PAPER);
    }

    /**
     * Returns a descriptive sentence explaining why this move beats the other.
     *
     * @param other the losing move
     * @return an explanation string
     */
    public String getWinReason(Move other) {
        if (this == ROCK && other == SCISSORS) {
            return "Rock crushes Scissors!";
        } else if (this == PAPER && other == ROCK) {
            return "Paper covers Rock!";
        } else if (this == SCISSORS && other == PAPER) {
            return "Scissors cut Paper!";
        }
        return "";
    }

    /**
     * Parses user input into a corresponding Move.
     * Accepts numeric choices ("1", "2", "3"), letter abbreviations ("r", "p", "s"),
     * and full names ("rock", "paper", "scissors").
     *
     * @param input the raw string input from user
     * @return corresponding Move, or null if input is invalid
     */
    public static Move fromInput(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim().toLowerCase();
        return switch (trimmed) {
            case "1", "r", "rock" -> ROCK;
            case "2", "p", "paper" -> PAPER;
            case "3", "s", "scissors" -> SCISSORS;
            default -> null;
        };
    }
}
