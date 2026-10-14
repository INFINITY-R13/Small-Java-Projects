package bagels;

/**
 * Tracks session statistics across Bagels games played.
 */
public class GameStats {

    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private int currentStreak;
    private int bestStreak;
    private int fewestGuessesToWin = Integer.MAX_VALUE;
    private int totalGuessesInWins;

    public void recordGame(boolean won, int guessesTaken) {
        gamesPlayed++;
        if (won) {
            gamesWon++;
            currentStreak++;
            if (currentStreak > bestStreak) {
                bestStreak = currentStreak;
            }
            if (guessesTaken < fewestGuessesToWin) {
                fewestGuessesToWin = guessesTaken;
            }
            totalGuessesInWins += guessesTaken;
        } else {
            gamesLost++;
            currentStreak = 0;
        }
    }

    public void reset() {
        gamesPlayed = 0;
        gamesWon = 0;
        gamesLost = 0;
        currentStreak = 0;
        bestStreak = 0;
        fewestGuessesToWin = Integer.MAX_VALUE;
        totalGuessesInWins = 0;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getGamesLost() {
        return gamesLost;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public int getFewestGuessesToWin() {
        return fewestGuessesToWin == Integer.MAX_VALUE ? 0 : fewestGuessesToWin;
    }

    public double getWinPercentage() {
        if (gamesPlayed == 0) {
            return 0.0;
        }
        return ((double) gamesWon / gamesPlayed) * 100.0;
    }

    public double getAverageGuessesPerWin() {
        if (gamesWon == 0) {
            return 0.0;
        }
        return (double) totalGuessesInWins / gamesWon;
    }

    public String getFormattedStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("             SESSION STATS               \n");
        sb.append("=========================================\n");
        sb.append(String.format(" Games Played    : %d%n", gamesPlayed));
        sb.append(String.format(" Games Won       : %d (%.1f%%)%n", gamesWon, getWinPercentage()));
        sb.append(String.format(" Games Lost      : %d%n", gamesLost));
        sb.append(String.format(" Current Streak  : %d%n", currentStreak));
        sb.append(String.format(" Best Streak     : %d%n", bestStreak));
        if (gamesWon > 0) {
            sb.append(String.format(" Fewest Guesses  : %d%n", getFewestGuessesToWin()));
            sb.append(String.format(" Avg Guesses/Win : %.1f%n", getAverageGuessesPerWin()));
        } else {
            sb.append(" Fewest Guesses  : N/A\n");
            sb.append(" Avg Guesses/Win : N/A\n");
        }
        sb.append("=========================================");
        return sb.toString();
    }
}
