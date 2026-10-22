import java.util.Scanner;

public class RockPaperScissors {
    private final Scanner scanner;
    private final ComputerPlayer computer;
    private final ScoreBoard scoreBoard;

    public RockPaperScissors() {
        this.scanner = new Scanner(System.in);
        this.computer = new ComputerPlayer("Computer");
        this.scoreBoard = new ScoreBoard();
    }

    public static void main(String[] args) {
        RockPaperScissors game = new RockPaperScissors();
        game.run();
    }

    public void run() {
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = prompt("Enter your choice: ");
            if (choice == null) {
                // EOF encountered
                break;
            }

            switch (choice.trim()) {
                case "1" -> playContinuousMode();
                case "2" -> playSeriesMode();
                case "3" -> {
                    System.out.println();
                    scoreBoard.printDetailedStats();
                    pause();
                }
                case "4" -> {
                    printRules();
                    pause();
                }
                case "5" -> {
                    scoreBoard.reset();
                    System.out.println("\n✓ Scoreboard and streaks have been reset!\n");
                    pause();
                }
                case "6", "q", "quit", "exit" -> {
                    running = false;
                }
                default -> {
                    System.out.println("❌ Invalid option. Please select 1 through 6.");
                }
            }
        }

        printExitMessage();
    }

    /**
     * Continuous / Endless play mode where player plays round by round until they quit.
     */
    private void playContinuousMode() {
        System.out.println("\n--- Quick Play Mode ---");
        System.out.println("Type '1' (Rock), '2' (Paper), '3' (Scissors), or 'q' to return to menu.\n");

        int localRound = 1;
        while (true) {
            System.out.printf("--- [Round %d] ---%n", localRound);
            Move playerMove = getPlayerMove();
            if (playerMove == null) {
                // Player chose to quit back to menu or EOF
                System.out.println("Returning to main menu...\n");
                break;
            }

            playSingleRound(playerMove);
            scoreBoard.printQuickScore();
            System.out.println();
            localRound++;
        }
    }

    /**
     * Series mode where the player and computer race to a target number of wins.
     */
    private void playSeriesMode() {
        System.out.println("\n--- Series Mode ---");
        System.out.println("Choose series type:");
        System.out.println("  1) Best of 3 (First to 2 wins)");
        System.out.println("  2) Best of 5 (First to 3 wins)");
        System.out.println("  3) Custom target wins");
        System.out.println("  4) Back to Main Menu");

        String option = prompt("Select series option: ");
        if (option == null || option.trim().equals("4") || option.equalsIgnoreCase("q")) {
            return;
        }

        int targetWins;
        switch (option.trim()) {
            case "1" -> targetWins = 2;
            case "2" -> targetWins = 3;
            case "3" -> {
                targetWins = promptForPositiveInt("Enter target number of wins (e.g. 5): ", 1, 100);
                if (targetWins <= 0) {
                    return;
                }
            }
            default -> {
                System.out.println("Invalid option. Returning to menu.");
                return;
            }
        }

        System.out.printf("%nStarting series: First to %d wins wins the series!%n%n", targetWins);

        int seriesPlayerWins = 0;
        int seriesComputerWins = 0;
        int seriesTies = 0;
        int roundNumber = 1;

        while (seriesPlayerWins < targetWins && seriesComputerWins < targetWins) {
            System.out.printf("--- [Series Round %d | Series Score: You %d - %d Computer] ---%n",
                    roundNumber, seriesPlayerWins, seriesComputerWins);

            Move playerMove = getPlayerMove();
            if (playerMove == null) {
                System.out.println("Series aborted by player.\n");
                return;
            }

            Move computerMove = computer.makeMove();
            GameResult result = playRoundWithMoves(playerMove, computerMove);

            switch (result) {
                case WIN -> seriesPlayerWins++;
                case LOSE -> seriesComputerWins++;
                case TIE -> seriesTies++;
            }

            System.out.printf("Series Progress -> You: %d/%d | Computer: %d/%d (Ties: %d)%n%n",
                    seriesPlayerWins, targetWins, seriesComputerWins, targetWins, seriesTies);

            roundNumber++;
        }

        // Series wrap up
        System.out.println("==========================================");
        if (seriesPlayerWins >= targetWins) {
            System.out.println("  🏆 CHAMPION! You won the series " + seriesPlayerWins + " to " + seriesComputerWins + "! 🏆");
        } else {
            System.out.println("  🤖 DEFEAT! Computer won the series " + seriesComputerWins + " to " + seriesPlayerWins + ".");
        }
        System.out.println("==========================================\n");
        pause();
    }

    /**
     * Prompts the player for a move or special command.
     * Returns null when player wants to quit the round/mode.
     */
    private Move getPlayerMove() {
        while (true) {
            System.out.print("Choose [1] Rock 🪨, [2] Paper 📄, [3] Scissors ✂️ (or 'q' to quit): ");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("back")) {
                return null;
            }

            if (input.equalsIgnoreCase("stats") || input.equalsIgnoreCase("s")) {
                scoreBoard.printQuickScore();
                continue;
            }

            Move move = Move.fromInput(input);
            if (move != null) {
                return move;
            }

            System.out.println("⚠️  Invalid choice! Type 1 (Rock), 2 (Paper), 3 (Scissors), or 'q' to quit.");
        }
    }

    /**
     * Executes a single round with the provided player move and generated computer move.
     */
    private void playSingleRound(Move playerMove) {
        Move computerMove = computer.makeMove();
        playRoundWithMoves(playerMove, computerMove);
    }

    /**
     * Evaluates, displays, and records the round between player and computer moves.
     */
    private GameResult playRoundWithMoves(Move playerMove, Move computerMove) {
        System.out.println("\n  Your choice     : " + playerMove.getDisplayName());
        System.out.println("  Computer choice : " + computerMove.getDisplayName());

        GameResult result = GameResult.evaluate(playerMove, computerMove);

        System.out.println("  Result          : " + result.getDescription());
        if (result == GameResult.WIN) {
            System.out.println("  Details         : " + playerMove.getWinReason(computerMove));
        } else if (result == GameResult.LOSE) {
            System.out.println("  Details         : " + computerMove.getWinReason(playerMove));
        } else {
            System.out.println("  Details         : Both players chose " + playerMove.getName() + "!");
        }
        System.out.println();

        scoreBoard.record(result);
        return result;
    }

    private int promptForPositiveInt(String message, int min, int max) {
        while (true) {
            String input = prompt(message);
            if (input == null) return -1;
            try {
                int value = Integer.parseInt(input.trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Try again.");
            }
        }
    }

    private String prompt(String message) {
        System.out.print(message);
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    private void pause() {
        System.out.print("Press [Enter] to continue...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
        System.out.println();
    }

    private void printBanner() {
        System.out.println("======================================================");
        System.out.println("        🪨   📄   ✂️   ROCK PAPER SCISSORS   ✂️   📄   🪨");
        System.out.println("======================================================");
        System.out.println(" Welcome to the ultimate command-line Rock-Paper-Scissors!");
        System.out.println(" Test your luck and strategy against the computer.\n");
    }

    private void printMainMenu() {
        System.out.println("================== MAIN MENU ==================");
        System.out.println("  1. Quick Play (Endless rounds)");
        System.out.println("  2. Series Mode (Best-of-N competition)");
        System.out.println("  3. View Scoreboard & Statistics");
        System.out.println("  4. Rules & Help");
        System.out.println("  5. Reset Scoreboard");
        System.out.println("  6. Exit");
        System.out.println("===============================================");
    }

    private void printRules() {
        System.out.println("\n================ GAME RULES ================");
        System.out.println(" - 🪨 Rock crushes Scissors (Rock beats Scissors)");
        System.out.println(" - ✂️ Scissors cut Paper    (Scissors beat Paper)");
        System.out.println(" - 📄 Paper covers Rock     (Paper beats Rock)");
        System.out.println(" - Identical moves result in a Tie (🤝)");
        System.out.println("\nCommands during gameplay:");
        System.out.println(" - '1' or 'r' or 'rock'     -> Pick Rock");
        System.out.println(" - '2' or 'p' or 'paper'    -> Pick Paper");
        System.out.println(" - '3' or 's' or 'scissors' -> Pick Scissors");
        System.out.println(" - 'stats'                  -> Check current score");
        System.out.println(" - 'q' or 'quit'            -> Return to main menu");
        System.out.println("============================================\n");
    }

    private void printExitMessage() {
        System.out.println("\n======================================================");
        System.out.println("Thanks for playing Rock Paper Scissors! Final stats:");
        scoreBoard.printDetailedStats();
        System.out.println("Goodbye! 👋");
        System.out.println("======================================================");
    }
}
