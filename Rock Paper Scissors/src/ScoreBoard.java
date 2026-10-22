public class ScoreBoard {
    private int roundsPlayed;
    private int playerWins;
    private int computerWins;
    private int ties;
    private int currentWinStreak;
    private int bestWinStreak;

    public ScoreBoard() {
        reset();
    }

    /**
     * Records the outcome of a single round and updates streaks.
     *
     * @param result the outcome of the round
     */
    public void record(GameResult result) {
        roundsPlayed++;
        switch (result) {
            case WIN -> {
                playerWins++;
                currentWinStreak++;
                if (currentWinStreak > bestWinStreak) {
                    bestWinStreak = currentWinStreak;
                }
            }
            case LOSE -> {
                computerWins++;
                currentWinStreak = 0;
            }
            case TIE -> {
                ties++;
                // In ties, streak can be preserved or kept as is
            }
        }
    }

    /**
     * Resets all tracked scores and streak counters.
     */
    public void reset() {
        this.roundsPlayed = 0;
        this.playerWins = 0;
        this.computerWins = 0;
        this.ties = 0;
        this.currentWinStreak = 0;
        this.bestWinStreak = 0;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public int getPlayerWins() {
        return playerWins;
    }

    public int getComputerWins() {
        return computerWins;
    }

    public int getTies() {
        return ties;
    }

    public int getCurrentWinStreak() {
        return currentWinStreak;
    }

    public int getBestWinStreak() {
        return bestWinStreak;
    }

    /**
     * Computes player win percentage (excluding or over total games).
     *
     * @return percentage as a double from 0.0 to 100.0
     */
    public double getWinPercentage() {
        if (roundsPlayed == 0) {
            return 0.0;
        }
        return ((double) playerWins / roundsPlayed) * 100.0;
    }

    /**
     * Prints a compact single-line score banner.
     */
    public void printQuickScore() {
        System.out.printf("[Score] You: %d | Computer: %d | Ties: %d (Win Streak: %d)%n",
                playerWins, computerWins, ties, currentWinStreak);
    }

    /**
     * Prints a comprehensive, formatted statistics box.
     */
    public void printDetailedStats() {
        System.out.println("+-----------------------------------------+");
        System.out.println("|           CAREER SCOREBOARD             |");
        System.out.println("+-----------------------------------------+");
        System.out.printf("|  Total Rounds Played : %-16d |%n", roundsPlayed);
        System.out.printf("|  Your Wins           : %-16d |%n", playerWins);
        System.out.printf("|  Computer Wins       : %-16d |%n", computerWins);
        System.out.printf("|  Ties                : %-16d |%n", ties);
        String winRateStr = String.format("%.1f%%", getWinPercentage());
        System.out.printf("|  Win Rate            : %-16s |%n", winRateStr);
        System.out.printf("|  Current Win Streak  : %-16d |%n", currentWinStreak);
        System.out.printf("|  Best Win Streak     : %-16d |%n", bestWinStreak);
        System.out.println("+-----------------------------------------+");
    }
}
