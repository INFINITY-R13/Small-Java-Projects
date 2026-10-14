package bagels;

import java.util.Scanner;

/**
 * Interactive command-line interface for the Bagels deduction game.
 */
public class Bagels {

    private final Scanner scanner;
    private final GameStats stats;
    private Difficulty currentDifficulty;

    public Bagels(Scanner scanner) {
        this.scanner = scanner;
        this.stats = new GameStats();
        this.currentDifficulty = Difficulty.NORMAL;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Bagels app = new Bagels(scanner);
        app.run();
    }

    public void run() {
        printBanner();
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = prompt("Select an option (1-6): ").trim();

            switch (choice) {
                case "1" -> playRound();
                case "2" -> selectDifficulty();
                case "3" -> showRules();
                case "4" -> showStats();
                case "5" -> toggleColors();
                case "6", "q", "exit", "quit" -> {
                    running = false;
                    System.out.println(Ansi.boldCyan("\nThanks for playing Bagels! Goodbye!\n"));
                }
                default -> System.out.println(Ansi.boldRed("Invalid option. Please choose 1-6."));
            }
        }
    }

    private void printBanner() {
        System.out.println(Ansi.boldCyan("""
             ____                    _           
            |  _ \\                  | |          
            | |_) | __ _  __ _  ___| |___       
            |  _ < / _` |/ _` |/ _ \\ / __|      
            | |_) | (_| | (_| |  __/ \\__ \\      
            |____/ \\__,_|\\__, |\\___|_|___/      
                          __/ |                 
                         |___/                  
             === Deductive Logic Number Game ===
            """));
    }

    private void printMainMenu() {
        System.out.println("\n" + Ansi.bold("MAIN MENU"));
        System.out.println("1. Play Game [" + Ansi.boldGreen(currentDifficulty.toString()) + "]");
        System.out.println("2. Change Difficulty");
        System.out.println("3. How to Play (Rules)");
        System.out.println("4. View Session Statistics");
        System.out.println("5. Toggle Color Output (Currently: " + (Ansi.isEnabled() ? Ansi.green("ON") : "OFF") + ")");
        System.out.println("6. Quit");
    }

    public void playRound() {
        int digits = currentDifficulty.digits();
        int maxGuesses = currentDifficulty.maxGuesses();
        String secret = BagelsGame.generateSecretNumber(digits);

        System.out.println("\n" + "=".repeat(50));
        System.out.printf(Ansi.bold("NEW GAME: %s%n"), currentDifficulty.name());
        System.out.printf("I am thinking of a %s-digit number with no repeated digits.%n",
            Ansi.boldYellow(String.valueOf(digits)));
        System.out.printf("You have %s guesses to deduce it.%n",
            Ansi.boldYellow(String.valueOf(maxGuesses)));
        System.out.println(Ansi.dim("Commands: 'help' for rules | 'stats' for record | 'quit' to forfeit"));
        System.out.println("=".repeat(50));

        boolean won = false;
        int guessNum = 1;

        while (guessNum <= maxGuesses) {
            String promptText = String.format("[%s of %d] Enter guess: ",
                Ansi.bold(String.format("Guess #%d", guessNum)), maxGuesses);
            String input = prompt(promptText).trim();

            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                String confirm = prompt("Are you sure you want to forfeit this round? (y/n): ").trim();
                if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
                    System.out.println(Ansi.boldRed("\nRound forfeited!"));
                    System.out.printf("The secret number was: %s%n", Ansi.boldGreen(secret));
                    stats.recordGame(false, guessNum);
                    return;
                }
                continue;
            }

            if (input.equalsIgnoreCase("help") || input.equalsIgnoreCase("rules")) {
                showRules();
                continue;
            }

            if (input.equalsIgnoreCase("stats")) {
                showStats();
                continue;
            }

            BagelsGame.ValidationResult validation = BagelsGame.validateGuess(input, digits);
            if (!validation.isValid()) {
                System.out.println(Ansi.boldRed("  -> " + validation.errorMessage()));
                continue;
            }

            if (BagelsGame.isWinningGuess(input, secret)) {
                won = true;
                System.out.println("\n" + Ansi.boldGreen("🎉 YOU GOT IT! YOU WON! 🎉"));
                System.out.printf("You cracked the code %s in %s %s!%n",
                    Ansi.boldCyan(secret),
                    Ansi.boldYellow(String.valueOf(guessNum)),
                    guessNum == 1 ? "guess" : "guesses"
                );
                stats.recordGame(true, guessNum);
                break;
            } else {
                String clues = BagelsGame.getClues(input, secret);
                System.out.printf("  -> Clues: %s%n", Ansi.formatClue(clues));
                guessNum++;
            }
        }

        if (!won) {
            System.out.println("\n" + Ansi.boldRed("Game Over! You ran out of guesses."));
            System.out.printf("The secret number was: %s%n", Ansi.boldGreen(secret));
            stats.recordGame(false, maxGuesses);
        }

        // Ask to play another round immediately
        String playAgain = prompt("\nPlay another round? (y/n): ").trim();
        if (playAgain.equalsIgnoreCase("y") || playAgain.equalsIgnoreCase("yes")) {
            playRound();
        }
    }

    private void selectDifficulty() {
        System.out.println("\n" + Ansi.bold("SELECT DIFFICULTY"));
        System.out.println("1. " + Difficulty.EASY.description());
        System.out.println("2. " + Difficulty.NORMAL.description() + " [Default]");
        System.out.println("3. " + Difficulty.HARD.description());
        System.out.println("4. " + Difficulty.EXPERT.description());
        System.out.println("5. Custom (choose your own digits and guesses)");
        System.out.println("6. Back to Main Menu");

        String choice = prompt("Enter choice (1-6): ").trim();
        switch (choice) {
            case "1" -> {
                currentDifficulty = Difficulty.EASY;
                System.out.println(Ansi.boldGreen("Difficulty set to Easy!"));
            }
            case "2" -> {
                currentDifficulty = Difficulty.NORMAL;
                System.out.println(Ansi.boldGreen("Difficulty set to Normal!"));
            }
            case "3" -> {
                currentDifficulty = Difficulty.HARD;
                System.out.println(Ansi.boldGreen("Difficulty set to Hard!"));
            }
            case "4" -> {
                currentDifficulty = Difficulty.EXPERT;
                System.out.println(Ansi.boldGreen("Difficulty set to Expert!"));
            }
            case "5" -> configureCustomDifficulty();
            case "6" -> {}
            default -> System.out.println(Ansi.boldRed("Invalid choice. Difficulty unchanged."));
        }
    }

    private void configureCustomDifficulty() {
        System.out.println("\n" + Ansi.bold("CUSTOM DIFFICULTY CONFIGURATION"));

        int digits = promptInt("Enter number of digits (1 to 10): ", 1, 10);
        int guesses = promptInt("Enter maximum number of guesses (1 to 50): ", 1, 50);

        currentDifficulty = Difficulty.custom(digits, guesses);
        System.out.printf(Ansi.boldGreen("Custom difficulty configured: %s%n"), currentDifficulty);
    }

    private int promptInt(String message, int min, int max) {
        while (true) {
            String input = prompt(message).trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf(Ansi.boldRed("Value must be between %d and %d.%n"), min, max);
            } catch (NumberFormatException e) {
                System.out.println(Ansi.boldRed("Please enter a valid integer."));
            }
        }
    }

    private void showRules() {
        System.out.println("\n" + BagelsGame.getRulesText());
    }

    private void showStats() {
        System.out.println("\n" + stats.getFormattedStats());
    }

    private void toggleColors() {
        boolean newState = !Ansi.isEnabled();
        Ansi.setEnabled(newState);
        if (newState) {
            System.out.println(Ansi.green("Color output ENABLED."));
        } else {
            System.out.println("Color output DISABLED.");
        }
    }

    private String prompt(String message) {
        System.out.print(message);
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return "quit";
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }

    public GameStats getStats() {
        return stats;
    }
}
