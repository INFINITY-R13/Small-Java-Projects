public enum GameResult {
    WIN("You Won! 🎉"),
    LOSE("Computer Won! 🤖"),
    TIE("It's a Tie! 🤝");

    private final String description;

    GameResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Evaluates the outcome for the player against the computer.
     *
     * @param playerMove the player's move
     * @param computerMove the computer's move
     * @return WIN if player wins, LOSE if computer wins, TIE if same move
     */
    public static GameResult evaluate(Move playerMove, Move computerMove) {
        if (playerMove == computerMove) {
            return TIE;
        }
        return playerMove.beats(computerMove) ? WIN : LOSE;
    }
}
