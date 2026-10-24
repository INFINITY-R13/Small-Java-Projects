import java.util.Random;
import java.util.Scanner;

/**
 * Number Guessing Game
 *
 * The computer picks a random number and the player tries to guess it.
 * After each guess, hints ("Too high" / "Too low") are given.
 * The game tracks the number of attempts and supports multiple rounds.
 */
public class GuessTheNumber {

    // ── Game configuration ──────────────────────────────────────────────────
    private static final int MIN_NUMBER   = 1;
    private static final int MAX_NUMBER   = 100;
    private static final int MAX_ATTEMPTS = 7;

    // ── Colours (ANSI) ──────────────────────────────────────────────────────
    private static final String RESET  = "\u001B[0m";
    private static final String GREEN  = "\u001B[32m";
    private static final String RED    = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN   = "\u001B[36m";
    private static final String BOLD   = "\u001B[1m";

    // ── State ───────────────────────────────────────────────────────────────
    private final Random  random  = new Random();
    private final Scanner scanner = new Scanner(System.in);

    private int totalRoundsPlayed = 0;
    private int totalRoundsWon    = 0;

    // ────────────────────────────────────────────────────────────────────────
    //  Entry Point
    // ────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        new GuessTheNumber().run();
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Game Loop
    // ────────────────────────────────────────────────────────────────────────
    private void run() {
        printBanner();

        boolean keepPlaying = true;
        while (keepPlaying) {
            playRound();
            keepPlaying = askToPlayAgain();
        }

        printFinalStats();
        scanner.close();
    }

    /** Plays one complete round. */
    private void playRound() {
        int secretNumber = random.nextInt(MAX_NUMBER - MIN_NUMBER + 1) + MIN_NUMBER;
        int attemptsLeft = MAX_ATTEMPTS;
        boolean won      = false;

        totalRoundsPlayed++;

        System.out.printf("%n%s🎯  Round %d — Guess a number between %d and %d.%s%n",
                CYAN + BOLD, totalRoundsPlayed, MIN_NUMBER, MAX_NUMBER, RESET);
        System.out.printf("     You have %s%d attempts%s.%n%n", BOLD, MAX_ATTEMPTS, RESET);

        while (attemptsLeft > 0 && !won) {
            int guess = promptGuess(attemptsLeft);

            attemptsLeft--;

            if (guess == secretNumber) {
                won = true;
                totalRoundsWon++;
                int usedAttempts = MAX_ATTEMPTS - attemptsLeft;
                System.out.printf("%n%s🎉  Correct! The number was %d. You got it in %d attempt%s!%s%n",
                        GREEN + BOLD, secretNumber, usedAttempts,
                        usedAttempts == 1 ? "" : "s", RESET);
            } else if (attemptsLeft == 0) {
                System.out.printf("%n%s💀  Out of attempts! The number was %d.%s%n",
                        RED + BOLD, secretNumber, RESET);
            } else if (guess < secretNumber) {
                System.out.printf("     %s↑  Too low!%s  (%d attempt%s left)%n",
                        YELLOW, RESET, attemptsLeft, attemptsLeft == 1 ? "" : "s");
            } else {
                System.out.printf("     %s↓  Too high!%s (%d attempt%s left)%n",
                        YELLOW, RESET, attemptsLeft, attemptsLeft == 1 ? "" : "s");
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Input Helpers
    // ────────────────────────────────────────────────────────────────────────

    /** Prompts the user for a valid integer guess. */
    private int promptGuess(int attemptsLeft) {
        while (true) {
            System.out.printf("  %s[Attempt %d/%d]%s Enter your guess: ",
                    BOLD, MAX_ATTEMPTS - attemptsLeft + 1, MAX_ATTEMPTS, RESET);
            String input = scanner.nextLine().trim();

            try {
                int guess = Integer.parseInt(input);
                if (guess < MIN_NUMBER || guess > MAX_NUMBER) {
                    System.out.printf("     %s⚠  Please enter a number between %d and %d.%s%n",
                            RED, MIN_NUMBER, MAX_NUMBER, RESET);
                } else {
                    return guess;
                }
            } catch (NumberFormatException e) {
                System.out.printf("     %s⚠  Invalid input. Please enter a whole number.%s%n",
                        RED, RESET);
            }
        }
    }

    /** Asks the player whether they want another round. */
    private boolean askToPlayAgain() {
        while (true) {
            System.out.printf("%nPlay again? %s[y / n]%s: ", BOLD, RESET);
            String answer = scanner.nextLine().trim().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) return true;
            if (answer.equals("n") || answer.equals("no"))  return false;
            System.out.printf("     %s⚠  Please type y or n.%s%n", RED, RESET);
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Display Helpers
    // ────────────────────────────────────────────────────────────────────────

    private void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       🔢  GUESS  THE  NUMBER  🔢      ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println(RESET);
    }

    private void printFinalStats() {
        System.out.println();
        System.out.println(BOLD + "── Final Stats ────────────────────────" + RESET);
        System.out.printf("   Rounds played : %d%n", totalRoundsPlayed);
        System.out.printf("   Rounds won    : %s%d%s%n", GREEN, totalRoundsWon, RESET);
        System.out.printf("   Rounds lost   : %s%d%s%n", RED, totalRoundsPlayed - totalRoundsWon, RESET);
        System.out.printf("   Win rate      : %.0f%%%n",
                totalRoundsPlayed == 0 ? 0.0 : (totalRoundsWon * 100.0 / totalRoundsPlayed));
        System.out.println(BOLD + "───────────────────────────────────────" + RESET);
        System.out.println("  Thanks for playing! 👋");
        System.out.println();
    }
}
